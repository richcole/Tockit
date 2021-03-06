/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import org.tockit.docco.indexer.DocumentProcessingException;


public class DocumentHandlerException extends DocumentProcessingException {

	private static final long serialVersionUID = 1L;

	public DocumentHandlerException(String message) {
		super(message);

	}

	public DocumentHandlerException(String message, Throwable e) {
		super(message, e);
	}
}
