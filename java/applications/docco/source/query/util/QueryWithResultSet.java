/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package query.util;

import java.util.Collection;
import java.util.Iterator;

import query.QueryWithResult;

public interface QueryWithResultSet {
	public int size();
	public boolean isEmpty();
	public Iterator iterator();

	public boolean contains(QueryWithResult qwr);

	public QueryWithResult[] toArray();

	public boolean add(QueryWithResult qwr);

	public boolean remove(QueryWithResult qwr);

	public boolean addAll(Collection collection);
	public boolean retainAll(Collection collection);
	public boolean removeAll(Collection collection);
	
	public void clear();
}
