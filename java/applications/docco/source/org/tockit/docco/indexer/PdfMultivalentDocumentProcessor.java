/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tool.ExtractText;

import multivalent.ParseException;
import multivalent.std.adaptor.pdf.PDFReader;



public class PdfMultivalentDocumentProcessor implements DocumentProcessor {
	private File file;
	private Map infoMap;

	public void readDocument(File file) throws IOException {
		this.file = file;
	}

	public DocumentContent getDocumentContent() throws IOException, DocumentProcessingException {
		try {
			PDFReader reader = new PDFReader(this.file);
			this.infoMap = reader.getInfo();
			try {
				URI uri = file.getCanonicalFile().toURI();	
				String text = ExtractText.extract(uri, null, null, false);
				return new DocumentContent(text);
			}
			catch (URISyntaxException e) {
				throw new DocumentProcessingException(e);
			}
			catch (Exception e) {
				throw new DocumentProcessingException(e);
			}
		}
		catch (ParseException e) {
			throw new DocumentProcessingException(e);
		}
	}

	public List getAuthors() {
		if (this.infoMap.get("Author") != null) {
			List authors = new LinkedList();
			authors.add(this.infoMap.get("Author").toString());
			return authors;
		}
		return null;
	}

	public String getTitle() {
		if (this.infoMap.get("Title") != null) {
			return this.infoMap.get("Title").toString();
		}
		return null;
	}

	public String getSummary() {
		return null;
	}

	public Date getModificationDate() {
		if (this.infoMap.get("ModDate") != null) {
			String modDateStr = this.infoMap.get("ModDate").toString();
			DateFormat df = DateFormat.getDateInstance();
			try {
				return df.parse(modDateStr);
			}
			catch (java.text.ParseException e) {
			}
		}
		return null;
	}

	public String getKeywords() {
		return null;
	}

}
