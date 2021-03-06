/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public class Property {
    private PropertyType type;
    private String name;

    public Property(PropertyType type, String name) {
        this.type = type;
        this.name = name;
    }

    public PropertyType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    @Override
	public String toString() {
    	return getName();
    }
}
