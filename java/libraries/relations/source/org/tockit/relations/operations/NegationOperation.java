/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.relations.model.Tuple;
import org.tockit.relations.operations.util.AbstractUnaryRelationOperation;


public class NegationOperation<D> extends AbstractUnaryRelationOperation<D> {
	public static<R> Relation<R> negate(Relation<R> input, Set<R>[] domains) {
		NegationOperation<R> op = new NegationOperation<R>(domains);
		return op.doApply(input);
	}
	
	private Set<D>[] domains;
	private String name;
	
    public NegationOperation(Set<D>[] domains) {
    	this.domains = domains;
		this.name = "Negation";
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
    	if(input.getArity() != this.domains.length) {
    		throw new IllegalArgumentException("Relation arity does not match number of domains");
    	}
    	RelationImplementation<D> result = new RelationImplementation<D>(input.getDimensionNames());
    	for (Iterator<Tuple<D>> iter = new CrossProductIterator<D>(this.domains); iter.hasNext();) {
            Tuple<D> tuple = iter.next();
            if(!input.isRelated(tuple)) {
				result.addTuple(tuple);
            }
        }
        return result;
    }

	private static class CrossProductIterator<T> implements Iterator<Tuple<T>> {
		private Set<T>[] sets;
        private Iterator<T>[] iterators;
		private T[] curObject;

		@SuppressWarnings("unchecked")
		public CrossProductIterator(Set<T>[] sets) {
			this.sets = sets;
			this.iterators = new Iterator[sets.length];
			for (int i = 0; i < sets.length; i++) {
                this.iterators[i] = sets[i].iterator();
            }			
		}
		
        public boolean hasNext() {
        	for (int i = 0; i < this.iterators.length; i++) {
                Iterator<T> it = this.iterators[i];
                if(it.hasNext()) {
                	return true;
                }
            }
            return false;
        }

        @SuppressWarnings("unchecked")
		public Tuple<T> next() {
        	if(this.curObject == null) {
        		this.curObject = (T[]) new Object[this.iterators.length];
				for (int i = 0; i < this.iterators.length; i++) {
					Iterator<T> it = this.iterators[i];
					this.curObject[i] = it.next();
				}
				return new Tuple<T>(this.curObject);
        	}
			for (int i = 0; i < this.iterators.length; i++) {
				Iterator<T> it = this.iterators[i];
				if(it.hasNext()) {
					this.curObject[i] = it.next();
					return new Tuple<T>(this.curObject);
				} else {
					// reset this one, try to increase the next
					this.iterators[i] = this.sets[i].iterator();
				}
			}
			throw new NoSuchElementException();
        }

        public void remove() {
        	throw new UnsupportedOperationException("Remove not allowed");
        }
	}
}
