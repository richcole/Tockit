/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.indexer.filefilter.*;
import org.tockit.docco.indexer.filefilter.FileExtensionExtractor;

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

	public void registerExtension (FileFilter fileFilter, Class docProcessorClass) {
		this.docRegistry.put(fileFilter, docProcessorClass);
	}

	public Document processDocument(File file) throws DocumentProcessingException, 
													NotFoundFileExtensionException,
													UnknownFileExtensionException, 
													InstantiationException, 
													IllegalAccessException, 
													IOException {
		
		Class docProcessorClass = null;
		Enumeration enum = this.docRegistry.keys();
		while (enum.hasMoreElements()) {
			FileFilter curFileFilter = (FileFilter) enum.nextElement();
			if (curFileFilter.accept(file)) {
				docProcessorClass = (Class) this.docRegistry.get(curFileFilter);
			}
		}

		if (docProcessorClass != null) {

			DocumentProcessor docProcessor = (DocumentProcessor) docProcessorClass.newInstance();
			DocumentSummary docSummary = docProcessor.parseDocument(file);
			
			/// @todo check what else we can get from the JDK side. Every feature we can get from the File API should be
			/// worthwhile keeping
			Document doc = new Document(); 
			
			DocumentContent docContent = docSummary.content;
			if (docContent != null) {
				if (docContent.getReader() != null) {
					doc.add(Field.Text(GlobalConstants.FIELD_QUERY_BODY, docContent.getReader()));
				}
				else {
					doc.add(Field.UnStored(GlobalConstants.FIELD_QUERY_BODY, docContent.getString()));
				}
			}
			
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
			//printDebug(doc);
			return doc;								
		}
		else {
			/// @todo shall we add all files at least as files?
			throw new UnknownFileExtensionException(
								"Don't know how to process document with extension " 
								+ FileExtensionExtractor.getExtension(file) +
								" ( file: " + file.getPath() + ")");
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
