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
import java.util.Date;

/**
 * based on lucene demo indexer
 * 
 * @todo this could all be static 
 */
public class Indexer {
	public interface CallbackRecipient {
		void showFeedbackMessage(String message);
	}

	private CallbackRecipient callbackRecipient;
    private DocumentProcessingFactory docProcessingFactory = new DocumentProcessingFactory();
	
	private String curDir;
	public Indexer (String filesToIndexLocation, String indexLocation, CallbackRecipient output) {
		this.callbackRecipient = output;
		try {
			Date start = new Date();
			
			Class htmlDocProcessorClass = HtmlDocumentProcessor.class;
			this.docProcessingFactory.registerExtension("html", htmlDocProcessorClass);
			this.docProcessingFactory.registerExtension("htm", htmlDocProcessorClass);

			Class plainTextDocProcessorClass = PlainTextDocumentProcessor.class;
			this.docProcessingFactory.registerExtension("txt", plainTextDocProcessorClass);

			this.docProcessingFactory.registerExtension("pdf", PdfDocumentProcessor.class);

			this.docProcessingFactory.registerExtension("doc", MSWordProcessor.class);

			this.docProcessingFactory.registerExtension("xls", MSExcelDocProcessor.class);


			File f = new File(indexLocation);
			createDirPath(f);


			IndexWriter writer = new IndexWriter(
									indexLocation,
									GlobalConstants.DEFAULT_ANALYZER,
									false);

			indexDocs(writer, new File(filesToIndexLocation));
			
			writer.optimize();
			writer.close();

			Date end = new Date();
			long diff = end.getTime() - start.getTime();
			diff /= 1000;
			long secs = diff % 60;
			diff /= 60;
			long mins = diff % 60;
			diff /= 60;
			long hours = diff;
			
			if(this.callbackRecipient != null) {
				this.callbackRecipient.showFeedbackMessage("Indexing took " + 
												hours + " hours " + mins + " mins " + secs + " secs");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void indexDocs(IndexWriter writer, File file) {
		showFeedback(writer.docCount());
		try {
			if (file.isDirectory()) {
				String[] files = file.list();
				if(files == null) {
					// seems to happen if dir access denied
					return; 
				}
				for (int i = 0; i < files.length; i++) {
					this.curDir = file.getAbsolutePath();
					indexDocs(writer, new File(file, files[i]));
				}
			}
			else {
				writer.addDocument(this.docProcessingFactory.processDocument(file));
			}
		}
		catch (Exception e) {
			// sometimes shit happens. E.g. the PDF header might be screwed. Some other things
			// might be broken. We don't want to stop indexing whenever one document fails to be
			// read properly, so we just ignore it for now. Of course we should consider
			// @todo some error handling/reporting
		}
	}


	private void showFeedback(int docCount) {
		if(this.callbackRecipient != null) {
			this.callbackRecipient.showFeedbackMessage("Indexing: " +
														docCount + " documents so far" +
														" (" + curDir + ")");
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
	
	private void errorExit (Exception e) {
		e.printStackTrace();
		System.exit(1);
	}

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Usage: Indexer path_to_files_to_index indexLocation");
			System.exit(1);
		}
		new Indexer(args[0], args[1], null);

	}
}
