/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class ConceptualFile extends ConscriptStructure {
	private StringMap objectMap;
	private RealisedScale[] realisedScales;
	
	public ConceptualFile(String name) {
        super(name);
	}

	public StringMap getObjectMap() {
		return objectMap;
	}

	public RealisedScale[] getRealisedScales() {
		return realisedScales;
	}

	public void setObjectMap(StringMap objectMap) {
		this.objectMap = objectMap;
	}
}