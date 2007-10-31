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


public class CrossproductOperation<D> extends AbstractBinaryRelationOperation<D> {
	public static<R> Relation<R> crossproduct(Relation<R> left, Relation<R> right) {
		CrossproductOperation<R> op = new CrossproductOperation<R>();
		return op.doApply(left, right);
	}
	
    @Override
	public Relation<D> doApply(Relation<D> leftHandInput, Relation<D> rightHandInput) {
		int arity = leftHandInput.getArity() + rightHandInput.getArity();
        String[] dimensionNames = getDimensionNames(leftHandInput, rightHandInput, arity);
    	RelationImplementation<D> result = new RelationImplementation<D>(dimensionNames);
		for (Iterator<Tuple<? extends D>> iterLeft = leftHandInput.getTuples().iterator(); iterLeft.hasNext();) {
			Tuple<? extends D> leftTuple = iterLeft.next();
			for (Iterator<Tuple<? extends D>> iterRight = rightHandInput.getTuples().iterator(); iterRight.hasNext();) {
				Tuple<? extends D> rightTuple = iterRight.next();
				result.addTuple(join(leftTuple, rightTuple, arity));
			}
		}
        return result;
    }

    @SuppressWarnings("unchecked")
	private D[] join(Tuple<? extends D> leftTuple, Tuple<? extends D> rightTuple, int arity) {
    	D[] result = (D[]) new Object[arity];
		for (int i = 0; i < leftTuple.getLength(); i++) {
			result[i] = leftTuple.getElement(i);
		}
		for (int i = 0; i < rightTuple.getLength(); i++) {
			result[leftTuple.getLength() + i] = rightTuple.getElement(i);
		}
		return result;
    }

    private String[] getDimensionNames(Relation<D> leftHandInput, Relation<D> rightHandInput, int arity) {
        String[] dimensionNames = new String[arity];
        for (int i = 0; i < leftHandInput.getDimensionNames().length; i++) {
        	dimensionNames[i] = leftHandInput.getDimensionNames()[i];
        }
        for (int i = 0; i < rightHandInput.getDimensionNames().length; i++) {
        	dimensionNames[leftHandInput.getDimensionNames().length + i] = rightHandInput.getDimensionNames()[i];
        }
        return dimensionNames;
    }

    public String getName() {
        return "Crossproduct";
    }
}
