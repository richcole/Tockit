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

public class ConceptualFile extends ConscriptStructure {
	private StringMap objectMap;
	private List realisedScales = new ArrayList();
	
	public ConceptualFile(String name) {
        super(name);
	}

	public StringMap getObjectMap() {
		return objectMap;
	}

	public List getRealisedScales() {
		return Collections.unmodifiableList(realisedScales);
	}

	public void setObjectMap(StringMap objectMap) {
		this.objectMap = objectMap;
	}
    
    public void addRealisedScale(RealisedScale scale) {
        this.realisedScales.add(scale);
    }

    public void printCSC(PrintStream stream) {
        printIdentifierLine(stream);
        stream.print("\t\t(" + this.objectMap.getName());
        for (Iterator iter = this.realisedScales.iterator(); iter.hasNext();) {
            RealisedScale scale = (RealisedScale) iter.next();
            stream.print(", " + scale.getName());
        }
        stream.println(");");
    }
}