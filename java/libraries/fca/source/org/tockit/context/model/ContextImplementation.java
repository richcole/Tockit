/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.context.model;


import java.util.Set;
import java.util.Iterator;

import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;

/**
 * @todo hide access to collections and relation by playing man in the middle.
 */
public class ContextImplementation implements Context {
    private ListSet objects = new ListSetImplementation();
    private ListSet attributes = new ListSetImplementation();
    private BinaryRelationImplementation relation = new BinaryRelationImplementation();
    private String name = null;

    public ContextImplementation() {
    }

    public ContextImplementation(String name) {
    	this.name = name;
    }

    public Set getObjects() {
        return objects;
    }

    public Set getAttributes() {
        return attributes;
    }

    public BinaryRelation getRelation() {
        return relation;
    }
    
    public BinaryRelationImplementation getRelationImplementation() {
    	return this.relation;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Context createSum(Context other, String title) {
		ContextImplementation context = new ContextImplementation(title);
		Set objects = context.getObjects();
		Set attributes = context.getAttributes();
		BinaryRelationImplementation relation = context.getRelationImplementation();
		
		Iterator objIt = this.getObjects().iterator();
		while (objIt.hasNext()) {
			Object object = objIt.next();
			objects.add(object);
		}
		objIt = other.getObjects().iterator();
		while (objIt.hasNext()) {
			Object object = objIt.next();
			objects.add(object);
		}
		Iterator attrIt = this.getAttributes().iterator();
		while (attrIt.hasNext()) {
			Object attribute = attrIt.next();
			attributes.add(attribute);
		}
		attrIt = other.getAttributes().iterator();
		while (attrIt.hasNext()) {
			Object attribute = attrIt.next();
			attributes.add(attribute);
		}
		objIt = objects.iterator();
		while (objIt.hasNext()) {
			Object object = objIt.next();
			attrIt = attributes.iterator();
			while (attrIt.hasNext()) {
				Object attribute = attrIt.next();
				if(this.getRelation().contains(object,attribute) ||
				   other.getRelation().contains(object,attribute)) {
					relation.insert(object,attribute);
				}
			}
		}
		return context;
	}

    public ListSet getObjectList() {
        return this.objects;
    }

    public ListSet getAttributeList() {
        return this.attributes;
    }
}
