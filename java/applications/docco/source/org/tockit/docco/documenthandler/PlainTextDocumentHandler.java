/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.tockit.docco.indexer.DocumentContent;
import org.tockit.docco.indexer.DocumentSummary;

public class PlainTextDocumentHandler implements DocumentHandler {

	public DocumentSummary parseDocument(URL url) throws IOException, DocumentHandlerException {
		Reader reader = new InputStreamReader(url.openStream());
		DocumentSummary docSummary = new DocumentSummary();
		docSummary.content = new DocumentContent(reader);
		return docSummary;
	}

	public String getDisplayName() {
		return "Plain Text";
	}


}
