/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.filefilter;

/**
 * @todo search for something doing file magic (as in the GNU "file" command). That would be
 *   way better than the extension and reg-exp based stuff.
 */
public interface FileFilterFactory {
	public DoccoFileFilter createNewFilter(String filterExpression);
	public String getDisplayName();
}
