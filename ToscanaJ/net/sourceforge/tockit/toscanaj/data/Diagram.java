package net.sourceforge.tockit.toscanaj.data;

import net.sourceforge.tockit.toscanaj.diagram.DiagramObserver;
import net.sourceforge.tockit.toscanaj.gui.MainPanel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * This class is an abstraction of all diagram related information.
 */

public class Diagram implements DiagramObservable, Diagram2D
{
    /**
     * The list of objects currently observing changes.
     */
    private List diagramObserver = null;

    /**
     * The title used for this diagram.
     */
    private String title;

    /**
     * The list of nodes in the diagram.
     */
    private List nodes;

    /**
     * The list of starting points of lines in the diagram.
     *
     * This has to be of the same size as lineEndPoints and has to refer to
     * ints matching the numbers used in the nodes list.
     */
    private List lineStartPoints;

    /**
     * The list of ending points of lines in the diagram.
     *
     * This has to be of the same size as lineStartPoints and has to refer to
     * ints matching the numbers used in the nodes vector.
     */
    private List lineEndPoints;

    /**
     * The default constructor creates a diagram with just nothing in it at all.
     */
    public Diagram() {
        title = "";
        nodes = new LinkedList();
        lineStartPoints = new LinkedList();
        lineEndPoints = new LinkedList();
        diagramObserver = new LinkedList();
    }

    /**
     * Method to add observer
     */
    public void addObserver(DiagramObserver observer) {
        this.diagramObserver.add(observer);
    }

    /**
     * Send to all obvservers that a change has been made
     */
    public void emitChangeSignal() {
        Iterator iterator = diagramObserver.iterator();
        while(iterator.hasNext()){
            ((DiagramObserver)iterator.next()).diagramChanged();
        }
    }

    /**
     * Returns the title of the diagram.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Change the title of the diagram.
     */
    public void setTitle( String title ) {
        this.title = title;
    }

    /**
     * Returns the number of nodes in the diagram.
     */
    public int getNumberOfNodes() {
        return this.nodes.size();
    }

    /**
     * Returns the number of lines in the diagram.
     */
    public int getNumberOfLines() {
        return this.lineStartPoints.size();
    }

    /**
     * Calculates a rectangle that includes all points.
     */
    public Rectangle2D getBounds() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for( int i = 0; i < this.nodes.size(); i++ ) {
            Point2D p = ((DiagramNode)this.nodes.get( i )).getPosition();
            double x = p.getX();
            double y = p.getY();

            if( x < minX ) {
                minX = x;
            }
            if( x > maxX ) {
                maxX = x;
            }
            if( y < minY ) {
                minY = y;
            }
            if( y > maxY ) {
                maxY = y;
            }
        }
        return new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
    }

    /**
     * Returns a node in the diagram.
     *
     * Numbers start with zero.
     */
    public DiagramNode getNode( int nodeNumber ) {
        return (DiagramNode)this.nodes.get( nodeNumber );
    }

    /**
     * Adds a point to the diagram (at the end of the list).
     */
    public void addNode( Point2D position ) {
        this.nodes.add(new DiagramNode(position,null,null));
    }

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getFromPosition( int lineNumber ) {
        Integer num = (Integer)this.lineStartPoints.get( lineNumber );
        DiagramNode node = (DiagramNode) this.nodes.get(num.intValue());
        return node.getPosition();
    }

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getToPosition( int lineNumber ) {
        Integer num = (Integer)this.lineEndPoints.get( lineNumber );
        DiagramNode node = (DiagramNode) this.nodes.get(num.intValue());
        return node.getPosition();
    }

    /**
     * Adds a line to the diagram (at the end of the list).
     *
     * The from and to parameters are assumed to refer to some points already
     * existing in the points list (not checked yet).
     */
    public void addLine( int from, int to ) {
        this.lineStartPoints.add( new Integer(from) );
        this.lineEndPoints.add( new Integer(to) );
    }

    /**
     * Returns the information on the object label of the diagram.
     */
    public LabelInfo getObjectLabel( int nodeNumber ) {
        return ((DiagramNode)this.nodes.get(nodeNumber)).getObjectLabelInfo();
    }

    /**
     * Returns the information on the attribute label of the diagram.
     */
    public LabelInfo getAttributeLabel( int nodeNumber ) {
        return ((DiagramNode)this.nodes.get(nodeNumber)).getAttributeLabelInfo();
    }
}