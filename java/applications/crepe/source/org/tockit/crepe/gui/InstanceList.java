/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui;

import org.tockit.crepe.gui.datatransfer.*;
import org.tockit.crepe.gui.treeviews.RelationHierachyTreeNode;
import org.tockit.cgs.model.Type;
import org.tockit.cgs.model.Instance;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.dnd.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.*;
import java.io.IOException;

public class InstanceList extends JList  implements DropTargetListener, DragSourceListener, DragGestureListener{
    private DragSource dragSource;

    public InstanceList() {
        super();
        initializeDragAndDropMembers();
    }

    private void initializeDragAndDropMembers() {
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY, this);
    }

    public void dragEnter(DropTargetDragEvent dtde) {
    }

    public void dragOver(DropTargetDragEvent event) {
        if (true) {
            event.rejectDrag();
        } else {
            event.acceptDrag(DnDConstants.ACTION_COPY);
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void drop(DropTargetDropEvent event) {
        try {
            Transferable transferable = event.getTransferable();
            if (transferable.isDataFlavorSupported(CGFlavors.TypeFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Type type = (Type) transferable.getTransferData(CGFlavors.TypeFlavor);
                System.out.println(type.getName());
                event.getDropTargetContext().dropComplete(true);
            } else {
                event.rejectDrop();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            event.rejectDrop();
        } catch (UnsupportedFlavorException ufException) {
            ufException.printStackTrace();
            event.rejectDrop();
        }
    }

    public void dragEnter(DragSourceDragEvent dsde) {
    }

    public void dragOver(DragSourceDragEvent dsde) {
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    public void dragExit(DragSourceEvent dse) {
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
    }

    public void dragGestureRecognized(DragGestureEvent event) {
        Point dragOrigin = event.getDragOrigin();
        int itemNum = this.locationToIndex(dragOrigin);
        Instance instance = (Instance) this.getModel().getElementAt(itemNum);
        InstanceTransferable transferable = new InstanceTransferable(instance);
        dragSource.startDrag(event, DragSource.DefaultCopyDrop, transferable, this);
    }
}
