/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.lattice.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;


/**
 * This implements concepts.
 *
 * Intent and extent are mapped into filter and ideal resp. to avoid redundant
 * storage. Filter and ideal are explicitely stored to reduce computational
 * efforts for these operations. The calculation of intent and extent size is
 * done in this class, the joins on the sets themselves are done by creating an
 * iterator which iterates over all contingents in filter and ideal resp.
 *
 * To use this class one has to ensure all sub- and superconcept relations
 * are set up properly. If only the neighbourhood relation is set the method
 * buildClosures() can be called to extent this to the full sub-/superconcept
 * relation.
 */
public class ConceptImplementation<O,A> implements Concept<O,A> {
    private ListSet<A> attributeContingent = new ListSetImplementation<A>();
    private ListSet<O> objectContingent = new ListSetImplementation<O>();

    /**
     * This class implements an iterator that iterates over all attribute
     * contingents of a given concept set.
     */
    class AttributeIterator implements Iterator<A> {
        /**
         * Stores the main iterator on the concepts.
         */
        Iterator<Concept<O,A>> mainIterator;

        /**
         * Stores the secondary iterator on the attributes of one concept.
         */
        Iterator<A> secondaryIterator;

        /**
         * We start with the iterator of all concepts that we want to visit.
         */
        AttributeIterator(Iterator<Concept<O,A>> main) {
            this.mainIterator = main;
            if (main.hasNext()) {
                Concept<O,A> first = main.next();
                this.secondaryIterator = first.getAttributeContingentIterator();
            } else {
                this.secondaryIterator = null;
            }
        }

        /**
         * Returns true if we didn't iterate through all attributes in the filter
         * yet.
         */
        public boolean hasNext() {
            if (this.secondaryIterator == null) {
                return false;
            }
            // make sure that we point to the next object, even if there are
            // empty contingents coming ahead
            while (!this.secondaryIterator.hasNext() && this.mainIterator.hasNext()) {
                // go to next concept
                Concept<O,A> next = this.mainIterator.next();
                this.secondaryIterator = next.getAttributeContingentIterator();
            }
            return this.secondaryIterator.hasNext();
        }

        /**
         * Returns the next attribute.
         */
        public A next() {
            if (this.secondaryIterator == null) {
                throw new NoSuchElementException();
            }
            // Assume: secIt not null
            if (!this.secondaryIterator.hasNext() && !this.mainIterator.hasNext()) {
                // we were already finished
                throw new NoSuchElementException();
            }
            // make sure that we point to the next attribute, even if there are
            // empty contingents coming ahead
            while (!this.secondaryIterator.hasNext() && this.mainIterator.hasNext()) {
                // go to next concept
                Concept<O,A> next = this.mainIterator.next();
                this.secondaryIterator = next.getAttributeContingentIterator();
            }
            return this.secondaryIterator.next();
        }

        /**
         * Throws UnsupportedOperationException.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * This class implements an iterator that iterates over all object
     * contingents of a given concept set.
     */
    class ObjectIterator implements Iterator<O> {
        /**
         * Stores the main iterator on the concepts.
         */
        Iterator<Concept<O,A>> mainIterator;

        /**
         * Stores the secondary iterator on the objects of one concept.
         */
        Iterator<O> secondaryIterator;

        /**
         * We start with the iterator of all concepts that we want to visit.
         */
        ObjectIterator(Iterator<Concept<O,A>> main) {
            this.mainIterator = main;
            if (main.hasNext()) {
                Concept<O,A> first = main.next();
                this.secondaryIterator = first.getObjectContingentIterator();
            } else {
                this.secondaryIterator = null;
            }
        }

        /**
         * Returns true if we didn't iterate through all objects in the ideal
         * yet.
         */
        public boolean hasNext() {
            if (this.secondaryIterator == null) {
                return false;
            }
            // make sure that we point to the next object, even if there are
            // empty contingents coming ahead
            while (!this.secondaryIterator.hasNext() && this.mainIterator.hasNext()) {
                // go to next concept
                Concept<O,A> next = this.mainIterator.next();
                this.secondaryIterator = next.getObjectContingentIterator();
            }
            return this.secondaryIterator.hasNext();
        }

        /**
         * Returns the next object.
         */
        public O next() {
            if (this.secondaryIterator == null) {
                throw new NoSuchElementException();
            }
            // Assume: secIt not null
            if (!this.secondaryIterator.hasNext() && !this.mainIterator.hasNext()) {
                // we were already finished
                throw new NoSuchElementException();
            }
            // make sure that we point to the next object, even if there are
            // empty contingents coming ahead
            while (!this.secondaryIterator.hasNext() && this.mainIterator.hasNext()) {
                // go to next concept
                Concept<O,A> next = this.mainIterator.next();
                this.secondaryIterator = next.getObjectContingentIterator();
            }
            return this.secondaryIterator.next();
        }

        /**
         * Throws UnsupportedOperationException.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Stores all concepts in the filter, including this.
     */
    protected Set<Concept<O,A>> filter = new HashSet<Concept<O,A>>();

    /**
     * Stores all concepts in the ideal, including this.
     */
    protected Set<Concept<O,A>> ideal = new HashSet<Concept<O,A>>();

    /**
     * Stores the number of objects in the extent to avoid unneccessary
     * calculations.
     *
     * This is initialized as lazy fetching in getExtentSize().
     */
    private int extentSize = -1;

    /**
     * Stores the number of attribut the intent to avoid unnecc essary
     * calculations.
     *
     * This is initialized as lazy fetching in getIntentSize().
     */
    private int intentSize = -1;

    /**
     * Initializes the ideal and filter with linked list having a reference to
     * this.
     *
     * Use addSuperConcept(Concept) and addSubConcept(Concept) to extent filter
     * and ideal.
     */
    public ConceptImplementation() {
        this.filter.add(this);
        this.ideal.add(this);
    }

    /**
     * Adds a concept to the filter.
     */
    public void addSuperConcept(Concept<O,A> superConcept) {
        this.filter.add(superConcept);
    }

    /**
     * Adds a concept to the ideal.
     */
    public void addSubConcept(Concept<O,A> subConcept) {
        this.ideal.add(subConcept);
    }

    /**
     * Calculates the ideal and filter for this concept if only direct neighbours
     * are given.
     *
     * If only direct neighbours in the neighbourhoud relation where given this
     * method can be called to create the ideal and filter by building the
     * transitive closures.
     */
    public void buildClosures() {
        List<Concept<O,A>> idealList = new LinkedList<Concept<O,A>>(ideal);
        while (!idealList.isEmpty()) {
        	Concept<O,A> other = idealList.remove(0);
            Iterator<Concept<O,A>> it = other.getDownset().iterator();
            while (it.hasNext()) {
            	Concept<O,A> trans = it.next();
                if (ideal.add(trans)) {
                    idealList.add(trans);
                }
            }
        }

        List<Concept<O,A>> filterList = new LinkedList<Concept<O,A>>(filter);
        while (!filterList.isEmpty()) {
        	Concept<O,A> other = filterList.remove(0);
            Iterator<Concept<O,A>> it = other.getUpset().iterator();
            while (it.hasNext()) {
            	Concept<O,A> trans = it.next();
                if (filter.add(trans)) {
                    filterList.add(trans);
                }
            }
        }
    }

    /**
     * Calculates the intent size based on the contingent sizes in the filter.
     */
    public int getIntentSize() {
        if (intentSize < 0) { // not yet calculated
            intentSize = 0;
            Iterator<Concept<O, A>> it = filter.iterator();
            while (it.hasNext()) {
                Concept<O,A> cur = it.next();
                intentSize += cur.getAttributeContingentSize();
            }
        }
        return intentSize;
    }

    /**
     * Calculates the relative intent size.
     */
    public double getIntentSizeRelative() {
        return getIntentSize() / (double) getNumberOfAttributes();
    }

    /**
     * Calculates the extent size based on the contingent sizes in the ideal.
     */
    public int getExtentSize() {
        if (extentSize < 0) { // not yet calculated
            extentSize = 0;
            Iterator<Concept<O,A>> it = ideal.iterator();
            while (it.hasNext()) {
                Concept<O,A> cur = it.next();
                extentSize += cur.getObjectContingentSize();
            }
        }
        return extentSize;
    }

    /**
     * Calculates the relative extent size.
     */
    public double getExtentSizeRelative() {
        return getExtentSize() / (double) getNumberOfObjects();
    }

    /**
     * Iterates over all attribute contingents in the filter.
     */
    public Iterator<A> getIntentIterator() {
        return new AttributeIterator(this.filter.iterator());
    }

    /**
     * Iterates over all object contingents in the ideal.
     */
    public Iterator<O> getExtentIterator() {
        return new ObjectIterator(this.ideal.iterator());
    }

    /**
     * Calculates the relative attribute contingent size.
     */
    public double getAttributeContingentSizeRelative() {
        return getAttributeContingentSize() / (double) getNumberOfAttributes();
    }

    /**
     * Calculates the relative object contingent size.
     */
    public double getObjectContingentSizeRelative() {
        return getObjectContingentSize() / (double) getNumberOfObjects();
    }

    /**
     * Find the number of objects in this diagram.
     *
     * This is equal to the size of the extent of the top node.
     */
    private int getNumberOfObjects() {
        Concept<O,A> cur = this;
        while (cur.getUpset().size() != 1) {
            // there is another concept in the filter which is not this
            // (this is always the first) ==> go up
            // The concept itself is in the filter, too -- we have to avoid
            // infinite recursion here
            Iterator<Concept<O,A>> it = cur.getUpset().iterator();
            Concept<O,A> next = cur;
            while (cur == next) { // we know there has to be a next()
                next = it.next();
            }
            cur = next;
        }
        // now we are at the top
        return cur.getExtentSize();
    }

    /**
     * Find the number of attributes in this diagram.
     *
     * This is equal to the size of the intent of the bottom node.
     */
    private int getNumberOfAttributes() {
        Concept<O,A> cur = this;
        while (cur.getDownset().size() != 1) {
            // there is another concept in the ideal which is not this
            // (this is always the first) ==> go down
            // The concept itself is in the filter, too -- we have to avoid
            // infinite recursion here
            Iterator<Concept<O,A>> it = cur.getDownset().iterator();
            Concept<O,A> next = cur;
            while (cur == next) { // we know there has to be a next()
                next = it.next();
            }
            cur = next;
        }
        // now we are at the bottom
        return cur.getIntentSize();
    }

    /**
     * Returns true if this is the top concept.
     */
    public boolean isTop() {
        return this.filter.size() == 1;
    }

    /**
     * Returns true if this is the bottom concept.
     */
    public boolean isBottom() {
        return this.ideal.size() == 1;
    }

    /**
     * Returns true iff the given concept is in the filter of this one.
     */
    public boolean hasSuperConcept(Concept<O,A> concept) {
        return this.filter.contains(concept);
    }

    /**
     * Returns true iff the given concept is in the ideal of this one.
     */
    public boolean hasSubConcept(Concept<O,A> concept) {
        return this.ideal.contains(concept);
    }

    public Collection<Concept<O,A>> getDownset() {
        return this.ideal;
    }

    public Collection<Concept<O,A>> getUpset() {
        return this.filter;
    }

    public int getAttributeContingentSize() {
        return this.attributeContingent.size();
    }

    public int getObjectContingentSize() {
        return this.objectContingent.size();
    }

    public Iterator<A> getAttributeContingentIterator() {
        return this.attributeContingent.iterator();
    }

    public Iterator<O> getObjectContingentIterator() {
        return this.objectContingent.iterator();
    }

    public void addObject(O object) {
        this.objectContingent.add(object);
    }

    public void addAttribute(A attribute) {
        this.attributeContingent.add(attribute);
    }
    
    public void replaceObject(O objectToReplace, O newObject) {
		// @todo make sure new object is inserted at the same position where old one was
    	this.objectContingent.remove(objectToReplace);
    	this.objectContingent.add(newObject);
    }

    public void removeObject(O object) {
        this.objectContingent.remove(object);
    }

    public void removeAttribute(A attribute) {
        this.attributeContingent.remove(attribute);
    }

    public boolean isMeetIrreducible() {
        for (Iterator<Concept<O,A>> iterator = filter.iterator(); iterator.hasNext();) {
            Concept<O,A> concept = iterator.next();
            if (concept.getUpset().size() == this.filter.size() - 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isJoinIrreducible() {
        for (Iterator<Concept<O,A>> iterator = getDownset().iterator(); iterator.hasNext();) {
        	Concept<O,A> concept = iterator.next();
            if (concept.getDownset().size() == this.getDownset().size() - 1) {
                return true;
            }
        }
        return false;
    }

    public void removeObjectContingent() {
    	this.objectContingent.clear();
    }

	public Concept<O,A> getTopConcept() {
		Concept<O,A> topCandidate = this;
		while (!topCandidate.isTop()) {
			Concept<O,A> other = topCandidate;
			Iterator<Concept<O,A>> it = topCandidate.getUpset().iterator();
			do {
				other = it.next();
			} while (other == topCandidate);
			topCandidate = other;
		}
		return topCandidate;
	}

	public Concept<O,A> getBottomConcept() {
		Concept<O,A> bottomCandidate = this;
		while (!bottomCandidate.isBottom()) {
			Concept<O,A> other = bottomCandidate;
			Iterator<Concept<O,A>> it = bottomCandidate.getDownset().iterator();
			do {
				other = it.next();
			} while (other == bottomCandidate);
			bottomCandidate = other;
		}
		return bottomCandidate;
	}
}
