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


public class CrossproductOperation extends AbstractBinaryRelationOperation {
	public static Relation crossproduct(Relation left, Relation right) {
		CrossproductOperation op = new CrossproductOperation();
		return op.apply(left, right);
	}
	
    public Relation apply(Relation leftHandInput, Relation rightHandInput) {
		int arity = leftHandInput.getArity() + rightHandInput.getArity();
        String[] dimensionNames = getDimensionNames(leftHandInput, rightHandInput, arity);
    	RelationImplementation result = new RelationImplementation(dimensionNames);
		for (Iterator iterLeft = leftHandInput.getTuples().iterator(); iterLeft.hasNext();) {
			Tuple leftTuple = (Tuple) iterLeft.next();
			for (Iterator iterRight = rightHandInput.getTuples().iterator(); iterRight.hasNext();) {
				Tuple rightTuple = (Tuple) iterRight.next();
				result.addTuple(join(leftTuple, rightTuple, arity));
			}
		}
        return result;
    }

    private Object[] join(Tuple leftTuple, Tuple rightTuple, int arity) {
    	Object[] result = new Object[arity];
		for (int i = 0; i < leftTuple.getLength(); i++) {
			result[i] = leftTuple.getElement(i);
		}
		for (int i = 0; i < rightTuple.getLength(); i++) {
			result[leftTuple.getLength() + i] = rightTuple.getElement(i);
		}
		return result;
    }

    private String[] getDimensionNames(Relation leftHandInput, Relation rightHandInput, int arity) {
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
