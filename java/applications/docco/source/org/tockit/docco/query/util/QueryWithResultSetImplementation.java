/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.query.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.tockit.docco.query.QueryWithResult;



public class QueryWithResultSetImplementation implements QueryWithResultSet {
	private HashSet qwrSet = new HashSet();
	
	public int size() {
		return qwrSet.size();
	}

	public boolean isEmpty() {
		return qwrSet.isEmpty();
	}

	public boolean contains (QueryWithResult qwr) {
		return qwrSet.contains(qwr);
	}

	public Iterator iterator() {
		return qwrSet.iterator();
	}

	public QueryWithResult[] toArray() {
		int size = this.qwrSet.size();
		return (QueryWithResult[]) qwrSet.toArray(new QueryWithResult[size]);
	}

	public boolean addAll(Collection collection) {
		Iterator it = collection.iterator();
		boolean setIsChanged = false;
		while (it.hasNext()) {
			QueryWithResult cur = (QueryWithResult) it.next();
			if (this.add(cur)) {
				setIsChanged = true;
			}
		}
		return setIsChanged;
	}

	public boolean retainAll(Collection collection) {
		return qwrSet.retainAll(collection);
	}

	public boolean removeAll(Collection collection) {
		return qwrSet.removeAll(collection);
	}

	public void clear() {
		qwrSet.clear();
	}

	public boolean add(QueryWithResult qwr) {
		return qwrSet.add(qwr);
	}

	public boolean remove(QueryWithResult qwr) {
		return qwrSet.remove(qwr);
	}
	
	public String toString() {
		String str = "QueryWithResult: size = " + this.qwrSet.size() + "\n";
		Iterator it = this.qwrSet.iterator();
		while (it.hasNext()) {
			QueryWithResult cur = (QueryWithResult) it.next();
			str = str + "\t" + cur;
		}
		return str;
	}
	

}
