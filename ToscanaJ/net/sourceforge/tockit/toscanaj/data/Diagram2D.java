package net.sourceforge.tockit.toscanaj.data;

import net.sourceforge.tockit.toscanaj.diagram.DiagramNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Interface for getting diagram related information.
 */

public interface Diagram2D
{
    /**
     * Returns the title of the diagram.
     */
    public String getTitle();

    /**
     * Returns the number of points in the diagram.
     */
    public int getNumberOfPoints();

    /**
     * Returns the number of lines in the diagram.
     */
    public int getNumberOfLines();

    /**
     * Calculates a rectangle that includes all points.
     */
    public Rectangle2D getBounds();

    /**
     * Returns the coordinates of one point.
     *
     * Numbers start with zero.
     */
    public Point2D getPoint( int pointNumber );

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getFromPoint( int lineNumber );

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getToPoint( int lineNumber );

    /**
     * Returns the information on the object label of the diagram.
     */
    public LabelInfo getObjectLabel( int pointNumber );

    /**
     * Returns the information on the attribute label of the diagram.
     */
    public LabelInfo getAttributeLabel( int pointNumber );
}