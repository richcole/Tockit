/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;
import org.tockit.docco.indexer.DocumentSummary;
import org.tockit.plugin.Plugin;

public class PdfDocumentHandler implements DocumentHandler, Plugin {
	

	public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException {
		
		PDFParser pdfParser = new PDFParser(url.openStream());
		pdfParser.parse();
		PDDocument pdfDoc = pdfParser.getPDDocument();
		if (pdfDoc.isEncrypted()) {
			/// @todo handle exception better here?
			throw new DocumentHandlerException("Couldn't read document " + url.getPath() 
							+ " because it is encrypted");
		}
		PDDocumentInformation info = pdfDoc.getDocumentInformation();
		
		DocumentSummary docSummary =  new DocumentSummary();
		
		docSummary.authors = getAuthors(info);
		docSummary.contentReader = getDocumentContent(pdfParser, url);
		docSummary.creationDate = getDate(info.getCreationDate());
		docSummary.keywords = new ArrayList();
		docSummary.keywords.add(info.getKeywords());
		docSummary.modificationDate = getDate(info.getModificationDate());
		docSummary.title = info.getTitle();
		
		return docSummary;
	}

	private Reader getDocumentContent(PDFParser pdfParser, URL url) 
										throws IOException, DocumentHandlerException {
		PDFTextStripper pdfToText = new PDFTextStripper();
		StringWriter writer = new StringWriter();
		COSDocument cosDoc = pdfParser.getDocument();
		try {
			pdfToText.writeText(cosDoc, writer);
			return new StringReader(writer.toString());
		} catch (NullPointerException e) {
			/// @todo something seems to be dodgy with the PDFBox tool -- I get NPEs on some files (OOo?)
			throw new DocumentHandlerException("Caught null pointer exception in PDF reader for document " + url, e);
		}
	}

	private List getAuthors(PDDocumentInformation info) {
		List res = new LinkedList();
		if (info.getAuthor() != null) {
			res.add(info.getAuthor());
		}
		if (info.getCreator() != null) {
			res.add(info.getCreator());
		}
		return res;
	}

	public Date getDate(Calendar cal) {
		if (cal != null) {
			return cal.getTime();
		}
		return null;
	}

	public String getDisplayName() {
		return "PDF using pdfbox";
	}

	public void load() {
		DocumentHandlerRegistry.registerDocumentHandler(this);
	}
}
