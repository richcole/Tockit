/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.toscanaj.controller.ConfigurationManager;

import org.tockit.docco.indexer.documenthandler.DocumentHandler;
import org.tockit.docco.indexer.filefilter.DoccoFileFilter;
import org.tockit.docco.indexer.filefilter.ExtensionFileFilter;

public class DocumentHandlersRegistery {
	
	private static final String CONFIGURATION_MAPPING_ENTRY_NAME = "extension_mappings";
	private static final String CONFIGURATION_SECTION_NAME = "Indexer";
	
	List docHandlersList = new LinkedList();
	
	public DocumentHandlersRegistery () {
		try {
			loadDocumentHandlersRegistery();
		}
		catch (Exception e) {
			// @todo what to do with these exceptions here?!!
			e.printStackTrace();
		}
	}

	public boolean register (DoccoFileFilter fileFilter, DocumentHandler docHandler) {
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
	
	public Collection getDocumentMappingCollection () {
		return docHandlersList; 	
	}
	
	private void loadDocumentHandlersRegistery()
				throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		List mappings = ConfigurationManager.fetchStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, 50);
		if(mappings.size() == 0) {
			mappings = new ArrayList(20);
			mappings.add("html:org.tockit.docco.indexer.documenthandler.HtmlDocumentHandler");
			mappings.add("htm:org.tockit.docco.indexer.documenthandler.HtmlDocumentHandler");
			//mappings.add("pdf:org.tockit.docco.indexer.documenthandler.PdfMultivalentDocumentHandler");
			mappings.add("pdf:org.tockit.docco.indexer.documenthandler.PdfDocumentHandler");
			mappings.add("doc:org.tockit.docco.indexer.documenthandler.MSWordHandler");
			mappings.add("txt:org.tockit.docco.indexer.documenthandler.PlainTextDocumentHandler");
			mappings.add("xls:org.tockit.docco.indexer.documenthandler.MSExcelDocHandler");
			ConfigurationManager.storeStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, mappings);
		}
		for (Iterator iter = mappings.iterator(); iter.hasNext();) {
			String mapping = (String) iter.next();
			int colonIndex = mapping.indexOf(':');
			String extension = mapping.substring(0,colonIndex);
			String className = mapping.substring(colonIndex + 1);
			try {
				DoccoFileFilter fileFilter = new ExtensionFileFilter(extension);
				DocumentHandler docHandler = (DocumentHandler) Class.forName(className).newInstance();
				this.register(fileFilter, docHandler);
			} catch(ClassCastException e) {
				System.err.println("WARNING: class " + className + " could not be loaded due to this error:");
				e.printStackTrace();
			}
		}
	}	
}
