/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

import java.util.Hashtable;

public abstract class TypeImplementation implements PropertyType {
    protected String name;
    protected Hashtable valueGroups = new Hashtable();

    public TypeImplementation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Value[] getValueRange() {
        return null;
    }

    public void addValueGroup(ValueGroup column, String id) {
        this.valueGroups.put(id, column);
    }

    public ValueGroup getValueGroup(String id) {
        return (ValueGroup) valueGroups.get(id);
    }

    public ValueGroup[] getValueGroups() {
        return (ValueGroup[]) this.valueGroups.values().toArray();
    }
}
