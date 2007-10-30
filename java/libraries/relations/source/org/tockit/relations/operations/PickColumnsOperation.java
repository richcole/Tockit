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
public class PickColumnsOperation<D> extends AbstractUnaryRelationOperation<D> {
	public static<R> Relation<R> pickColumns(Relation<R> input, int[] columnsToPick) {
		PickColumnsOperation<R> op = new PickColumnsOperation<R>(columnsToPick);
		return op.doApply(input);
	}
	
	public static<R> Relation<R> pickColumn(Relation<R> input, int columnToPick) {
		PickColumnsOperation<R> op = new PickColumnsOperation<R>(new int[]{columnToPick});
		return op.doApply(input);
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

    @Override
	public String[] getParameterNames() {
        return new String[]{"input"};
    }

	/**
	 * @throws ArrayIndexOutOfBoundsException iff one of the column positions is out of range
	 */
    @Override
	public Relation<D> doApply(Relation<D> input) {
    	String[] dimensionNames = new String[this.columnsToPick.length];
		for (int i = 0; i < this.columnsToPick.length; i++) {
			int col = this.columnsToPick[i];
			dimensionNames[i] = input.getDimensionNames()[col];
		}
    	RelationImplementation<D> result = new RelationImplementation<D>(dimensionNames);
    	for (Iterator<Tuple<D>> iter = input.getTuples().iterator(); iter.hasNext();) {
            Tuple<D> tuple = iter.next();
            result.addTuple(project(tuple.getData()));
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
	private D[] project(D[] input) {
    	D[] result = (D[]) new Object[this.columnsToPick.length];
    	for (int i = 0; i < this.columnsToPick.length; i++) {
            int col = this.columnsToPick[i];
            result[i] = input[col];
        }
    	return result;
    }
}
