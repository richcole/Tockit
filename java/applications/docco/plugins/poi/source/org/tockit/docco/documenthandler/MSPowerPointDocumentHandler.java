/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.poi.hpsf.*;
import org.apache.poi.poifs.eventfilesystem.*;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndian;
import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.docco.indexer.DocumentSummary;
import org.tockit.plugin.Plugin;

/**
 * A document handler for PowerPoint files.
 * 
 * This is based on the code found on the POI user list:
 * http://www.mail-archive.com/poi-dev@jakarta.apache.org/msg08085.html
 */
public class MSPowerPointDocumentHandler implements DocumentHandler, Plugin {
	/**
	 * A little helper class to get parse exceptions out of ContentReaderListener.
	 */
	private static class POIFSException extends RuntimeException {
		public POIFSException(Exception ex) {
			super(ex);
		}
	};
	
	private static class ContentReaderListener implements POIFSReaderListener {
		private String text = null;
		
		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
			try {
				DocumentInputStream dis = event.getStream();
				StringBuffer textBuffer = new StringBuffer();

				byte btoWrite[] = new byte[dis.available()];
				dis.read(btoWrite, 0, dis.available());
				for (int i = 0; i < btoWrite.length - 20; i++) {
					long type = LittleEndian.getUShort(btoWrite, i + 2);
					long size = LittleEndian.getUInt(btoWrite, i + 4);
					if (type == 4008) {
						textBuffer.append(new String(btoWrite, i + 4 + 1, (int) size + 3));
					}
				}
				
				this.text = textBuffer.toString();
				System.out.println(this.text);
			} catch (Exception ex) {
				throw new POIFSException(ex);
			}
		}
		
		public String getText() {
			return text;
		}
	}

	public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException {
        InputStream inputStream = null;
		try {		
            inputStream = url.openStream();
            
			POIFSReader poiReader = new POIFSReader();
			DocSummaryPOIFSReaderListener summaryListener = new DocSummaryPOIFSReaderListener();
			ContentReaderListener contentReaderListener = new ContentReaderListener();
			
			poiReader.registerListener(summaryListener, "\005SummaryInformation");
			poiReader.registerListener(contentReaderListener, "PowerPoint Document");
			poiReader.read(inputStream);
            
			/// @todo there is more fields that we may want to use
			SummaryInformation info = summaryListener.getSummary();
			
			DocumentSummary docSummary = new DocumentSummary();
			docSummary.authors = DocSummaryPOIFSReaderListener.getAuthors(info);
			docSummary.contentReader = new StringReader(contentReaderListener.getText());
			docSummary.creationDate = info.getCreateDateTime();
			docSummary.keywords = new ArrayList();
			docSummary.keywords.add(info.getKeywords());
			docSummary.modificationDate = info.getEditTime();
			docSummary.title = info.getTitle();
			
			return docSummary;
		} catch (IOException e) {
			if (e.getMessage().startsWith("Unable to read entire header")) {
				throw new DocumentHandlerException("Couldn't process document", e);
			} else {
				throw e;
			}
		} catch (POIFSException e) {
			throw new DocumentHandlerException("Couldn't process document", e.getCause());
		} finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
	}

	public String getDisplayName() {
		return "Microsoft PowerPoint Document";
	}

	public DoccoFileFilter getDefaultFilter() {
		return new ExtensionFileFilterFactory().createNewFilter("ppt;pps");
	}

	public void load() {
		DocumentHandlerRegistry.registerDocumentHandler(this);
	}
}