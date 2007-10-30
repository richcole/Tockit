/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;

public class RealisedScale extends ConscriptStructure{
    private ConcreteScale concreteScale;
	private IdentifierMap identifierMap;
	
	public RealisedScale(String identifier) {
        super(identifier);
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

    @Override
	public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        stream.print("\t\t(" + this.concreteScale.getName() + ", "); 
        stream.println(this.identifierMap.getName() + ");");
    }
}