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
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.filefilter.FileExtensionExtractor;
import org.tockit.docco.filefilter.NotFoundFileExtensionException;

/**
 * @todo search for something doing file magic (as in the GNU "file" command). That would be
 *   way better than the extension-based stuff.
 */
public class DocumentProcessingFactory {
	
	private DocumentHandlerRegistry docHandlersRegistry;
	
	public DocumentProcessingFactory(DocumentHandlerRegistry docHandlersRegistry) {
		this.docHandlersRegistry = docHandlersRegistry;
	}

	public Document processDocument(File file) throws DocumentProcessingException,
													IOException {
		Iterator it = this.docHandlersRegistry.getDocumentMappingList().iterator();
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
			//printDebug(doc);
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

	private Document fillDocumentFields(File file, DocumentSummary docSummary)
											throws NotFoundFileExtensionException {
		/// @todo check what else we can get from the JDK side. Every feature we can get from the File API should be
		/// worthwhile keeping
		Document doc = new Document(); 
		
		doc.add(Field.Text(GlobalConstants.FIELD_QUERY_BODY, docSummary.contentReader));
		
		if (docSummary.authors != null) {
			Iterator it = docSummary.authors.iterator();
			while (it.hasNext()) {
				String author = (String) it.next();
				doc.add(Field.Text(GlobalConstants.FIELD_DOC_AUTHOR, author));
			}
		}
		
		if (docSummary.title != null) {
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_TITLE, docSummary.title));					
		}
		
		if (docSummary.summary != null) {
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_SUMMARY, docSummary.summary));
		}
		
		if (docSummary.modificationDate != null) {
			try {
				doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_MODIFICATION_DATE, docSummary.modificationDate));
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
			doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_KEYWORDS, docSummary.keywords));
		}
		
		doc.add(Field.Text(GlobalConstants.FIELD_DOC_PATH, file.getPath()));
		doc.add(Field.Text(GlobalConstants.FIELD_DOC_PATH_WORDS, file.getParent().replace(File.separatorChar, ' ')));
		String fileExtension = FileExtensionExtractor.getExtension(file);
		if (fileExtension != null) {
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_EXTENSION, fileExtension));
			String fileNameWithoutExtension = file.getName().substring(0,file.getName().length() - fileExtension.length() - 1);
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_NAME,fileNameWithoutExtension));
		}
		if (doc.get(GlobalConstants.FIELD_DOC_MODIFICATION_DATE) == null) {
			doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_MODIFICATION_DATE,new Date(file.lastModified())));
		}
		doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_SIZE, new Long(file.length()).toString()));
		return doc;
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
