/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class HitReferencesSetImplementation implements HitReferencesSet {
	private HashSet references = new HashSet();
	
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
		return (HitReference[]) references.toArray(new HitReference[0]);
	}

	public boolean addAll(Collection collection) {
		Iterator it = collection.iterator();
		boolean setIsChanged = false;
		while (it.hasNext()) {
			HitReference cur = (HitReference) it.next();
			if (this.add(cur)) {
				setIsChanged = true;
			}
		}
		return setIsChanged;
	}

	public boolean retainAll(Collection collection) {
		return references.retainAll(collection);
	}

	public boolean removeAll(Collection collection) {
		return references.removeAll(collection);
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
	

}
