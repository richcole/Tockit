/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 */
package org.tockit.relations.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A very basic and not performance-optimized implementation of the Relation inteface.
 * 
 * @see Relation
 */
public class RelationImplementation<R> implements Relation<R> {
    private String[] dimensionNames;
    private Set<Tuple<? extends R>> tuples = new HashSet<Tuple<? extends R>>();
    
    /**
     * Creates a relation with the given arity but no names.
     */
    public RelationImplementation(int arity) {
    	this.dimensionNames = new String[arity];
    	for (int i = 0; i < this.dimensionNames.length; i++) {
            this.dimensionNames[i] = String.valueOf(i + 1);
        }
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
    public void addTuple(Tuple<? extends R> tuple) {
        if(tuple.getLength() != this.dimensionNames.length) {
            throw new IllegalArgumentException("Tuples have to have the same length as the relation's arity");
        }
        this.tuples.add(tuple);
    }
    
    /**
     * Implements Relation.addTuple(Object[]).
     */    
    public void addTuple(R[] data) {
        addTuple(new Tuple<R>(data));
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
    public Set<Tuple<? extends R>> getTuples() {
        return Collections.unmodifiableSet(tuples);
    }

    /**
     * Implements Relation.isRelated(Tuple).
     */    
    public boolean isRelated(Tuple<? extends R> tuple) {
        if(tuple.getLength() != this.dimensionNames.length) {
            throw new IllegalArgumentException("Tuples have to have the same length as the relation's arity");
        }
        return this.tuples.contains(tuple);
    }

    /**
     * Implements Relation.isRelated(Object[]).
     */    
    public boolean isRelated(R[] data) {
        return isRelated(new Tuple<R>(data));
    }

    public Set<Tuple<? extends R>> toSet() {
        return Collections.unmodifiableSet(this.tuples);
    }

	/**
	 * Creates a relation from a set of tuples or objects representing tuples.
	 * 
	 * @todo this should really be two typesafe methods and not one insafe one
	 * 
	 * @param baseSet a set containing either Tuple object or objects whose toString() contains tab-delimited tuples
	 * @return the relation using this tuple set. The arity is either the tuple length or 0 if there are no tuples.
	 * @throws IllegalArgumentException if the objects in the input set are not of consistent length
	 * 
	 * @deprecated Not typesafe. Use fromTupleSet(Set<Tuple<T>) or fromStringSet(Set<String>) instead. Avoid mixed sets.
	 */
    @Deprecated
    public static Relation<Object> fromSet(Set<?> baseSet) {
    	Relation<Object> retVal = null;
    	for (Iterator<?> iter = baseSet.iterator(); iter.hasNext();) {
            Object cur = iter.next();
            Tuple<?> tuple;
            if(cur instanceof Tuple) {
            	tuple = (Tuple<?>) cur;
            } else {
            	tuple = Tuple.fromString(cur.toString());
            }
            if(retVal == null) {
            	retVal = new RelationImplementation<Object>(tuple.getLength());
            }
            retVal.addTuple(tuple);
        }
		if(retVal == null) {
			retVal = new RelationImplementation<Object>(0);
		}
        return retVal;
    }
    
    public static<T> Relation<T> fromTupleSet(Set<Tuple<T>> baseSet) {
    	Relation<T> retVal = null;
    	for (Iterator<Tuple<T>> iter = baseSet.iterator(); iter.hasNext();) {
            Tuple<T> tuple = iter.next();
            if(retVal == null) {
            	retVal = new RelationImplementation<T>(tuple.getLength());
            }
            retVal.addTuple(tuple);
        }
		if(retVal == null) {
			retVal = new RelationImplementation<T>(0);
		}
        return retVal;
    }
    
    public static Relation<String> fromStringSet(Set<String> baseSet) {
    	Relation<String> retVal = null;
    	for (Iterator<String> iter = baseSet.iterator(); iter.hasNext();) {
            String cur = iter.next();
            Tuple<String> tuple = Tuple.fromString(cur.toString());
            if(retVal == null) {
            	retVal = new RelationImplementation<String>(tuple.getLength());
            }
            retVal.addTuple(tuple);
        }
		if(retVal == null) {
			retVal = new RelationImplementation<String>(0);
		}
        return retVal;
    }

    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<Tuple<? extends R>> iter = this.tuples.iterator(); iter.hasNext();) {
            Tuple<? extends R> tuple = iter.next();
            buffer.append(tuple);
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
