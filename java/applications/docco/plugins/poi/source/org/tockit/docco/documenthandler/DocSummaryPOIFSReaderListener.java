/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;


class DocSummaryPOIFSReaderListener implements POIFSReaderListener {
	private SummaryInformation summary = null;
	
	public void processPOIFSReaderEvent(POIFSReaderEvent event) {
		try {
			summary = (SummaryInformation) PropertySetFactory.create(event.getStream());
		} catch (Exception ex) {
			throw new RuntimeException
					("Property set stream \"" + event.getPath() +
					event.getName() + "\": " + ex);
		}
	}
	
	SummaryInformation getSummary() {
		return summary;
	}

	static List getAuthors(SummaryInformation info) {
		if (info.getAuthor() != null) {
			List res = new ArrayList();
			res.add(info.getAuthor());
			return res;
		}
		return null;
	}
}