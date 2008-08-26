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

import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.plugin.Plugin;


public class MSExcelDocumentHandler extends POIExtractorDocumentHandler implements DocumentHandler, Plugin {

	public String getDisplayName() {
		return "Microsoft Excel";
	}

	public DoccoFileFilter getDefaultFilter() {
		return new ExtensionFileFilterFactory().createNewFilter("xls");
	}

	public void load() {
		DocumentHandlerRegistry.registerDocumentHandler(this);
	}

    protected POIOLE2TextExtractor createExtractor(InputStream inputStream)
            throws IOException {
        ExcelExtractor excelExtractor = new ExcelExtractor(new POIFSFileSystem(inputStream));
        excelExtractor.setFormulasNotResults(true);
        excelExtractor.setIncludeSheetNames(false);
        return excelExtractor;
    }
}
