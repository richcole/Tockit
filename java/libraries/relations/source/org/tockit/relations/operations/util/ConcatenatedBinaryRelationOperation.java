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

public class ConcatenatedBinaryRelationOperation extends AbstractBinaryRelationOperation {
	private UnaryRelationOperation leftHandOperation;
	private UnaryRelationOperation rightHandOperation;
	private BinaryRelationOperation finalOperation;
	
	public ConcatenatedBinaryRelationOperation(
							UnaryRelationOperation leftHandOperation, 
							UnaryRelationOperation rightHandOperation,
							BinaryRelationOperation finalOperation) {
		if(leftHandOperation == null || rightHandOperation == null || finalOperation == null) {
			throw new NullPointerException("Input parameter for concatenation must not be null");			
		}
		this.leftHandOperation = leftHandOperation;
		this.rightHandOperation = rightHandOperation;
		this.finalOperation = finalOperation;
	}

	public Relation apply(Relation leftHandInput, Relation rightHandInput) {
		return finalOperation.apply(leftHandOperation.apply(leftHandInput), rightHandOperation.apply(rightHandInput));
	}

	public String getName() {
		return finalOperation.getName() + "(" + leftHandOperation.getName() + "," + rightHandOperation.getName() + ")";
	}
}
