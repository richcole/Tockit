/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hdf.extractor.WordDocument;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.tockit.docco.indexer.DocumentSummary;
import org.tockit.plugin.Plugin;

public class MSWordDocumentHandler implements DocumentHandler, Plugin {
	
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

	public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException {
		try {		
			InputStream inputStream = url.openStream();
			
			POIFSReader poiReader = new POIFSReader();
			DocSummaryPOIFSReaderListener summaryListener = new DocSummaryPOIFSReaderListener();
			poiReader.registerListener(summaryListener, "\005SummaryInformation");
			poiReader.read(inputStream);	
	
			/// @todo there is more fields that we may want to use
			SummaryInformation info = summaryListener.getSummary();
			
			DocumentSummary docSummary = new DocumentSummary();
			docSummary.authors = getAuthors(info);
			docSummary.contentReader = getDocumentContent(inputStream);
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
	
	private Reader getDocumentContent(InputStream inputStream) throws IOException, DocumentHandlerException {
		WordDocument wordDoc = new WordDocument(inputStream);
		Writer docTextWriter = new StringWriter();
		wordDoc.writeAllText(docTextWriter);
		return new StringReader(docTextWriter.toString());
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

	public void load() {
		DocumentHandlerRegistry.registerDocumentHandler(this);
	}

}