/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.docco.documenthandler.HtmlDocumentHandler;
import org.tockit.docco.documenthandler.PlainTextDocumentHandler;
import org.tockit.docco.documenthandler.XmlDocumentHandler;
import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.FileFilterFactory;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;

public class DocumentHandlerRegistry {
	private static final DocumentHandlerMapping[] DEFAULT_MAPPINGS = new DocumentHandlerMapping[]{
		new DocumentHandlerMapping(new ExtensionFileFilterFactory().createNewFilter("html;htm"),
								   new HtmlDocumentHandler()),
		new DocumentHandlerMapping(new ExtensionFileFilterFactory().createNewFilter("txt"),
								   new PlainTextDocumentHandler()),
		new DocumentHandlerMapping(new ExtensionFileFilterFactory().createNewFilter("xml"),
								   new XmlDocumentHandler())
	};
	
	List docHandlersList = new LinkedList();

	public DocumentHandlerRegistry () {
		for (int i = 0; i < DEFAULT_MAPPINGS.length; i++) {
			DocumentHandlerMapping mapping = DEFAULT_MAPPINGS[i];
			this.docHandlersList.add(mapping);
		}
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
        	String fileFilterClassName = mapping.substring(0,firstColonIndex);
        	String filterExpression = mapping.substring(firstColonIndex + 1, lastColonIndex);
        	String docHandlerClassName = mapping.substring(lastColonIndex + 1);			
        	try {
        		Class fileFilterFactoryClass = Class.forName(fileFilterClassName);
        		FileFilterFactory fileFilterFactory = (FileFilterFactory) fileFilterFactoryClass.newInstance();
        		DocumentHandler docHandler = (DocumentHandler) Class.forName(docHandlerClassName).newInstance();
        		this.register(fileFilterFactory.createNewFilter(filterExpression), docHandler);
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
			mappings[index] = cur.getFileFilter().toSerializationString() + 
							  ":" + cur.getHandler().getClass().getName();
			index++;
		}
		return mappings;
	}
}
