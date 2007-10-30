/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public abstract class FCAElement {
	private Point point;
	private String identifier;
	private FormattedString label;
	
	public FCAElement(Point point, String identifier, FormattedString label) {
		this.point = point;
		this.identifier = identifier;
		this.label = label;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Point getPoint() {
		return this.point;
	}

    public FormattedString getDescription() {
        return this.label;
    }
    
    @Override
	public String toString() {
        return "" + this.point.getNumber() + " " + 
               this.identifier + " " + this.label;
    }
}