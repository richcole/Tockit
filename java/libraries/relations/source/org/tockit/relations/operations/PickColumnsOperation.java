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
	public static Relation pickColumns(Relation input, int[] columnsToPick) {
		PickColumnsOperation op = new PickColumnsOperation(columnsToPick);
		return op.apply(input);
	}
	
	public static Relation pickColumn(Relation input, int columnToPick) {
		PickColumnsOperation op = new PickColumnsOperation(new int[]{columnToPick});
		return op.apply(input);
	}
	
	private int[] columnsToPick;
	private String name;
	
    public PickColumnsOperation(int[] columnsToPick) {
    	this.columnsToPick = columnsToPick;
		this.name = "Pick columns (";
		for (int i = 0; i < columnsToPick.length; i++) {
			if(i != 0) {
				this.name += ",";
			}
			this.name += columnsToPick[i];
		}
		this.name += ")";
    }
    
    public void setName(String name) {
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
    	for (Iterator<Tuple> iter = input.getTuples().iterator(); iter.hasNext();) {
            Tuple tuple = iter.next();
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
