/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbstractScale extends ConscriptStructure{
	private FormalContext context;
	private List lineDiagrams = new ArrayList();
	
	public AbstractScale(CSCFile file, String name) {
        super(file, name);
	}
		
	public FormalContext getContext() {
		return context;
	}
	public List getLineDiagrams() {
		return Collections.unmodifiableList(lineDiagrams);
	}

	public void setContext(FormalContext context) {
		this.context = context;
	}

	public void addLineDiagram(LineDiagram lineDiagram) {
		this.lineDiagrams.add(lineDiagram);
	}
}