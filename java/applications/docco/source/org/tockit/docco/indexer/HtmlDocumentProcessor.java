/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.tockit.docco.GlobalConstants;



public class HtmlDocumentProcessor implements DocumentProcessor {

	/**
	 * java sun example on parsing html can be found here:
	 * http://java.sun.com/products/jfc/tsc/articles/bookmarks/index.html
	 */
	private class CallbackHandler extends HTMLEditorKit.ParserCallback {
		
		private boolean tagIsTitle = false;
			
		StringBuffer docTextContent = new StringBuffer();
		String metaDescription = "";
		String metaSummary = "";
		String metaAuthor = "";
		String metaKeywords = "";
		Date metaDate;
		String title = "";

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
				if (name.equalsIgnoreCase("description")) {
					metaDescription += content;
					return;
				}
				if (name.equalsIgnoreCase("summary")) {
					metaSummary += content;
					return;
				}
				if (name.equalsIgnoreCase("author")) {
					// @todo handle metatags better when we have multiple
					// entries for the same tag (for instance: author). At the
					// moment we are just appending them together - could be nasty if
					// we want to do something meaninfull with them
					if (metaAuthor.length() > 0) {
						metaAuthor += ", " + content;
					}
					else {
						metaAuthor = content;
					}
					return;
				}
				if (name.equalsIgnoreCase("keywords")) {
					metaKeywords += content;
					return;
				}
				if (name.equalsIgnoreCase("date")) {
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

	public Document getDocument(File file) throws FileNotFoundException, IOException {
		Document doc = new Document();
	
		FileReader fileReader = new FileReader(file);
		BufferedReader br = new BufferedReader(fileReader);
		CallbackHandler handler = new CallbackHandler();
		new ParserDelegator().parse(br, handler, true);

		doc.add(Field.Text(GlobalConstants.FIELD_DOC_TITLE, handler.title));

		doc.add(Field.UnStored(GlobalConstants.FIELD_QUERY_BODY, handler.docTextContent.toString()));
	
		String summary = "";
		if (handler.metaDescription.length() > 0 ) {
			summary = handler.metaDescription;
		}
		if (handler.metaSummary.length() > 0) {
			summary += "\n" + handler.metaSummary;
		}

		doc.add(Field.Text(GlobalConstants.FIELD_DOC_SUMMARY, summary));

		doc.add(Field.Text(GlobalConstants.FIELD_DOC_AUTHOR, handler.metaAuthor));
	
		if (handler.metaDate != null) {
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_MODIFICATION_DATE, handler.metaDate.toString()));
		}
	
		doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_KEYWORDS, handler.metaKeywords));
			
		return doc;
	}
}
