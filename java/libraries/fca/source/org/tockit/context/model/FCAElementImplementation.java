/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.context.model;

import org.jdom.Element;

public class FCAElementImplementation implements WritableFCAElement, Comparable {
	private Object data;
	private Element description;
    private int contextPosition = -1; // -1 means "not set"

	public FCAElementImplementation(Object data) {
		this(data,null);
	}

	public FCAElementImplementation(Object data, Element description) {
		this.data = data;
		this.description = description;
	}
	
	public Object getData() {
		return this.data;
	}

	public Element getDescription() {
		return this.description;
	}

	public String toString() {
		return this.data.toString();
	}

	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @todo in ConceptualSchema.setDescription(Element) we clone the parameter,
	 * here we don't --> check why
	 * @todo notify schema that something has changed
	 */
	public void setDescription(Element description) {
		this.description = description;
	}
	
    public int getContextPosition() {
        return this.contextPosition;
    }
    
    public void setContextPosition(int contextPosition) {
        this.contextPosition = contextPosition;
    }
    
	public boolean equals(Object other) {
        if(other == null) {
            return false;
        }
		if(this.getClass() != other.getClass()) {
			return false;
		}
		FCAElementImplementation otherImp = (FCAElementImplementation) other;
		return this.data.equals(otherImp.data);
	}
	
	public int hashCode() {
		return this.data.hashCode();
	}

    /**
     * Determines order based on the context position stored.
     * 
     * If the context position has not been set on the objects, all objects
     * will be considered equal. Objects with a context position are always
     * considered greater than those without.
     */
    public int compareTo(Object o) {
        return this.contextPosition - ((FCAElementImplementation)o).contextPosition;
    }
}
