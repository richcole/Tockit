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

public class QueryMap extends ConscriptStructure {
    /**
     * We store the information in the opposite direction given, since that is
     * the lookup direction.
     */
    private Map map = new Hashtable();
		
	public QueryMap(String identifier) {
        super(identifier);
    }

	public Map getMap() {
		return Collections.unmodifiableMap(map);
	}
    
    public String getQuery(String abstractObjectId) {
        return (String) this.map.get(abstractObjectId);
    }

	public void addEntry(String concreteObject, String abstractObjectId) {
		this.map.put(abstractObjectId, concreteObject);
	}

    public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        for (Iterator iter = this.map.keySet().iterator(); iter.hasNext(); ) {
            String concreteObj = (String) iter.next();
            stream.println("\t\t(\"" + concreteObj + "\", " + this.map.get(concreteObj) + ");");
        }
    }
}