/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui.datatransfer;

import org.tockit.cgs.model.Instance;

import java.awt.datatransfer.*;
import java.io.IOException;

public class InstanceTransferable implements Transferable {
    private Instance instance;

    public InstanceTransferable(Instance instance) {
        this.instance = instance;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{CGFlavors.InstanceFlavor};
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.getRepresentationClass() == Instance.class;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if(flavor.getRepresentationClass() == Instance.class) {
            return instance;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}