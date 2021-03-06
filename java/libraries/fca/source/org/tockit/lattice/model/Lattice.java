/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.lattice.model;


public interface Lattice<O,A> {
    Concept<O,A>[] getConcepts();

    Concept<O,A> getTop();

    Concept<O,A> getBottom();
}
