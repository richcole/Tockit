/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ListSetImplementation extends AbstractList implements ListSet {
    private Set set;
    private List list;
    
    public ListSetImplementation() {
        this.set = new HashSet();
        this.list = new ArrayList();
    }
    
    public int size() {
        return this.set.size();
    }

    public void clear() {
        this.set.clear();
        this.list.clear();
    }
    
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    public Object[] toArray() {
        return this.list.toArray();
    }

    public boolean add(Object o) {
        if(this.set.contains(o)) {
            throw new IllegalArgumentException("Can not add objects twice");
        }
        this.set.add(o);
        this.list.add(o);
        return true;
    }

    public Object get(int index) {
        return this.list.get(index);
    }

    public void add(int index, Object element) {
        if(this.set.contains(element)) {
            throw new IllegalArgumentException("Can not add objects twice");
        }
        this.set.add(element);
        this.list.add(index, element);
    }

    public Object remove(int index) {
        Object element = this.list.remove(index);
        this.set.remove(element);
        return element;
    }

    public Object set(int index, Object element) {
        if(this.set.contains(element) && this.list.get(index) != element) {
            throw new IllegalArgumentException("Can not add objects twice");
        }
        this.set.remove(this.list.get(index));
        this.set.add(element);
        return this.list.set(index, element);
    }
}
