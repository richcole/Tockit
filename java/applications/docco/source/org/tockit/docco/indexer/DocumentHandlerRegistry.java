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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tockit.docco.indexer.documenthandler.DocumentHandler;
import org.tockit.docco.indexer.filefilter.DoccoFileFilter;

public class DocumentHandlerRegistry {
	public static final String[] DEFAULT_MAPPINGS = new String[]{
		"html?:org.tockit.docco.indexer.filefilter.RegularExpresionExtensionFileFilter:org.tockit.docco.indexer.documenthandler.HtmlDocumentHandler",
		"txt:org.tockit.docco.indexer.filefilter.ExtensionFileFilter:org.tockit.docco.indexer.documenthandler.PlainTextDocumentHandler",
	};
	
	List docHandlersList = new LinkedList();

	public DocumentHandlerRegistry () {
	}
	
	public DocumentHandlerRegistry (String[] mappings) {
		try {
			registerMappings(mappings);
		}
		catch (Exception e) {
			// @todo what to do with these exceptions here?!!
			e.printStackTrace();
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
	
	/**
	 * @todo do we need this method?...
	 */
	public void moveMapping (int originalPositionIndex, int destinationPositionIndex) {
		Object mapping = docHandlersList.remove(originalPositionIndex);
		docHandlersList.add(destinationPositionIndex, mapping);
	}
	
	public DocumentHandlerMapping getMappingAt (int index) {
		return (DocumentHandlerMapping) this.docHandlersList.get(index);
	}
	
	public List getDocumentMappingList() {
		return this.docHandlersList; 	
	}
	
	public void setDocumentMappingList(List documentMappingslist) {
		this.docHandlersList = documentMappingslist; 	
	}
	
    private void registerMappings(String[] mappings) throws ClassNotFoundException, SecurityException, 
    												        NoSuchMethodException, IllegalArgumentException, 
    												        InstantiationException, IllegalAccessException, 
    												        InvocationTargetException {
        for (int i = 0; i < mappings.length; i++) {
            String mapping = mappings[i];
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
	
	public String[] getMappingStringsList() {
		String[] mappings = new String[this.docHandlersList.size()];
		Iterator it = this.docHandlersList.iterator();
		int index = 0;
		while (it.hasNext()) {
			DocumentHandlerMapping cur = (DocumentHandlerMapping) it.next();
			mappings[index] = cur.getFileFilter().getFilteringString() + 
							  ":" + cur.getFileFilter().getClass().getName() + 
							  ":" + cur.getHandler().getClass().getName();
			index++;
		}
		return mappings;
	}

	public void restoreDefaultMappingList () {
		docHandlersList = new LinkedList();
		try {
			registerMappings(DocumentHandlerRegistry.DEFAULT_MAPPINGS);	
		}
		catch (Exception e) {
			// @todo what to do with these exceptions here?!!
			e.printStackTrace();			
		}
	}
}
