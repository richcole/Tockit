/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.model;

/**
 * A tuple as part of a relation.
 * 
 * This is basically just an Object[], but with value identity.
 * 
 * For convenience there are two ways of accessing tuples: either with the getLength()
 * and getElement(int) methods or via getting the full data via getData().
 */
public class Tuple {
	private Object[] data;

	public Tuple(Object[] data) {
		this.data = data;    
	}
	
	public Object[] getData() {
		return this.data;
	}
	
	public int getLength() {
		return this.data.length;
	}
	
	/**
	 * Retrieves the element in a certain dimension.
	 * 
	 * Equivalent to calling getData()[dimension].
	 * 
	 * @throws ArrayIndexOutOfBoundsException iff dimension parameter invalid
	 */
	public Object getElement(int dimension) {
		return this.data[dimension];
	}

	public boolean equals(Object other) {
		if(this.getClass() != other.getClass()) {
			return false;
		}
		Tuple otherTuple = (Tuple) other;
		if(otherTuple.data.length != this.data.length) {
			return false;
		}
		for (int i = 0; i < otherTuple.data.length; i++) {
			if(otherTuple.data[i] == null && this.data[i] != null) {
				return false; // inverse case is handled by equals
			}
			if(!otherTuple.data[i].equals(this.data[i])) {
				return false;
			}
		}
		return true;
	}

	public int hashCode() {
		int hashCode = 7;
		for (int i = 0; i < this.data.length; i++) {
			Object element = this.data[i];
			hashCode = 42*hashCode + (element == null ? 0 : element.hashCode());
		}
		return hashCode;
	}

	/**
	 * Returns a space-delimited version of this tuple for debug purposes.
	 * 
	 * The toString method will be called on each element.
	 */
	public String toString() {
		StringBuffer retVal = new StringBuffer();
		for (int i = 0; i < this.data.length; i++) {
			if(i != 0) {
				retVal.append(" ");
			}
			retVal.append(this.data[i].toString());
		}
		return retVal.toString();
	}
}
