package net.sourceforge.tockit.toscanaj.data;

import net.sourceforge.tockit.toscanaj.data.LabelObservable;
import net.sourceforge.tockit.toscanaj.gui.MainPanel;
import net.sourceforge.tockit.toscanaj.diagram.LabelObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * This class encapsulates all information needed to paint a label.
 */

public class LabelInfo implements LabelObservable
{
    //
    private Vector labelObservers = null;

    /**
     * Method to add observer
     */
    public void addObserver(LabelObserver observer){
        this.labelObservers.addElement(observer);
    }

    /**
     * Notifies all observes about a change.
     */
    private void emitChangeSignal(){
        if(labelObservers != null){
            Iterator iterator = labelObservers.iterator();
            while(iterator.hasNext()) {
                ((LabelObserver)iterator.next()).diagramChanged();
            }
        }
    }

    /**
     * The list of entries in the label.
     *
     * These are just plain String instances.
     */
    private Vector _entries;

    /**
     * The offset for the label position.
     */
    private Point2D _offset;

    /**
     * The background color for the label.
     */
    private Color _backgroundColor;

    /**
     * The background color for the label.
     */
    private Color _textColor;

    /**
     * The alignment of the text in the label.
     */
    private int _textAlignment;

    /**
     * A constant for left alignment.
     */
    public static final int ALIGNLEFT = 0;

    /**
     * A constant for center alignment.
     */
    public static final int ALIGNCENTER = 1;

    /**
     * A constant for right alignment.
     */
    public static final int ALIGNRIGHT = 2;

    /**
     * The default constructor creates an empty label with default settings.
     */
    public LabelInfo()
    {
        _entries = new Vector();
        _offset = new Point2D.Double( 0, 0 );
        _backgroundColor = Color.white;
        _textColor = Color.black;
        _textAlignment = ALIGNLEFT;
        labelObservers = new Vector();
    }

    /**
     * Adds an entry add the end of the label.
     */
    public void addEntry( String entry )
    {
        _entries.add( entry );
        emitChangeSignal();
    }

    /**
     * Returns the number of entries in the label.
     */
    public int getNumberOfEntries()
    {
        return _entries.size();
    }

    /**
     * Returns an entry from the label.
     */
    public String getEntry( int num )
    {
        return (String) _entries.get( num );
    }

    /**
     * Returns the current offset.
     */
    public Point2D getOffset()
    {
        return _offset;
    }

    /**
     * Sets the label offset.
     *
     * The offset defines how far the label is moved from the point. The
     * bahaviour is different for attribute and object labels: the attribute
     * labels are positioned on top of a point, directly connecting to the
     * top edge if the offset is (0,0). The object labels are below the points,
     * contacting them at the bottom.
     */
    public void setOffset( Point2D offset )
    {
        _offset = offset;
        emitChangeSignal();
    }

    /**
     * A convenience method mapping to setOffset(Point2D).
     */
    public void setOffset( double x, double y ) {
        setOffset(new Point2D.Double(x,y));
    }

    /**
     * Returns the current background color.
     */
    public Color getBackgroundColor()
    {
        return _backgroundColor;
    }

    /**
     * Sets the background color.
     */
    public void setBackgroundColor( Color color )
    {
        _backgroundColor = color;
        emitChangeSignal();
    }

    /**
     * Returns the current text color.
     */
    public Color getTextColor()
    {
        return _textColor;
    }

    /**
     * Sets the text color.
     */
    public void setTextColor( Color color )
    {
        _textColor = color;
        emitChangeSignal();
    }

    /**
     * Returns the current text alignment.
     */
    public int getTextAlignment()
    {
        return _textAlignment;
    }

    /**
     * Sets the alignment of the text.
     */
    public void setTextAligment( int alignment )
    {
        _textAlignment = alignment;
        emitChangeSignal();
    }
}