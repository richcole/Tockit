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
public class ContextImplementation<O,A> implements ListsContext<O,A> {
    private ListSet<O> objects = new ListSetImplementation<O>();
    private ListSet<A> attributes = new ListSetImplementation<A>();
    private BinaryRelationImplementation<O,A> relation = new BinaryRelationImplementation<O,A>();
    private String name = null;

    public ContextImplementation() {
        // nothing to do
    }

    public ContextImplementation(String name) {
    	this.name = name;
    }

    public Set<O> getObjects() {
        return objects;
    }

    public Set<A> getAttributes() {
        return attributes;
    }

    public BinaryRelation<O,A> getRelation() {
        return relation;
    }
    
    public BinaryRelationImplementation<O,A> getRelationImplementation() {
    	return this.relation;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Context<O,A> createSum(Context<O,A> other, String title) {
		ContextImplementation<O,A> context = new ContextImplementation<O,A>(title);
		Set<O> allObjects = context.getObjects();
		Set<A> allAttributes = context.getAttributes();
        // @todo this is probably a bug: the relation gets changed and potentially there is overlap in
        // the objects
		BinaryRelationImplementation<O,A> combinedRelation = context.getRelationImplementation();
		
		Iterator<O> objIt = this.getObjects().iterator();
		while (objIt.hasNext()) {
			O object = objIt.next();
			allObjects.add(object);
		}
		objIt = other.getObjects().iterator();
		while (objIt.hasNext()) {
			O object = objIt.next();
			allObjects.add(object);
		}
		Iterator<A> attrIt = this.getAttributes().iterator();
		while (attrIt.hasNext()) {
			A attribute = attrIt.next();
			allAttributes.add(attribute);
		}
		attrIt = other.getAttributes().iterator();
		while (attrIt.hasNext()) {
			A attribute = attrIt.next();
			allAttributes.add(attribute);
		}
		objIt = allObjects.iterator();
		while (objIt.hasNext()) {
			O object = objIt.next();
			attrIt = allAttributes.iterator();
			while (attrIt.hasNext()) {
				A attribute = attrIt.next();
				if(this.getRelation().contains(object,attribute) ||
				   other.getRelation().contains(object,attribute)) {
					combinedRelation.insert(object,attribute);
				}
			}
		}
		return context;
	}

    public ListSet<O> getObjectList() {
        return this.objects;
    }

    public ListSet<A> getAttributeList() {
        return this.attributes;
    }
}
