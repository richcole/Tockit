/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.context.model;

/**
 * A default implementation of a writable FCAElement.
 * 
 * It implements the methods and the identity semantics of WritableFCAElement and
 * adds an implementation of Comparable that determines order based on the context
 * position.
 */
public class FCAElementImplementation implements WritableFCAElement, Comparable {
	private Object data;
	private Object description;
    private int contextPosition = -1; // -1 means "not set"

    /**
     * Creates an instance without description.
     */
	public FCAElementImplementation(Object data) {
		this(data,null);
	}

    /**
     * Creates an instance with a description.
     */
	public FCAElementImplementation(Object data, Object description) {
		this.data = data;
		this.description = description;
	}
	
	public Object getData() {
		return this.data;
	}

	public Object getDescription() {
		return this.description;
	}

	public String toString() {
		return this.data.toString();
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setDescription(Object description) {
		this.description = description;
	}
	
    public int getContextPosition() {
        return this.contextPosition;
    }
    
    public void setContextPosition(int contextPosition) {
        this.contextPosition = contextPosition;
    }
    
    /**
     * Implements identity as required by FCAElement.
     * 
     * Returns true iff the other object is an FCAElementImplementation whose data 
     * is equal to the data stored in this object.
     */
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
	
    /**
     * Implements a hashcode matching equals().
     */
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
