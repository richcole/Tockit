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
    private Set<String> allocatedIds = new HashSet<String>();
    private int nextNumber = 1;

    public String getFreeId() {
        String retVal;
        do {
            retVal = String.valueOf(nextNumber);
            nextNumber++;
        } while (idReserved(retVal));
        reserveId(retVal);
        return retVal;
    }
    
    public String getFreeId(String preferredId) {
    	if ( idReserved(preferredId)) {
    		return getFreeId();
    	} else {
    		reserveId(preferredId);
    		return preferredId;
    	}
    }

    public void releaseId(String id) {
        this.allocatedIds.remove(id);
    }

    public void reserveId(String id) throws IllegalArgumentException {
        if (idReserved(id)) {
        	throw new IllegalArgumentException("Id " + id + " is already reserved");
        }
        this.allocatedIds.add(id);
    }
    
    /**
     * @deprecated use idReserved(String) instead
     * @param id
     * @return
     */
    public boolean idIsReserved (String id) {
    	return this.idReserved(id);
    }

    public boolean idReserved (String id) {
    	return this.allocatedIds.contains(id);
    }
    
}
