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
 * A very basic and not performance-optimized implementation of the Relation inteface.
 * 
 * @see Relation
 */
public class RelationImplementation implements Relation {
    private String[] dimensionNames;
    private Set tuples = new HashSet();
    
    /**
     * Creates a relation with the given arity but no names.
     */
    public RelationImplementation(int arity) {
    	this.dimensionNames = new String[arity];
    }
    
    /**
     * Creates a relation with the arity of the array length and the given names.
     */
    public RelationImplementation(String[] dimensionNames) {
        this.dimensionNames = dimensionNames;  
    }

    /**
     * Implements Relation.addTuple(Tuple).
     */    
    public void addTuple(Tuple tuple) {
        if(tuple.getLength() != this.dimensionNames.length) {
            throw new IllegalArgumentException("Tuples have to have the same length as the relation's arity");
        }
        this.tuples.add(tuple);
    }
    
    /**
     * Implements Relation.addTuple(Object[]).
     */    
    public void addTuple(Object[] data) {
        addTuple(new Tuple(data));
    }

    /**
     * Implements Relation.getDimensionNames().
     */    
    public String[] getDimensionNames() {
        return this.dimensionNames;
    }
    
    /**
     * Implements Relation.getArity().
     */    
    public int getArity() {
    	return this.dimensionNames.length;
    }
    
    /**
     * Implements Relation.getSize().
     */    
    public int getSize() {
        return this.tuples.size();
    }
    
    /**
     * Implements Relation.getTuples().
     */    
    public Set getTuples() {
        return Collections.unmodifiableSet(tuples);
    }

    /**
     * Implements Relation.isRelated(Tuple).
     */    
    public boolean isRelated(Tuple tuple) {
        return false;
    }

    /**
     * Implements Relation.isRelated(Object[]).
     */    
    public boolean isRelated(Object[] data) {
        return false;
    }
}
