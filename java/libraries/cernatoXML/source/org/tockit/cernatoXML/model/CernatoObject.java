/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public class CernatoObject {
    private String name;

    public CernatoObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
	public String toString() {
        return getName();
    }
}
