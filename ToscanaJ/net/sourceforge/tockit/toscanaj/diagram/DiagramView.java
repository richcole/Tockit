package net.sourceforge.tockit.toscanaj.diagram;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import net.sourceforge.tockit.toscanaj.data.Diagram;
import net.sourceforge.tockit.toscanaj.data.LabelInfo;
import net.sourceforge.tockit.toscanaj.diagram.LabelView;
import net.sourceforge.tockit.toscanaj.diagram.ScalingInfo;
import net.sourceforge.tockit.toscanaj.gui.MainPanel;

/**
 * This class paints a diagram defined by the Diagram class.
 */

public class DiagramView extends JComponent implements MouseListener, MouseMotionListener
{
    /**
     * Holds sacaling info. Is updated on each redraw
     */
    ScalingInfo si;

    /**
     * The size of a point.
     */
    private final int RADIUS = 10;

    /**
     * This is a generic margin used for all four edges.
     *
     * The margin should be big enough to allow a RADIUS to lap over.
     */
    private final int MARGIN = 80;

    /**
     * The diagram to display.
     */
    Diagram _diagram;

    /**
     * Creates a new vew displaying an empty digram (i.e. nothing at all).
     */
    public DiagramView()
    {
        _diagram = new Diagram();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Paints the diagram on the screen.
     */
    public void paintComponent( Graphics g )
    {
        Graphics2D g2d = (Graphics2D) g;

        // calculate paintable area
        Insets insets = getInsets();
        int x = getX() + insets.left + MARGIN;
        int y = getY() + insets.top + MARGIN;
        int h = getHeight() - insets.left - insets.right - 2 * MARGIN;
        int w = getWidth() - insets.top - insets.bottom - 2 * MARGIN;

        // check if there is enough left to paint on, otherwise abort
        if( ( h < RADIUS ) || ( w < RADIUS ) )
        {
            return;
        }

        // draw diagram title in the top left corner
        g2d.drawString( _diagram.getTitle(), x - MARGIN/2, y - MARGIN/2 );

        // get the dimensions of the diagram
        Rectangle2D diagBounds = _diagram.getBounds();

        // check if the y-coordinate in the file is denoted in math-style, i.e.
        // up is positive
        // invY is one unless this is the case, then invY will be minus one
        // invY has to be used wherever Y coordinates from the diagram are used
        // in combination with absolute Y coordinates like e.g. the predefined
        // RADIUS of the points
        int invY = 1;
        if( _diagram.getNumberOfPoints() > 0 )
        {
            if( diagBounds.getY() < _diagram.getPoint(0).getY() )
            {
                invY = -1;
            }
        }

        // we need some values to do the projection -- the initial values are
        // centered and no size change. This is useful if the diagram has no
        // extent in a direction
        double xOrigin = x + w/2;
        double yOrigin = y + h/2;
        double xScale = 1;
        double yScale = 1;

        // adjust the scaling values if the diagram has extent in a dimension
        // and move the top/left edge of the diagram to the top/left edge of
        // the painting area. If the diagram has no extent in one direction
        // there will be no scaling and it will be placed centered
        /** @TODO change this to calculate the lable sizes/offsets into the
         *  scaling */
        if( diagBounds.getWidth() != 0 )
        {
            xScale = w / diagBounds.getWidth();
            xOrigin = x - diagBounds.getX() * xScale;
        }
        if( diagBounds.getHeight() != 0 && _diagram.getNumberOfPoints() != 0 )
        {
            yScale = h / diagBounds.getHeight() * invY;
            yOrigin = y - _diagram.getPoint(0).getY() * yScale;
        }

        // store the scaling information for further use
        si = new ScalingInfo(
                     new Point2D.Double( xOrigin, yOrigin ), xScale, yScale );

        // paint all lines
        for( int i = 0; i < _diagram.getNumberOfLines(); i++ )
        {
            Point2D pf = _diagram.getFromPoint( i );
            Point2D pt = _diagram.getToPoint( i );

            g2d.draw( new Line2D.Double( si.projectX( pf.getX() ),
                                         si.projectY( pf.getY() ),
                                         si.projectX( pt.getX() ),
                                         si.projectY( pt.getY() ) ) );
        }
        // paint all points and labels
        for( int i = 0; i < _diagram.getNumberOfPoints(); i++ )
        {
            Point2D p = _diagram.getPoint(i);

            double px = si.projectX( p.getX() );
            double py = si.projectY( p.getY() );

            // paint the point
            g2d.fill( new Ellipse2D.Double( px - RADIUS, py - RADIUS,
                                            RADIUS * 2, RADIUS * 2 ) );

            LabelView label = new LabelView( _diagram.getAttributeLabel( i ) );
            label.draw( g2d, si, px, py + RADIUS * invY, LabelView.ABOVE );
            label = new LabelView( _diagram.getObjectLabel( i ) );
            label.draw( g2d, si, px, py - RADIUS * invY, LabelView.BELOW );
        }
    }

    // mouse listeners for diagram events
    // Example moving labels

    public void mouseClicked(MouseEvent e){
      //System.out.println("mouseClicked");
    }

    public void mouseReleased(MouseEvent e) {
      //System.out.println("mouseReleased");
      if(labelSelected == true && dragMode == true) {
        dragMode = false;
        labelSelected = false;
      }
    }
    public void mouseEntered(MouseEvent e) {
      //System.out.println("mouseEntered");
    }
    public void mouseExited(MouseEvent e) {
      //System.out.println("mouseExited");
    }

    // Mouse Motion Listener for label drag events
    LabelInfo li = null;
    boolean labelSelected = false;
    boolean dragMode = false;
    Point2D lastPoint = null;
    int dragMin = 10;

    public void mouseDragged(MouseEvent e) {
      if((getDistance(lastPoint.getX(), lastPoint.getY(), e.getX(), e.getY()) >= dragMin) && labelSelected == true) {
        li.setOffset(new Point2D.Double(li.getOffset().getX() -
                        si.inverseScaleX(lastPoint.getX() - e.getX()),
                        li.getOffset().getY() -
                        si.inverseScaleY(lastPoint.getY() - e.getY())
                      ));
        lastPoint = new Point2D.Double(e.getX(), e.getY());
        repaint();
        dragMode = true;
      }
    }

    public void mouseMoved(MouseEvent e) {
      //System.out.println("mouseMoved");
    }
    public void mousePressed(MouseEvent e) {
      int index = 0;
      double min = getDistance(0, 0, getWidth(), getHeight());
      double distNode = 0;
      double x, y;
      String type = "";
      Point2D node, found = null;
      LabelInfo objectLabel, attributeLabel;
      lastPoint = new Point2D.Double(e.getX(), e.getY());
      //if (e.getClickCount() == 2){
        for( int i = 0; i < _diagram.getNumberOfPoints(); i++ ) {
          node = si.project(_diagram.getPoint(i));
          distNode = getDistance(e.getX(), e.getY(), node.getX(), node.getY());
          if(distNode < min) {
            min = distNode;
            found = node;
            index = i;
            type = "Node";
          }
          objectLabel = _diagram.getObjectLabel(i);
          x = e.getX() - objectLabel.labelX;
          y = e.getY() - objectLabel.labelY;
          if(!(x < 0 || y < 0)  && (x <= objectLabel.labelWidth) && (y <= objectLabel.labelHeight)  ) {
            System.out.println("\nObject label clicked on");
            for(int o = 0; o < objectLabel.getNumberOfEntries(); o++) {
              System.out.println("objectLabels " + objectLabel.getEntry(o));
            }
            li = objectLabel;
            labelSelected = true;
            return;
          }
          attributeLabel = _diagram.getAttributeLabel(i);
          x = e.getX() - attributeLabel.labelX;
          y = e.getY() - attributeLabel.labelY;
          if(!(x < 0 || y < 0)  && (x <= attributeLabel.labelWidth) && (y <= attributeLabel.labelHeight)  ) {
            System.out.println("\nAttribute labelLabel clicked on");
            for(int a = 0; a < attributeLabel.getNumberOfEntries(); a++) {
              System.out.println("objectLabels " + attributeLabel.getEntry(a));
            }
            li = attributeLabel;
            labelSelected = true;
            return;
          }
        }
        labelSelected = false;
        if(found != null && MainPanel.debug) {
          System.out.println("\nx y " + e.getX() + " " + e.getY());
          System.out.println("Clostest point is " + found);
          System.out.println("Type is " + type);
          System.out.println("min dist " + min);
        }
    }

    private void printLabelInfo(int index) {
      Point2D p;
      LabelInfo objectLabel = _diagram.getObjectLabel(index);
      LabelInfo attributeLabel = _diagram.getAttributeLabel(index);
      p = si.project(objectLabel.getOffset());
      System.out.println("objectLabels offsets" + p);
      for(int o = 0; o < objectLabel.getNumberOfEntries(); o++) {
        System.out.println("objectLabels " + objectLabel.getEntry(o));
      }
      p = si.project(attributeLabel.getOffset());
      System.out.println("attributeLabel offsets" + p);
      for(int a = 0; a < attributeLabel.getNumberOfEntries(); a++) {
        System.out.println("objectLabels " + attributeLabel.getEntry(a));
      }
    }

    private double getDistance(double x1, double y1, double x2, double y2){
      return Math.abs(Math.sqrt(sqr(x2 - x1) + sqr(y2 - y1)));
    }

    private double sqr(double x) {
      return x * x;
    }

    /**
     * Sets the given diagram as new diagram to display.
     *
     * This will automatically cause a repaint of the view.
     */
    public void showDiagram( Diagram diagram )
    {
        _diagram = diagram;
        repaint();
    }
}