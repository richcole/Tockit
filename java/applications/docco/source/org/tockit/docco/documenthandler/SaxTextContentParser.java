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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


class SaxTextContentParser extends DefaultHandler {
    private StringBuffer elementBuffer;
	private StringBuffer contentBuffer;

	public String parse(InputSource inputSource) throws IOException, ParserConfigurationException, SAXException {		
		this.elementBuffer = new StringBuffer();
		this.contentBuffer = new StringBuffer();

		inputSource.setSystemId("just getting SAX parser to try to resolve");

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(false);
		SAXParser parser = parserFactory.newSAXParser();
		parser.parse(inputSource, this);

		return this.contentBuffer.toString();
	}
	
	public InputSource resolveEntity (String publicId, String systemId) throws SAXException {
		InputSource mockSource = new InputSource(new StringReader(""));
		return mockSource;
	}

	public void startElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
		elementBuffer.setLength(0);
	}

	public void characters(char[] text, int start, int length) {
		elementBuffer.append(text, start, length);
	}

	public void endElement(String uri, String localName, String qname) throws SAXException	{
		contentBuffer.append(elementBuffer);
	}
}