package net.sourceforge.tockit.toscanaj.diagram;

import net.sourceforge.tockit.toscanaj.canvas.CanvasItem;
import net.sourceforge.tockit.toscanaj.gui.ToscanajGraphics2D;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * class DiagramNode holds details on node position and size
 */

public class DiagramNode extends CanvasItem {

  /**
   * The size of a point.
   */
  private static final int RADIUS = 10;

  /**
   * Point2D hold the point for a node in the diagram
   */
  Point2D point2D = null;

  /**
   * set node to default position
   */
  public DiagramNode() {
    point2D = new Point2D.Double(0,0);
  }

  /**
   * Construct a node at a point
   */
  public DiagramNode(Point2D point2D){
    this.point2D = point2D;
  }

  /**
   * get the current node position
   */
  public Point2D getPoint(){
    return point2D;
  }
  /**
   * set the node position
   */
  public void setPoint(Point2D point2D){
    this.point2D = point2D;
  }

  /**
   * get the x coordinate
   */
  public double getX() {
    return point2D.getX();
  }

  /**
   * get the y coordinate
   */
  public double getY() {
    return point2D.getY();
  }

  public double getRadius() {
    return RADIUS;
  }

  /**
   * return the coordinates of this node
   */
  public String toString() {
    return "X = " + point2D.getX() + " Y = " + point2D.getY();
  }

  public void draw(ToscanajGraphics2D g) {
    g.drawEllipse2D(point2D, RADIUS);
  }

    /**
     * Returns always false at the moment.
     *
     * @TODO: implement correct behaviour.
     */
    public boolean containsPoint(Point2D point) {
        return false;
    }
}