/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.documenthandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tockit.docco.GlobalConstants;
import org.tockit.docco.indexer.DocumentHandlerMapping;


public class DocumentHandlerRegistry {
	private static List defaultMappings = new ArrayList();
	private static List docHandlers = new ArrayList();
	
	private DocumentHandlerRegistry() {
	}
	
	public static void registerDefaults() {
		DocumentHandler[] defaultDocHandlers = GlobalConstants.DEFAULT_DOC_HANDLER_IMPLEMENTATIONS;
		for (int i = 0; i < defaultDocHandlers.length; i++) {
			registerDocumentHandler(defaultDocHandlers[i]);
		}
	}
	
	public static void registerDocumentHandler(DocumentHandler docHandler) {
		docHandlers.add(docHandler);
		defaultMappings.add(new DocumentHandlerMapping(docHandler.getDefaultFilter(), docHandler));
	}
	
	public static List getDocumentHandlers() {
		return Collections.unmodifiableList(docHandlers);
	}

	public static List getDefaultMappings() {
		return Collections.unmodifiableList(defaultMappings);
	}
}
