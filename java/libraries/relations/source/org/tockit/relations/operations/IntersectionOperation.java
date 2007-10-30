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


public class IntersectionOperation<D> extends AbstractBinaryRelationOperation<D> {
	public static<R> Relation<R> intersect(Relation<R> left, Relation<R> right) {
		IntersectionOperation<R> op = new IntersectionOperation<R>();
		return op.doApply(left, right);
	}
	
    @Override
	public Relation<D> doApply(Relation<D> leftHandInput, Relation<D> rightHandInput) {
    	if(leftHandInput.getArity() != rightHandInput.getArity()) {
    		throw new IllegalArgumentException("Relation arities don't match for intersection");
    	}
    	RelationImplementation<D> result = new RelationImplementation<D>(leftHandInput.getDimensionNames());
		for (Iterator<Tuple<D>> iter = leftHandInput.getTuples().iterator(); iter.hasNext();) {
			Tuple<D> tuple = iter.next();
			if(rightHandInput.isRelated(tuple)) {
                result.addTuple(tuple);            
			}
		}
        return result;
    }

    public String getName() {
        return "Intersection";
    }
}
