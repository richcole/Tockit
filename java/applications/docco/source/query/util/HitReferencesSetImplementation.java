/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package query.util;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

import query.HitReference;

public class HitReferencesSetImplementation implements HitReferencesSet {
	private Set references;
	
	public HitReferencesSetImplementation() {
		this(new HashSet());
	}
	
	public HitReferencesSetImplementation(Set innerSet) {
		this.references = innerSet;
	}
	
	public int size() {
		return references.size();
	}

	public boolean isEmpty() {
		return references.isEmpty();
	}

	public boolean contains (HitReference ref) {
		return references.contains(ref);
	}

	public Iterator iterator() {
		return references.iterator();
	}

	public HitReference[] toArray() {
		int size = this.references.size();
		return (HitReference[]) references.toArray(new HitReference[size]);
	}

	public boolean addAll(HitReferencesSet other) {
		Iterator it = other.iterator();
		boolean setIsChanged = false;
		while (it.hasNext()) {
			HitReference cur = (HitReference) it.next();
			if (this.add(cur)) {
				setIsChanged = true;
			}
		}
		return setIsChanged;
	}

	public boolean retainAll(HitReferencesSet other) {
		return references.retainAll(other.toSet());
	}

	public boolean removeAll(HitReferencesSet other) {
		return references.removeAll(other.toSet());
	}

	public void clear() {
		references.clear();
	}

	public boolean add(HitReference ref) {
		return references.add(ref);
	}

	public boolean remove(HitReference ref) {
		return references.remove(ref);
	}
	
	public String toString() {
		String str = "HitReferenceSet: size = " + this.references.size() + "\n";
		Iterator it = this.references.iterator();
		while (it.hasNext()) {
			HitReference cur = (HitReference) it.next();
			str = str + "\t" + cur.getDocument().getField("path") + " (score: " + cur.getScore() + ")\n";
		}
		return str;
	}

    public Set toSet() {
        return this.references;
    }
}
