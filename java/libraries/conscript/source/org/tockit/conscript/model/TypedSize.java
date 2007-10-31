/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TypedSize {
	private static final Logger LOGGER = Logger.getLogger(TypedSize.class.getName()); 

	private double value;
	private String type;

	public TypedSize(double value, String type) {
		this.value = value;
		this.type = type;
	}

	public TypedSize(String string) {
        //@todo parse string
		LOGGER.log(Level.WARNING, "Parsing TypeSize is not yet implemented, the string will be ignored (" + string + ")");
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

    @Override
	public String toString() {
        return "" + this.value + " " + this.type;
    }
}