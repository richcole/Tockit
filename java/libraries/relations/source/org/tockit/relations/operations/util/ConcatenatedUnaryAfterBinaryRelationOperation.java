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

public class ConcatenatedUnaryAfterBinaryRelationOperation<D> extends AbstractBinaryRelationOperation<D> {
	private BinaryRelationOperation<D> first;
	private UnaryRelationOperation<D> second;
	
	public ConcatenatedUnaryAfterBinaryRelationOperation(BinaryRelationOperation<D> first, UnaryRelationOperation<D> second) {
		if(first == null || second == null) {
			throw new NullPointerException("Input parameter for concatenation must not be null");			
		}
		this.first = first;
		this.second = second;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Relation<D> doApply(Relation<D> left, Relation<D> right) {
		return second.apply(first.apply(left, right));
	}

	public String getName() {
		return first.getName() + "(" + second.getName() + ")";
	}
}
