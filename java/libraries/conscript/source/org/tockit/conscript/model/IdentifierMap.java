/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class IdentifierMap extends ConscriptStructure{
    private Map map = new Hashtable();
    
	public IdentifierMap(String identifier) {
        super(identifier);
    }
		
	public Map getMap() {
		return Collections.unmodifiableMap(map);
	}

    public void addEntry(String fromId, String toId) {
        this.map.put(fromId, toId);
    }

    public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        for (Iterator iter = this.map.keySet().iterator(); iter.hasNext(); ) {
            String from = (String) iter.next();
            stream.println("\t\t(" + from + ", " + this.map.get(from) + ");");
        }
    }
}