/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;


public class NotFoundFileExtensionException extends DocumentProcessingException {

	public NotFoundFileExtensionException(String message) {
		super(message);

	}

	public NotFoundFileExtensionException(String message, Throwable e) {
		super(message, e);
	}

	public NotFoundFileExtensionException(Throwable e) {
		super(e);
	}
}
