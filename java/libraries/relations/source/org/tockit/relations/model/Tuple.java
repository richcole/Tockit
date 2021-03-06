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
public class Tuple<D> {
	private D[] data;

	private Tuple(){ // used for fromString()
	}

	/**
	 * Constructs a new tuple with the data given.
	 * 
	 * The constructor makes defensive copy of the data given in the sense that a copy of the
	 * array is used. Of course changes in the elements can't be avoided since we don't want to
	 * enforce clonable objects. Data is still considered immutable. 
	 */
	@SuppressWarnings("unchecked")
	public Tuple(D[] data) {
		this.data = (D[]) new Object[data.length];
		for (int i = 0; i < data.length; i++) {
            this.data[i] = data[i];
        }    
	}
	
	public static Tuple<String> fromString(String tabDelimitedData) {
		Tuple<String> retVal = new Tuple<String>();
		retVal.data = tabDelimitedData.split("\t");
		return retVal;
	}
	
	public D[] getData() {
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
	public D getElement(int dimension) {
		return this.data[dimension];
	}

	@Override
	public boolean equals(Object other) {
		if(this.getClass() != other.getClass()) {
			return false;
		}
		Tuple<?> otherTuple = (Tuple<?>) other;
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

	@Override
	public int hashCode() {
		int hashCode = 7;
		for (int i = 0; i < this.data.length; i++) {
			Object element = this.data[i];
			hashCode = 42*hashCode + (element == null ? 0 : element.hashCode());
		}
		return hashCode;
	}

	/**
	 * Returns a tab-delimited version of this tuple.
	 * 
	 * The toString method will be called on each element.
	 */
	@Override
	public String toString() {
		StringBuffer retVal = new StringBuffer();
		for (int i = 0; i < this.data.length; i++) {
			if(i != 0) {
				retVal.append("\t");
			}
			retVal.append(this.data[i].toString());
		}
		return retVal.toString();
	}
}
