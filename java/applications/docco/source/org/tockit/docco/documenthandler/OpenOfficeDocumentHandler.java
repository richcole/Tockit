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
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.docco.indexer.DocumentSummary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class OpenOfficeDocumentHandler implements DocumentHandler {
    private static final String META_FILE_NAME = "meta.xml";
	private static final String CONTENT_FILE_NAME = "content.xml";

	private static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";
	private static final String OFFICE_NAMESPACE = "http://openoffice.org/2000/office";
	private static final String META_NAMESPACE = "http://openoffice.org/2000/meta";

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
				// the indirection avoids closing the zip stream too early
				InputSource source = new InputSource(new InputStream(){
					public int read() throws IOException {
						return zipStream.read();
					}
				});
				if(entry.getName().equals(META_FILE_NAME)) {
					extractMetaData(source, docSummary);		
				} else if(entry.getName().endsWith(CONTENT_FILE_NAME)) {
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

	private void extractMetaData(InputSource inputSource, DocumentSummary docSummary)
						throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuildFac = DocumentBuilderFactory.newInstance();
		docBuildFac.setValidating(false);
		docBuildFac.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuildFac.newDocumentBuilder();
        docBuilder.setEntityResolver(new EntityResolver(){
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return new InputSource(new StringReader(""));
            }
        });
		inputSource.setSystemId("fake to get null resolving instead of exceptions");
	
        Document doc = docBuilder.parse(inputSource);
        Element root = doc.getDocumentElement();
        Element metaElem = (Element) root.getElementsByTagNameNS(OFFICE_NAMESPACE, "meta").item(0);
        
		NodeList nodes = metaElem.getElementsByTagNameNS(DC_NAMESPACE,"title");
		for (int i=0; i<nodes.getLength(); i++) {
			Node n = nodes.item(i);
			docSummary.title = n.getFirstChild().getNodeValue();
		}
        
		nodes = metaElem.getElementsByTagNameNS(DC_NAMESPACE,"subject");
		for (int i=0; i<nodes.getLength(); i++) {
			Node n = nodes.item(i);
			docSummary.summary = n.getFirstChild().getNodeValue();
		}
        
		nodes = metaElem.getElementsByTagNameNS(META_NAMESPACE,"creation-date");
		for (int i=0; i<nodes.getLength(); i++) {
			Node n = nodes.item(i);
			// @todo parse creation date
		}
        
		nodes = metaElem.getElementsByTagNameNS(DC_NAMESPACE,"creator");
		docSummary.authors = new ArrayList();
		for (int i=0; i<nodes.getLength(); i++) {
			Node n = nodes.item(i);
			docSummary.authors.add(n.getFirstChild().getNodeValue());
		}
        
		Element keywordsElem = (Element) metaElem.getElementsByTagNameNS(META_NAMESPACE,"keywords").item(0);
		if(keywordsElem != null) {
			nodes = keywordsElem.getElementsByTagNameNS(META_NAMESPACE,"keyword");
			docSummary.keywords = new ArrayList();
			for (int i=0; i<nodes.getLength(); i++) {
				Node n = nodes.item(i);
				docSummary.keywords.add(n.getFirstChild().getNodeValue());
			}
		}
    }

    public String getDisplayName() {
		return "OpenOffice Documents";
	}

    public DoccoFileFilter getDefaultFilter() {
        return new ExtensionFileFilterFactory().createNewFilter("sxw;sxc;sxi;sxd");
    }
}
