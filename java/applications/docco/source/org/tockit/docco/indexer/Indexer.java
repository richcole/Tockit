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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * based on lucene demo indexer
 * 
 * @todo this could all be static 
 */
public class Indexer {

	private DocumentProcessingFactory docProcessingFactory = new DocumentProcessingFactory();
	private int docCount = 0;
	
	public Indexer (String filesToIndexLocation) {
		try {
			Date start = new Date();
			
			Class htmlDocProcessorClass = HtmlDocumentProcessor.class;
			this.docProcessingFactory.registerExtension("html", htmlDocProcessorClass);
			this.docProcessingFactory.registerExtension("htm", htmlDocProcessorClass);

			Class plainTextDocProcessorClass = PlainTextDocumentProcessor.class;
			this.docProcessingFactory.registerExtension("txt", plainTextDocProcessorClass);
			this.docProcessingFactory.registerExtension("java", plainTextDocProcessorClass);
			this.docProcessingFactory.registerExtension("c", plainTextDocProcessorClass);
			this.docProcessingFactory.registerExtension("cc", plainTextDocProcessorClass);
			this.docProcessingFactory.registerExtension("cpp", plainTextDocProcessorClass);
			this.docProcessingFactory.registerExtension("h", plainTextDocProcessorClass);
			this.docProcessingFactory.registerExtension("hh", plainTextDocProcessorClass);

			this.docProcessingFactory.registerExtension("pdf", PdfDocumentProcessor.class);

			this.docProcessingFactory.registerExtension("doc", MSWordProcessor.class);


			File f = new File(GlobalConstants.DEFAULT_INDEX_LOCATION);
			createDirPath(f);


			IndexWriter writer = new IndexWriter(
									GlobalConstants.DEFAULT_INDEX_LOCATION,
									GlobalConstants.DEFAULT_ANALYZER,
									true);

			indexDocs(writer, new File(filesToIndexLocation));
			
			writer.optimize();
			writer.close();

			Date end = new Date();

			System.out.println("total processed documents: " + this.docCount);
			System.out.print(end.getTime() - start.getTime());
			System.out.println(" total milliseconds");

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void indexDocs(IndexWriter writer, File file) {
		try {
			if (file.isDirectory()) {
				System.out.print("\ndir: " + file.getAbsolutePath());
				String[] files = file.list();
				if(files == null) {
					// seems to happen if dir access denied
					return; 
				}
				for (int i = 0; i < files.length; i++) {
					indexDocs(writer, new File(file, files[i]));
				}
			}
			else {
				writer.addDocument(this.docProcessingFactory.processDocument(file));
				System.out.print(".");
				docCount++;
			}
		}
		catch (UnknownFileExtensionException e) {
		}
		catch (DocumentProcessingException e) {
			System.err.println("Couldn't process '" + file.getAbsolutePath() + "' - " + e.getMessage());
		}
		catch (FileNotFoundException e) {
			// this most probably means we don't have access rights -- we coudln't figure out
			// how to know if we have or have not right to read a file and Java considers
			// "file not found" and "no access right" to be the same problem (except for the
			// message string, but we don't really want to start parsing that.
			// The other situation I can think of is that a file was deleted during the indexing,
			// but then there is no point in indexing it, so it is not really a problem.
		}
		catch (InstantiationException e) {
			errorExit(e);
		}
		catch (IllegalAccessException e) {
			errorExit(e);
		}
		catch (IOException e) {
//			// @todo this a hack: pdf parser throws IO exception when it doesn't
//			// get expected input, but we don't want to stop then. Perhaps this should be 
//			// caught and checked earlier
//			if (e.getMessage().startsWith("expected")) {
//				System.err.println("Error processing " + file.getAbsolutePath() + ": " + e.getMessage());
//			}
//			else {
//				errorExit(e);
//			}
			// sometimes shit happens. E.g. the PDF header might be screwed. Some other things
			// might be broken. We don't want to stop indexing whenever one document fails to be
			// read properly, so we just ignore it for now. Of course we should consider
			// @todo some error handling/reporting
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

		if (args.length == 0) {
			System.out.println("Usage: Indexer path_to_files_to_index");
			System.exit(1);
		}
		new Indexer(args[0]);

	}
}
