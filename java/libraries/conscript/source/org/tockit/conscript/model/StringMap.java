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

public class StringMap extends ConscriptStructure{
	private Map map = new Hashtable();
		
	public StringMap(String identifier) {
        super(identifier);
	}

	public Map getMap() {
		return Collections.unmodifiableMap(map);
	}
    
    public FormattedString getLabel(String entry) {
        return (FormattedString) this.map.get(entry);
    }
    
    public void addEntry(String attributeId, FormattedString label) {
        this.map.put(attributeId, label);
    }

    public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        for (Iterator iter = this.map.keySet().iterator(); iter.hasNext(); ) {
        	String id = (String) iter.next();
            stream.println("\t\t(" + id + ", " + this.map.get(id) + ");");
        }
    }
}