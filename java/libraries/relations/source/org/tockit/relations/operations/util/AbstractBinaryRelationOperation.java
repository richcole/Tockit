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

public abstract class AbstractBinaryRelationOperation implements BinaryRelationOperation {
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
	public Relation apply(Relation[] input) {
		if(input.length != 2) {
			throw new IllegalArgumentException("Parameter length for binary relation operation is not two");
		}
		return apply(input[0], input[1]);
	}

	/**
	 * Implements BinaryRelationOperation.concatenate(BinaryRelationOperation,BinaryRelationOperation).
	 **/
	public BinaryRelationOperation concatenate(UnaryRelationOperation leftOperation, UnaryRelationOperation rightOperation) {
		return new ConcatenatedBinaryRelationOperation(leftOperation, rightOperation, this);
	}
	
	public String toString() {
		return getName();
	}
}
