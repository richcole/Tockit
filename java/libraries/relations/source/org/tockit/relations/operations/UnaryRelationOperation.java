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
 * An operation taking one relation as input.
 */
public interface UnaryRelationOperation extends RelationOperation {
	/**
	 * A convenience method for applying the operation straigth to a single relation.
	 * 
	 * Equivalent to calling apply(new Relation[]{input}).
	 * 
	 * @see apply(Relation[])
	 */
	Relation apply(Relation input);
	
	/**
	 * Creates a new operator applying this one to the result of the other.
	 * 
	 * @param other another unary relation operation, not null
	 * @throws NullPointerException iff other is null
	 */
	UnaryRelationOperation concatenate(UnaryRelationOperation other);
}
