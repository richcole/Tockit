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


public class UnionOperation extends AbstractBinaryRelationOperation {
	public static Relation unite(Relation left, Relation right) {
		UnionOperation op = new UnionOperation();
		return op.apply(left, right);
	}
	
    public Relation apply(Relation leftHandInput, Relation rightHandInput) {
    	if(leftHandInput.getArity() != rightHandInput.getArity()) {
    		throw new IllegalArgumentException("Relation arities don't match for union");
    	}
    	RelationImplementation result = new RelationImplementation(leftHandInput.getDimensionNames());
		for (Iterator iter = leftHandInput.getTuples().iterator(); iter.hasNext();) {
			result.addTuple((Tuple) iter.next());            
		}
		for (Iterator iter = rightHandInput.getTuples().iterator(); iter.hasNext();) {
			result.addTuple((Tuple) iter.next());            
		}
        return result;
    }

    public String getName() {
        return "Union";
    }
}
