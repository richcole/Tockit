/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.util;

import java.util.List;
import java.util.Set;


/**
 * An interface to implement both the List and Set interfaces at once.
 * 
 * In other words any implementation of this interface is a list in which
 * each element can occur only once.
 * 
 * Note that the implementation of the add(Object) method can not follow
 * both definitions at the same time.  
 */
public interface ListSet extends List, Set {
    /**
     * Adds an object into the list if not already added.
     * 
     * This method has to implement both List.add(Object) and Set.add(Object).
     * This is impossible in the case the added object does already exist in
     * the list, since the List interface requires it to be added at the end,
     * while Set requires no change at all.
     * 
     * Implementations of this interface should follow the Set interface, i.e.
     * a call to add(Object) returns false and does not change the collection
     * in the case the object does already exist in it.
     */
    boolean add(Object o);
    
    /**
     * Adds an object at the specified position.
     * 
     * In the case that the object already exists in the collection, it will
     * be removed first, then it will be inserted at the given position. This
     * means that the index refers to the final position of the given element.
     * 
     * Note that it is not possible to insert an object at size() in that
     * case, it will cause an IndexOutOfBoundException.
     */
    void add(int index, Object element);
}
