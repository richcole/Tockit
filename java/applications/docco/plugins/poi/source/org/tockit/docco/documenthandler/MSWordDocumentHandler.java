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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;

import org.textmining.text.extraction.WordExtractor;
import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
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
			docSummary.contentReader = getDocumentContent(url.openStream());
			docSummary.creationDate = info.getCreateDateTime();
			docSummary.keywords = new ArrayList();
			docSummary.keywords.add(info.getKeywords());
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
	
	private Reader getDocumentContent(InputStream inputStream) throws DocumentHandlerException {
        WordExtractor extractor = new WordExtractor();
		try {
            return new StringReader(extractor.extractText(inputStream));
        } catch (Exception e) {
            throw new DocumentHandlerException("Failed to extract text from Word document.", e);
        }
	}

	private List getAuthors(SummaryInformation info) {
		if (info.getAuthor() != null) {
			List res = new ArrayList();
			res.add(info.getAuthor());
			return res;
		}
		return null;
	}

	public String getDisplayName() {
		return "Microsoft Word Document";
	}

	public DoccoFileFilter getDefaultFilter() {
		return new ExtensionFileFilterFactory().createNewFilter("doc;dot");
	}

	public void load() {
		DocumentHandlerRegistry.registerDocumentHandler(this);
	}
}
