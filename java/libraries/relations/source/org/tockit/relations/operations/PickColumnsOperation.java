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
import org.tockit.relations.operations.util.AbstractUnaryRelationOperation;


/**
 * This class does combine permutations and projections.
 * 
 * The operation takes an int[] as defining parameter, which denotes the columns to pick
 * from the input relation. Any order is possible, repetitions are allowed. 
 */
public class PickColumnsOperation extends AbstractUnaryRelationOperation {
	private int[] columnsToPick;
	private String name;
	
	public PickColumnsOperation(int[] columnsToPick) {
		this(columnsToPick, "Permute and project");
	}
	
    public PickColumnsOperation(int[] columnsToPick, String name) {
    	this.columnsToPick = columnsToPick;
    	this.name = name;
    }

    public String getName() {
        return name;
    }

    public String[] getParameterNames() {
        return new String[]{"input"};
    }

	/**
	 * @throws ArrayIndexOutOfBoundsException iff one of the column positions is out of range
	 */
    public Relation apply(Relation input) {
    	String[] dimensionNames = new String[this.columnsToPick.length];
		for (int i = 0; i < this.columnsToPick.length; i++) {
			int col = this.columnsToPick[i];
			dimensionNames[i] = input.getDimensionNames()[col];
		}
    	RelationImplementation result = new RelationImplementation(dimensionNames);
    	for (Iterator iter = input.getTuples().iterator(); iter.hasNext();) {
            Tuple tuple = (Tuple) iter.next();
            result.addTuple(project(tuple.getData()));
        }
        return result;
    }
    
    private Object[] project(Object[] input) {
    	Object[] result = new Object[this.columnsToPick.length];
    	for (int i = 0; i < this.columnsToPick.length; i++) {
            int col = this.columnsToPick[i];
            result[i] = input[col];
        }
    	return result;
    }
}
