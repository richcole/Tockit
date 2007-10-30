/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations;

import org.tockit.relations.model.Relation;
import org.tockit.relations.operations.util.AbstractUnaryRelationOperation;

public class IdentityOperation<D> extends AbstractUnaryRelationOperation<D> {
	@Override
	public Relation<D> doApply(Relation<D> input) {
		return input;
	}

	public String getName() {
		return "Identity";
	}
}
