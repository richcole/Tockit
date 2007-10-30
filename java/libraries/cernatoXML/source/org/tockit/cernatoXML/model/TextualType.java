/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public class TextualType extends TypeImplementation {
    public TextualType(String name) {
        super(name);
    }

    @Override
	public void addValueGroup(ValueGroup valueGroup, String id) {
        if (valueGroup instanceof TextualValueGroup) {
            super.addValueGroup(valueGroup, id);
            return;
        }
        throw new RuntimeException("Wrong value group type");
    }
}
