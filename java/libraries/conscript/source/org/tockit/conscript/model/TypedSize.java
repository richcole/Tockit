/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class TypedSize {

	private double value;
	private String type;

	public TypedSize(double value, String type) {
		this.value = value;
		this.type = type;
	}

	public TypedSize(String string) {
        //@todo parse string
    }

    public String getType() {
		return type;
	}

	public double getValue() {
		return value;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(double value) {
		this.value = value;
	}

}