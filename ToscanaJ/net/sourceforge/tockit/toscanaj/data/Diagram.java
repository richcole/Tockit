package net.sourceforge.tockit.toscanaj.data;

import net.sourceforge.tockit.toscanaj.diagram.DiagramObserver;
import net.sourceforge.tockit.toscanaj.diagram.DiagramNode;
import net.sourceforge.tockit.toscanaj.gui.MainPanel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Enumeration;
import java.util.*;

/**
 * This class is an abstraction of all diagram related information.
 */

public class Diagram implements DiagramObservable, Diagram2D
{
    /**
     * vector holds all observers
     */
    private Vector diagramObserver = null;

    /**
     * The title used for this diagram.
     */
    private String _title;

    /**
     * The list of points in the diagram.
     */
    private Vector _points;

    /**
     * The list of object labels.
     *
     * The order has to be the same as in _points.
     */
    private Vector _objectLabels;

    /**
     * The list of attribute labels.
     *
     * The order has to be the same as in _points.
     */
    private Vector _attributeLabels;

    /**
     * The list of starting points of lines in the diagram.
     *
     * This has to be of the same size as _lineEndPoints and has to refer to
     * ints matching the numbers used in the _points vector.
     */
    private Vector _lineStartPoints;

    /**
     * The list of ending points of lines in the diagram.
     *
     * This has to be of the same size as _lineSatrtPoints and has to refer to
     * ints matching the numbers used in the _points vector.
     */
    private Vector _lineEndPoints;

    /**
     * The default constructor creates a diagram with just nothing in it at all.
     */
    public Diagram()
    {
        _title = "";
        _points = new Vector();
        _attributeLabels = new Vector();
        _objectLabels = new Vector();
        _lineStartPoints = new Vector();
        _lineEndPoints = new Vector();
        diagramObserver = new Vector();
    }

    /**
     * Method to add observer
     */
    public void addObserver(DiagramObserver observer){
        this.diagramObserver.addElement(observer);
    }

    /**
     * Send to all obvservers that a change has been made
     */
    public void emitChangeSignal(){
        Iterator iterator = diagramObserver.iterator();
        while(iterator.hasNext()){
            ((DiagramObserver)iterator.next()).diagramChanged();
        }
    }

    /**
     * Returns the title of the diagram.
     */
    public String getTitle()
    {
        return _title;
    }

    /**
     * Change the title of the diagram.
     */
    public void setTitle( String title )
    {
        _title = title;
    }

    /**
     * Returns the number of points in the diagram.
     */
    public int getNumberOfPoints()
    {
        return _points.size();
    }

    /**
     * Returns the number of lines in the diagram.
     */
    public int getNumberOfLines()
    {
        return _lineStartPoints.size();
    }

    /**
     * Calculates a rectangle that includes all points.
     */
    public Rectangle2D getBounds()
    {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for( int i = 0; i < _points.size(); i++ )
        {
            Point2D p = (Point2D) _points.get( i );
            double x = p.getX();
            double y = p.getY();

            if( x < minX )
            {
                minX = x;
            }
            if( x > maxX )
            {
                maxX = x;
            }
            if( y < minY )
            {
                minY = y;
            }
            if( y > maxY )
            {
                maxY = y;
            }
        }
        return new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
    }

    /**
     * Returns the coordinates of one point.
     *
     * Numbers start with zero.
     */
    public Point2D getPoint( int pointNumber )
    {
        return (Point2D)_points.get( pointNumber );
    }

    /**
     * Adds a point to the diagram (at the end of the list).
     */
    public void addPoint( Point2D point )
    {
        _points.add(point);
        _objectLabels.add( new LabelInfo() );
        _attributeLabels.add( new LabelInfo() );
    }

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getFromPoint( int lineNumber )
    {
        Integer num = (Integer)_lineStartPoints.get( lineNumber );
        return (Point2D)_points.get( num.intValue() );
    }

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getToPoint( int lineNumber )
    {
        Integer num = (Integer)_lineEndPoints.get( lineNumber );
        return (Point2D)_points.get( num.intValue() );
    }

    /**
     * Adds a line to the diagram (at the end of the list).
     *
     * The from and to parameters are assumed to refer to some points already
     * existing in the points list (not checked yet).
     */
    public void addLine( int from, int to )
    {
        _lineStartPoints.add( new Integer(from) );
        _lineEndPoints.add( new Integer(to) );
    }

    /**
     * Returns the information on the object label of the diagram.
     */
    public LabelInfo getObjectLabel( int pointNumber )
    {
        return (LabelInfo)_objectLabels.get( pointNumber );
    }

    /**
     * Returns the information on the attribute label of the diagram.
     */
    public LabelInfo getAttributeLabel( int pointNumber )
    {
        return (LabelInfo)_attributeLabels.get( pointNumber );
    }
}