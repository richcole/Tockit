/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class FCAObject {
	private long number;
	private String identifier;
	private FormattedString description;
	
	public FCAObject(long number, String identifier, FormattedString description) {
		this.number = number;
		this.identifier = identifier;
		this.description = description;
	}

	public FormattedString getDescription() {
		return description;
	}

	public String getIdentifier() {
		return identifier;
	}

	public long getNumber() {
		return number;
	}

	public void setDescription(FormattedString description) {
		this.description = description;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setNumber(long number) {
		this.number = number;
	}
}