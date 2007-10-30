/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations;

import org.tockit.relations.model.Relation;

/**
 * Models an n-ary operation on a relation.
 * 
 * @todo add concatenation for the general case
 * 
 * @see UnaryRelationOperation
 * @see BinaryRelationOperation
 */
public interface RelationOperation<D> {
	/**
	 * Returns a name for UI and debug usage.
	 * 
	 * @return String a non-null, non-empty name
	 */
	String getName();
	
	/**
	 * Returns the number of input parameters used.
	 * 
	 * @return int a non-negative integer
	 */
	int getArity();
	
	/**
	 * Return the names of the input parameters
	 * 
	 * @return String[] names for the different inputs in the same order as used in 
	 *                   apply(Relation[]). Array is not null, contents can be if 
	 *                   naming is not applicable. The length has to be the same as
	 *                   the arity of the operation.
	 */
	String[] getParameterNames();
	
	/**
	 * Applies the defined operation.
	 * 
	 * @param input the n relations the operation should be applied upon.
	 * @return Relation the result of the operation
	 * @throws IllegalArgumentException iff the size of the array doesn't match the
	 *          arity of the operation
	 */
	Relation<D> apply(Relation<D>... input);
}
