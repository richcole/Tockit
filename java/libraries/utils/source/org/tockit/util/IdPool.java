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
        } while (allocatedIds.contains(retVal));
        reserveId(retVal);
        return retVal;
    }

    public void releaseId(String id) {
        this.allocatedIds.remove(id);
    }

    public void reserveId(String id) {
        this.allocatedIds.add(id);
    }
}
