package net.sourceforge.tockit.toscanaj.diagram;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import net.sourceforge.tockit.toscanaj.data.LabelInfo;
import net.sourceforge.tockit.toscanaj.diagram.ScalingInfo;
import net.sourceforge.tockit.toscanaj.data.Diagram;

/**
 * This class encapsulates all label drawing code.
 */
public class LabelView extends JComponent implements LabelObserver
{
    /**
     * Label current width
     */
    private double lw;
    /**
     * Label current height
     */
    private double lh;
    /**
     * Label current x coordinate
     */
    private double lx;
    /**
     * Label current y coordinate
     */
    private double ly;

    /**
     * Used when the label should be drawn above the given point.
     *
     * See Draw( Graphics2D, double ,double ,int ).
     */
    public static final int ABOVE = 0;

    /**
     * Used when the label should be drawn below the given point.
     *
     * See Draw( Graphics2D, double ,double ,int ).
     */
    public static final int BELOW = 1;

    /**
     * The label information that should be drawn.
     */
    private LabelInfo _labelInfo;

    /**
     * Store the diagram view that the label belongs to
     */
    private DiagramView diagramView = null;

    /**
     * Creates a view for the given label information.
     */
    public LabelView( DiagramView diagramView, LabelInfo label ) {
        this.diagramView = diagramView;
        _labelInfo = label;
        _labelInfo.addObserver(this);    }

    /**
     * Update label view as label info has change
     */
    public void diagramChanged(){
      diagramView.updateAllObservers();
    }

    /**
     * Return Label width
     */
    public double getLabelWidth() {
      return lw;
    }
    /**
     * Return Label height
     */
    public double getLabelHeight() {
      return lh;
    }
    /**
     * Return label x coordinate
     */
    public double getLabelX() {
      return lx;
    }
    /**
     * Return label y coordinate
     */
    public double getLabelY() {
      return ly;
    }

    /**
     * Draws the label at the given position in the graphic context.
     *
     * The position is placed above or below the label, horizontally centered
     * plus the offset from the LabelInfo.
     *
     * The placement should be either LabelView::ABOVE or LabelView::BELOW.
     * A dashed line will be drawn from the central top/bottom point to the
     * given point.
     *
     * The scaling information is needed to scale the offset.
     */
    public void draw( Graphics2D graphics, ScalingInfo scInfo,
                      double x, double y, int placement )
    {
        // if no entries are there, just do nothing
        if( _labelInfo.getNumberOfEntries() == 0 )
        {
            return;
        }

        // remember some settings to restore them later
        Paint oldPaint = graphics.getPaint();

        // get the font metrics
        FontMetrics fm = graphics.getFontMetrics();

        // find the size and position
        lw = getWidth( fm );
        lh = getHeight( fm );
        lx = x - lw/2 + scInfo.scaleX( _labelInfo.getOffset().getX() );
        if( placement == ABOVE )
        {
            ly = y - lh + scInfo.scaleY( _labelInfo.getOffset().getY() );
        }
        else
        {
            ly = y + scInfo.scaleY( _labelInfo.getOffset().getY() );
        }
        // draw a dashed line from the given point to the calculated
        Stroke oldStroke = graphics.getStroke();
        float[] dashstyle = { 4, 4 };
        graphics.setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_BEVEL, 1, dashstyle, 0 ) );
        graphics.draw( new Line2D.Double( x, y, lx + lw/2, ly + lh ) );
        graphics.setStroke( oldStroke );

        // draw the label itself
        graphics.setPaint( _labelInfo.getBackgroundColor() );
        graphics.fill( new Rectangle2D.Double( lx, ly, lw, lh ) );
        graphics.setPaint( _labelInfo.getTextColor() );
        graphics.draw( new Rectangle2D.Double( lx, ly, lw, lh ) );

        // draw the text
        if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNLEFT )
        {
            for( int j = 0; j < _labelInfo.getNumberOfEntries(); j++ )
            {
                graphics.drawString( _labelInfo.getEntry( j ),
                           (int) lx + fm.getLeading() + fm.getDescent(),
                           (int) ly + fm.getAscent() + fm.getLeading() +
                                      j * fm.getHeight() );
            }
        }
        else if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNCENTER )
        {
            for( int j = 0; j < _labelInfo.getNumberOfEntries(); j++ )
            {
                graphics.drawString( _labelInfo.getEntry( j ),
                           (int) (lx + fm.getLeading()/2 + fm.getDescent()/2 +
                                    ( lw - fm.stringWidth(
                                              _labelInfo.getEntry( j ) ) )/2 ),
                           (int) ly + fm.getAscent() + fm.getLeading() +
                                      j * fm.getHeight() );
            }
        }
        else if( _labelInfo.getTextAlignment() == LabelInfo.ALIGNRIGHT )
        {
            for( int j = 0; j < _labelInfo.getNumberOfEntries(); j++ )
            {
                graphics.drawString( _labelInfo.getEntry( j ),
                           (int) (lx - fm.getLeading() - fm.getDescent() + lw -
                                  fm.stringWidth( _labelInfo.getEntry( j ) ) ),
                           (int) ly + fm.getAscent() + fm.getLeading() +
                                      j * fm.getHeight() );
            }
        }

        // restore old settings
        graphics.setPaint( oldPaint );
    }

    /**
     * Calculates the width of the label given a specific font metric.
     *
     * The width is calculated as the maximum string width plus two times the
     * leading and the descent from the font metrics. When drawing the text the
     * horizontal position should be the left edge of the label plus one times
     * thetwo values (FontMetrics::getLeading() and FontMetrics::getDescent()).
     */
    public double getWidth( FontMetrics fontMetrics )
    {
        double result = 0;

        // find maximum width of string
        for( int i = 0; i < _labelInfo.getNumberOfEntries(); i++ )
        {
            double w = fontMetrics.stringWidth( _labelInfo.getEntry( i ) );
            if( w > result )
            {
                result = w;
            }
        }

        // add two leadings and two descents to have some spacing on the left
        // and right side
        result += 2 * fontMetrics.getLeading() + 2 * fontMetrics.getDescent();

        return result;
    }

    /**
     * Calculates the height of the label given a specific font metric.
     */
    public int getHeight( FontMetrics fontMetrics )
    {
        return _labelInfo.getNumberOfEntries() * fontMetrics.getHeight();
    }
}