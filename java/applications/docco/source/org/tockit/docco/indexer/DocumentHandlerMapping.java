/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.docco.filefilter.DoccoFileFilter;

public class DocumentHandlerMapping {
	private DoccoFileFilter fileFilter;
	private DocumentHandler docHandler;
	
	public DocumentHandlerMapping (DoccoFileFilter fileFilter, DocumentHandler docHandler) {
		this.fileFilter = fileFilter;
		this.docHandler = docHandler;
	}
	
	public DocumentHandler getHandler() {
		return this.docHandler;
	}
	
	public DoccoFileFilter getFileFilter() {
		return this.fileFilter;
	}
	
	public String toString() {
		String str = "DocumentHandlerMapping: from " + this.fileFilter.toString() + 
						" to " + this.docHandler.getDisplayName();
		return str;
	}

}
