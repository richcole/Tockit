/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui.datatransfer;

import org.tockit.cgs.model.*;

import java.awt.datatransfer.DataFlavor;

public class CGFlavors {
    public static final DataFlavor TypeFlavor = new DataFlavor(Type.class, "Type");
    public static final DataFlavor RelationFlavor = new DataFlavor(Relation.class, "Relation");
    public static final DataFlavor InstanceFlavor = new DataFlavor(Instance.class, "Instance");

    private CGFlavors() {
    }
}
