/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import org.tockit.docco.indexer.documenthandler.*;


public class UnknownFileExtensionException extends DocumentHandlerException {

	public UnknownFileExtensionException(String message) {
		super(message);

	}

	public UnknownFileExtensionException(String message, Throwable e) {
		super(message, e);
	}

	public UnknownFileExtensionException(Throwable e) {
		super(e);
	}
}
