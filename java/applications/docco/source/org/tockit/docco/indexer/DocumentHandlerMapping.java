/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.FileFilter;

import org.tockit.docco.indexer.documenthandler.DocumentHandler;

public class DocumentHandlerMapping {
	private FileFilter fileFilter;
	private DocumentHandler docHandler;
	
	public DocumentHandlerMapping (FileFilter fileFilter, DocumentHandler docHandler) {
		this.fileFilter = fileFilter;
		this.docHandler = docHandler;
	}
	
	public DocumentHandler getHandler() {
		return this.docHandler;
	}
	
	public FileFilter getFileFilter() {
		return this.fileFilter;
	}

}
