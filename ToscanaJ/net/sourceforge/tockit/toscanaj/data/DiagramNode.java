package net.sourceforge.tockit.toscanaj.data;

import java.awt.geom.Point2D;

/**
 * Stores the information on a node in a diagram.
 *
 * This is mainly the position, the concept for the node and the information
 * on the labels attached to it.
 */
public class DiagramNode {

    /**
     * The size of a point.
     */
    private static final int RADIUS = 10;

    /**
     * Point2D hold the point for a node in the diagram
     */
    Point2D point2D = null;

    /**
     * The layout information for the attribute label.
     */
    private LabelInfo attributeLabel;

    /**
     * The layout information for the attribute label.
     */
    private LabelInfo objectLabel;

    /**
     * Construct a node at a point with two labels attached.
     *
     * The labels can be null if there is no label in this position.
     */
    public DiagramNode(Point2D point2D, LabelInfo attributeLabel, LabelInfo objectLabel){
        this.point2D = point2D;
        this.attributeLabel = attributeLabel;
        this.objectLabel = objectLabel;
    }

    /**
     * Get the current node position
     */
    public Point2D getPosition(){
        return point2D;
    }

    /**
     * Set the node position
     */
    public void setPoint(Point2D point2D){
       this.point2D = point2D;
    }

    /**
     * Get the x coordinate
     */
    public double getX() {
       return point2D.getX();
    }

    /**
     * Get the y coordinate
     */
    public double getY() {
        return point2D.getY();
    }

    /**
     * Get the radius set for the point.
     */
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Returns the layout information for the attribute label.
     *
     * This might be null if there is no label attached.
     */
    public LabelInfo getAttributeLabelInfo() {
        return this.attributeLabel;
    }

    /**
     * Returns the layout information for the object label.
     *
     * This might be null if there is no label attached.
     */
    public LabelInfo getObjectLabelInfo() {
        return this.objectLabel;
    }

    /**
     * Sets the layout information for the attribute label attached.
     */
    public void setAttributeLabelInfo(LabelInfo labelInfo) {
        this.attributeLabel = labelInfo;
    }

    /**
     * Sets the layout information for the object label attached.
     */
    public void setObjectLabelInfo(LabelInfo labelInfo) {
        this.objectLabel = labelInfo;
    }

    /**
     * Debug output.
     */
    public String toString() {
      return "X = " + point2D.getX() + " Y = " + point2D.getY();
    }
}