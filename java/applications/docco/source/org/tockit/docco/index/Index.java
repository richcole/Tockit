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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexWriter;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.indexer.DocumentHandlerRegistry;
import org.tockit.docco.indexer.Indexer;
import org.tockit.docco.indexer.Indexer.CallbackRecipient;

public class Index {
    /**
	 * Indicates wether this index is used for querying at the moment.
	 */
	private boolean active = true;
	
    private File indexLocation;
	private File baseDirectory;
	private Indexer indexer;
	private Thread indexThread;
	private DocumentHandlerRegistry docHandlersRegistry;
	public static int indexingPriority = Thread.MIN_PRIORITY;
	private CallbackRecipient callbackRecipient;
	
    public static Index openIndex(File indexLocation, Indexer.CallbackRecipient callbackRecipient) throws IOException {
		String[] paths = getLinesOfFile(getContentsFile(indexLocation));
		try {
			File baseDirectory = new File(paths[0]); 
			Index retVal = new Index(indexLocation, baseDirectory, callbackRecipient);
			retVal.callbackRecipient = callbackRecipient;
			return retVal;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IOException("No base directory found in '" + getContentsFile(indexLocation).getPath() + "'");
		}
	}
	
	public static Index createIndex(File indexLocation, File baseDirectory, 
									Indexer.CallbackRecipient callbackRecipient) throws IOException {
		createDirPath(indexLocation);
		IndexWriter writer = new IndexWriter(
								indexLocation,
								GlobalConstants.DEFAULT_ANALYZER,
								true);
		writer.close();
		Index retVal = new Index(indexLocation, baseDirectory, callbackRecipient);
		retVal.callbackRecipient = callbackRecipient;
		retVal.updateIndex();
		return retVal;
	}
	
	public void updateIndex() {
		if(this.indexThread != null && this.indexThread.isAlive()) {
			throw new IllegalStateException("Index is already been updated.");
		}
		this.callbackRecipient.showFeedbackMessage("Updating...");
		this.indexThread = new Thread(this.indexer);
        this.indexThread.setPriority(indexingPriority);
		this.indexThread.start();
    }

    private Index(File indexLocation, File baseDirectory, Indexer.CallbackRecipient callbackRecipient) throws IOException {
		this.indexLocation = indexLocation;
		this.baseDirectory = baseDirectory;
		
		File mappingsFile = getMappingsFile(indexLocation);
		if(mappingsFile.exists()) {
			this.docHandlersRegistry = new DocumentHandlerRegistry(getLinesOfFile(mappingsFile));
		} else {
			this.docHandlersRegistry = new DocumentHandlerRegistry(DocumentHandlerRegistry.DEFAULT_MAPPINGS);
		}
		
        this.indexer = new Indexer(indexLocation, baseDirectory, this.docHandlersRegistry, callbackRecipient);
	}

	private static File getContentsFile(File indexLocation) {
        return new File(cleanPath(indexLocation.getPath()) + ".contents");
	}

    private static String cleanPath(String indexLocation) {
        if(indexLocation.endsWith(File.separator)) {
        	indexLocation = indexLocation.substring(0, indexLocation.length() - File.separator.length());
        }
        return indexLocation;
    }

	private static File getMappingsFile(File indexLocation) {
		return new File(cleanPath(indexLocation.getPath()) + ".mappings");
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
		this.callbackRecipient.showFeedbackMessage("Shutting down...");
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
		try {
			PrintStream out = new PrintStream(new FileOutputStream(getContentsFile(this.indexLocation)));
			out.println(this.baseDirectory.getPath());
			out.close();
			out = new PrintStream(new FileOutputStream(getMappingsFile(this.indexLocation)));
			String[] mappings = this.docHandlersRegistry.getMappingStringsList();
			for (int i = 0; i < mappings.length; i++) {
				out.println(mappings[i]);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace(); // nothing we could do, but throwing on shutdown is not nice either.
		}
	}
	
    public DocumentHandlerRegistry getDocHandlersRegistry() {
        return docHandlersRegistry;
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
}
