/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

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

	public void registerExtension (String fileExtension, Class docProcessorClass) {
		this.docRegistry.put(fileExtension, docProcessorClass);
	}

	public Document processDocument(File file) throws DocumentProcessingException, 
													UnknownFileExtensionException, 
													InstantiationException, 
													IllegalAccessException, 
													IOException {
		String fileExtension = getFileExtension(file);
		
		Class docProcessorClass = (Class) this.docRegistry.get(fileExtension);

		if (docProcessorClass != null) {

			DocumentProcessor docProcessor = (DocumentProcessor) docProcessorClass.newInstance();
			docProcessor.readDocument(file);
			
			/// @todo check what else we can get from the JDK side. Every feature we can get from the File API should be
			/// worthwhile keeping
			Document doc = new Document(); 
			
			DocumentContent docContent = docProcessor.getDocumentContent();
			if (docContent != null) {
				if (docContent.getReader() != null) {
					doc.add(Field.Text(GlobalConstants.FIELD_QUERY_BODY, docContent.getReader()));
				}
				else {
					doc.add(Field.UnStored(GlobalConstants.FIELD_QUERY_BODY, docContent.getString()));
				}
			}
			
			if (docProcessor.getAuthors() != null) {
				Iterator it = docProcessor.getAuthors().iterator();
				while (it.hasNext()) {
					String author = (String) it.next();
					doc.add(Field.Text(GlobalConstants.FIELD_DOC_AUTHOR, author));
				}
			}
			
			if (docProcessor.getTitle() != null) {
				doc.add(Field.Text(GlobalConstants.FIELD_DOC_TITLE, docProcessor.getTitle()));					
			}
			
			if (docProcessor.getSummary() != null) {
				doc.add(Field.Text(GlobalConstants.FIELD_DOC_SUMMARY, docProcessor.getSummary()));
			}
			
			if (docProcessor.getModificationDate() != null) {
				doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_MODIFICATION_DATE, docProcessor.getModificationDate()));
			}
			
			if (docProcessor.getKeywords() != null) {
				doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_KEYWORDS, docProcessor.getKeywords()));
			}

			doc.add(Field.Text(GlobalConstants.FIELD_DOC_PATH, file.getPath()));
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_PATH_WORDS, file.getPath().replace(File.separatorChar, ' ')));
			if (doc.get(GlobalConstants.FIELD_DOC_MODIFICATION_DATE) == null) {
				doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_MODIFICATION_DATE,new Date(file.lastModified())));
			}
			doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_SIZE, new Long(file.length()).toString()));
			//printDebug(doc);
			return doc;								
		}
		else {
			throw new UnknownFileExtensionException(
								"Don't know how to process document with extension " 
								+ fileExtension +
								" ( file: " + file.getPath() + ")");
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
						"\n\t date = " +  doc.get(GlobalConstants.FIELD_DOC_MODIFICATION_DATE) + 
						"\n\t size = " +  doc.get(GlobalConstants.FIELD_DOC_SIZE) + 
						"\n\t author = " + doc.get(GlobalConstants.FIELD_DOC_AUTHOR) + 
						"\n\t summary = " + doc.get(GlobalConstants.FIELD_DOC_SUMMARY)+ 
						"\n\t keywords = " + doc.get(GlobalConstants.FIELD_DOC_KEYWORDS));
		
	}

}
