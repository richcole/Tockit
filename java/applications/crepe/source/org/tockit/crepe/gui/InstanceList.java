/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.JList;

import org.tockit.cgs.model.Instance;
import org.tockit.cgs.model.Type;
import org.tockit.crepe.gui.datatransfer.CGFlavors;
import org.tockit.crepe.gui.datatransfer.InstanceTransferable;

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
    	// nothing to do
    }

    public void dragOver(DropTargetDragEvent event) {
        if (true) {
            event.rejectDrag();
        } else {
            event.acceptDrag(DnDConstants.ACTION_COPY);
        }
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    	// nothing to do
    }

    public void dragExit(DropTargetEvent dte) {
    	// nothing to do
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
    	// nothing to do
    }

    public void dragOver(DragSourceDragEvent dsde) {
    	// nothing to do
    }

    public void dropActionChanged(DragSourceDragEvent dsde) {
    	// nothing to do
    }

    public void dragExit(DragSourceEvent dse) {
    	// nothing to do
    }

    public void dragDropEnd(DragSourceDropEvent dsde) {
    	// nothing to do
    }

    public void dragGestureRecognized(DragGestureEvent event) {
        Point dragOrigin = event.getDragOrigin();
        int itemNum = this.locationToIndex(dragOrigin);
        Instance instance = (Instance) this.getModel().getElementAt(itemNum);
        InstanceTransferable transferable = new InstanceTransferable(instance);
        dragSource.startDrag(event, DragSource.DefaultCopyDrop, transferable, this);
    }
}
