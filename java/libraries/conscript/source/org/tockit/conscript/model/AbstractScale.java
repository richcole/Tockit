/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class AbstractScale extends SchemaPart{
	private FormalContext context;
	private LineDiagram[] lineDiagrams;
	
	public AbstractScale(ConceptualFile file, String name) {
        super(file, name);
	}
		
	public FormalContext getContext() {
		return context;
	}
	public LineDiagram[] getLineDiagrams() {
		return lineDiagrams;
	}

	public void setContext(FormalContext context) {
		this.context = context;
	}

	public void setLineDiagrams(LineDiagram[] lineDiagrams) {
		this.lineDiagrams = lineDiagrams;
	}
}