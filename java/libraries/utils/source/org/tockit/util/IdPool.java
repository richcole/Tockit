/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.util;

import java.util.HashSet;
import java.util.Set;


public class IdPool {
    private Set allocatedIds = new HashSet();
    private int nextNumber = 1;

    public String getFreeId() {
        String retVal;
        do {
            retVal = String.valueOf(nextNumber);
            nextNumber++;
        } while (idIsReserved(retVal));
        reserveId(retVal);
        return retVal;
    }

    public void releaseId(String id) {
        this.allocatedIds.remove(id);
    }

    public void reserveId(String id) throws IllegalArgumentException {
        if (idIsReserved(id)) {
        	throw new IllegalArgumentException("Id " + id + " is already reserved");
        }
        this.allocatedIds.add(id);
    }
    
    public boolean idIsReserved (String id) {
    	return this.allocatedIds.contains(id);
    }
}
