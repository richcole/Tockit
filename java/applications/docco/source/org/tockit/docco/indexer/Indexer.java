/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package org.tockit.docco.indexer;

import org.apache.lucene.index.IndexWriter;
import org.tockit.docco.GlobalConstants;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Indexer extends Thread {
	public interface CallbackRecipient {
		void showFeedbackMessage(String message);
	}

    private IndexWriter writer;
	private boolean stateChangeRequested = false;
	private boolean running = true;
    private List fileQueue = new LinkedList();
	private CallbackRecipient callbackRecipient;
    private DocumentProcessingFactory docProcessingFactory = new DocumentProcessingFactory();
	
	public Indexer(CallbackRecipient output) {
		this.callbackRecipient = output;
		try {
			Class htmlDocProcessorClass = HtmlDocumentProcessor.class;
			this.docProcessingFactory.registerExtension("html", htmlDocProcessorClass);
			this.docProcessingFactory.registerExtension("htm", htmlDocProcessorClass);

			Class plainTextDocProcessorClass = PlainTextDocumentProcessor.class;
			this.docProcessingFactory.registerExtension("txt", plainTextDocProcessorClass);

			this.docProcessingFactory.registerExtension("pdf", PdfDocumentProcessor.class);

			this.docProcessingFactory.registerExtension("doc", MSWordProcessor.class);

			this.docProcessingFactory.registerExtension("xls", MSExcelDocProcessor.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	synchronized public void startIndexing(String indexLocation) throws IOException {
		this.writer = new IndexWriter(indexLocation,
									  GlobalConstants.DEFAULT_ANALYZER,
								      false);
	}

	synchronized public void stopIndexing() throws IOException {
		if(this.writer == null) {
			return; // nothing to stop
		}
		this.writer.optimize();
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
		showProgress(writer.docCount(), file.getAbsolutePath());
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


	private void showProgress(int docCount, String dir) {
		showFeedbackMessage("Indexing: " + docCount + " documents so far" + " (" + dir + ")");
	}

	private void showFeedbackMessage(String string) {
		if(this.callbackRecipient != null) {
			this.callbackRecipient.showFeedbackMessage(string);
		}
    }

    private void createDirPath(File file) {
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
}
