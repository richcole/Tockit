/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tockit.docco.indexer.documenthandler.DocumentHandler;

/**
 * Assumption:
 *  we only allow one instance of each file filter to be registered, there 
 * are no duplicate file filters in the list. 
 * @todo is this a fair assumption or not?
 */
public class DocumentHandlersRegistery {
	List docHandlersList = new LinkedList();
	
	public void register (FileFilter fileFilter, DocumentHandler docHandler) {
		addMapping(fileFilter, docHandler);
	}
	
	public DocumentHandler getHandler (File file) throws UnknownFileTypeException {
		Iterator it = docHandlersList.iterator();
		while (it.hasNext()) {
			DocumentHandlerMapping cur = (DocumentHandlerMapping) it.next();
			if (cur.getFileFilter().accept(file)) {
				return cur.getHandler();
			}
		}
		throw new UnknownFileTypeException(
								"Don't know how to process this type of document " 
								+ file.getPath());
	}

	private boolean addMapping (FileFilter fileFilter, DocumentHandler docHandler) {
		if (getHanlderForFileFilter(fileFilter) == null) {
			return docHandlersList.add(new DocumentHandlerMapping(fileFilter, docHandler));
		}
		return false;
	}
	
	private DocumentHandler getHanlderForFileFilter (FileFilter fileFilter) {
		Iterator it = docHandlersList.iterator();
		while (it.hasNext()) {
			DocumentHandlerMapping cur = (DocumentHandlerMapping) it.next();
			if (cur.getFileFilter().equals(fileFilter)) {
				return cur.getHandler();
			}
		}
		return null;
	}
	

	
}
