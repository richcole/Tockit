/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations.util;

import org.tockit.relations.model.Relation;
import org.tockit.relations.operations.UnaryRelationOperation;

public abstract class AbstractUnaryRelationOperation implements UnaryRelationOperation {
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
	public Relation apply(Relation[] input) {
		if(input.length != 1) {
			throw new IllegalArgumentException("Parameter length for unary relation operation is not one");
		}
		return apply(input[0]);
	}

	/**
	 * Implements UnaryRelationOperation.concatenate(UnaryRelationOperation).
	 **/
	public UnaryRelationOperation concatenate(UnaryRelationOperation other) {
		return new ConcatenatedUnaryRelationOperation(other, this);
	}
	
	public String toString() {
		return getName();
	}
}
