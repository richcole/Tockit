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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hdf.extractor.WordDocument;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;

public class MSWordProcessor implements DocumentProcessor {
	private File file;
	private SummaryInformation info;
	
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

	public void readDocument(File file) throws IOException, DocumentProcessingException {
		this.file = file;

		try {		
			POIFSReader poiReader = new POIFSReader();
			DocSummaryPOIFSReaderListener summaryListener = new DocSummaryPOIFSReaderListener();
			poiReader.registerListener(summaryListener, "\005SummaryInformation");
			poiReader.read(new FileInputStream(file));	
	
			/// @todo there is more fields that we may want to use
			this.info = summaryListener.getSummary();
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

	public DocumentContent getDocumentContent() throws IOException {
		try {
			WordDocument wordDoc = new WordDocument(this.file.getAbsolutePath());
			Writer docTextWriter = new StringWriter();
			wordDoc.writeAllText(docTextWriter);
			return new DocumentContent(docTextWriter.toString());
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("ArrayIndexOutOfBoundsException on doc " + file.getPath());
		}
		/// @todo this is dodgy - we should just bail out with an exception
		return null;
	}

	public List getAuthors() {
		if (info.getAuthor() != null) {
			List res = new LinkedList();
			res.add(info.getAuthor());
			return res;
		}
		return null;
	}

	public String getTitle() {
		return this.info.getTitle();
	}

	public String getSummary() {
		return null;
	}

	public Date getModificationDate() {
		//return info.getEditTime();
		return null;
	}

	public String getKeywords() {
		return info.getKeywords();
	}
	

}
