/*
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 21, 2002
 * Time: 5:52:53 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tockit.cgs.util;

import java.util.HashSet;


public class IdPool {
    HashSet allocatedIds = new HashSet();
    int nextNumber = 1;

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
