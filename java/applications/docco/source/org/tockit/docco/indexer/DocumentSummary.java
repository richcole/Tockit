/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.util.Date;
import java.util.List;

public class DocumentSummary {
	public String title;
	public List authors;
	public String summary;
	public String keywords;
	public Date creationDate;
	public Date modificationDate;
	public String mimeType;
	public DocumentContent content;
}
