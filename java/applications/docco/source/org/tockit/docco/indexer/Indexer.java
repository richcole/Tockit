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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * based on lucene demo indexer
 * 
 * @todo this could all be static 
 */
public class Indexer {

	private List errorsList = new LinkedList();
	private DocumentProcessingFactory docProcessingFactory = new DocumentProcessingFactory();
	private int docCount = 0;
	
	public Indexer (String filesToIndexLocation) {
		try {
			Date start = new Date();
			
			HtmlDocumentProcessor htmlDocProcessor = new HtmlDocumentProcessor();
			this.docProcessingFactory.registerExtension("html", htmlDocProcessor);
			this.docProcessingFactory.registerExtension("htm", htmlDocProcessor);

			PlainTextDocumentProcessor plainTextDocProcessor = new PlainTextDocumentProcessor();
			this.docProcessingFactory.registerExtension("txt", plainTextDocProcessor);
			this.docProcessingFactory.registerExtension("java", plainTextDocProcessor);
			this.docProcessingFactory.registerExtension("c", plainTextDocProcessor);
			this.docProcessingFactory.registerExtension("cc", plainTextDocProcessor);
			this.docProcessingFactory.registerExtension("cpp", plainTextDocProcessor);
			this.docProcessingFactory.registerExtension("h", plainTextDocProcessor);
			this.docProcessingFactory.registerExtension("hh", plainTextDocProcessor);

			this.docProcessingFactory.registerExtension("pdf", new PdfDocumentProcessor());


			File f = new File(GlobalConstants.DEFAULT_INDEX_LOCATION);
			createDirPath(f);


			IndexWriter writer = new IndexWriter(
									GlobalConstants.DEFAULT_INDEX_LOCATION,
									GlobalConstants.DEFAULT_ANALYZER,
									true);

			indexDocs(writer, new File(filesToIndexLocation));
			Iterator it = errorsList.iterator();
			while (it.hasNext()) {
				Exception curErr = (Exception) it.next();
				System.err.println("Error: " + curErr.getMessage());
			}
			
			writer.optimize();
			writer.close();

			Date end = new Date();

			System.out.println("total processed documents: " + this.docCount);
			System.out.println("skipped documents: " + this.errorsList.size());
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
		catch (DocumentProcessingException e) {
			//e.printStackTrace();
			this.errorsList.add(e);
		}
		catch (IOException e) {
			errorExit(e);
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
