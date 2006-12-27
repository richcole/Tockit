/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;

import org.apache.poi.hssf.record.RecordFormatException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.docco.indexer.DocumentSummary;
import org.tockit.plugin.Plugin;


public class MSExcelDocumentHandler implements DocumentHandler, Plugin {

	public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException {
		InputStream inputStream = null;
		try {
            inputStream = url.openStream();
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);	
			
			DocumentSummary docSummary = new DocumentSummary();
			docSummary.contentReader = getDocumentContent(workbook);
			
			return docSummary;	
		} catch (RecordFormatException e) {
			throw new DocumentHandlerException("Caught RecordFormatException while processing " + url.toString(), e);
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
	}

	private Reader getDocumentContent(HSSFWorkbook workbook) {
		StringBuffer content = new StringBuffer();
		int numOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < numOfSheets; i++) {
			HSSFSheet curSheet = workbook.getSheetAt(i);
			/// @todo should we also look at Header and Footer?
			Iterator rowIterator = curSheet.rowIterator();
			while (rowIterator.hasNext()) {
				HSSFRow curRow = (HSSFRow) rowIterator.next();
				Iterator cellIterator = curRow.cellIterator();
				while (cellIterator.hasNext()) {
					HSSFCell curCell = (HSSFCell) cellIterator.next();
					String cellStringValue = null;
					switch (curCell.getCellType()) {
						case HSSFCell.CELL_TYPE_BOOLEAN :
							boolean booleanValue = curCell.getBooleanCellValue();
							cellStringValue = Boolean.toString(booleanValue);
							break;
						case HSSFCell.CELL_TYPE_NUMERIC :
							double doubleValue = curCell.getNumericCellValue();
							cellStringValue = Double.toString(doubleValue);
							break;
						case HSSFCell.CELL_TYPE_STRING :
							cellStringValue = curCell.getStringCellValue();
							break;
						default :
							break;
					}
					if (cellStringValue != null) {
						content.append(cellStringValue);
						// need to add some separators otherwise we get everything squished 
						// together into one word.
						content.append("\t");
					}
				}
				// add line break for estetic reasons.
				content.append("\n");
			}
		}
		return new StringReader(content.toString());
	}

	public String getDisplayName() {
		return "Microsoft Excel";
	}

	public DoccoFileFilter getDefaultFilter() {
		return new ExtensionFileFilterFactory().createNewFilter("xls");
	}

	public void load() {
		DocumentHandlerRegistry.registerDocumentHandler(this);
	}
}
