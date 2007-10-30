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


public class ListSetImplementation<E> extends AbstractList<E> implements ListSet<E> {
    private List<E> list;
    
    private static final class UnmodifiableListSet<E> implements ListSet<E> {
        private static final class UnmodifiableListIterator<E> implements ListIterator<E> {
            ListIterator<E> origIt;
            private UnmodifiableListIterator(ListIterator<E> i) {
                super();
                this.origIt = i;
            }
            public boolean hasNext()     {return origIt.hasNext();}
            public E next()         {return origIt.next();}
            public boolean hasPrevious() {return origIt.hasPrevious();}
            public E previous()     {return origIt.previous();}
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

        private ListSet<E> orig;
        
        public UnmodifiableListSet(ListSet<E> orig) {
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

        public E get(int index) {
            return this.orig.get(index);
        }

        public E remove(int index) {
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

        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection<?> c) {
            return this.orig.containsAll(c);
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public Iterator<E> iterator() {
            return new UnmodifiableListIterator<E>(this.orig.listIterator());
        }

        public List<E> subList(int fromIndex, int toIndex) {
            return Collections.unmodifiableList(orig.subList(fromIndex, toIndex));
        }

        public ListIterator<E> listIterator() {
            return new UnmodifiableListIterator<E>(this.orig.listIterator());
        }

        public ListIterator<E> listIterator(int index) {
            return new UnmodifiableListIterator<E>(this.orig.listIterator(index));
        }

        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }

        public <T> T[] toArray(T[] a) {
            return this.orig.toArray(a);
        }
    }
    
    public ListSetImplementation() {
        this.list = new ArrayList<E>();
    }
    
    public ListSetImplementation(Collection<E> other) {
        this.list = new ArrayList<E>();
        addAll(other);
    }
    
    @Override
	public int size() {
        return this.list.size();
    }

    @Override
	public void clear() {
        this.list.clear();
    }
    
    @Override
	public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
	public Object[] toArray() {
        return this.list.toArray();
    }

    @Override
	public boolean add(E o) {
        if(this.list.contains(o)) {
            return false;
        }
        this.list.add(o);
        return true;
    }

    @Override
	public E get(int index) {
        return this.list.get(index);
    }

    @Override
	public void add(int index, E element) {
        if(this.list.contains(element)) {
            this.list.remove(element);
        }
        this.list.add(index, element);
    }

    @Override
	public E remove(int index) {
        return this.list.remove(index);
    }

    @Override
	public E set(int index, E element) {
        if(this.list.contains(element) && this.list.get(index) != element) {
            throw new IllegalArgumentException("Can not add objects twice");
        }
        return this.list.set(index, element);
    }

    public static<T> ListSet<T> unmodifiableListSet(ListSet<T> set) {
        return new UnmodifiableListSet<T>(set);
    }
}
