package docsearcher;

import java.awt.*;
import java.awt.print.*;
import javax.swing.*;

public class JComponentVista
    extends Vista
    implements Printable
{
    private static final boolean SYMMETRIC_SCALING = true;
    private static final boolean ASYMMETRIC_SCALING = false;
    private double mScaleX;
    private double mScaleY;
    private JComponent mComponent;

    public JComponentVista(JComponent c,
                           PageFormat format)
    {
        setPageFormat(format);
        setPrintable(this);
        setComponent(c);

        Rectangle componentBounds = c.getBounds(null);
        setSize(componentBounds.width, componentBounds.height);
        setScale(1, 1);
    }

    protected void setComponent(JComponent c)
    {
        mComponent = c;
    }

    protected void setScale(double scaleX,
                            double scaleY)
    {
        mScaleX = scaleX;
        mScaleY = scaleY;
    }

    public void scaleToFitX()
    {
        PageFormat format = getPageFormat();
        Rectangle componentBounds = mComponent.getBounds(null);
        double scaleX = format.getImageableWidth() / componentBounds.width;
        double scaleY = scaleX;
        if (scaleX < 1)
        {
            setSize((float)format.getImageableWidth(),
                    (float)(componentBounds.height * scaleY));
            setScale(scaleX, scaleY);
        }
    }

    public void scaleToFitY()
    {
        PageFormat format = getPageFormat();
        Rectangle componentBounds = mComponent.getBounds(null);
        double scaleY = format.getImageableHeight() / componentBounds.height;
        double scaleX = scaleY;
        if (scaleY < 1)
        {
            setSize((float)(componentBounds.width * scaleX),
                    (float)format.getImageableHeight());
            setScale(scaleX, scaleY);
        }
    }

    public void scaleToFit(boolean useSymmetricScaling)
    {
        PageFormat format = getPageFormat();
        Rectangle componentBounds = mComponent.getBounds(null);
        double scaleX = format.getImageableWidth() / componentBounds.width;
        double scaleY = format.getImageableHeight() / componentBounds.height;
        System.out.println("Scale: " + scaleX + " " + scaleY);
        if ((scaleX < 1) || (scaleY < 1))
        {
            if (useSymmetricScaling)
            {
                if (scaleX < scaleY)
                {
                    scaleY = scaleX;
                }
                else
                {
                    scaleX = scaleY;
                }
            }

            setSize((float)(componentBounds.width * scaleX),
                    (float)(componentBounds.height * scaleY));
            setScale(scaleX, scaleY);
        }
    }

    public int print(Graphics graphics,
                     PageFormat pageFormat,
                     int pageIndex)
        throws PrinterException
    {
        Graphics2D g2 = (Graphics2D)graphics;
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        Rectangle componentBounds = mComponent.getBounds(null);
        g2.translate(-componentBounds.x, -componentBounds.y);
        g2.scale(mScaleX, mScaleY);
        boolean wasBuffered = mComponent.isDoubleBuffered();
        mComponent.paint(g2);
        mComponent.setDoubleBuffered(wasBuffered);

        return PAGE_EXISTS;
    }
}
