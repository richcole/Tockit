/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Date;
import java.util.ArrayList;

import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.docco.indexer.DocumentSummary;
import org.tockit.plugin.Plugin;

public class PdfDocumentHandler implements DocumentHandler, Plugin {
	/**
	 * Pretty much copy and paste code from the PDFbox LucenePDFDocument class.
	 * 
	 * @todo check if we want to add the extra bits of metadata into our model.
	 */
	public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException {
		DocumentSummary docSummary =  new DocumentSummary();
		PDDocument pdfDocument = null;
		try
		{
			PDFParser parser = new PDFParser( url.openStream() );
			parser.parse();

			pdfDocument = parser.getPDDocument();

			//create a tmp output stream with the size of the content.
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter( out );
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.writeText( pdfDocument, writer );
			writer.close();

			byte[] contents = out.toByteArray();
			docSummary.contentReader = new InputStreamReader( new ByteArrayInputStream( contents ) ); 

			PDDocumentInformation info = pdfDocument.getDocumentInformation();
			if( info.getAuthor() != null )
			{
				docSummary.authors = new ArrayList();
				docSummary.authors.add(info.getAuthor());
			}
//			if( info.getCreationDate() != null )
//			{
//				Date date = info.getCreationDate().getTime();
//				//for some reason lucene cannot handle dates before the epoch
//				//and throws a nasty RuntimeException, so we will check and
//				//verify that this does not happen
//				if( date.getTime() >= 0 )
//				{
//					document.add(Field.Text("CreationDate", DateField.dateToString( date ) ) );
//				}
//			}
//			if( info.getCreator() != null )
//			{
//				document.add( Field.Text( "Creator", info.getCreator() ) );
//			}
			if( info.getKeywords() != null )
			{
				docSummary.keywords = new ArrayList();
				docSummary.keywords.add(info.getKeywords());
			}
			if( info.getModificationDate() != null )
			{
				Date date = info.getModificationDate().getTime();
				//for some reason lucene cannot handle dates before the epoch
				//and throws a nasty RuntimeException, so we will check and
				//verify that this does not happen
				if( date.getTime() >= 0 )
				{
					docSummary.modificationDate = date;
				}
			}
//			if( info.getProducer() != null )
//			{
//				document.add( Field.Text( "Producer", info.getProducer() ) );
//			}
//			if( info.getSubject() != null )
//			{
//				document.add( Field.Text( "Subject", info.getSubject() ) );
//			}
			if( info.getTitle() != null )
			{
				docSummary.title = info.getTitle();
			}
//			if( info.getTrapped() != null )
//			{
//				document.add( Field.Text( "Trapped", info.getTrapped() ) );
//			}

//			int summarySize = Math.min( contents.length, 500 );
//			// Add the summary as an UnIndexed field, so that it is stored and returned
//			// with hit documents for display.
//			docSummary.summary = new String( contents, 0, summarySize );
		}
		finally
		{
			if (pdfDocument != null) {
				pdfDocument.close();
			}			
		}
		
		return docSummary;
	}

	public String getDisplayName() {
		return "PDF using pdfbox";
	}

	public DoccoFileFilter getDefaultFilter() {
		return new ExtensionFileFilterFactory().createNewFilter("pdf");
	}

	public void load() {
		DocumentHandlerRegistry.registerDocumentHandler(this);
	}
}
