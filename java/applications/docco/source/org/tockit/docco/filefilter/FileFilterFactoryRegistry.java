/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.filefilter;

// @todo this is just a quick hack to get things going
public class FileFilterFactoryRegistry {
	public static FileFilterFactory[] registry = new FileFilterFactory[]{
		new ExtensionFileFilterFactory(),
		new MatchNameRegExpFileFilterFactory(),
		new MatchPathRegExpFileFilterFactory()
	};
}
