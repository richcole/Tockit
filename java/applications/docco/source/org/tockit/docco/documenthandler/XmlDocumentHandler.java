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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.docco.gui.GuiMessages;
import org.tockit.docco.indexer.DocumentSummary;


public class XmlDocumentHandler implements DocumentHandler  {
	public DocumentSummary parseDocument(URL url)
								throws IOException, DocumentHandlerException {
		try {
			DocumentSummary documentSummary = new DocumentSummary();
			SaxTextContentParser saxParser = new SaxTextContentParser();
			InputSource inputSource = new InputSource(url.openStream());
			documentSummary.contentReader = new StringReader(saxParser.parse(inputSource));
			return documentSummary;
		} catch (SAXException e) {
			throw new DocumentHandlerException(GuiMessages.getString("XmlDocumentHandler.xmlParsingErrorMessage.header") + e.getMessage(), e); //$NON-NLS-1$
		} catch (ParserConfigurationException e) {
			throw new DocumentHandlerException(GuiMessages.getString("XmlDocumentHandler.xmlParsingErrorMessage.header") + e.getMessage(), e); //$NON-NLS-1$
		}
	}

	public String getDisplayName() {
		return GuiMessages.getString("XmlDocumentHandler.name"); //$NON-NLS-1$
	}

	public DoccoFileFilter getDefaultFilter() {
		return new ExtensionFileFilterFactory().createNewFilter("xml;svg"); //$NON-NLS-1$
	}
}
