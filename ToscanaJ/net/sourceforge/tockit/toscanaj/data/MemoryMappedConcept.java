package net.sourceforge.tockit.toscanaj.data;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An implementation of the Concept interface which holds all data in memory.
 *
 * This is based on AbstractConceptImplementation to reduce implementation
 * effort and to increase reuse.
 */
public class MemoryMappedConcept
    extends AbstractConceptImplementation
    implements Concept
{
    /**
     * Stores the information on the attribute contingent.
     */
    LabelInfo attributeContingent;

    /**
     * Stores the information on the object contingent.
     */
    LabelInfo objectContingent;

    /**
     * Initialisation constructor which takes the information on the two
     * contingents in form of LabelInfo instances.
     */
    public MemoryMappedConcept( LabelInfo attrContingent, LabelInfo objContingent ) {
        super();
        attributeContingent = attrContingent;
        objectContingent = objContingent;
    }

    /**
     * Returns if the concept is realized or not.
     *
     * @TODO Implement correctly once we have to handle not realized concepts.
     */
    public boolean isRealized() {
        return true;
    }

    /**
     * Implements Concept.getAttributeContingentSize().
     */
    public int getAttributeContingentSize() {
        return attributeContingent._entries.size();
    }

    /**
     * Implements Concept.getObjectContingentSize().
     */
    public int getObjectContingentSize() {
        return objectContingent._entries.size();
    }

    /**
     * Implements Concept.getAttributeContingentIterator().
     */
    public Iterator getAttributeContingentIterator() {
        return attributeContingent._entries.iterator();
    }

    /**
     * Implements Concept.getObjectContingentIterator().
     */
    public Iterator getObjectContingentIterator() {
        return objectContingent._entries.iterator();
    }

    /**
     * Will some day implement Concept.directProduct(Concept), returns >this< at
     * the moment.
     */
    public Concept directProduct( Concept other ) {
        return this;
    }
}