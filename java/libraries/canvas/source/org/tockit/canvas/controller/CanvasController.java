/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.controller;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Timer;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemActivatedEvent;
import org.tockit.canvas.events.CanvasItemClickedEvent;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemMouseExitEvent;
import org.tockit.canvas.events.CanvasItemMouseMovementEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.canvas.events.CanvasItemMouseEnterEvent;
import org.tockit.events.EventBroker;

public class CanvasController implements MouseListener, MouseMotionListener {

    private Canvas canvas;

    /**
     * @todo this is ugly: we can't properly type the broker since events can be either about
     *       the canvas (such as CanvasDrawnEvent) or the items on it
     */
    private EventBroker<Object> eventBroker;

    /**
     * Flag to prevent canvas item from being moved when just clicked on
     */
    private boolean dragMode = false;

    /**
     * This is true if a popup might have been opened as reaction on a mouse press
     * event.
     */
    private boolean popupOpen = false;
    
    /**
     * True iff the last mouse down was the primary mouse button.
     */
    private boolean buttonOnePressed = false;

    /**
     * Distance that label has to be moved to enable dragMode
     */
    private int dragMin = 5;

    /**
     * The position where the mouse was when the last event came.
     */
    private Point2D lastMousePos = null;

    /**
     * A timer to distinguish between single and double clicks.
     */
    private Timer doubleClickTimer = null;

    /**
     * Holds the selected CanvasItem
     * that the user has clicked on with intent to move
     */
    private CanvasItem selectedCanvasItem = null;

    /**
     * Holds last pointed CanvasItem
     * (last canvas item that has been pointed at with a mouse)
     */
    private CanvasItem pointedCanvasItem = null;

    public CanvasController(Canvas canvas, EventBroker<Object> eventBroker) {
        this.canvas = canvas;
        this.eventBroker = eventBroker;
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    /**
     * Not used -- mouse clicks are handled as press/release combinations.
     */
    public void mouseClicked(MouseEvent e) {
        // not used
    }

    /**
     * Handles mouse release events.
     *
     * Resets the diagram from dragging mode back into normal mode or calls
     * singleClicked() or doubleClicked() on the CanvasItem hit. If no item was hit
     * backgroundSingleClicked() or backgroundDoubleClicked() on the canvas is
     * called.
     *
     * singleClicked() will only be send if it is not a double click. In any case
     * clicked() or backgroundClicked() will be send.
     *
     * @todo Use system double click timing instead of hard-coded 300ms
     */
    public void mouseReleased(MouseEvent e) {
        if (popupOpen) {
            popupOpen = false;
            return; // nothing to do, we react only on normal clicks
        }
        if(this.selectedCanvasItem == null) {
        	return;
        }
        Point screenPos = e.getPoint();
        if (e.isPopupTrigger()) {
            Point2D canvasPos = canvas.getCanvasCoordinates(screenPos);
            handlePopupRequest(e.getModifiers(), canvasPos, screenPos);
            popupOpen = true;
        }
        if (dragMode) {
            dragMode = false;
            dragFinished(e);
            canvas.repaint();
        } else {
            Point2D modelPos = null;
            modelPos = canvas.getCanvasCoordinates(screenPos);
            this.eventBroker.processEvent(
                    new CanvasItemClickedEvent(this.selectedCanvasItem,
                                               e.getModifiers(), modelPos, screenPos));
			if(this.buttonOnePressed) {
	            if (e.getClickCount() == 1) {
	                this.doubleClickTimer = new Timer();
	                this.doubleClickTimer.schedule(
	                        new CanvasItemSingleClickTask(this.selectedCanvasItem,
	                                e.getModifiers(), modelPos, screenPos, eventBroker), 300);
	            } else if (e.getClickCount() == 2) {
	                this.doubleClickTimer.cancel();
	                this.eventBroker.processEvent(
	                        new CanvasItemActivatedEvent(selectedCanvasItem,
	                                                     e.getModifiers(), modelPos, screenPos));
	            }
            }
        }
        selectedCanvasItem = null;
    }

    protected void dragFinished(MouseEvent e) {
        Point mousePos = e.getPoint();
        Point2D mousePosTr = findCanvasPositionOnGrid(mousePos);
        Point2D lastMousePosTr = findCanvasPositionOnGrid(lastMousePos);
        this.eventBroker.processEvent(new CanvasItemDroppedEvent(
                this.selectedCanvasItem,
                e.getModifiersEx(),
                lastMousePosTr, lastMousePos,
                mousePosTr, mousePos));
    }

    protected Point2D findCanvasPositionOnGrid(Point2D mousePos) {
        Point2D mousePosTr = canvas.getCanvasCoordinates(mousePos);
        if(canvas.hasGridEnabled()) {
        	return canvas.getClosestPointOnGrid(mousePosTr);
        } else {
        	return mousePosTr;
        }
    }

    public void mouseEntered(MouseEvent e) {
    	mouseMoved(e);
    }

    public void mouseExited(MouseEvent e) {
		this.pointedCanvasItem = null;
		hideMouseFromItems(true);
    }

    /**
     * Handles dragging the canvas items.
     */
    public void mouseDragged(MouseEvent e) {
        if (selectedCanvasItem == null) {
            return;
        }
        Point mousePos = e.getPoint();
        if (!canvas.contains(mousePos)) {
            return;
        }
        boolean newDrag = false;
        if (!dragMode && (lastMousePos.distance(mousePos) >= dragMin)) {
            dragMode = true;
            newDrag = true;
        }
        if (dragMode) {
            Point2D mousePosTr = findCanvasPositionOnGrid(mousePos);
            Point2D lastMousePosTr = findCanvasPositionOnGrid(lastMousePos);
            if (newDrag) {
                this.eventBroker.processEvent(new CanvasItemPickupEvent(
                        this.selectedCanvasItem,
                        e.getModifiersEx(),
                        lastMousePosTr, lastMousePos,
                        mousePosTr, mousePos));
            } else {
                this.eventBroker.processEvent(new CanvasItemDraggedEvent(
                        this.selectedCanvasItem,
                        e.getModifiersEx(),
                        lastMousePosTr, lastMousePos,
                        mousePosTr, mousePos));
            }
            lastMousePos = mousePos;
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (dragMode) {
            return;
        }
        Point2D mousePos = e.getPoint();
        Point2D canvasPos = canvas.getCanvasCoordinates(mousePos);
        CanvasItem pointedItem = canvas.getCanvasItemAt(canvasPos);
        if (pointedItem == null) {
            return;
        }
        this.eventBroker.processEvent(new CanvasItemMouseMovementEvent(
                                            pointedItem, e.getModifiers(),
                                            canvasPos, mousePos));
        if (this.pointedCanvasItem != pointedItem) {
			this.eventBroker.processEvent(new CanvasItemMouseEnterEvent(
												pointedItem, e.getModifiers(),
												canvasPos, mousePos));
			if(this.pointedCanvasItem != null) {
				this.eventBroker.processEvent(new CanvasItemMouseExitEvent(
													this.pointedCanvasItem, e.getModifiers(),
													canvasPos, mousePos));
			}
            this.pointedCanvasItem = pointedItem;
        }
    }

    /**
     * Finds, raises and stores the canvas item hit.
     */
    public void mousePressed(MouseEvent e) {
        Point screenPos = e.getPoint();
        Point2D canvasPos = canvas.getCanvasCoordinates(screenPos);
        this.selectedCanvasItem = canvas.getCanvasItemAt(canvasPos);
        if (this.selectedCanvasItem.hasAutoRaise()) {
            canvas.raiseItem(this.selectedCanvasItem);
        }

        int onMask = InputEvent.BUTTON1_DOWN_MASK;
        int offMask = InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK |
                      InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK |
                      InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
        this.buttonOnePressed = (e.getModifiersEx() & (onMask | offMask)) == onMask;
        this.lastMousePos = screenPos;
        if (e.isPopupTrigger()) {
            handlePopupRequest(e.getModifiers(), canvasPos, screenPos);
        }
        this.popupOpen = e.isPopupTrigger();
    }

    private void handlePopupRequest(int modifiers, Point2D canvasPos, Point screenPos) {
        this.eventBroker.processEvent(new CanvasItemContextMenuRequestEvent(
                this.selectedCanvasItem, modifiers, canvasPos, screenPos));
    }
    
	public EventBroker<Object> getEventBroker() {
		return eventBroker;
	}

	public void hideMouseFromItems(boolean hide) {
		if(this.pointedCanvasItem != null) {
			if(hide) {
				this.eventBroker.processEvent(new CanvasItemMouseExitEvent(
													this.pointedCanvasItem, 0,
													null, null));
			} else {
				/// @todo see if we can find the mouse position here
				this.eventBroker.processEvent(new CanvasItemMouseEnterEvent(
													this.pointedCanvasItem, 0,
													null, null));
			}
		}
	}
}
