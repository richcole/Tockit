/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public abstract class TypeImplementation implements AttributeType {
    protected String name;
    protected Scale scale;

    public TypeImplementation(String name) {
        this.name = name;
        this.scale = new ScaleImplementation(name);
    }

    public String getName() {
        return name;
    }

    public Scale[] getScales() {
        return new Scale[]{this.scale};
    }
    
    public AttributeValue[] getValueRange() {
        return null;
    }
}