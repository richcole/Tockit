/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.util.Map;

public class IdentifierMap extends ConscriptStructure{
    private Map map;
    
	public IdentifierMap(String identifier) {
        super(identifier);
    }
		
	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

}