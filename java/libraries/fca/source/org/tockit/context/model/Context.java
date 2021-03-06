/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.context.model;

import java.util.Set;

public interface Context<O,A> {
	String getName();
	
    Set<O> getObjects();

    Set<A> getAttributes();

    BinaryRelation<O,A> getRelation();
}
