/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public class TextualValue implements AttributeValue {
    private String value;

    public TextualValue(String value) {
        this.value = value;
    }

    public String getDisplayString() {
        return value;
    }

    public String toString() {
        return getDisplayString();
    }

    public boolean equals(Object other) {
        // copied from Double.equals()
        return (other instanceof TextualValue)
               && ((TextualValue)other).value.equals(this.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean isLesserThan(AttributeValue other) {
        if(!(other instanceof TextualValue)) {
            return false;
        }
        TextualValue otherTV = (TextualValue) other;
        return this.value.compareTo(otherTV.value) < 0;
    }

    public boolean isEqual(AttributeValue other) {
        return this.equals(other);
    }
}
