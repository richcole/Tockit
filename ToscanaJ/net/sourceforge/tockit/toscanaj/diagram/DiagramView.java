package net.sourceforge.tockit.toscanaj.diagram;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import net.sourceforge.tockit.toscanaj.data.Diagram;
import net.sourceforge.tockit.toscanaj.data.LabelInfo;
import net.sourceforge.tockit.toscanaj.diagram.LabelView;
import net.sourceforge.tockit.toscanaj.diagram.ScalingInfo;

/**
 * This class paints a diagram defined by the Diagram class.
 */

public class DiagramView extends JComponent
{
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
        ScalingInfo si = new ScalingInfo(
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