/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.tockit.docco.documenthandler.DocumentHandlerRegistry;
import org.tockit.docco.gui.GuiMessages;
import org.tockit.docco.indexer.DocumentHandlerMapping;
import org.tockit.docco.indexer.Indexer;
import org.tockit.docco.indexer.Indexer.CallbackRecipient;

public class Index {
    /**
	 * Indicates whether this index is used for querying at the moment.
	 */
	private boolean active = true;
	
	private String name;
    private File indexLocation;
	private File baseDirectory;
	private Indexer indexer;
	private Thread indexThread;
	private int indexingPriority = Thread.MIN_PRIORITY;
	private CallbackRecipient callbackRecipient;

    private Analyzer analyzer;
	
    /**
     * Opens an existing index.
     * 
     * @param name The name of the index. Not null.
     * @param indexDirectory The directory in which the index resides. Not null.
     * @param callbackRecipient An object that gets the callbacks for changes from the index. Not null.
     * @param forceAccess Iff true, a locked index will be silently unlocked.
     * 
     * @return A representation of the opened index.
     * 
     * @throws FileNotFoundException Iff the index can not be found (name or directory don't match).
     * @throws IOException Iff any error happens while opening the index.
     * @throws ClassNotFoundException Iff a required document handler can not be instantiated.
     */
    public static Index openIndex(String name, File indexDirectory, Indexer.CallbackRecipient callbackRecipient, boolean forceAccess) 
    				throws FileNotFoundException, IOException, ClassNotFoundException {
        Properties settings = new Properties();
        File settingsFile = getPropertiesFile(indexDirectory, name);
        settings.load(new FileInputStream(settingsFile));
		try {
			File baseDirectory = new File(settings.getProperty("baseDirectory")); //$NON-NLS-1$
            String analyzerClassName = settings.getProperty("analyzer"); //$NON-NLS-1$
            Analyzer analyzer;
            try {
                analyzer = (Analyzer) Class.forName(analyzerClassName).newInstance();
            } catch (Exception e) {
                throw new IOException(MessageFormat.format(GuiMessages.getString("Index.failedToInstantiateAnalyserError.text"),  //$NON-NLS-1$
                		new Object[]{analyzerClassName}));
            }
            boolean active = "true".equals(settings.getProperty("active")); //$NON-NLS-1$ //$NON-NLS-2$
			File mappingsFile = getMappingsFile(indexDirectory, name);
			Index retVal;
			if(mappingsFile.exists()) {
				List documentMappings = new ArrayList();
				String[] mappings = getLinesOfFile(mappingsFile);
				for (int i = 0; i < mappings.length; i++) {
					documentMappings.add(new DocumentHandlerMapping(mappings[i]));
				}
				retVal = new Index(name, indexDirectory, baseDirectory, analyzer, documentMappings, callbackRecipient, active);
			} else {
				retVal = new Index(name, indexDirectory, baseDirectory, analyzer, DocumentHandlerRegistry.getDefaultMappings(), 
				    			   callbackRecipient, active);
			}
			if(retVal.isLocked() && forceAccess) {
				retVal.removeLock();
			}
			return retVal;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IOException(MessageFormat.format(GuiMessages.getString("Index.noBaseDirectoryFoundError.text"), new Object[]{settingsFile})); //$NON-NLS-1$
		}
	}
	
    public static Index createIndex(String name, File indexDirectory, File baseDirectory, 
                                    Analyzer analyzer, List documentMappings, 
									Indexer.CallbackRecipient callbackRecipient) throws IOException {
        createDirPath(indexDirectory);
		IndexWriter writer = new IndexWriter(new File(indexDirectory, name),
                                             analyzer, true, MaxFieldLength.LIMITED);
		writer.close();
		Index retVal = new Index(name, indexDirectory, baseDirectory, analyzer, documentMappings, callbackRecipient, true);
		retVal.updateIndex();
		return retVal;
	}
    
    public boolean isLocked() throws IOException {
        return IndexWriter.isLocked(getIndexLocation().getPath());
    }
	
	public void updateIndex() {
		if(this.indexThread != null && this.indexThread.isAlive()) {
			throw new IllegalStateException(GuiMessages.getString("Index.indexAlreadyBeingUpdatedError.text")); //$NON-NLS-1$
		}
		this.callbackRecipient.showFeedbackMessage(GuiMessages.getString("Index.feedbackMessageUpdating.text")); //$NON-NLS-1$
        this.indexThread = new Thread(this.indexer);
        this.indexThread.setPriority(indexingPriority);
		this.indexThread.start();
    }
    
    public void setPriority(int priority) {
		if(priority < Thread.MIN_PRIORITY) {
			throw new IllegalArgumentException(GuiMessages.getString("Index.priorityTooLowError.text")); //$NON-NLS-1$
		}
		if(priority > Thread.MAX_PRIORITY) {
			throw new IllegalArgumentException(GuiMessages.getString("Index.priorityTooHighError.text")); //$NON-NLS-1$
		}
    	this.indexingPriority = priority;
    	if(this.indexThread != null) {
			this.indexThread.setPriority(priority);
    	}
    }

    private Index(String name, File indexDirectory, File baseDirectory, Analyzer analyzer, 
                  List documentMappings, Indexer.CallbackRecipient callbackRecipient,
                  boolean active) {
    	this.name = name;
		this.indexLocation = new File(indexDirectory, name);
		this.baseDirectory = baseDirectory;
        this.analyzer = analyzer;
        this.indexer = new Indexer(this.indexLocation, baseDirectory, analyzer, documentMappings, callbackRecipient);
        this.callbackRecipient = callbackRecipient;
        this.active = active;
        saveSettingsAndMappings();
	}

	private static File getPropertiesFile(File indexLocation, String indexName) {
        return new File(cleanPath(new File(indexLocation, indexName).getPath()) + ".properties"); //$NON-NLS-1$
	}
	
	private File getPropertiesFile() {
		return getPropertiesFile(this.indexLocation.getParentFile(), this.name);
	}

	private static File getMappingsFile(File indexLocation, String indexName) {
		return new File(cleanPath(new File(indexLocation, indexName).getPath()) + ".mappings"); //$NON-NLS-1$
	}

	private File getMappingsFile() {
		return getMappingsFile(this.indexLocation.getParentFile(), this.name);
	}

	private static String cleanPath(String indexLocation) {
		if(indexLocation.endsWith(File.separator)) {
			return indexLocation.substring(0, indexLocation.length() - File.separator.length());
		}
		return indexLocation;
	}

    private static String[] getLinesOfFile(File inputFile) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        List lines = new ArrayList();
        while(reader.ready()) {
        	lines.add(reader.readLine());
        }
        reader.close();
        return (String[]) lines.toArray(new String[lines.size()]);
    }
	
	public File getIndexLocation() {
		return this.indexLocation;
	}
	
	public void shutdown() {
		this.callbackRecipient.showFeedbackMessage(GuiMessages.getString("Index.feedbackMessageShuttingDown.text")); //$NON-NLS-1$
		if(this.indexThread != null) {
			this.indexer.stopIndexing();
			while(this.indexThread.isAlive()) {
				try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                	e.printStackTrace(); // just ignore otherwise, nothing we could do about it
                }
			}
		}
		saveSettingsAndMappings();
	}
	
    private void saveSettingsAndMappings() {
        try {
            Properties props = new Properties();
            props.setProperty("baseDirectory", this.baseDirectory.getPath()); //$NON-NLS-1$
            props.setProperty("analyzer", this.analyzer.getClass().getName()); //$NON-NLS-1$
            props.setProperty("active", this.active?"true":"false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            props.store(new FileOutputStream(getPropertiesFile()), "Configuration for a Docco index, do not edit"); //$NON-NLS-1$
            
			PrintStream out = new PrintStream(new FileOutputStream(getMappingsFile()));
            Iterator it = this.indexer.getDocumentMappings().iterator();
            while (it.hasNext()) {
            	DocumentHandlerMapping cur = (DocumentHandlerMapping) it.next();
            	out.println(cur.getSerialization());
            }
			out.close();
		} catch (IOException e) {
			e.printStackTrace(); // nothing we could do, but throwing on shutdown is not nice either.
		}
    }

    public List getDocumentMappings() {
        return this.indexer.getDocumentMappings();
    }

	public void setDocumentMappings(List documentMappings) {
		this.indexer.setDocumentMappings(documentMappings);
	}

	private static void createDirPath(File file) {
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (!parent.exists()) {
				createDirPath(parent);
			}
			else {
				file.mkdir();
			}
		}
	}

    public File getBaseDirectory() {
    	return this.baseDirectory;
    }
    
    public boolean isWorking() {
    	if(this.indexThread != null) {
			return this.indexThread.isAlive();
    	} else {
    		return false;
    	}
    }

    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
    	this.active = active;
    }
    
    public String getName() {
    	return this.name;
    }

    public void delete() throws IOException {
    	shutdown();

        getPropertiesFile().delete();
		getMappingsFile().delete();
		File[] indexContents = this.indexLocation.listFiles();
		for (int i = 0; i < indexContents.length; i++) {
            File file = indexContents[i];
            file.delete();
        }
        boolean deleted = this.indexLocation.delete();

        if(!deleted) {
        	throw new IOException(MessageFormat.format(GuiMessages.getString("Index.deletingIndexFailedError.text"), new Object[]{this.indexLocation})); //$NON-NLS-1$
        }

		this.callbackRecipient.showFeedbackMessage(MessageFormat.format(GuiMessages.getString("Index.feedbackMessageIndexDeleted.text"), new Object[]{getName()})); //$NON-NLS-1$
    }

    public void removeLock() throws IOException {
        IndexWriter.unlock(FSDirectory.getDirectory(getIndexLocation()));
    }

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }
}
