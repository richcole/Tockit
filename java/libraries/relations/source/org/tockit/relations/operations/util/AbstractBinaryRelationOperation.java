/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations.util;

import org.tockit.relations.model.Relation;
import org.tockit.relations.operations.BinaryRelationOperation;
import org.tockit.relations.operations.UnaryRelationOperation;

public abstract class AbstractBinaryRelationOperation<D> implements BinaryRelationOperation<D> {
	/**
	 * Implements RelationOperation.getArity().
	 **/
	public int getArity() {
		return 2;
	}
	
	/**
	 * Implements RelationOperation.getParameterNames().
	 **/
	public String[] getParameterNames() {
		return new String[2];
	}

	/**
	 * Implements RelationOperation.apply(Relation[]).
	 **/
	final public Relation<D> apply(Relation<D>... input) {
		if(input.length != 2) {
			throw new IllegalArgumentException("Parameter length for binary relation operation is not two");
		}
		return doApply(input[0], input[1]);
	}

	protected abstract Relation<D> doApply(Relation<D> leftHand, Relation<D> rightHand);
	
	/**
	 * Implements BinaryRelationOperation.concatenate(BinaryRelationOperation,BinaryRelationOperation).
	 **/
	public BinaryRelationOperation<D> concatenate(UnaryRelationOperation<D> leftOperation, UnaryRelationOperation<D> rightOperation) {
		return new ConcatenatedBinaryRelationOperation<D>(leftOperation, rightOperation, this);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
