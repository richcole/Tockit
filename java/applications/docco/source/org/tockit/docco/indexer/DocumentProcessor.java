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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools.Resolution;
import org.tockit.docco.GlobalConstants;

public class DocumentProcessor {
    private static Logger logger = Logger.getLogger(DocumentProcessor.class.getName());
	
	private List documentMappings;
	
	public DocumentProcessor(List documentMappings) {
		this.documentMappings = documentMappings;
	}

	public Document processDocument(File file) throws DocumentProcessingException,
													IOException {
		Iterator it = this.documentMappings.iterator();
		DocumentSummary docSummary = null;
		DocumentProcessingException caughtException = null;
		while (it.hasNext()) {
			DocumentHandlerMapping cur = (DocumentHandlerMapping) it.next();
			if (cur.getFileFilter().accept(file)) {
				try {
					docSummary = cur.getHandler().parseDocument(file.toURL());
					break;
				}
				catch (DocumentProcessingException e) {
					caughtException = e;
				}
				
			}
		}
		
		if (docSummary != null) {
			Document doc = fillDocumentFields(file, docSummary);
			logDocument(doc);
			return doc;								
		}
		else {
			if (caughtException != null) {
				throw caughtException;
			}
			else {
				throw new UnknownFileTypeException("Don't know how to process this type of document " 
										+ file.getPath());
			}
		}
	}

	private Document fillDocumentFields(File file, DocumentSummary docSummary) {
		/// @todo check what else we can get from the JDK side. Every feature we can get from the File API should be
		/// worthwhile keeping
		Document doc = new Document(); 
		
		doc.add(new Field(GlobalConstants.FIELD_QUERY_BODY, docSummary.contentReader));
		
		if (docSummary.authors != null) {
			Iterator it = docSummary.authors.iterator();
			while (it.hasNext()) {
				String author = (String) it.next();
				addTextField(doc, GlobalConstants.FIELD_DOC_AUTHOR, author);
			}
		}
		
		if (docSummary.title != null) {
			addTextField(doc, GlobalConstants.FIELD_DOC_TITLE, docSummary.title);					
		}
		
		if (docSummary.summary != null) {
			addTextField(doc, GlobalConstants.FIELD_DOC_SUMMARY, docSummary.summary);
		}
		
		if (docSummary.modificationDate != null) {
			try {
				addKeyword(doc, GlobalConstants.FIELD_DOC_MODIFICATION_DATE, DateTools.dateToString(docSummary.modificationDate, Resolution.SECOND));
			}
			catch (RuntimeException e) {
				/// @todo another nasty hack
				if (e.getMessage().startsWith("time too early")) {
					System.err.println("Caught exception \"time too early\" for time " + 
											docSummary.modificationDate.toString() + 
											", in document " + file.getAbsolutePath());
				}
				else {
					throw e;
				}
			}
		}
		
		if (docSummary.keywords != null) {
			for (Iterator iter = docSummary.keywords.iterator(); iter.hasNext();) {
                String keyword = (String) iter.next();
				if (keyword != null) {
					addKeyword(doc, GlobalConstants.FIELD_DOC_KEYWORD, keyword);
                }
            }
		}
		
		// @todo use paths relative to the base directory of the index
		addKeyword(doc, GlobalConstants.FIELD_DOC_PATH, file.getPath());
		addTextField(doc, GlobalConstants.FIELD_DOC_PATH_WORDS, file.getParent().replace(File.separatorChar, ' '));
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".") + 1;
		if (index > 0) {
			String fileExtension = fileName.substring(index, fileName.length()).toLowerCase();
			addTextField(doc, GlobalConstants.FIELD_DOC_EXTENSION, fileExtension);
			String fileNameWithoutExtension = file.getName().substring(0,file.getName().length() - fileExtension.length() - 1);
			addTextField(doc, GlobalConstants.FIELD_DOC_NAME,fileNameWithoutExtension);
		} else {
			addTextField(doc, GlobalConstants.FIELD_DOC_NAME,fileName);
		}
		if (doc.get(GlobalConstants.FIELD_DOC_MODIFICATION_DATE) == null) {
			addKeyword(doc, GlobalConstants.FIELD_DOC_MODIFICATION_DATE,DateTools.timeToString(file.lastModified(), Resolution.SECOND));
		}
		addKeyword(doc, GlobalConstants.FIELD_DOC_SIZE, new Long(file.length()).toString());
		return doc;
	}

	private void addKeyword(Document doc, String fieldName, String content) {
		doc.add(new Field(fieldName, content, Field.Store.YES, Field.Index.UN_TOKENIZED));
	}

	private void addTextField(Document doc, String fieldName, String content) {
		doc.add(new Field(fieldName, content, Field.Store.YES, Field.Index.TOKENIZED));
	}
	
	private void logDocument(Document doc) {
		logger.log(Level.FINE, "DOCUMENT:: path = " + doc.get(GlobalConstants.FIELD_DOC_PATH) +
						"\n\t date = " +  doc.get(GlobalConstants.FIELD_DOC_MODIFICATION_DATE) + 
						"\n\t size = " +  doc.get(GlobalConstants.FIELD_DOC_SIZE) + 
						"\n\t author = " + doc.get(GlobalConstants.FIELD_DOC_AUTHOR) + 
						"\n\t summary = " + doc.get(GlobalConstants.FIELD_DOC_SUMMARY)+ 
						"\n\t keywords = " + doc.get(GlobalConstants.FIELD_DOC_KEYWORD));
		
	}

    public List getDocumentMappings() {
        return this.documentMappings;
    }

	public void setDocumentMappings(List documentMappings) {
		this.documentMappings = documentMappings;
	}
}
