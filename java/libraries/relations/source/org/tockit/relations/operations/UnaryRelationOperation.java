/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations;


/**
 * An operation taking one relation as input.
 */
public interface UnaryRelationOperation<D> extends RelationOperation<D> {
	/**
	 * Creates a new operator applying this one to the result of the other.
	 * 
	 * @param other another unary relation operation, not null
	 * @throws NullPointerException iff other is null
	 */
	UnaryRelationOperation<D> concatenate(UnaryRelationOperation<D> other);

	/**
	 * Creates a new operator applying this one to the result of the other.
	 * 
	 * @param other another binary relation operation, not null
	 * @throws NullPointerException iff other is null
	 */
	BinaryRelationOperation<D> concatenate(BinaryRelationOperation<D> other);
}
