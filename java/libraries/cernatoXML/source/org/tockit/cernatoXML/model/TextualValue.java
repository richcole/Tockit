/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public class TextualValue implements Value {
    private String value;

    public TextualValue(String value) {
        this.value = value;
    }

    public String getDisplayString() {
        return value;
    }

    @Override
	public String toString() {
        return getDisplayString();
    }

    @Override
	public boolean equals(Object other) {
        // copied from Double.equals()
        return (other instanceof TextualValue)
               && ((TextualValue)other).value.equals(this.value);
    }

    @Override
	public int hashCode() {
        return this.value.hashCode();
    }

    public boolean isLesserThan(Value other) {
        if(!(other instanceof TextualValue)) {
            return false;
        }
        TextualValue otherTV = (TextualValue) other;
        return this.value.compareTo(otherTV.value) < 0;
    }

    public boolean isEqual(Value other) {
        return this.equals(other);
    }
}
