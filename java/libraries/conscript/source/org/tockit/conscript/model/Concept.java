/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class Concept {
	private Point point;
	private String identifier;
	private FormattedString description;
	
	public Concept(Point point, String identifier, FormattedString description){
		this.point = point;
		this.identifier = identifier;
		this.description = description;
	}
	
	public FormattedString getDescription() {
		return description;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Point getPoint() {
		return point;
	}

	public void setDescription(FormattedString description) {
		this.description = description;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
}