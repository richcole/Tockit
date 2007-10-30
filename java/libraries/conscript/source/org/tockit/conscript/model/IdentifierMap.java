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
    private Map<String, String> map = new Hashtable<String, String>();
    
	public IdentifierMap(String identifier) {
        super(identifier);
    }
		
	public Map<String, String> getMap() {
		return Collections.unmodifiableMap(map);
	}
    
    public String getTargetIdentifier(String fromId) {
        return this.map.get(fromId);
    }

    public void addEntry(String fromId, String toId) {
        this.map.put(fromId, toId);
    }

    @Override
	public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        for (Iterator<String> iter = this.map.keySet().iterator(); iter.hasNext(); ) {
            String from = iter.next();
            stream.println("\t\t(" + from + ", " + this.map.get(from) + ");");
        }
    }
}