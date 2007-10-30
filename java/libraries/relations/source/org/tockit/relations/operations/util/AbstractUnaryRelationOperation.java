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

public abstract class AbstractUnaryRelationOperation<D> implements UnaryRelationOperation<D> {
	/**
	 * Implements RelationOperation.getArity().
	 **/
	public int getArity() {
		return 1;
	}
	
	/**
	 * Implements RelationOperation.getParameterNames().
	 **/
	public String[] getParameterNames() {
		return new String[1];
	}

	/**
	 * Implements RelationOperation.apply(Relation[]).
	 **/
	final public Relation<D> apply(Relation<D>... input) {
		if(input.length != 1) {
			throw new IllegalArgumentException("Parameter length for unary relation operation is not one");
		}
		return doApply(input[0]);
	}
	
	protected abstract Relation<D> doApply(Relation<D> input);

	/**
	 * Implements UnaryRelationOperation.concatenate(UnaryRelationOperation).
	 **/
	public UnaryRelationOperation<D> concatenate(UnaryRelationOperation<D> other) {
		return new ConcatenatedUnaryRelationOperation<D>(other, this);
	}
	
	/**
	 * Implements UnaryRelationOperation.concatenate(BinaryRelationOperation).
	 **/
	public BinaryRelationOperation<D> concatenate(BinaryRelationOperation<D> other) {
		return new ConcatenatedUnaryAfterBinaryRelationOperation<D>(other, this);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
