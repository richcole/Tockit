/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class ListSetImplementation extends AbstractList implements ListSet {
    private List list;
    
    private static final class UnmodifiableListSet implements ListSet {
        private static final class UnmodifiableListIterator implements ListIterator {
            ListIterator origIt;
            private UnmodifiableListIterator(ListIterator i) {
                super();
                this.origIt = i;
            }
            public boolean hasNext()     {return origIt.hasNext();}
            public Object next()         {return origIt.next();}
            public boolean hasPrevious() {return origIt.hasPrevious();}
            public Object previous()     {return origIt.previous();}
            public int nextIndex()       {return origIt.nextIndex();}
            public int previousIndex()   {return origIt.previousIndex();}
            public void remove() {
                throw new UnsupportedOperationException();
            }
            public void set(Object o) {
                throw new UnsupportedOperationException();
            }
            public void add(Object o) {
                throw new UnsupportedOperationException();
            }
        }

        private ListSet orig;
        
        public UnmodifiableListSet(ListSet orig) {
            if(orig == null) {
                throw new NullPointerException("Wrapped ListSet must not be null");
            }
            this.orig = orig;
        }
        
        public int size() {
            return this.orig.size();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean isEmpty() {
            return this.orig.isEmpty();
        }

        public Object[] toArray() {
            return this.orig.toArray();
        }

        public Object get(int index) {
            return this.orig.get(index);
        }

        public Object remove(int index) {
            throw new UnsupportedOperationException();
        }

        public void add(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        public int indexOf(Object o) {
            return this.orig.indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return this.orig.lastIndexOf(o);
        }

        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean contains(Object o) {
            return this.orig.contains(o);
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(int index, Collection c) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection c) {
            return this.orig.containsAll(c);
        }

        public boolean removeAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection c) {
            throw new UnsupportedOperationException();
        }

        public Iterator iterator() {
            return new UnmodifiableListIterator(this.orig.listIterator());
        }

        public List subList(int fromIndex, int toIndex) {
            return Collections.unmodifiableList(orig.subList(fromIndex, toIndex));
        }

        public ListIterator listIterator() {
            return new UnmodifiableListIterator(this.orig.listIterator());
        }

        public ListIterator listIterator(int index) {
            return new UnmodifiableListIterator(this.orig.listIterator(index));
        }

        public Object set(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        public Object[] toArray(Object[] a) {
            return this.orig.toArray(a);
        }
    }
    
    public ListSetImplementation() {
        this.list = new ArrayList();
    }
    
    public int size() {
        return this.list.size();
    }

    public void clear() {
        this.list.clear();
    }
    
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public Object[] toArray() {
        return this.list.toArray();
    }

    public boolean add(Object o) {
        if(this.list.contains(o)) {
            throw new IllegalArgumentException("Can not add objects twice");
        }
        this.list.add(o);
        return true;
    }

    public Object get(int index) {
        return this.list.get(index);
    }

    public void add(int index, Object element) {
        if(this.list.contains(element)) {
            throw new IllegalArgumentException("Can not add objects twice");
        }
        this.list.add(index, element);
    }

    public Object remove(int index) {
        return this.list.remove(index);
    }

    public Object set(int index, Object element) {
        if(this.list.contains(element) && this.list.get(index) != element) {
            throw new IllegalArgumentException("Can not add objects twice");
        }
        return this.list.set(index, element);
    }

    public static ListSet unmodifiableListSet(ListSet set) {
        return new UnmodifiableListSet(set);
    }
}
