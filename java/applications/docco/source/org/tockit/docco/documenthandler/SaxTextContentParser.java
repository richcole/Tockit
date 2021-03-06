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


// @todo it might be nice to have at least the option to index the attribute values, too
class SaxTextContentParser extends DefaultHandler {
    private StringBuffer elementBuffer;
	private StringBuffer contentBuffer;

	// @todo this is very expensive in memory consumption, it would be better to implement an InputStream with input piped from the SAX parser
	// idea: put SAX parser in thread, write to PipedOutputStream, connected to PipedInputStream as return value 
	public String parse(InputSource inputSource) throws IOException, ParserConfigurationException, SAXException {		
		this.elementBuffer = new StringBuffer();
		this.contentBuffer = new StringBuffer();

		inputSource.setSystemId("just getting SAX parser to try to resolve"); //$NON-NLS-1$

		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(false);
		SAXParser parser = parserFactory.newSAXParser();
		parser.parse(inputSource, this);

		return this.contentBuffer.toString();
	}
	
	public InputSource resolveEntity (String publicId, String systemId) throws SAXException {
		InputSource mockSource = new InputSource(new StringReader("")); //$NON-NLS-1$
		return mockSource;
	}

	public void startElement(String uri, String localName, String qname, Attributes atts) throws SAXException {
        // we need to flush here so we don't lose anything with mixed content
        flushElementBufferIntoContent();
	}

	public void characters(char[] text, int start, int length) {
		elementBuffer.append(text, start, length);
	}

	public void endElement(String uri, String localName, String qname) throws SAXException	{
        // we need to flush here so we get the last one
		flushElementBufferIntoContent();
	}

    private void flushElementBufferIntoContent() {
        contentBuffer.append(elementBuffer);
		contentBuffer.append(" "); //$NON-NLS-1$
        elementBuffer.setLength(0);
    }
}