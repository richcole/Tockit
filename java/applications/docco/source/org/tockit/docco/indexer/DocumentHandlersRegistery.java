/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tockit.docco.ConfigurationManager;

import org.tockit.docco.indexer.documenthandler.DocumentHandler;
import org.tockit.docco.indexer.filefilter.DoccoFileFilter;

public class DocumentHandlersRegistery {
	
	private static final String CONFIGURATION_MAPPING_ENTRY_NAME = "extension_mappings";
	private static final String CONFIGURATION_SECTION_NAME = "Indexer";
	
	List docHandlersList = new LinkedList();

	public DocumentHandlersRegistery () {
	}
	
	public DocumentHandlersRegistery (boolean loadFromConfig) {
		if (loadFromConfig) {
			try {
				loadDocumentHandlersRegistery();
			}
			catch (Exception e) {
				// @todo what to do with these exceptions here?!!
				e.printStackTrace();
			}
		}
	}

	public boolean register (DocumentHandlerMapping mapping) {
		return docHandlersList.add(mapping);
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
	
	public DocumentHandlerMapping getMappingAt (int index) {
		return (DocumentHandlerMapping) this.docHandlersList.get(index);
	}
	
	public List getDocumentMappingList () {
		return docHandlersList; 	
	}
	
	private void loadDocumentHandlersRegistery()
				throws InstantiationException, IllegalAccessException, ClassNotFoundException,
						NoSuchMethodException, InvocationTargetException {
		List mappings = ConfigurationManager.fetchStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, 50);
		if(mappings.size() == 0) {
			mappings = new ArrayList(20);
			mappings.add("html?:org.tockit.docco.indexer.filefilter.RegularExpresionExtensionFileFilter:org.tockit.docco.indexer.documenthandler.HtmlDocumentHandler");
			mappings.add("pdf:org.tockit.docco.indexer.filefilter.ExtensionFileFilter:org.tockit.docco.indexer.documenthandler.PdfDocumentHandler");
			mappings.add("doc:org.tockit.docco.indexer.filefilter.ExtensionFileFilter:org.tockit.docco.indexer.documenthandler.MSWordHandler");
			mappings.add("txt:org.tockit.docco.indexer.filefilter.ExtensionFileFilter:org.tockit.docco.indexer.documenthandler.PlainTextDocumentHandler");
			mappings.add("xls:org.tockit.docco.indexer.filefilter.ExtensionFileFilter:org.tockit.docco.indexer.documenthandler.MSExcelDocHandler");
			ConfigurationManager.storeStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, mappings);
		}
		for (Iterator iter = mappings.iterator(); iter.hasNext();) {
			String mapping = (String) iter.next();
			int firstColonIndex = mapping.indexOf(':');
			int lastColonIndex = mapping.lastIndexOf(':');
			String extension = mapping.substring(0,firstColonIndex);
			String fileFilterClassName = mapping.substring(firstColonIndex + 1, lastColonIndex);
			String docHandlerClassName = mapping.substring(lastColonIndex + 1);			
			try {
				Class fileFilterClass = Class.forName(fileFilterClassName);
				
				Class[] parameterTypes = { String.class };
				Constructor constructor = fileFilterClass.getConstructor(parameterTypes);
				
				Object[] args = { extension };
				DoccoFileFilter fileFilter = (DoccoFileFilter) constructor.newInstance(args);

				DocumentHandler docHandler = (DocumentHandler) Class.forName(docHandlerClassName).newInstance();

				this.register(fileFilter, docHandler);
			} catch(ClassCastException e) {
				System.err.println("WARNING: class " + docHandlerClassName + " could not be loaded due to this error:");
				e.printStackTrace();
			}
		}
	}	
	
	public void saveDocumentHandlersRegisteryConfig (int deleteUpToIndex) {
		List mappings = new LinkedList();
		Iterator it = this.docHandlersList.iterator();
		while (it.hasNext()) {
			DocumentHandlerMapping cur = (DocumentHandlerMapping) it.next();
			String str = cur.getFileFilter().getFilteringString() + 
							":" + cur.getFileFilter().getClass().getName() + 
							":" + cur.getHandler().getClass().getName();
			mappings.add(str);
		}
		ConfigurationManager.storeStringList(CONFIGURATION_SECTION_NAME, CONFIGURATION_MAPPING_ENTRY_NAME, mappings, deleteUpToIndex);		
	}
}
