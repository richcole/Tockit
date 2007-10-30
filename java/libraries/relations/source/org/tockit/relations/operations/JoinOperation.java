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


public class JoinOperation extends AbstractBinaryRelationOperation {
	public static Relation join(Relation left, int[] leftColumns, Relation right, int[] rightColumns) {
		JoinOperation op = new JoinOperation(leftColumns, false, rightColumns, true);
		return op.apply(left, right);
	}
	
	public static Relation join(Relation left, int leftColumn, Relation right, int rightColumn) {
		JoinOperation op = new JoinOperation(new int[]{leftColumn}, false, new int[]{rightColumn}, true);
		return op.apply(left, right);
	}
	
	public static Relation join(Relation left, int[] leftColumns, Relation right, int[] rightColumns, boolean dropColumns) {
		JoinOperation op = new JoinOperation(leftColumns, dropColumns, rightColumns, true);
		return op.apply(left, right);
	}
	
	public static Relation join(Relation left, int leftColumn, Relation right, int rightColumn, boolean dropColumn) {
		JoinOperation op = new JoinOperation(new int[]{leftColumn}, dropColumn, new int[]{rightColumn}, true);
		return op.apply(left, right);
	}
	
    private int[] leftHandColumns;
	private boolean dropLeftHandColumns;
	private int[] rightHandColumns;
	private boolean dropRightHandColumns;
	
	public JoinOperation(int[] leftHandColumns, boolean dropLeftHandColumns, int[] rightHandColumns, boolean dropRightHandColumns) {
		if(leftHandColumns.length != rightHandColumns.length) {
			throw new IllegalArgumentException("Equal number of columns needed for join");
		}
		if(leftHandColumns.length == 0) {
			throw new IllegalArgumentException("Columns must be specified for join");
		}
		this.leftHandColumns = leftHandColumns;
		this.dropLeftHandColumns = dropLeftHandColumns;
		this.rightHandColumns = rightHandColumns;
		this.dropRightHandColumns = dropRightHandColumns;	
	}
	
    public Relation apply(Relation leftHandInput, Relation rightHandInput) {
		int arity = leftHandInput.getArity() + rightHandInput.getArity();
		if(this.dropLeftHandColumns) {
			arity -= this.leftHandColumns.length;
		}
		if(this.dropRightHandColumns) {
			arity -= this.rightHandColumns.length;
		}
        String[] dimensionNames = getDimensionNames(leftHandInput, rightHandInput, arity);
    	RelationImplementation result = new RelationImplementation(dimensionNames);
		for (Iterator<Tuple> iterLeft = leftHandInput.getTuples().iterator(); iterLeft.hasNext();) {
			Tuple leftTuple = iterLeft.next();
			for (Iterator<Tuple> iterRight = rightHandInput.getTuples().iterator(); iterRight.hasNext();) {
				Tuple rightTuple = iterRight.next();
				if(joinPossible(leftTuple, rightTuple)) {
					result.addTuple(join(leftTuple, rightTuple, arity));
				}
			}
		}
        return result;
    }

    private Object[] join(Tuple leftTuple, Tuple rightTuple, int arity) {
    	Object[] result = new Object[arity];
		int skippedDimensions = 0;
		tupleLoop: for (int i = 0; i < leftTuple.getLength(); i++) {
			if(this.dropLeftHandColumns) {
				for (int j = 0; j < this.leftHandColumns.length; j++) {
					if(this.leftHandColumns[j] == i) {
						skippedDimensions ++;
						continue tupleLoop;
					}
				}
			}
			result[i - skippedDimensions] = leftTuple.getElement(i);
		}
		tupleLoop: for (int i = 0; i < rightTuple.getLength(); i++) {
			if(this.dropRightHandColumns) {
				for (int j = 0; j < this.rightHandColumns.length; j++) {
					if(this.rightHandColumns[j] == i) {
						skippedDimensions ++;
						continue tupleLoop;
					}
				}
			}
			result[leftTuple.getLength() + i - skippedDimensions] = rightTuple.getElement(i);
		}
		return result;
    }

    private boolean joinPossible(Tuple leftTuple, Tuple rightTuple) {
    	for (int i = 0; i < this.leftHandColumns.length; i++) {
			int colLeft = this.leftHandColumns[i];
			int colRight = this.rightHandColumns[i];
            if(!leftTuple.getElement(colLeft).equals(rightTuple.getElement(colRight))) {
            	return false;
            }
        }
        return true;
    }

    private String[] getDimensionNames(Relation leftHandInput, Relation rightHandInput, int arity) {
        String[] dimensionNames = new String[arity];
        int skippedDimensions = 0;
        for (int i = 0; i < leftHandInput.getDimensionNames().length; i++) {
        	if(this.dropLeftHandColumns) {
        		for (int j = 0; j < this.leftHandColumns.length; j++) {
        			if(this.leftHandColumns[j] == i) {
        				skippedDimensions ++;
        				continue;
        			}
        		}
        	}
        	dimensionNames[i - skippedDimensions] = leftHandInput.getDimensionNames()[i];
        }
        for (int i = 0; i < rightHandInput.getDimensionNames().length; i++) {
        	if(this.dropRightHandColumns) {
        		for (int j = 0; j < this.rightHandColumns.length; j++) {
        			if(this.rightHandColumns[j] == i) {
        				skippedDimensions ++;
        				continue;
        			}
        		}
        	}
        	dimensionNames[leftHandInput.getDimensionNames().length + i - skippedDimensions] = rightHandInput.getDimensionNames()[i];
        }
        return dimensionNames;
    }

	/**
	 * @todo give better naming scheme
	 */
    public String getName() {
        return "Join";
    }
}
