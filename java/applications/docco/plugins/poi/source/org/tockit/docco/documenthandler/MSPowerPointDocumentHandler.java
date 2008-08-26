/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;
import org.tockit.plugin.Plugin;

/**
 * A document handler for PowerPoint files.
 */
public class MSPowerPointDocumentHandler extends POIExtractorDocumentHandler implements DocumentHandler, Plugin {
	protected POIOLE2TextExtractor createExtractor(InputStream inputStream)
            throws IOException {
        return new PowerPointExtractor(inputStream);
    }

	public String getDisplayName() {
		return "Microsoft PowerPoint Document";
	}

	public DoccoFileFilter getDefaultFilter() {
		return new ExtensionFileFilterFactory().createNewFilter("ppt;pps");
	}

	public void load() {
		DocumentHandlerRegistry.registerDocumentHandler(this);
	}
}