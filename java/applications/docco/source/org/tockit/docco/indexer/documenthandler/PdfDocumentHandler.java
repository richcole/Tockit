/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.documenthandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;
import org.tockit.docco.indexer.DocumentContent;
import org.tockit.docco.indexer.DocumentSummary;

public class PdfDocumentHandler implements DocumentHandler {
	
	private File file;

	public DocumentSummary parseDocument(File file) throws IOException, DocumentHandlerException {
		this.file = file;
		
		PDFParser pdfParser = new PDFParser(new FileInputStream (file));
		pdfParser.parse();
		PDDocument pdfDoc = pdfParser.getPDDocument();
		if (pdfDoc.isEncrypted()) {
			/// @todo handle exception better here?
			throw new DocumentHandlerException("Couldn't read document " + file.getPath() 
							+ " because it is encrypted");
		}
		PDDocumentInformation info = pdfDoc.getDocumentInformation();
		
		DocumentSummary docSummary =  new DocumentSummary();
		
		docSummary.authors = getAuthors(info);
		docSummary.content = getDocumentContent(pdfParser);
		docSummary.creationDate = getDate(info.getCreationDate());
		docSummary.keywords = info.getKeywords();
		docSummary.modificationDate = getDate(info.getModificationDate());
		docSummary.title = info.getTitle();
		
		return docSummary;
	}

	private DocumentContent getDocumentContent(PDFParser pdfParser) throws IOException {
		PDFTextStripper pdfToText = new PDFTextStripper();
		StringWriter writer = new StringWriter();
		COSDocument cosDoc = pdfParser.getDocument();
		try {
			pdfToText.writeText(cosDoc, writer);
			return new DocumentContent(writer.toString());
		} catch (NullPointerException e) {
			/// @todo something seems to be dodgy with the PDFBox tool -- I get NPEs on some files (OOo?)
			System.err.println("Caught null pointer exception in PDF reader for document " + file.getAbsolutePath());
		}
		return null;
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
}
