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
import java.io.StringReader;

import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.tockit.docco.indexer.DocumentSummary;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OpenOfficeDocumentHandler implements DocumentHandler {
	private static final String META_FILE_NAME = "meta.xml";
	private static final String CONTENT_FILE_NAME = "content.xml";

    public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException {
		try {
			final ZipInputStream zipStream = new ZipInputStream(url.openStream());
			DocumentSummary docSummary = new DocumentSummary();
			
			// concatenating the readers might be far more efficient than writing it all into a big buffer
			StringBuffer contentBuffer = new StringBuffer();
			SaxTextContentParser parser = new SaxTextContentParser(); 
			while(true) {
				ZipEntry entry = zipStream.getNextEntry();
				if(entry == null) {
					break; 
					// stupid zip stream doesn't have a proper check for more entries and I don't dare to
					// use available(). So much about reusing interfaces like Iterator or Enumeration :(
				}
				// check for the top (and hopefully only one) meta file and any content file inside
				// there can be multiple content files if there are embedded objects
				String entryName = entry.getName();
				if(entry.getName().equals(META_FILE_NAME)) {
					// @todo get the meta data out of the file		
				} else if(entry.getName().endsWith(CONTENT_FILE_NAME)) {
					// the indirection avoids closing the zip stream too early
					InputSource source = new InputSource(new InputStream(){
                        public int read() throws IOException {
                            return zipStream.read();
                        }
                    });
	                contentBuffer.append(parser.parse(source));
				}
				zipStream.closeEntry();
			}

			zipStream.close();
			
			docSummary.contentReader = new StringReader(contentBuffer.toString());
			return docSummary;
		} catch (SAXException e) {
			throw new DocumentHandlerException("Couldn't parse XML: " + e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new DocumentHandlerException("Couldn't parse XML: " + e.getMessage(), e);
		}
	}

	public String getDisplayName() {
		return "OpenOffice Documents";
	}
}
