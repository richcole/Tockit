/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.tockit.docco.GlobalConstants;


public class DocumentHandlersRegistry {

	private static List docHandlers = new ArrayList();
	
	private DocumentHandlersRegistry() {
	}
	
	public static void registerDefaults () throws ClassNotFoundException,
											InstantiationException,
											IllegalAccessException {
		String[] defaultDocHandlers = GlobalConstants.DEFAULT_DOC_HANDLER_IMPLEMENTATIONS;
		for (int i = 0; i < defaultDocHandlers.length; i++) {
			String docHandlerName = defaultDocHandlers[i];
			DocumentHandler curInstance = (DocumentHandler) Class.forName(docHandlerName).newInstance();
			docHandlers.add(curInstance);
		}
	}
	
	public static void registerDocumentHandler (DocumentHandler docHandler) {
		docHandlers.add(docHandler);
	}
	
	public static Iterator getIterator () {
		return docHandlers.iterator();
	}

}
