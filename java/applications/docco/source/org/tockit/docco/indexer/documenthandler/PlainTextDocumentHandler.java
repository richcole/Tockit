/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.documenthandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.tockit.docco.indexer.DocumentContent;
import org.tockit.docco.indexer.DocumentSummary;

public class PlainTextDocumentHandler implements DocumentHandler {

	public DocumentSummary parseDocument(File file) throws IOException, DocumentHandlerException {
		Reader reader = new FileReader(file);
		DocumentSummary docSummary = new DocumentSummary();
		docSummary.content = new DocumentContent(reader);
		return docSummary;
	}

	public String getDisplayName() {
		return "Plain Text";
	}


}
