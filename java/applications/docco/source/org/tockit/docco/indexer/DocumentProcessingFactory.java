/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.tockit.docco.GlobalConstants;

/**
 * @todo search for something doing file magic (as in the GNU "file" command). That would be
 *   way better than the extension-based stuff.
 */
public class DocumentProcessingFactory {

	/**
	 * keys - file extension
	 * values - corresponding DocumentProcessor
	 * @todo plan was to have this registry searchable for regular expressions
	 * and ordered. HashTable is not suited for this purposes. will need to reimplement.
	 */
	private Hashtable docRegistry = new Hashtable();

	public void registerExtension (String fileExtension, DocumentProcessor docProcessor) {
		this.docRegistry.put(fileExtension, docProcessor);
	}

	public Document processDocument(File file) throws DocumentProcessingException {
		try {
			String fileExtension = getFileExtension(file);
	
			DocumentProcessor docProcessor = (DocumentProcessor) this.docRegistry.get(fileExtension);
	
			if (docProcessor != null) {
				/// @todo check what else we can get from the JDK side. Every feature we can get from the File API should be
				/// worthwhile keeping
				Document doc = docProcessor.getDocument(file);
				doc.add(Field.Text(GlobalConstants.FIELD_DOC_PATH, file.getPath()));
				if (doc.get(GlobalConstants.FIELD_DOC_DATE) == null) {
					doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_DATE,new Date(file.lastModified())));
				}
				doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_SIZE, new Long(file.length()).toString()));
				//printDebug(doc);
				return doc;								
			}
			else {
				throw new DocumentProcessingException(
									"Don't know how to process document with extension " 
									+ fileExtension +
									" ( file: " + file.getPath() + ")");
			}
		} catch (Exception e) {
			DocumentProcessingException docExc = new DocumentProcessingException("Couldn't process document: " + file.getAbsolutePath(), e);
			docExc.printStackTrace();
            throw docExc;
		}
	}
	
	private String getFileExtension (File file) throws DocumentProcessingException {
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".") + 1;
		if (index > 0) {
			return fileName.substring(index, fileName.length());
		}
		else {
			throw new DocumentProcessingException("Couldn't extract file extention for file " + file.getPath());
		}
	}
	
	private void printDebug (Document doc) {
		System.out.println("DOCUMENT:: path = " + doc.get(GlobalConstants.FIELD_DOC_PATH) +
						"\n\t date = " +  doc.get(GlobalConstants.FIELD_DOC_DATE) + 
						"\n\t size = " +  doc.get(GlobalConstants.FIELD_DOC_SIZE) + 
						"\n\t author = " + doc.get(GlobalConstants.FIELD_DOC_AUTHOR) + 
						"\n\t summary = " + doc.get(GlobalConstants.FIELD_DOC_SUMMARY)+ 
						"\n\t keywords = " + doc.get(GlobalConstants.FIELD_DOC_KEYWORDS));
		
	}

}
