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

public class ConcatenatedUnaryRelationOperation extends AbstractUnaryRelationOperation {
	private UnaryRelationOperation first;
	private UnaryRelationOperation second;
	
	public ConcatenatedUnaryRelationOperation(UnaryRelationOperation first, UnaryRelationOperation second) {
		if(first == null || second == null) {
			throw new NullPointerException("Input parameter for concatenation must not be null");			
		}
		this.first = first;
		this.second = second;
	}

	public Relation apply(Relation input) {
		return second.apply(first.apply(input));
	}

	public String getName() {
		return first.getName() + "(" + second.getName() + ")";
	}
}
