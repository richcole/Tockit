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


public class DropColumnsOperation extends AbstractUnaryRelationOperation {
	public static Relation drop(Relation input, int[] columnsToDrop) {
		DropColumnsOperation op = new DropColumnsOperation(columnsToDrop);
		return op.apply(input);
	}

	public static Relation drop(Relation input, int columnToDrop) {
		DropColumnsOperation op = new DropColumnsOperation(new int[]{columnToDrop});
		return op.apply(input);
	}

	private int[] columnsToDrop;
	private String name;

	/**
	 * @todo add sanity checking: no duplicates allowed
	 */	
	public DropColumnsOperation(int[] columnsToDrop) {
		this.columnsToDrop = columnsToDrop;
		this.name = "Drop columns (";
		for (int i = 0; i < columnsToDrop.length; i++) {
            if(i != 0) {
            	this.name += ",";
            }
            this.name += columnsToDrop[i];
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
	 * @todo add check for dropping things that aren't there
	 */
    public Relation apply(Relation input) {
    	String[] dimensionNames = new String[input.getArity() - this.columnsToDrop.length];
    	int columnsDropped = 0;
    	outerLoop: for (int i = 0; i < input.getDimensionNames().length; i++) {
			for (int j = 0; j < this.columnsToDrop.length; j++) {
				if(i == this.columnsToDrop[j]) {
					columnsDropped++;
					continue outerLoop;
				}
			}
			dimensionNames[i - columnsDropped] = input.getDimensionNames()[i];
        }
    	RelationImplementation result = new RelationImplementation(dimensionNames);
    	for (Iterator<Tuple> iter = input.getTuples().iterator(); iter.hasNext();) {
            Tuple tuple = iter.next();
            result.addTuple(project(tuple.getData()));
        }
        return result;
    }
    
    private Object[] project(Object[] input) {
    	Object[] result = new Object[input.length - this.columnsToDrop.length];
		int columnsDropped = 0;
		outerLoop: for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < this.columnsToDrop.length; j++) {
				if(i == this.columnsToDrop[j]) {
					columnsDropped++;
					continue outerLoop;
				}
			}
			result[i - columnsDropped] = input[i];
		}
    	return result;
    }
}
