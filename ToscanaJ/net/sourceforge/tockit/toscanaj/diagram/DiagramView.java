package net.sourceforge.tockit.toscanaj.diagram;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.tockit.toscanaj.canvas.CanvasItem;
import net.sourceforge.tockit.toscanaj.data.Diagram;
import net.sourceforge.tockit.toscanaj.data.Diagram2D;
import net.sourceforge.tockit.toscanaj.data.LabelInfo;
import net.sourceforge.tockit.toscanaj.diagram.LabelView;
import net.sourceforge.tockit.toscanaj.gui.MainPanel;
import net.sourceforge.tockit.toscanaj.gui.ToscanajGraphics2D;


/**
 * This class paints a diagram defined by the Diagram class.
 */

public class DiagramView extends JComponent implements MouseListener, MouseMotionListener, DiagramObserver
{
    /**
     * A list of all canvas items to draw.
     */
    List canvasItems;

    ToscanajGraphics2D tg = null;

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
    private Diagram2D _diagram = null;

    /**
     * Holds the LabelView for selected label view
     * that the user has clicked on with intent to move
     */
    private LabelView selectedLabel = null;

    /**
     * Flag to prevent label from being moved when just clicked on
     */
    private boolean dragMode = false;

    /**
     * Distance that label has to be moved to enable dragMode
     */
    private int dragMin = 5;

    /**
     * The position where the mouse was when the last event came.
     */
    private Point2D lastMousePos = null;

    /**
     * Creates a new vew displaying an empty digram (i.e. nothing at all).
     */
    public DiagramView()
    {
        addMouseListener(this);
        addMouseMotionListener(this);
   }

    /**
    * method to notify observer that a change has been made
    */
    public void diagramChanged(){
      repaint();
    }

    /**
    * Method called by LabelView to update all observers
    */
    public void updateAllObservers() {
      ((Diagram)_diagram).emitChangeSignal();
    }

    /**
     * Paints the diagram on the screen.
     */
    public void paintComponent( Graphics g )
    {
        if( _diagram == null ) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g;

        // calculate paintable area
        Insets insets = getInsets();
        int x = getX() + insets.left + MARGIN;
        int y = getY() + insets.top + MARGIN;
        int h = getHeight() - insets.left - insets.right - 2 * MARGIN;
        int w = getWidth() - insets.top - insets.bottom - 2 * MARGIN;

        // check if there is enough left to paint on, otherwise abort
        if( ( h < RADIUS ) || ( w < RADIUS ) ) {
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
        if( _diagram.getNumberOfPoints() > 0 ) {
            if( diagBounds.getY() < _diagram.getPoint(0).getY() ) {
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
        //store updated ToscanajGraphics2D
        tg = new ToscanajGraphics2D(g2d, new Point2D.Double( xOrigin, yOrigin ), xScale, yScale );
        // paint all items on canvas
        Iterator it = this.canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem) it.next();
            cur.draw(tg);
        }
    }

    // mouse listeners for diagram events
    // Example moving labels

    public void mouseClicked(MouseEvent e){
      //System.out.println("mouseClicked");
    }

    public void mouseReleased(MouseEvent e) {
        dragMode = false;
        selectedLabel = null;
    }
    public void mouseEntered(MouseEvent e) {
      //System.out.println("mouseEntered");
    }
    public void mouseExited(MouseEvent e) {
      //System.out.println("mouseExited");
    }

    // Mouse Motion Listener for label drag events

    public void mouseDragged(MouseEvent e) {
        if(selectedLabel != null && (dragMode || ((getDistance(lastMousePos.getX(), lastMousePos.getY(), e.getX(), e.getY()) >= dragMin)))) {
            selectedLabel.moveBy(tg.inverseScaleX(e.getX() - lastMousePos.getX()),
                                 tg.inverseScaleY(e.getY() - lastMousePos.getY()));
            lastMousePos = new Point2D.Double(e.getX(), e.getY());
            dragMode = true;
        }
    }

    public void mouseMoved(MouseEvent e) {
      //System.out.println("mouseMoved");
    }

    public void mousePressed(MouseEvent e) {
        ListIterator it = this.canvasItems.listIterator(this.canvasItems.size());
        while(it.hasPrevious()) {
            CanvasItem cur = (CanvasItem) it.previous();
            Point2D point = this.tg.inverseProject(e.getPoint());
            if(cur.containsPoint(point)) {
                if(cur instanceof LabelView) {
                    // store the information needed for moving the label
                    this.selectedLabel = (LabelView) cur;
                    this.lastMousePos = e.getPoint();
                    // raise the label
                    it.remove();
                    this.canvasItems.add(cur);
                    break;
                }
            }
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
        ((Diagram)_diagram).addObserver(this);
        this.canvasItems = new LinkedList();
        // add all lines to the canvas
        for( int i = 0; i < _diagram.getNumberOfLines(); i++ )
        {
            this.canvasItems.add( new DiagramLine( _diagram.getFromPoint( i ), _diagram.getToPoint( i )) );
        }
        // add all points and labels to the canvas
        for( int i = 0; i < _diagram.getNumberOfPoints(); i++ )
        {
            DiagramNode node = new DiagramNode(_diagram.getPoint(i));
            this.canvasItems.add( node );
            this.canvasItems.add( new LabelView( this, node, LabelView.ABOVE, _diagram.getAttributeLabel( i ) ) );
            this.canvasItems.add( new LabelView( this, node, LabelView.BELOW, _diagram.getObjectLabel( i ) ) );
        }
       repaint();
    }
}