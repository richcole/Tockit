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


public class SelectionOperation extends AbstractUnaryRelationOperation {
	public static Relation selectByValue(Relation input, int column, Object value) {
		SelectionOperation op = getValueSelect(column, value);
		return op.apply(input);
	}
	
	public static Relation selectByColumnEquality(Relation input, int firstColumn, int secondColumn) {
		SelectionOperation op = getColumnEqualitySelect(firstColumn, secondColumn);
		return op.apply(input);
	}
	
	public static Relation select(Relation input, TuplePredicate predicate) {
		SelectionOperation op = new SelectionOperation(predicate);
		return op.apply(input);
	}
	
	public static interface TuplePredicate {
		boolean test(Tuple tuple);
	}
	
	private TuplePredicate predicate;
	private String name;
	
    public SelectionOperation(TuplePredicate predicate) {
    	this.predicate = predicate;
    	this.name = "Selection via predicate";
    }
    
    public static SelectionOperation getValueSelect(final int columnToTest, final Object value) {
    	SelectionOperation result = new SelectionOperation(new TuplePredicate(){
            public boolean test(Tuple tuple) {
                return tuple.getElement(columnToTest).equals(value);
            }
    	});
    	result.setName("Select if column " + columnToTest + " equals: " + value);
        return result;
    }
    
	public static SelectionOperation getColumnEqualitySelect(final int columnToTest, final int columnToCompareTo) {
		SelectionOperation result = new SelectionOperation(new TuplePredicate(){
			public boolean test(Tuple tuple) {
				return tuple.getElement(columnToTest).equals(tuple.getElement(columnToCompareTo));
			}
		});
		result.setName("Select if column " + columnToTest + " equals column " + columnToCompareTo);
        return result;
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
    	RelationImplementation result = new RelationImplementation(input.getDimensionNames());
    	for (Iterator iter = input.getTuples().iterator(); iter.hasNext();) {
            Tuple tuple = (Tuple) iter.next();
            if(this.predicate.test(tuple)) {
				result.addTuple(tuple);
            }
        }
        return result;
    }
}
