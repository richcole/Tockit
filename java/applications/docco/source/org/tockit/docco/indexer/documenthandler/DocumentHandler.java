/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.documenthandler;

import java.io.IOException;
import java.net.URL;

import org.tockit.docco.indexer.DocumentSummary;

public interface DocumentHandler {
	public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException;
	/**
	 * return name of this handler suitable for displaying in UI.
	 */
	public String getDisplayName ();
}
