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


public class SelectionOperation<D> extends AbstractUnaryRelationOperation<D> {
	public static<R> Relation<R> selectByValue(Relation<R> input, int column, R value) {
		SelectionOperation<R> op = getValueSelect(column, value);
		return op.doApply(input);
	}
	
	public static<R> Relation<R> selectByColumnEquality(Relation<R> input, int firstColumn, int secondColumn) {
		SelectionOperation<R> op = getColumnEqualitySelect(firstColumn, secondColumn);
		return op.doApply(input);
	}
	
	public static<R> Relation<R> select(Relation<R> input, TuplePredicate<R> predicate) {
		SelectionOperation<R> op = new SelectionOperation<R>(predicate);
		return op.doApply(input);
	}
	
	public static interface TuplePredicate<R> {
		boolean test(Tuple<? extends R> tuple);
	}
	
	private TuplePredicate<D> predicate;
	private String name;
	
    public SelectionOperation(TuplePredicate<D> predicate) {
    	this.predicate = predicate;
    	this.name = "Selection via predicate";
    }
    
    public static<R> SelectionOperation<R> getValueSelect(final int columnToTest, final R value) {
    	SelectionOperation<R> result = new SelectionOperation<R>(new TuplePredicate<R>(){
            public boolean test(Tuple<? extends R> tuple) {
                return tuple.getElement(columnToTest).equals(value);
            }
    	});
    	result.setName("Select if column " + columnToTest + " equals: " + value);
        return result;
    }
    
	public static<R> SelectionOperation<R> getColumnEqualitySelect(final int columnToTest, final int columnToCompareTo) {
		SelectionOperation<R> result = new SelectionOperation<R>(new TuplePredicate<R>(){
			public boolean test(Tuple<? extends R> tuple) {
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

    @Override
	public String[] getParameterNames() {
        return new String[]{"input"};
    }

    @Override
	public Relation<D> doApply(Relation<D> input) {
    	RelationImplementation<D> result = new RelationImplementation<D>(input.getDimensionNames());
    	for (Iterator<Tuple<? extends D>> iter = input.getTuples().iterator(); iter.hasNext();) {
            Tuple<? extends D> tuple = iter.next();
            if(this.predicate.test(tuple)) {
				result.addTuple(tuple);
            }
        }
        return result;
    }
}
