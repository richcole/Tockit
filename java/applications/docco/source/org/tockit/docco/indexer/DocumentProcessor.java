/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface DocumentProcessor {
	///@todo should use contsructors instead of this method.
	public void readDocument(File file) throws IOException, DocumentProcessingException;
	public DocumentContent getDocumentContent () throws IOException, DocumentProcessingException;
	public List getAuthors ();
	public String getTitle ();
	public String getSummary();
	public Date getModificationDate ();
	public String getKeywords ();
}
