/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class PlainTextDocumentProcessor implements DocumentProcessor {

	public DocumentSummary parseDocument(File file) throws IOException, DocumentProcessingException {
		Reader reader = new FileReader(file);
		DocumentSummary docSummary = new DocumentSummary();
		docSummary.content = new DocumentContent(reader);
		return docSummary;
	}


}
