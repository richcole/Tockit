/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.filefilter;

import java.io.FileFilter;


/**
 * @todo Refactoring task: use factory to init these objects
 * @todo Perhaps this class should be an abstract class and
 * extend swing FileFilter, implementing io.FileFilter
 */
public interface DoccoFileFilter  extends FileFilter {

	public String getFilteringString();
	public String getDisplayName();
}
