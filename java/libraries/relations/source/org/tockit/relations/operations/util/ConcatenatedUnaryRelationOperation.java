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

public class ConcatenatedUnaryRelationOperation<D> extends AbstractUnaryRelationOperation<D> {
	private UnaryRelationOperation<D> first;
	private UnaryRelationOperation<D> second;
	
	public ConcatenatedUnaryRelationOperation(UnaryRelationOperation<D> first, UnaryRelationOperation<D> second) {
		if(first == null || second == null) {
			throw new NullPointerException("Input parameter for concatenation must not be null");			
		}
		this.first = first;
		this.second = second;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Relation<D> doApply(Relation<D> input) {
		return second.apply(first.apply(input));
	}

	public String getName() {
		return first.getName() + "(" + second.getName() + ")";
	}
}
