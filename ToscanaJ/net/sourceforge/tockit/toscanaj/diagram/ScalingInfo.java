package net.sourceforge.tockit.toscanaj.diagram;

import java.awt.geom.Point2D;

/**
 * This class encapsulates the information DiagramView needs to scale the
 * coordinate system of Diagram onto the paint area.
 */
public class ScalingInfo
{
    /**
     * The offset used to shift the projection in the new coordinate system.
     */
    Point2D _offset;

    /**
     * The scaling factor for the horizontal direction.
     */
    double _xScale;

    /**
     * The scaling factor for the horizontal direction.
     */
    double _yScale;

    /**
     * The default constructor just creates a scaling set with no change at all.
     */
    public ScalingInfo()
    {
        _offset = new Point2D.Double( 0, 0 );
        _xScale = 1;
        _yScale = 1;
    }

    /**
     * An usual initialization constructor, taking an offset and two different
     * scaling factors.
     */
    public ScalingInfo( Point2D offset, double xscale, double yscale )
    {
        _offset = offset;
        _xScale = xscale;
        _yScale = yscale;
    }

    /**
     * An usual initialization constructor, taking an offset and only one
     * scaling factor, which is used for both directions.
     */
    public ScalingInfo( Point2D offset, double scale )
    {
        _offset = offset;
        _xScale = scale;
        _yScale = scale;
    }

    /**
     * Projects a point using the current scaling information.
     *
     * This includes scaling and moving it to the new offset.
     */
    public Point2D project( Point2D point )
    {
        return new Point2D.Double( projectX( point.getX() ),
                                   projectY( point.getY() ) );
    }

    /**
     * Projects a x-coordinate using the current scaling information.
     *
     * This includes scaling and moving it to the new offset.
     */
    public double projectX( double x )
    {
        return _offset.getX() + x * _xScale;
    }

    /**
     * Projects a y-coordinate using the current scaling information.
     *
     * This includes scaling and moving it to the new offset.
     */
    public double projectY( double y )
    {
        return _offset.getY() + y * _yScale;
    }

    /**
     * Projects a point using the current scaling information.
     *
     * The offset is not applied.
     */
    public Point2D scale( Point2D point )
    {
        return new Point2D.Double( scaleX( point.getX() ),
                                   scaleY( point.getY() ) );
    }

    /**
     * Projects a x-coordinate using the current scaling information.
     *
     * The offset is not applied.
     */
    public double scaleX( double x )
    {
        return x * _xScale;
    }

    /**
     * Projects a y-coordinate using the current scaling information.
     *
     * The offset is not applied.
     */
    public double scaleY( double y )
    {
        return y * _yScale;
    }

    /**
     * Projects a point applying the current scaling information in the
     * opposite direction.
     *
     * The offset is not applied.
     */
    public Point2D inverseScale( Point2D point )
    {
        return new Point2D.Double( inverseScaleX( point.getX() ),
                                   inverseScaleY( point.getY() ) );
    }

    /**
     * Projects an X-coordinate applying the current scaling information in the
     * opposite direction.
     *
     * The offset is not applied.
     */
    public double inverseScaleX( double x )
    {
        return x / _xScale;
    }

    /**
     * Projects an Y-coordinate applying the current scaling information in the
     * opposite direction.
     *
     * The offset is not applied.
     */
    public double inverseScaleY( double y )
    {
        return y / _yScale;
    }
}