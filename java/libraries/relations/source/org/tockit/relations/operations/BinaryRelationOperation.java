/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations;

import org.tockit.relations.model.Relation;

/**
 * An operation taking two relations as input.
 */
public interface BinaryRelationOperation extends RelationOperation {
	/**
	 * A convenience method for applying the operation straigth to two relations.
	 * 
	 * Equivalent to calling apply(new Relation[]{leftHandInput, rightHandInput}).
	 * 
	 * @see apply(Relation[])
	 */
	Relation apply(Relation leftHandInput, Relation rightHandInput);
	
	/**
	 * Creates a new operator applying this one to the results of the unary ones.
	 * 
	 * @param leftOperation a unary relation operation, not null
	 * @param rightOperation a unary relation operation, not null
	 * @throws NullPointerException iff either input is null
	 */
	BinaryRelationOperation concatenate(UnaryRelationOperation leftOperation, UnaryRelationOperation rightOperation);
}
