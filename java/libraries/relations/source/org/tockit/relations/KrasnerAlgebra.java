/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations;

import java.util.Set;

import org.tockit.relations.model.Relation;
import org.tockit.relations.operations.NegationOperation;


public class KrasnerAlgebra extends WeakKrasnerAlgebra {
	public static<D> Relation<D> complement(Relation<D> input, Set<D>[] domains) {
		return NegationOperation.negate(input, domains);
	}
}
