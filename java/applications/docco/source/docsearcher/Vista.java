package docsearcher;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class Vista
    implements Pageable
{
    private int mNumPagesX;
    private int mNumPagesY;
    private int mNumPages;
    private Printable mPainter;
    private PageFormat mFormat;

    public Vista(float width,
                 float height,
                 Printable painter,
                 PageFormat format)
    {
        setPrintable(painter);
        setPageFormat(format);
        setSize(width, height);
    }

    protected Vista()
    {
    }

    protected void setPrintable(Printable painter)
    {
        mPainter = painter;
    }

    protected void setPageFormat(PageFormat pageFormat)
    {
        mFormat = pageFormat;
    }

    protected void setSize(float width,
                           float height)
    {
        mNumPagesX =
            (int)(
                ((width + mFormat.getImageableWidth()) - 1) / mFormat
                                                              .getImageableWidth()
            );
        mNumPagesY =
            (int)(
                ((height + mFormat.getImageableHeight()) - 1) / mFormat
                                                                .getImageableHeight()
            );
        mNumPages = mNumPagesX * mNumPagesY;
    }

    public int getNumberOfPages()
    {
        return mNumPages;
    }

    protected PageFormat getPageFormat()
    {
        return mFormat;
    }

    public PageFormat getPageFormat(int pageIndex)
        throws IndexOutOfBoundsException
    {
        if (pageIndex >= mNumPages)
        {
            throw new IndexOutOfBoundsException();
        }

        return getPageFormat();
    }

    public Printable getPrintable(int pageIndex)
        throws IndexOutOfBoundsException
    {
        if (pageIndex >= mNumPages)
        {
            throw new IndexOutOfBoundsException();
        }

        double originX = (pageIndex % mNumPagesX) * mFormat.getImageableWidth();
        double originY =
            (pageIndex / mNumPagesX) * mFormat.getImageableHeight();
        Point2D.Double origin = new Point2D.Double(originX, originY);

        return new TranslatedPrintable(mPainter, origin);
    }

    public static final class TranslatedPrintable
        implements Printable
    {
        private Printable mPainter;
        private Point2D mOrigin;

        public TranslatedPrintable(Printable painter,
                                   Point2D origin)
        {
            mPainter = painter;
            mOrigin = origin;
        }

        public int print(Graphics graphics,
                         PageFormat pageFormat,
                         int pageIndex)
            throws PrinterException
        {
            Graphics2D g2 = (Graphics2D)graphics;
            g2.translate(-mOrigin.getX(), -mOrigin.getY());
            mPainter.print(g2, pageFormat, 1);

            return PAGE_EXISTS;
        }
    }
}
