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
import java.util.Date;
import java.util.List;


public class PlainTextDocumentProcessor implements DocumentProcessor {
	
	private File file;
	private Reader reader;
	
	public void readDocument(File file) throws IOException {
		this.file = file;
		this.reader = new FileReader(file);		
	}

	public DocumentContent getDocumentContent() {
		return new DocumentContent(reader);
	}

	public List getAuthors() {
		return null;
	}

	public String getTitle() {
		return null;
	}

	public String getSummary() {
		return null;
	}

	public Date getModificationDate() {
		return null;
	}

	public String getKeywords() {
		return null;
	}


}
