/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.filefilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileFilterFactoryRegistry {
	
	private static List fileFilterFactories = new ArrayList();

	static  {
		registerFileFilter(new ExtensionFileFilterFactory());
		registerFileFilter(new MatchNameRegExpFileFilterFactory());
		registerFileFilter(new MatchPathRegExpFileFilterFactory());
	}
	
	private FileFilterFactoryRegistry () {
	}
	
	
	public static void registerFileFilter (FileFilterFactory fileFilterFactory) {
		fileFilterFactories.add(fileFilterFactory);
	}
	
	public static List getFileFilters() {
		return Collections.unmodifiableList(fileFilterFactories);
	}	
}
