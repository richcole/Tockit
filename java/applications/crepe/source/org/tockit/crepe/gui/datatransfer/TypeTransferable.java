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

import org.tockit.cgs.model.Type;

public class TypeTransferable implements Transferable {
    private Type type;

    public TypeTransferable(Type type) {
        this.type = type;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{CGFlavors.TypeFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.getRepresentationClass() == Type.class;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if(flavor.getRepresentationClass() == Type.class) {
            return type;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
