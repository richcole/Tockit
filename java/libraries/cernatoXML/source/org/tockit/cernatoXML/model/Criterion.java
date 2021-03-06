/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public class Criterion {
    private Property property;
    private ValueGroup valueGroup;

    public Criterion(Property property, ValueGroup valueGroup) {
        this.valueGroup = valueGroup;
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    public ValueGroup getValueGroup() {
        return valueGroup;
    }

    public String getDisplayString() {
        return property.getName() + ":" + valueGroup.getName();
    }

    @Override
	public String toString() {
        return getDisplayString();
    }

    public boolean isLesserThan(Criterion other) {
        return (this.property == other.property) && this.valueGroup.isLesserThan(other.valueGroup);
    }

    public boolean isEqual(Criterion other) {
        return (this.property == other.property) && this.valueGroup.isEqual(other.valueGroup);
    }
}
