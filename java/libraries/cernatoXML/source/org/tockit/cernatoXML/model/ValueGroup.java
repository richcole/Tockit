/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

public interface ValueGroup {
    String getName();
    boolean containsValue(AttributeValue value);
    boolean isSuperSetOf(ValueGroup otherColumn);
    boolean isLesserThan(ValueGroup other);
    boolean isEqual(ValueGroup other);
}
