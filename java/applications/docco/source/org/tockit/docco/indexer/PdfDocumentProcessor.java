/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

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

public class PdfDocumentProcessor implements DocumentProcessor {
	private File file;
	private PDDocumentInformation info;
	private PDFParser pdfParser;

	public void readDocument(File file) throws IOException {
		this.file = file;
		this.pdfParser = new PDFParser(new FileInputStream (file));
		pdfParser.parse();
		PDDocument pdfDoc = pdfParser.getPDDocument();
		if (pdfDoc.isEncrypted()) {
			/// @todo handle exception better here?
			throw new IOException("Couldn't read document " + file.getPath() 
							+ " because it is encrypted");
		}
		this.info = pdfDoc.getDocumentInformation();
	}

	public DocumentContent getDocumentContent() throws IOException {
		PDFTextStripper pdfToText = new PDFTextStripper();
		StringWriter writer = new StringWriter();
		COSDocument cosDoc = this.pdfParser.getDocument();
		try {
			pdfToText.writeText(cosDoc, writer);
			return new DocumentContent(writer.toString());
		} catch (NullPointerException e) {
			/// @todo something seems to be dodgy with the PDFBox tool -- I get NPEs on some files (OOo?)
			System.err.println("Caught null pointer exception in PDF reader for document " + file.getAbsolutePath());
		}
		return null;
	}

	public List getAuthors() {
		List res = new LinkedList();
		if (this.info.getAuthor() != null) {
			res.add(this.info.getAuthor());
		}
		if (this.info.getCreator() != null) {
			res.add(this.info.getCreator());
		}
		return res;
	}

	public String getTitle() {
		return this.info.getTitle();
	}

	public String getSummary() {
		return null;
	}

	public Date getModificationDate() {
		Calendar cal = this.info.getModificationDate();
		if (cal != null) {
			return cal.getTime();
		}
		return null;
	}

	public String getKeywords() {
		return this.info.getKeywords();
	}

}
