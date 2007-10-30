/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations;

import java.util.Iterator;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.relations.model.Tuple;
import org.tockit.relations.operations.util.AbstractBinaryRelationOperation;


public class UnionOperation<D> extends AbstractBinaryRelationOperation<D> {
	public static<R> Relation<R> unite(Relation<R> left, Relation<R> right) {
		UnionOperation<R> op = new UnionOperation<R>();
		return op.doApply(left, right);
	}
	
    @Override
	public Relation<D> doApply(Relation<D> leftHandInput, Relation<D> rightHandInput) {
    	if(leftHandInput.getArity() != rightHandInput.getArity()) {
    		throw new IllegalArgumentException("Relation arities don't match for union");
    	}
    	RelationImplementation<D> result = new RelationImplementation<D>(leftHandInput.getDimensionNames());
		for (Iterator<Tuple<D>> iter = leftHandInput.getTuples().iterator(); iter.hasNext();) {
			result.addTuple(iter.next());            
		}
		for (Iterator<Tuple<D>> iter = rightHandInput.getTuples().iterator(); iter.hasNext();) {
			result.addTuple(iter.next());            
		}
        return result;
    }

    public String getName() {
        return "Union";
    }
}
