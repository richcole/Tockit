/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Models a relation as set of tuples.
 * 
 * Relations can use either a named perspective or an unnamed perspective. If
 * you want to attach names to the dimensions, use the constructor taking a
 * String[]. If you don't want names, use the constructor taking an int. In that
 * case the names will all be null, but the call to getVariableNames() will never
 * return null itself.
 */
public class Relation {
    private String[] dimensionNames;
    private Set tuples = new HashSet();
    
    /**
     * Creates a relation with the given arity but no names.
     */
    public Relation(int arity) {
    	this.dimensionNames = new String[arity];
    }
    
    /**
     * Creates a relation with the arity of the array length and the given names.
     */
    public Relation(String[] dimensionNames) {
        this.dimensionNames = dimensionNames;  
    }
    
    /**
     * Adds a tuple into the relation.
     * 
     * @param tuple a Tuple matching the relation's arity
     * @throws IllegalArgumentException if the arity is not matched
     */
    public void addTuple(Tuple tuple) {
        if(tuple.getLength() != this.dimensionNames.length) {
            throw new IllegalArgumentException("Tuples have to have the same length as the relation's arity");
        }
        this.tuples.add(tuple);
    }

	/**
	 * Returns the names for the dimensions of the relation.
	 * 
	 * If the relation doesn't use names the array will still be an array with the
	 * right length, but containing only null values.
	 */    
    public String[] getDimensionNames() {
        return this.dimensionNames;
    }
    
    /**
     * Returns the relation's arity.
     * 
     * @see getSize()
     */
    public int getArity() {
    	return this.dimensionNames.length;
    }
    
    /**
     * Returns the number of tuples in this relation.
     * 
     * Equivalent to getTuples().size().
     * 
     * @see getArity()
     */
    public int getSize() {
        return this.tuples.size();
    }
    
    /**
     * Returns all tuples in the relation.
     * 
     * @return Set all tuples as unmodifiable collection, type Tuple
     */
    public Set getTuples() {
        return Collections.unmodifiableSet(tuples);
    }
}
