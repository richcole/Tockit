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


public class NegationOperation extends AbstractUnaryRelationOperation {
	public static Relation negate(Relation input, Set[] domains) {
		NegationOperation op = new NegationOperation(domains);
		return op.apply(input);
	}
	
	private Set[] domains;
	private String name;
	
    public NegationOperation(Set[] domains) {
    	this.domains = domains;
		this.name = "Negation";
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
    	if(input.getArity() != this.domains.length) {
    		throw new IllegalArgumentException("Relation arity does not match number of domains");
    	}
    	RelationImplementation result = new RelationImplementation(input.getDimensionNames());
    	for (Iterator iter = new CrossProductIterator(this.domains); iter.hasNext();) {
            Tuple tuple = (Tuple) iter.next();
            if(!input.isRelated(tuple)) {
				result.addTuple(tuple);
            }
        }
        return result;
    }

	private static class CrossProductIterator implements Iterator {
		private Set[] sets;
        private Iterator[] iterators;
		private Object[] curObject;

		public CrossProductIterator(Set[] sets) {
			this.sets = sets;
			this.iterators = new Iterator[sets.length];
			for (int i = 0; i < sets.length; i++) {
                this.iterators[i] = sets[i].iterator();
            }			
		}
		
        public boolean hasNext() {
        	for (int i = 0; i < this.iterators.length; i++) {
                Iterator it = this.iterators[i];
                if(it.hasNext()) {
                	return true;
                }
            }
            return false;
        }

        public Object next() {
        	if(this.curObject == null) {
        		this.curObject = new Object[this.iterators.length];
				for (int i = 0; i < this.iterators.length; i++) {
					Iterator it = this.iterators[i];
					this.curObject[i] = it.next();
				}
				return new Tuple(this.curObject);
        	}
			for (int i = 0; i < this.iterators.length; i++) {
				Iterator it = this.iterators[i];
				if(it.hasNext()) {
					this.curObject[i] = it.next();
					return new Tuple(this.curObject);
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
