/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.model;

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
public interface Relation {
    /**
     * Adds a tuple into the relation.
     * 
     * @see addTuple(Object[])
     * @param tuple a Tuple matching the relation's arity
     * @throws IllegalArgumentException if the arity is not matched
     */
    void addTuple(Tuple tuple);
    
    /**
     * Adds an array of objects as Tuple into the relation.
     * 
     * This is a convenience method equilvalent to calling addTuple(new Tuple(data)).
     * Note that the contents of the getTuple() set will always be Tuple objects.
     * 
     * @see addTuple(Tuple) 
     * @param data an Object[] matching the relation's arity
     * @throws IllegalArgumentException if the arity is not matched
     */
    void addTuple(Object[] data);
    
    /**
     * Returns the names for the dimensions of the relation.
     * 
     * If the relation doesn't use names the array will still be an array with the
     * right length, but containing only null values.
     */
    String[] getDimensionNames();
    
    /**
     * Returns the relation's arity.
     * 
     * @see getSize()
     */
    int getArity();
    
    /**
     * Returns the number of tuples in this relation.
     * 
     * Equivalent to getTuples().size().
     * 
     * @see getArity()
     */
    int getSize();
    
    /**
     * Returns all tuples in the relation.
     * 
     * @return Set all tuples as unmodifiable collection, type Tuple
     */
    Set getTuples();
    
    /**
     * Returns true iff the given Tuple is part of the relation.
     * 
     * @see isRelated(Object[])
     * @param tuple a Tuple matching the relation's arity
     * @throws IllegalArgumentException if the arity is not matched
     */
    boolean isRelated(Tuple tuple);
    
    /**
     * Returns true iff the given array is part of the relation.
     * 
     * This is a convenience method equivalent to isRelated(new Tuple(data)).
     * 
     * @see isRelated(Tuple)
     * @param data an Object[] matching the relation's arity
     * @throws IllegalArgumentException if the arity is not matched
     */
    boolean isRelated(Object[] data);
}