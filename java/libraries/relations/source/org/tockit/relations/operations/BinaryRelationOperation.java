/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations;


/**
 * An operation taking two relations as input.
 */
public interface BinaryRelationOperation<D> extends RelationOperation<D> {
	/**
	 * Creates a new operator applying this one to the results of the unary ones.
	 * 
	 * @param leftOperation a unary relation operation, not null
	 * @param rightOperation a unary relation operation, not null
	 * @throws NullPointerException iff either input is null
	 */
	BinaryRelationOperation<D> concatenate(UnaryRelationOperation<D> leftOperation, UnaryRelationOperation<D> rightOperation);
}
