/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.Reader;

import org.apache.lucene.document.Field;
import org.tockit.docco.GlobalConstants;

/**
 * This class is here for one purpose only - to unify return value
 * in DocumentProcessor.getDocumentContent method. In most cases
 * we can get a reader, but sometimes we can only get a string
 * as in HtmlDocumentProcessor case.
 * there is probably a better way to do this, I just can't think of it right now ;)
 */
public class DocumentContent {
	private Field documentField;
	
	public DocumentContent(String content) {
		this.documentField = Field.UnStored(GlobalConstants.FIELD_QUERY_BODY, content);
	}
	
	public DocumentContent(Reader content) {
		this.documentField = Field.Text(GlobalConstants.FIELD_QUERY_BODY, content);
	}

	public Field getDocumentField() {
		return documentField;
	}
}
