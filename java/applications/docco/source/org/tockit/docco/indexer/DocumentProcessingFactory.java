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

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.tockit.docco.GlobalConstants;

public class DocumentProcessingFactory {

	/**
	 * keys - file extention
	 * values - corresponding DocumentProcessor
	 * @todo plan was to have this registery searchable for regular expressions
	 * and ordered. HashTable is not suited for this purposes. will need to reimplement.
	 */
	private Hashtable docRegistry = new Hashtable();

	public void registerExtension (String fileExtension, DocumentProcessor docProcessor) {
		this.docRegistry.put(fileExtension, docProcessor);
	}

	public Document processDocument(File file) throws Exception {

		String fileExtension = getFileExtension(file);

		DocumentProcessor docProcessor = (DocumentProcessor) this.docRegistry.get(fileExtension);

		if (docProcessor != null) {
			Document doc = docProcessor.getDocument(file);
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_PATH, file.getPath()));
			doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_DATE,
					  DateField.timeToString(file.lastModified())));
			doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_SIZE, new Long(file.length()).toString()));
			return doc;
		}
		else {
			throw new Exception("Don't know how to process document with extension " + fileExtension +
								" ( file: " + file.getPath() + ")");
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
