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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * based on lucene demo indexer 
 */
public class Indexer {

	private List errorsList = new LinkedList();
	private DocumentProcessingFactory docProcessingFactory = new DocumentProcessingFactory();
	private int docCount = 0;
	
	public Indexer (String filesToIndexLocation) {
		try {
			Date start = new Date();
			
			this.docProcessingFactory.registerExtension("html", new HtmlDocumentProcessor());
			this.docProcessingFactory.registerExtension("txt", new PlainTextDocumentProcessor());


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

			System.out.println("total documents: " + this.docCount);
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
				System.out.print("\n");
				String[] files = file.list();
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
		catch (Exception e) {
			this.errorsList.add(e);
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

	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Usage: Indexer path_to_files_to_index");
			System.exit(1);
		}
		new Indexer(args[0]);

	}
}
