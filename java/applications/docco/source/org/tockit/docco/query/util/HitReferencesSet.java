/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.query.util;

import java.util.Iterator;
import java.util.Set;

import org.tockit.docco.query.HitReference;


public interface HitReferencesSet {
	public int size();
	public boolean isEmpty();
	public Iterator iterator();
	public Set toSet();

	public boolean contains(HitReference ref);

	public HitReference[] toArray();

	public boolean add(HitReference ref);

	public boolean remove(HitReference ref);

	public boolean addAll(HitReferencesSet other);
	public boolean retainAll(HitReferencesSet other);
	public boolean removeAll(HitReferencesSet other);
	
	public void clear();
}
