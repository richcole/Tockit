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
import org.tockit.docco.indexer.documenthandler.DocumentHandler;
import org.tockit.docco.indexer.documenthandler.DocumentHandlerException;
import org.tockit.docco.indexer.filefilter.ExtensionFileFilter;
import org.tockit.docco.indexer.filefilter.NotFoundFileExtensionException;

import java.io.File;
import java.io.FileFilter;
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
    private DocumentProcessingFactory docProcessingFactory;
    private DocumentHandlersRegistery handlersRegistery = new DocumentHandlersRegistery();
    private int filesSeen;
	
	public Indexer(CallbackRecipient output) {
		this.callbackRecipient = output;
		try {
			List mappings = ConfigurationManager.fetchStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, 50);
			if(mappings.size() == 0) {
				mappings = new ArrayList(20);
				mappings.add("html:org.tockit.docco.indexer.documenthandler.HtmlDocumentHandler");
				mappings.add("htm:org.tockit.docco.indexer.documenthandler.HtmlDocumentHandler");
				//mappings.add("pdf:org.tockit.docco.indexer.documenthandler.PdfMultivalentDocumentHandler");
				mappings.add("pdf:org.tockit.docco.indexer.documenthandler.PdfDocumentHandler");
				mappings.add("doc:org.tockit.docco.indexer.documenthandler.MSWordHandler");
				mappings.add("txt:org.tockit.docco.indexer.documenthandler.PlainTextDocumentHandler");
				mappings.add("xls:org.tockit.docco.indexer.documenthandler.MSExcelDocHandler");
				ConfigurationManager.storeStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, mappings);
			}
			for (Iterator iter = mappings.iterator(); iter.hasNext();) {
                String mapping = (String) iter.next();
                int colonIndex = mapping.indexOf(':');
				String extension = mapping.substring(0,colonIndex);
				String className = mapping.substring(colonIndex + 1);
				try {
					FileFilter fileFilter = new ExtensionFileFilter(extension);
					DocumentHandler docHandler = (DocumentHandler) Class.forName(className).newInstance();
					this.handlersRegistery.register(fileFilter, docHandler);
				} catch(ClassCastException e) {
					System.err.println("WARNING: class " + className + " could not be loaded due to this error:");
					e.printStackTrace();
				}
            }
            this.docProcessingFactory = new DocumentProcessingFactory(this.handlersRegistery);
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
		catch (UnknownFileTypeException e) {
		}
		catch (NotFoundFileExtensionException e) {
		}
		catch (DocumentHandlerException e) {
			System.err.println("Error processing document " + file.getAbsolutePath() + ": " + e.getMessage());
		}
		catch (DocumentProcessingException e) {
			System.err.println("Error processing document " + file.getAbsolutePath() + ": " + e.getMessage());
		}
		catch (Exception e) {
			// sometimes shit happens. E.g. the PDF header might be screwed. Some other things
			// might be broken. We don't want to stop indexing whenever one document fails to be
			// read properly, so we just ignore it for now. Of course we should consider
			// @todo some error handling/reporting
			//e.printStackTrace();
			System.err.println("Error processing document " + file.getAbsolutePath() + ": " + e.getMessage());
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
