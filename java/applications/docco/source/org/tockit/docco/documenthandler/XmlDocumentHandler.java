/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.tockit.docco.indexer.DocumentSummary;


public class XmlDocumentHandler extends DefaultHandler implements DocumentHandler  {
	private StringBuffer curElementBuffer = new StringBuffer();
	private StringBuffer content = new StringBuffer();

	public DocumentSummary parseDocument(URL url)
								throws IOException, DocumentHandlerException {

		try {
			DocumentSummary documentSummary = new DocumentSummary();
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(false);
			SAXParser parser = parserFactory.newSAXParser();
			parser.parse(url.openStream(), this);
			documentSummary.contentReader = new StringReader(content.toString());
			return documentSummary;
		}
		catch (SAXException e) {
			throw new DocumentHandlerException("Couldn't parse XML: " + e.getMessage(), e);
		}
		catch (ParserConfigurationException e) {
			throw new DocumentHandlerException("Couldn't parse XML: " + e.getMessage(), e);
		}
	}

	public String getDisplayName() {
		return "XML document handler";
	}
	
	public void startElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
		curElementBuffer.setLength(0);
	}

	public void characters(char[] text, int start, int length) {
		curElementBuffer.append(text, start, length);
	}

	public void endElement(String uri, String localName, String qname) throws SAXException	{
		content.append(curElementBuffer);
	}
}
