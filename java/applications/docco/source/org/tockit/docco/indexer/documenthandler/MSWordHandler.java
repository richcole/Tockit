/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.documenthandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hdf.extractor.WordDocument;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.tockit.docco.indexer.DocumentContent;
import org.tockit.docco.indexer.DocumentSummary;

public class MSWordHandler implements DocumentHandler {
	
	private class DocSummaryPOIFSReaderListener implements POIFSReaderListener {
		private SummaryInformation summary = null;
		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
			try {
				summary = (SummaryInformation) PropertySetFactory.create(event.getStream());
			} catch (Exception ex) {
				throw new RuntimeException
						("Property set stream \"" + event.getPath() +
						event.getName() + "\": " + ex);
			}
		}
		public SummaryInformation getSummary() {
			return summary;
		}
	}

	public DocumentSummary parseDocument(File file) throws IOException, DocumentHandlerException {
		try {		
			POIFSReader poiReader = new POIFSReader();
			DocSummaryPOIFSReaderListener summaryListener = new DocSummaryPOIFSReaderListener();
			poiReader.registerListener(summaryListener, "\005SummaryInformation");
			poiReader.read(new FileInputStream(file));	
	
			/// @todo there is more fields that we may want to use
			SummaryInformation info = summaryListener.getSummary();
			
			DocumentSummary docSummary = new DocumentSummary();
			docSummary.authors = getAuthors(info);
			docSummary.content = getDocumentContent(file);
			docSummary.creationDate = info.getCreateDateTime();
			docSummary.keywords = info.getKeywords();
			docSummary.modificationDate = info.getEditTime();
			docSummary.title = info.getTitle();
			
			return docSummary;
		}
		catch (IOException e) {
			if (e.getMessage().startsWith("Unable to read entire header")) {
				throw new DocumentHandlerException("Couldn't process document", e);
			} else {
				throw e;
			}
		}
	}
	
	private DocumentContent getDocumentContent(File file) throws IOException, DocumentHandlerException {
		WordDocument wordDoc = new WordDocument(file.getAbsolutePath());
		Writer docTextWriter = new StringWriter();
		wordDoc.writeAllText(docTextWriter);
		return new DocumentContent(docTextWriter.toString());
	}

	private List getAuthors(SummaryInformation info) {
		if (info.getAuthor() != null) {
			List res = new LinkedList();
			res.add(info.getAuthor());
			return res;
		}
		return null;
	}

	public String getDisplayName() {
		return "Microsoft Word Document";
	}

}
