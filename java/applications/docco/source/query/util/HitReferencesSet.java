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

import query.HitReference;

public interface HitReferencesSet {
	public int size();
	public boolean isEmpty();
	public Iterator iterator();

	public boolean contains(HitReference ref);

	public HitReference[] toArray();

	public boolean add(HitReference ref);

	public boolean remove(HitReference ref);

	public boolean addAll(Collection collection);
	public boolean retainAll(Collection collection);
	public boolean removeAll(Collection collection);
	
	public void clear();
}
