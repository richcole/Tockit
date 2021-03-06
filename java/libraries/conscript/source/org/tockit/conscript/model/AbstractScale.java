/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AbstractScale extends ConscriptStructure{
	private FormalContext context;
	private List<LineDiagram> lineDiagrams = new ArrayList<LineDiagram>();
	
	public AbstractScale(String name) {
        super(name);
	}
		
	public FormalContext getContext() {
		return context;
	}
	public List<LineDiagram> getLineDiagrams() {
		return Collections.unmodifiableList(lineDiagrams);
	}

	public void setContext(FormalContext context) {
		this.context = context;
	}

	public void addLineDiagram(LineDiagram lineDiagram) {
		this.lineDiagrams.add(lineDiagram);
	}

    @Override
	public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        stream.print("\t\t(" + this.context.getName() + ", "); // two commas for the missing lattice bit
        for (Iterator<LineDiagram> iter = this.lineDiagrams.iterator(); iter.hasNext();) {
            LineDiagram diagram = iter.next();
            stream.print(", " + diagram.getName());
        }
        stream.println(");");
    }
}