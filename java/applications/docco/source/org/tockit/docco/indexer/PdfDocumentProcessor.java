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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;
import org.tockit.docco.GlobalConstants;



public class PdfDocumentProcessor implements DocumentProcessor {

	public Document getDocument(File file) throws FileNotFoundException, IOException {
		Document doc = new Document();
		PDFParser pdfParser = new PDFParser(new FileInputStream (file));
		pdfParser.parse();
		COSDocument cosDoc = pdfParser.getDocument();
		PDDocument pdfDoc = pdfParser.getPDDocument();
		if (pdfDoc.isEncrypted()) {
			/// @todo handle exception better here?
			throw new IOException("Couldn't read document " + file.getPath() 
							+ " because it is encrypted");
		}

		PDDocumentInformation info = pdfDoc.getDocumentInformation();
		
		if (info.getTitle() != null) {
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_TITLE, info.getTitle()));
		}
		if (info.getAuthor() != null) {
			doc.add(Field.Text(GlobalConstants.FIELD_DOC_AUTHOR, info.getAuthor()));
		}
		if (info.getKeywords() != null) {
			doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_KEYWORDS, info.getKeywords()));
		}
		if (info.getCreator() != null) {
			doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_AUTHOR, info.getCreator()));
		}
		if (info.getCreationDate() != null) {
			Calendar creationDate = info.getCreationDate();
			doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_CREATION_DATE, creationDate.getTime()));
		}

		PDFTextStripper pdfToText = new PDFTextStripper();
		StringWriter writer = new StringWriter();
		try {
			pdfToText.writeText(cosDoc, writer);
			doc.add(Field.UnStored(GlobalConstants.FIELD_QUERY_BODY, writer.toString()));
		} catch (NullPointerException e) {
			/// @todo something seems to be dodgy with the PDFBox tool -- I get NPEs on some files (OOo?)
			System.err.println("Caught null pointer exception in PDF reader for document " + file.getAbsolutePath());
		}

		return doc;
	}

}
