/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.FileFilter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tockit.docco.indexer.documenthandler.DocumentHandler;

public class DocumentHandlersRegistery {
	List docHandlersList = new LinkedList();

	public boolean register (FileFilter fileFilter, DocumentHandler docHandler) {
		DocumentHandlerMapping docHandlerMapping = new DocumentHandlerMapping(fileFilter, docHandler);
		return docHandlersList.add(docHandlerMapping);
	}
	
	public void removeMapping (DocumentHandlerMapping docHandlerMapping) {
		docHandlersList.remove(docHandlerMapping);
	}
	
	public void moveMapping (int originalPositionIndex, int destinationPositionIndex) {
		Object mapping = docHandlersList.remove(originalPositionIndex);
		docHandlersList.add(destinationPositionIndex, mapping);
	}
	
	public Iterator getDocumentMappingIterator () {
		return docHandlersList.iterator(); 	
	}
}
