/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.util.Hashtable;

import org.apache.lucene.document.Document;

public class DocumentProcessingFactory {

	/**
	 * keys - file extention
	 * values - corresponding DocumentProcessor
	 * @todo plan was to have this registery searchable for regular expressions
	 * and ordered. HashTable is not suited for this purposes. will need to reimplement.
	 */
	private Hashtable docRegistry = new Hashtable();

	public DocumentProcessingFactory () {
	}
	
	public void registerExtension (String fileExtension, DocumentProcessor docProcessor) {
		this.docRegistry.put(fileExtension, docProcessor);
	}

	public Document processDocument(File file) throws Exception {

		String fileExtension = getFileExtension(file);
		
		DocumentProcessor docProcessor = (DocumentProcessor) this.docRegistry.get(fileExtension);
		if (docProcessor == null) {
			throw new Exception("Don't know how to process document with extension " + fileExtension +
								" ( file: " + file.getPath() + ")");
		}
		else {
			return docProcessor.getDocument(file);
		}

	}
	
	private String getFileExtension (File file) throws Exception {
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".") + 1;
		if (index > 0) {
			return fileName.substring(index, fileName.length());
		}
		else {
			throw new Exception("Couldn't extract file extention for file " + file.getPath());
		}
	}

}
