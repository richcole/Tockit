/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.documenthandler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tockit.docco.indexer.DocumentContent;
import org.tockit.docco.indexer.DocumentSummary;

import tool.ExtractText;

import multivalent.ParseException;
import multivalent.std.adaptor.pdf.PDFReader;



public class PdfMultivalentDocumentProcessor implements DocumentProcessor {

	public DocumentSummary parseDocument(File file) throws IOException, DocumentProcessingException {
		try {
			PDFReader reader = new PDFReader(file);
			Map infoMap = reader.getInfo();
			try {
				URI uri = file.getCanonicalFile().toURI();	

				String text = ExtractText.extract(uri, null, null, false);
				
				// @todo there maybe some other fields.
				DocumentSummary docSummary = new DocumentSummary();
				
				docSummary.authors = getAuthors(infoMap);
				docSummary.content = new DocumentContent(text);

				if (infoMap.get("Title") != null) {
					docSummary.title = infoMap.get("Title").toString();
				}

				if (infoMap.get("ModDate") != null) {
					String modDateStr = infoMap.get("ModDate").toString();
					DateFormat df = DateFormat.getDateInstance();
					try {
						docSummary.modificationDate = df.parse(modDateStr);
					}
					catch (java.text.ParseException e) {
					}
				}
				
				return docSummary;
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

	private List getAuthors(Map infoMap) {
		if (infoMap.get("Author") != null) {
			List authors = new LinkedList();
			authors.add(infoMap.get("Author").toString());
			return authors;
		}
		return null;
	}
}
