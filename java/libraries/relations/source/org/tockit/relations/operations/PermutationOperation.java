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


public class PermutationOperation extends AbstractUnaryRelationOperation {
	public static Relation permute(Relation input, int[] columnsToPermute) {
		PermutationOperation op = new PermutationOperation(columnsToPermute);
		return op.apply(input);
	}
	
	public static Relation exchange(Relation input, int column1, int column2) {
		PermutationOperation op = new PermutationOperation(new int[]{column1, column2});
		return op.apply(input);
	}
	
	private int[] columnsToPermute;
	private String name;

	/**
	 * @todo add sanity checking: no duplicates allowed
	 */	
	public PermutationOperation(int[] columnsToPermute) {
		if(columnsToPermute.length == 0) {
			throw new IllegalArgumentException("No columns given for permutation");
		}
		// we add the first one to the end so we have it easier later on
		this.columnsToPermute = new int[columnsToPermute.length + 1];
		for (int i = 0; i < columnsToPermute.length; i++) {
            this.columnsToPermute[i] = columnsToPermute[i];
        }
        this.columnsToPermute[columnsToPermute.length] = columnsToPermute[0];
		this.name = "Permute columns (";
		for (int i = 0; i < columnsToPermute.length; i++) {
            if(i != 0) {
            	this.name += ",";
            }
            this.name += columnsToPermute[i];
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

    public Relation apply(Relation input) {
        String[] dimensionNames = getDimensionNames(input);
    	RelationImplementation result = new RelationImplementation(dimensionNames);
    	for (Iterator iter = input.getTuples().iterator(); iter.hasNext();) {
            Tuple tuple = (Tuple) iter.next();
            result.addTuple(permute(tuple.getData()));
        }
        return result;
    }

	private String[] getDimensionNames(Relation input) {
		String[] dimensionNames = new String[input.getArity()];
		for (int i = 0; i < dimensionNames.length; i++) {
			String name = input.getDimensionNames()[i];
			for (int j = 0; j < this.columnsToPermute.length - 1; j++) {
				if(i == this.columnsToPermute[j]) {
					name = input.getDimensionNames()[this.columnsToPermute[j+1]];
					break;
				}
			}            
			dimensionNames[i] = name;
		}
		return dimensionNames;
	}

	private Object[] permute(Object[] input) {
		Object[] result = new Object[input.length];
		for (int i = 0; i < input.length; i++) {
			Object elem = input[i];
			for (int j = 0; j < this.columnsToPermute.length - 1; j++) {
				if(i == this.columnsToPermute[j]) {
					elem = input[this.columnsToPermute[j+1]];
					break;
				}
			}            
			result[i] = elem;
		}
		return result;
	}
}
