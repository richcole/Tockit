/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.poi.hdf.extractor.WordDocument;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.tockit.docco.GlobalConstants;

public class MSWordProcessor implements DocumentProcessor {
	
	private class DocSummaryPOIFSReaderListener implements POIFSReaderListener {
		
		private SummaryInformation summary = null;
		
		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
			try {
				summary = (SummaryInformation) PropertySetFactory.create(event.getStream());
			}
			catch (Exception ex) {
				throw new RuntimeException
					("Property set stream \"" + event.getPath() +
						event.getName() + "\": " + ex);
			}
	
		}
		
		public SummaryInformation getSummary() {
			return summary;
		}
	}

	public Document getDocument(File file) throws IOException, FileNotFoundException, DocumentProcessingException {
		try {
			Document doc = new Document();
		
		
			POIFSReader poiReader = new POIFSReader();
			DocSummaryPOIFSReaderListener summaryListener = new DocSummaryPOIFSReaderListener();
			poiReader.registerListener(summaryListener, "\005SummaryInformation");
			poiReader.read(new FileInputStream(file));	

			/// @todo there is more fields that we may want to use
			SummaryInformation info = summaryListener.getSummary();
			if (info.getTitle() != null) {
				System.out.println("Title: " + info.getTitle());
				doc.add(Field.Text(GlobalConstants.FIELD_DOC_TITLE, info.getTitle()));
			}		
			if (info.getAuthor() != null) {
				doc.add(Field.Text(GlobalConstants.FIELD_DOC_AUTHOR, info.getAuthor()));
			}
			if (info.getKeywords() != null) {
				doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_KEYWORDS, info.getKeywords()));
			}

			try {
				WordDocument wordDoc = new WordDocument(file.getAbsolutePath());
				Writer docTextWriter = new StringWriter();
				wordDoc.writeAllText(docTextWriter);
				doc.add(Field.UnStored(GlobalConstants.FIELD_QUERY_BODY, docTextWriter.toString()));
				System.out.println("DOCUMENT " + file.getPath() + ":\n" + docTextWriter.toString());
			}
			catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			}

			return doc;
		}
		catch (IOException e) {
			if (e.getMessage().startsWith("Unable to read entire header")) {
				throw new DocumentProcessingException(e);
			}
			else {
				throw e;
			}
		}
	}
	

}
