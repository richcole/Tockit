package net.sourceforge.tockit.toscanaj.diagram;

import net.sourceforge.tockit.toscanaj.canvas.CanvasItem;
import net.sourceforge.tockit.toscanaj.gui.ToscanajGraphics2D;

import java.awt.geom.Point2D;

/**
 * class DiagramLine draws a line between two points
 */

import java.awt.Graphics2D;

public class DiagramLine extends CanvasItem {

  /**
   * Holds the coordinants of the first node in a Diagram
   */
  Point2D fromPoint;

  /**
   * Holds the coordinants of the second node in a Diagram
   */
  Point2D toPoint;

  /**
   * Creates default DiagramLine, with fromPoint and toPoint variables holding 0 values
   */
  public DiagramLine() {
    fromPoint = new Point2D.Double(0, 0);
    toPoint = new Point2D.Double(0, 0);
  }

  /**
   * Creates DiagramLine object, initialising fromPoint and toPoint variables to required values
   * indicating positions of 2 nodes in a Diagram
   */
  public DiagramLine(Point2D fromPoint, Point2D toPoint) {
    this.fromPoint = fromPoint;
    this.toPoint = toPoint;
  }

  /**
   * Returns coordinates of first node in a Diagram in which DiagramLine object will be connecting
   */
  public Point2D getFromPoint() {
    return this.fromPoint;

  }

  /**
   * Returns coordinates of second node in a Diagram in which DiagramLine object will be connecting
   */
  public Point2D getToPoint() {
    return this.toPoint;
  }

  /**
   * Sets the fromPoint at the position of a node on a Diagram
   */
  public void setFromPoint(Point2D fromPoint) {
    this.fromPoint = fromPoint;
  }

  /**
   * Sets the toPoint at the position of a node on a Diagram
   */
  public void setToPoint(Point2D toPoint) {
    this.toPoint = toPoint;
  }

  /**
   * Returns the X coordinate of the fromPoint variable
   */
  public double getFromPointX() {
    return this.fromPoint.getX();
  }

  /**
   * Returns the Y coordinate of the fromPoint variable
   */
  public double getFromPointY() {
    return this.fromPoint.getY();
  }

  /**
   * Returns the X coordinate of the toPoint variable
   */
  public double getSecondPointX() {
    return this.toPoint.getX();
  }

  /**
   * Returns the Y coordinate of the toPoint variable
   */
  public double getSecondPointY() {
    return this.toPoint.getY();
  }

  public void draw(ToscanajGraphics2D g) {
    g.drawLine(this.fromPoint, this.toPoint);
  }

    /**
     * Returns always false since we assume the line to have no width.
     */
    public boolean containsPoint(Point2D point) {
        return false;
    }
}