/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.tockit.cgs.model.Relation;

public class RelationTransferable implements Transferable {
    private Relation relation;

    public RelationTransferable(Relation relation) {
        this.relation = relation;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{CGFlavors.RelationFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.getRepresentationClass() == Relation.class;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if(flavor.getRepresentationClass() == Relation.class) {
            return relation;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
