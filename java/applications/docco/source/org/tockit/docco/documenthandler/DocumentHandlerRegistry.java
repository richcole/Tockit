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


public class DocumentHandlerRegistry {

	private static List docHandlers = new ArrayList();
	
	private DocumentHandlerRegistry() {
	}
	
	public static void registerDefaults () {
		DocumentHandler[] defaultDocHandlers = GlobalConstants.DEFAULT_DOC_HANDLER_IMPLEMENTATIONS;
		for (int i = 0; i < defaultDocHandlers.length; i++) {
			registerDocumentHandler(defaultDocHandlers[i]);
		}
	}
	
	public static void registerDocumentHandler (DocumentHandler docHandler) {
		System.out.println("Registering document handler: " + docHandler);
		docHandlers.add(docHandler);
	}
	
	public static Iterator getIterator () {
		return docHandlers.iterator();
	}

}
