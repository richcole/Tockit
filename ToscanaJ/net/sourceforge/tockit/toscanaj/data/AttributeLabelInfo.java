package net.sourceforge.tockit.toscanaj.data;

import java.util.Iterator;

/**
 * Implements LabelInfo for the attribute contingent.
 */
public class AttributeLabelInfo extends LabelInfo {
    /**
     * Returns the number of attributes in the contingent of the concept attached.
     */
    public int getNumberOfEntries() {
        return this.getNode().getConcept().getAttributeContingentSize();
    }

    /**
     * Returns an attribute from the contingent of the concept attached.
     */
    public Iterator getEntryIterator() {
        return this.getNode().getConcept().getAttributeContingentIterator();
    }
}