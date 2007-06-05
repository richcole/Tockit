/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.ParserDelegator;

import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.docco.gui.GuiMessages;
import org.tockit.docco.indexer.DocumentSummary;

public class HtmlDocumentHandler implements DocumentHandler {
	/**
	 * java sun example on parsing html can be found here:
	 * http://java.sun.com/products/jfc/tsc/articles/bookmarks/index.html
	 */
	private class CallbackHandler extends HTMLEditorKit.ParserCallback {
		
		private boolean tagIsTitle = false;
			
		StringBuffer docTextContent = new StringBuffer();
		String metaDescription = ""; //$NON-NLS-1$
		String metaSummary = ""; //$NON-NLS-1$
		List metaAuthors = new LinkedList();
		List metaKeywords = new LinkedList();
		Date metaDate;
		String title = ""; //$NON-NLS-1$

		public void handleText(char[] data, int pos) {
			String text = new String(data);
			docTextContent.append(text);
			if (tagIsTitle) {
				title = text;
			}
		}
	
		public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			if (t.equals(HTML.Tag.TITLE)) {
				tagIsTitle = true;
			}
		}
	
		public void handleEndTag(HTML.Tag t, int pos) {
			tagIsTitle = false;
		}		
		
		public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
			if (t.equals(HTML.Tag.META)) {
				String name = (String) a.getAttribute(HTML.Attribute.NAME);
				String content = (String) a.getAttribute(HTML.Attribute.CONTENT);
				if ((name == null) || (content == null)) {
					// case of <meta content="text/html; charset=ISO-8859-1"/>
					return;
				}
				if (name.equalsIgnoreCase("description")) { //$NON-NLS-1$
					metaDescription += content;
					return;
				}
				if (name.equalsIgnoreCase("summary")) { //$NON-NLS-1$
					metaSummary += content;
					return;
				}
				if (name.equalsIgnoreCase("author")) { //$NON-NLS-1$
					metaAuthors.add(content);
					return;
				}
				if (name.equalsIgnoreCase("keywords")) { //$NON-NLS-1$
					metaKeywords.add(content);
					return;
				}
				if (name.equalsIgnoreCase("date")) { //$NON-NLS-1$
					try {
						metaDate = DateFormat.getDateInstance().parse(content);
					} catch (ParseException e) {
						// date is not formated well, just skip it
					}
					return;
				}
			}			
		}
		
	}
	
	public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException {
		Reader reader = new InputStreamReader(url.openStream());

		BufferedReader br = new BufferedReader(reader);
		CallbackHandler handler = new CallbackHandler();
		new ParserDelegator().parse(br, handler, true);
		
		DocumentSummary docSummary = new DocumentSummary();
		
		docSummary.authors = handler.metaAuthors;
		docSummary.contentReader =  new StringReader(handler.docTextContent.toString());
		docSummary.keywords = handler.metaKeywords;
		docSummary.modificationDate = handler.metaDate;
		docSummary.summary = getSummary(handler);
		docSummary.title = handler.title;
		
		return docSummary;
	}

	private String getSummary(CallbackHandler handler) {
		String summary = ""; //$NON-NLS-1$
		if (handler.metaDescription.length() > 0 ) {
			summary = handler.metaDescription;
		}
		if (handler.metaSummary.length() > 0) {
			summary += "\n" + handler.metaSummary; //$NON-NLS-1$
		}
		return summary;
	}

	public String getDisplayName() {
		return GuiMessages.getString("HtmlDocumentHandler.name"); //$NON-NLS-1$
	}

    public DoccoFileFilter getDefaultFilter() {
        return new ExtensionFileFilterFactory().createNewFilter("htm;html;xhtml"); //$NON-NLS-1$
    }
}
