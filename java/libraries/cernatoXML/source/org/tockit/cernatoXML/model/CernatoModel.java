/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

import java.util.Vector;

/**
 * @todo this is pretty much a hack since the class exposes its member in a writable fashion
 */
public class CernatoModel {
    private CernatoTable context = new CernatoTable();
    private Vector<TypeImplementation> types = new Vector<TypeImplementation>();
    private Vector<View> views = new Vector<View>();

    public CernatoModel() {
        // nothing to do
    }

    public CernatoTable getContext() {
        return context;
    }

    public Vector<TypeImplementation> getTypes() {
        return types;
    }

    public Vector<View> getViews() {
        return views;
    }
}
