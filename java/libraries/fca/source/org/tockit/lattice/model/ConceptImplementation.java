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



import org.tockit.context.model.*;
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
public class ConceptImplementation implements Concept {
    private ListSet attributeContingent = new ListSetImplementation();
    private ListSet objectContingent = new ListSetImplementation();

    /**
     * This class implements an iterator that iterates over all attribute
     * contingents of a given concept set.
     */
    class AttributeIterator implements Iterator {
        /**
         * Stores the main iterator on the concepts.
         */
        Iterator mainIterator;

        /**
         * Stores the secondary iterator on the attributes of one concept.
         */
        Iterator secondaryIterator;

        /**
         * We start with the iterator of all concepts that we want to visit.
         */
        AttributeIterator(Iterator main) {
            this.mainIterator = main;
            if (main.hasNext()) {
                Concept first = (Concept) main.next();
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
                Concept next = (Concept) this.mainIterator.next();
                this.secondaryIterator = next.getAttributeContingentIterator();
            }
            return this.secondaryIterator.hasNext();
        }

        /**
         * Returns the next attribute.
         */
        public Object next() {
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
                Concept next = (Concept) this.mainIterator.next();
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
    class ObjectIterator implements Iterator {
        /**
         * Stores the main iterator on the concepts.
         */
        Iterator mainIterator;

        /**
         * Stores the secondary iterator on the objects of one concept.
         */
        Iterator secondaryIterator;

        /**
         * We start with the iterator of all concepts that we want to visit.
         */
        ObjectIterator(Iterator main) {
            this.mainIterator = main;
            if (main.hasNext()) {
                Concept first = (Concept) main.next();
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
                Concept next = (Concept) this.mainIterator.next();
                this.secondaryIterator = next.getObjectContingentIterator();
            }
            return this.secondaryIterator.hasNext();
        }

        /**
         * Returns the next object.
         */
        public Object next() {
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
                Concept next = (Concept) this.mainIterator.next();
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
    protected Set filter = new HashSet();

    /**
     * Stores all concepts in the ideal, including this.
     */
    protected Set ideal = new HashSet();

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
    public void addSuperConcept(Concept superConcept) {
        this.filter.add(superConcept);
    }

    /**
     * Adds a concept to the ideal.
     */
    public void addSubConcept(Concept subConcept) {
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
        List idealList = new LinkedList(ideal);
        while (!idealList.isEmpty()) {
            ConceptImplementation other = (ConceptImplementation) idealList.remove(0);
            Iterator it = other.ideal.iterator();
            while (it.hasNext()) {
                Object trans = it.next();
                if (ideal.add(trans)) {
                    idealList.add(trans);
                }
            }
        }

        List filterList = new LinkedList(filter);
        while (!filterList.isEmpty()) {
            ConceptImplementation other = (ConceptImplementation) filterList.remove(0);
            Iterator it = other.filter.iterator();
            while (it.hasNext()) {
                Object trans = it.next();
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
            Iterator it = filter.iterator();
            while (it.hasNext()) {
                Concept cur = (Concept) it.next();
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
            Iterator it = ideal.iterator();
            while (it.hasNext()) {
                Concept cur = (Concept) it.next();
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
    public Iterator getIntentIterator() {
        return new AttributeIterator(this.filter.iterator());
    }

    /**
     * Iterates over all object contingents in the ideal.
     */
    public Iterator getExtentIterator() {
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
        ConceptImplementation cur = this;
        while (cur.filter.size() != 1) {
            // there is another concept in the filter which is not this
            // (this is always the first) ==> go up
            // The concept itself is in the filter, too -- we have to avoid
            // infinite recursion here
            Iterator it = cur.filter.iterator();
            Object next = cur;
            while (cur == next) { // we know there has to be a next()
                next = it.next();
            }
            cur = (ConceptImplementation) next;
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
        ConceptImplementation cur = this;
        while (cur.ideal.size() != 1) {
            // there is another concept in the ideal which is not this
            // (this is always the first) ==> go down
            // The concept itself is in the filter, too -- we have to avoid
            // infinite recursion here
            Iterator it = cur.ideal.iterator();
            Object next = cur;
            while (cur == next) { // we know there has to be a next()
                next = it.next();
            }
            cur = (ConceptImplementation) next;
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
    public boolean hasSuperConcept(Concept concept) {
        return this.filter.contains(concept);
    }

    /**
     * Returns true iff the given concept is in the ideal of this one.
     */
    public boolean hasSubConcept(Concept concept) {
        return this.ideal.contains(concept);
    }

    public Collection getDownset() {
        return this.ideal;
    }

    public Collection getUpset() {
        return this.filter;
    }

    public int getAttributeContingentSize() {
        return this.attributeContingent.size();
    }

    public int getObjectContingentSize() {
        return this.objectContingent.size();
    }

    public Iterator getAttributeContingentIterator() {
        return this.attributeContingent.iterator();
    }

    public Iterator getObjectContingentIterator() {
        return this.objectContingent.iterator();
    }

    public void addObject(FCAElement object) {
        this.objectContingent.add(object);
    }

    public void addAttribute(FCAElement attribute) {
        this.attributeContingent.add(attribute);
    }
    
    public void replaceObject(FCAElement objectToReplace, FCAElement newObject) {
		// @todo make sure new object is inserted at the same position where old one was
    	this.objectContingent.remove(objectToReplace);
    	this.objectContingent.add(newObject);
    }

    public void removeObject(FCAElement object) {
        this.objectContingent.remove(object);
    }

    public void removeAttribute(FCAElement attribute) {
        this.attributeContingent.remove(attribute);
    }

    public boolean isMeetIrreducible() {
        for (Iterator iterator = filter.iterator(); iterator.hasNext();) {
            ConceptImplementation conceptImplementation = (ConceptImplementation) iterator.next();
            if (conceptImplementation.filter.size() == this.filter.size() - 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isJoinIrreducible() {
        for (Iterator iterator = filter.iterator(); iterator.hasNext();) {
            ConceptImplementation conceptImplementation = (ConceptImplementation) iterator.next();
            if (conceptImplementation.filter.size() == this.filter.size() - 1) {
                return true;
            }
        }
        return false;
    }

    public void removeObjectContingent() {
    	this.objectContingent.clear();
    }

	public Concept getTopConcept() {
		Concept topCandidate = this;
		while (!topCandidate.isTop()) {
			Concept other = topCandidate;
			Iterator it = topCandidate.getUpset().iterator();
			do {
				other = (Concept) it.next();
			} while (other == topCandidate);
			topCandidate = other;
		}
		return topCandidate;
	}

	public Concept getBottomConcept() {
		Concept bottomCandidate = this;
		while (!bottomCandidate.isBottom()) {
			Concept other = bottomCandidate;
			Iterator it = bottomCandidate.getDownset().iterator();
			do {
				other = (Concept) it.next();
			} while (other == bottomCandidate);
			bottomCandidate = other;
		}
		return bottomCandidate;
	}
}
