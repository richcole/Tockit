/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class RealisedScale extends ConscriptStructure{
    private ConcreteScale concreteScale;
	private IdentifierMap identifierMap;
	
	public RealisedScale(CSCFile file, String identifier) {
        super(file, identifier);
    }

	public ConcreteScale getConcreteScale() {
		return concreteScale;
	}

	public IdentifierMap getIdentifierMap() {
		return identifierMap;
	}

	public void setConcreteScale(ConcreteScale concreteScale) {
		this.concreteScale = concreteScale;
	}

	public void setIdentifierMap(IdentifierMap identifierMap) {
		this.identifierMap = identifierMap;
	}

}