/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package org.tockit.docco.indexer;

import org.apache.lucene.index.IndexWriter;
import org.tockit.docco.ConfigurationManager;
import org.tockit.docco.GlobalConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Indexer extends Thread {
	private static final String CONFIGURATION_MAPPING_ENTRY_NAME = "extension_mappings";
    private static final String CONFIGURATION_SECTION_NAME = "Indexer";
    public interface CallbackRecipient {
		void showFeedbackMessage(String message);
	}

    private IndexWriter writer;
	private boolean stateChangeRequested = false;
	private boolean running = true;
    private List fileQueue = new LinkedList();
	private CallbackRecipient callbackRecipient;
    private DocumentProcessingFactory docProcessingFactory = new DocumentProcessingFactory();
    private int filesSeen;
	
	public Indexer(CallbackRecipient output) {
		this.callbackRecipient = output;
		try {
			List mappings = ConfigurationManager.fetchStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, 50);
			if(mappings.size() == 0) {
				mappings = new ArrayList(20);
				mappings.add("html:org.tockit.docco.indexer.HtmlDocumentProcessor");
				mappings.add("htm:org.tockit.docco.indexer.HtmlDocumentProcessor");
				mappings.add("pdf:org.tockit.docco.indexer.PdfDocumentProcessor");
				mappings.add("doc:org.tockit.docco.indexer.MSWordProcessor");
				mappings.add("txt:org.tockit.docco.indexer.PlainTextDocumentProcessor");
				mappings.add("xls:org.tockit.docco.indexer.MSExcelDocProcessor");
				ConfigurationManager.storeStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, mappings);
			}
			for (Iterator iter = mappings.iterator(); iter.hasNext();) {
                String mapping = (String) iter.next();
                int colonIndex = mapping.indexOf(':');
				String extension = mapping.substring(0,colonIndex);
				String className = mapping.substring(colonIndex + 1);
				try {
					this.docProcessingFactory.registerExtension(extension,Class.forName(className));
				} catch(ClassCastException e) {
					System.err.println("WARNING: class " + className + " could not be loaded due to this error:");
					e.printStackTrace();
				}
            }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	synchronized public void startIndexing(String indexLocation) throws IOException {
		this.writer = new IndexWriter(indexLocation,
									  GlobalConstants.DEFAULT_ANALYZER,
								      false);
		this.filesSeen = 0;
	}

	synchronized public void stopIndexing() throws IOException {
		if(this.writer == null) {
			return; // nothing to stop
		}
		this.writer.close();
		this.fileQueue.clear();
		this.writer = null;
	}

	public void run() {
		while(true) {
			File file;
			if(!this.fileQueue.isEmpty()) {  
				file = (File) this.fileQueue.remove(0);
			} else {
				synchronized(this) {
					if(this.writer!=null){
						try {
							this.writer.optimize();
						} catch (IOException e) {
							e.printStackTrace();
							/// @todo I think this is a potential lock
							showFeedbackMessage("ERROR: " + e.getMessage());
						}
					}
				}
				file = null;
			}
			if(this.writer != null && file != null) {
				indexDocs(file);
			} else {
				if(this.callbackRecipient != null) {
					this.callbackRecipient.showFeedbackMessage("Ready!");
				}
			}
		}
	}
	
	synchronized public void enqueue(File file) {
		this.fileQueue.add(file);
	}
	
	private void indexDocs(File file) {
		showProgress(writer.docCount(), this.filesSeen, file.getAbsolutePath());
		try {
			synchronized(this) {
				if (this.writer == null) {
					return;
				}
				if (file.isDirectory()) {
					String[] files = file.list();
					if(files == null) {
						// seems to happen if dir access denied
						return; 
					}
					for (int i = 0; i < files.length; i++) {
						this.fileQueue.add(new File(file, files[i]));
					}
				}
				else {
					this.filesSeen++;
					writer.addDocument(this.docProcessingFactory.processDocument(file));
				}
			}
		}
		catch (Exception e) {
			// sometimes shit happens. E.g. the PDF header might be screwed. Some other things
			// might be broken. We don't want to stop indexing whenever one document fails to be
			// read properly, so we just ignore it for now. Of course we should consider
			// @todo some error handling/reporting
		}
	}


	private void showProgress(int indexed, int total, String dir) {
		showFeedbackMessage("Indexing: " + indexed + " documents so far" + " (" + dir + ")");
	}

	private void showFeedbackMessage(String string) {
		if(this.callbackRecipient != null) {
			this.callbackRecipient.showFeedbackMessage(string);
		}
    }
}
