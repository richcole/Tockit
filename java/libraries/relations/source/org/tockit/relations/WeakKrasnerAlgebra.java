/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations;

import org.tockit.relations.model.Relation;
import org.tockit.relations.operations.UnionOperation;


public class WeakKrasnerAlgebra extends BasicRelationalAlgebra {
	public static<D> Relation<D> union(Relation<D> left, Relation<D> right) {
		return UnionOperation.unite(left, right);
	}
}
