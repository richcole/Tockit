/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui.treeviews;

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

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.tockit.cgs.model.Type;
import org.tockit.crepe.gui.datatransfer.CGFlavors;
import org.tockit.crepe.gui.datatransfer.RelationTransferable;

public class RelationHierarchyTreeView extends JTree implements DropTargetListener, DragSourceListener, DragGestureListener {

    private DragSource dragSource = null;

    public RelationHierarchyTreeView() {
        super();
        initializeDragAndDropMembers();
    }

    public RelationHierarchyTreeView(TreeModel newModel) {
        super(newModel);
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
            if (transferable.isDataFlavorSupported(CGFlavors.RelationFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Type type = (Type) transferable.getTransferData(CGFlavors.RelationFlavor);
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
        TreePath path = this.getPathForLocation((int)dragOrigin.getX(), (int)dragOrigin.getY());
        if(path == null) {
            return;
        }
        Object lastPathComponent = path.getLastPathComponent();
        if(!(lastPathComponent instanceof RelationHierachyTreeNode)) {
            // should not happen
            return;
        }
        RelationHierachyTreeNode node = (RelationHierachyTreeNode) lastPathComponent;
        RelationTransferable transferable = new RelationTransferable(node.getRelation());
        dragSource.startDrag(event, DragSource.DefaultCopyDrop, transferable, this);
    }
}
