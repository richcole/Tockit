/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

/**
 * @todo this is copied code from ToscanaJ (net.sf.tj.model). In the end this
 * package and TJ should use a common Tockit library.
 */
public interface BinaryRelation {
    boolean contains(Object domainObject, Object rangeObject);
}
