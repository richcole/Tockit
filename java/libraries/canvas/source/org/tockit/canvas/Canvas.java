/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.JPanel;

import org.tockit.canvas.controller.CanvasController;
import org.tockit.canvas.exceptions.IllegalLayerNameException;
import org.tockit.canvas.exceptions.NoSuchLayerException;
import org.tockit.events.EventBroker;

/**
 * A generic drawing canvas with z-order and controller structure.
 *
 * This class does implement a generic drawing canvas with a background
 * object, arbitrary CanvasItem objects on top of that which have z-order
 * and a controller object which handles the AWT/Swing mouse events and
 * maps them to canvas-specific callbacks and events.
 *
 * All drawing and related features like printing or graphic export are
 * handled by the canvas, too.
 *
 * @todo Move some more of the drawing code from ToscanaJ's DiagramView into
 *       this  class, add options for automatic rescaling or not.
 *
 * @todo Add zooming/panning options.
 * 
 * @todo add layer reordering
 * 
 * @todo add access to the layer names (it might be a good idea to introduce a
 *       layer  object for this)
 * 
 * @todo add tests for this class
 */

public class Canvas extends JPanel implements Printable {
    /**
     * This is the background item which is assumed to be wherever no other item is.
     */
    private CanvasBackground background = new CanvasBackground();

    /**
     * A list of all canvas layers to draw on top of the background.
     * 
     * A layer itself is a list of items to draw. This list has to be in synch
     * with the layerNameMapping, but we need both to ensure the proper order of
     * the layers.
     */
    protected List canvasLayers = new ArrayList();
    
    /**
     * Stores the names of the layers as a mapping into the layer objects.
     * 
     * The empty string is used to refer to an automatically created upper layer
     * (created when an object is added but no layer exists -- this ensures
     * backwards compatibility), otherwise each layer has to have its unique
     * name.
     */
    protected Hashtable layerNameMapping = new Hashtable();

	/**
	 * These items are marked to be raised.
	 * 
	 * Raisal is postponed to avoid ConcurrentModificationExceptions. Items are
	 * raised within their layer, they don't cross layers.
	 * 
	 * @todo we need the same for addition
	 */
    protected List itemsToRaise = new ArrayList();

    /**
     * Stores the transformation matrix we used on the last draw event.
     *
     * This is used to map mouse positions to the canvas coordinates.
     */
    private AffineTransform screenTransform = new AffineTransform();

    /**
     * Keeps the size of the canvas.
     *
     * This is done to avoid resizing while the mouse is dragged.
     */
    private Rectangle2D canvasSize = null;

    /**
     * The controller caring about the event handling and callbacks.
     */
    private CanvasController controller = null;

    /**
     * Creates a new, empty canvas with a new controller attached to it.
     */
    public Canvas(EventBroker eventBroker) {
        this.controller = new CanvasController(this, eventBroker);
    }

    /**
     * Returns the controller object for this canvas.
     */
    public CanvasController getController() {
        return controller;
    }

    /**
     * Paints the canvas including all CanvasItems on it.
     */
    public void paintCanvas(Graphics2D graphics) {
        /// @todo isn't that superflous?
        this.background.draw(graphics);
        raiseMarkedItems();
        // paint all items on canvas
        Iterator layerIt = this.canvasLayers.iterator();
        while (layerIt.hasNext()) {
        	Collection layer = (Collection) layerIt. next();
        	Iterator itemIt = layer.iterator();
        	while(itemIt.hasNext()) {
		        CanvasItem cur = (CanvasItem) itemIt.next();
		        cur.draw(graphics);
        	}
        }
    }

	/**
	 * Raises all items that have been requested to raise.
	 * 
	 * This might happen delayed since we can't change the item lists while we
	 * are still iterating over it (e.g. a raisal while drawing would cause
	 * ConcurrentModificationExceptions).
	 */
    private void raiseMarkedItems() {
        for (Iterator iterator = itemsToRaise.iterator(); iterator.hasNext();) {
            CanvasItem canvasItem = (CanvasItem) iterator.next();
            Iterator layerIt = this.canvasLayers.iterator();
            while (layerIt.hasNext()) {
                Collection layer = (Collection) layerIt. next();
                if(layer.contains(canvasItem)) {
		            layer.remove(canvasItem);
		            layer.add(canvasItem);
                }
            }
        }
        this.itemsToRaise.clear();
    }

    /**
     * Returns the canvas item representing the background.
     *
     * The background can not be raised and it covers the whole area of the
     * canvas but otherwise it can be treated as any other item on the canvas.
     */
    public CanvasBackground getBackgroundItem() {
        return background;
    }

    /**
     * Changes the transformation used for displaying the canvas on screen.
     */
    public void setScreenTransform(AffineTransform transform) {
        this.screenTransform = transform;
    }

    /**
     * Returns the transformation currently used for displaying the canvas on the
     * screen.
     */
    public AffineTransform getScreenTransform() {
        return screenTransform;
    }


    /**
     * Calculates the size of this canvas on a specific drawing context.
     *
     * This is the smallest upright rectangle that contains all canvas items.
     */
    public Rectangle2D getCanvasSize(Graphics2D graphics) {
    	Rectangle2D retVal = null;
        Iterator layerIt = this.canvasLayers.iterator();
		while(layerIt.hasNext()) {
			Collection layer = (Collection) layerIt.next();
			Iterator itemIt = layer.iterator();        
	        while (itemIt.hasNext()) {
	            CanvasItem cur = (CanvasItem) itemIt.next();
	            if(retVal == null) {
	                retVal = cur.getCanvasBounds(graphics);
	            } else {
	            	retVal = retVal.createUnion(cur.getCanvasBounds(graphics));
	            }
	        }
		}
        if (retVal == null) {
            return new Rectangle2D.Double(0, 0, 0, 0);
        }
        return retVal;
    }

    /**
     * This prints the canvas onto the printer defined by the graphic context.
     *
     * The canvas will always be scaled to fit on the page while being as large
     * as possible.
     *
     * @todo Add other printing options.
     *
     * @see Printable.print(Graphics, PageFormat, int).
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        if (pageIndex == 0) {
            Graphics2D graphics2D = (Graphics2D) graphics;

            Rectangle2D bounds = new Rectangle2D.Double(
                    pageFormat.getImageableX(),
                    pageFormat.getImageableY(),
                    pageFormat.getImageableWidth(),
                    pageFormat.getImageableHeight());
            AffineTransform transform = scaleToFit(graphics2D, bounds);
            graphics2D.transform(transform);
            // paint all items on canvas
            paintCanvas(graphics2D);

            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

    /**
     * Scales the graphic context in which the canvas items will be completely
     * visible in the rectangle.
     */
    public AffineTransform scaleToFit(Graphics2D graphics2D, Rectangle2D bounds) {
        this.canvasSize = this.getCanvasSize(graphics2D);

        // we need some values to do the projection -- the initial values are
        // centered and no size change. This is useful if the canvas has no
        // extent in a direction
        double xOrigin = bounds.getX() + bounds.getWidth() / 2;
        double yOrigin = bounds.getY() + bounds.getHeight() / 2;
        double xScale = 1;
        double yScale = 1;

        if (canvasSize.getWidth() != 0) {
            xScale = bounds.getWidth() / canvasSize.getWidth();
            xOrigin = bounds.getX() / xScale - canvasSize.getX();
        }
        if (canvasSize.getHeight() != 0) {
            yScale = bounds.getHeight() / canvasSize.getHeight();
            yOrigin = bounds.getY() / yScale - canvasSize.getY();
        }

        // scale proportionally, add half of the possible difference to the offset to center
        if ((canvasSize.getWidth() != 0) && (canvasSize.getHeight() != 0)) {
            if (xScale > yScale) {
                xScale = yScale;
                xOrigin = xOrigin + (bounds.getWidth() / xScale - canvasSize.getWidth()) / 2;
            } else {
                yScale = xScale;
                yOrigin = yOrigin + (bounds.getHeight() / yScale - canvasSize.getHeight()) / 2;
            }
        }
        AffineTransform transform = AffineTransform.getScaleInstance(xScale, yScale);
        transform.concatenate(AffineTransform.getTranslateInstance(xOrigin, yOrigin));
        return transform;
    }

    /**
     * Gives the uppermost item at the given position.
     *
     * This will return the background object if there is no other item at this position.
     * If multiple items are overlapping, the highest one will be returned.
     */
    public CanvasItem getCanvasItemAt(Point2D point) {
        ListIterator layerIt = this.canvasLayers.listIterator(this.canvasLayers.size());
        while(layerIt.hasPrevious()) {
        	List layer = (List) layerIt.previous();
	        ListIterator itemIt = layer.listIterator(layer.size());
	        while (itemIt.hasPrevious()) {
	            CanvasItem cur = (CanvasItem) itemIt.previous();
	            if (cur.containsPoint(point)) {
	                return cur;
	            }
	        }
        }
        return background;
    }

    /**
     * Returns all canvas items at the given point except the background.
     */
    public Collection getCanvasItemsAt(Point2D point) {
        Collection retVal = new HashSet();
        ListIterator layerIt = this.canvasLayers.listIterator(this.canvasLayers.size());
        while(layerIt.hasPrevious()) {
            List layer = (List) layerIt.next();
            ListIterator itemIt = layer.listIterator(layer.size());
            while (itemIt.hasPrevious()) {
                CanvasItem cur = (CanvasItem) itemIt.previous();
                if (cur.containsPoint(point)) {
                    retVal.add(cur);
                }
            }
        }
        return retVal;
    }

	/**
	 * Makes the given item the uppermost in its layer.
	 */
    public void raiseItem(CanvasItem item) {
        // can not be done here since this would easily cause ConcurrentModificationExceptions whenever someone iterates
        // over the items and calls this
        this.itemsToRaise.add(item);
    }

    /**
     * Maps the given point from screen coordinates into the canvas coordinates.
     */
    public Point2D getCanvasCoordinates(Point2D screenPos) {
        Point2D point = null;
        try {
            point = this.screenTransform.inverseTransform(screenPos, null);
        } catch (Exception ex) {
            //this should not happen
            ex.printStackTrace();
            throw new RuntimeException("Internal error: noninvertible transformation found.");
        }
        return point;
    }

    /**
     * Removes all canvas layers and items from the canvas (except the
     * background).
     */
    public void clearCanvas() {
        this.canvasLayers.clear();
    }

    /**
     * Adds a canvas item to the uppermost layer in the canvas.
     *
     * It will appear on top of all other items in the highest layer.
     */
    public void addCanvasItem(CanvasItem node) {
    	if(this.canvasLayers.isEmpty()) {
    		List newLayer = new ArrayList();
    		this.canvasLayers.add(newLayer);
    		this.layerNameMapping.put("", newLayer);
    	}
    	List uppermostLayer = (List) this.canvasLayers.get(this.canvasLayers.size() - 1);
        uppermostLayer.add(node);
    }
    
    /**
     * Adds a canvas item to a specific layer.
     * 
     * The item will be the uppermost item in the layer.
     */
    public void addCanvasItem(CanvasItem node, String layerName) {
    	List layer = (List) layerNameMapping.get(layerName);
    	if(layer == null) {
    		throw new NoSuchLayerException("Could not find layer with name \"" + layerName + "\"");
    	}
        layer.add(node);
    }

    /**
     * Removes an item from the canvas.
     */
    public void removeCanvasItem(CanvasItem item) {
        ListIterator layerIt = this.canvasLayers.listIterator(this.canvasLayers.size());
        while(layerIt.hasPrevious()) {
            List layer = (List) layerIt.next();
            layer.remove(item);
        }
        this.itemsToRaise.remove(item);
    }

	/**
	 * Returns a collection of all canvas items of a specific type.
	 * 
	 * Every layer is searched for items of this type and the matches are
	 * returned as a set.
	 */
    public Collection getCanvasItemsByType(Class type) {
        Set retVal = new HashSet();
        Iterator layerIt = this.canvasLayers.iterator();
        while(layerIt.hasNext()) {
            List layer = (List) layerIt.next();
	        for (Iterator iterator = layer.iterator(); iterator.hasNext();) {
	            CanvasItem canvasItem = (CanvasItem) iterator.next();
	            if( type.isAssignableFrom(canvasItem.getClass()) ) {
	                retVal.add(canvasItem);
	            }
	        }
        }
        return retVal;
    }
    
    /**
     * Creates a new layer with the given name.
     * 
     * The new layer will be the uppermost one in the canvas. The layer name
     * must not be null nor empty (otherwise an IllegalLayerNameException will
     * be thrown).
     */
    public void addLayer(String layerName) {
    	if(layerName == null) {
    		throw new IllegalLayerNameException("Layer name must be given to create a new layer");
    	}
    	if(layerName.length() == 0) {
    		throw new IllegalLayerNameException("Layer name must not be empty");
    	}
    	List newLayer = new ArrayList();
    	this.canvasLayers.add(newLayer);
    	this.layerNameMapping.put(layerName, newLayer);
    }
    
    /**
     * Removes a whole layer from the canvas.
     */
    public void removeLayer(String layerName) {
    	if(!this.layerNameMapping.containsKey(layerName)) {
    		throw new NoSuchLayerException("Could not find layer named \"" + layerName + "\"");
    	}
    	Object removeLayer = this.layerNameMapping.remove(layerName);
    	this.canvasLayers.remove(removeLayer);
    }
}
