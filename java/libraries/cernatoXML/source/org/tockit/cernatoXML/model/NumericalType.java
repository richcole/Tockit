/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public class NumericalType extends TypeImplementation {
    private int numberOfDecimals = 2;
    
    public NumericalType(String name) {
        super(name);
    }

    public void addValueGroup(ValueGroup valueGroup, String id) {
        if (valueGroup instanceof NumericalValueGroup) {
            super.addValueGroup(valueGroup, id);
            return;
        }
        throw new RuntimeException("Wrong value group type");
    }
    
    public int getNumberOfDecimals() {
        return numberOfDecimals;
    }
    
    public void setNumberOfDecimals(int numberOfDecimals) {
        this.numberOfDecimals = numberOfDecimals;
    }
}
