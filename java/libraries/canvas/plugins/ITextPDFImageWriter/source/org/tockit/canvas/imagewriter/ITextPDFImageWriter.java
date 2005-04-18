/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.tockit.canvas.Canvas;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Saves a Canvas as SVG graphic.
 */
public class ITextPDFImageWriter implements ImageWriter {
    /**
     * A format representing SVG.
     */
    static protected class GraphicFormatPDF extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        public String getName() {
            return "Portable Document Format (PDF)";
        }

        /**
         * Implements GraphicFormat.getExtensions().
         */
        public String[] getExtensions() {
            String[] retVal = new String[1];
            retVal[0] = "pdf";
            return retVal;
        }

        /**
         * Implements GraphicFormat.getWriter().
         */
        public ImageWriter getWriter() {
            return singleton;
        }
    }

    /**
     * The only instance of this class.
     */
    static private ITextPDFImageWriter singleton;

    /**
     * We use a singleton approach, no public constructor.
     */
    private ITextPDFImageWriter() {
        // no instances allowed
    }

    /**
     * Registers our graphic format and sets up the instance.
     */
    static public void initialize() {
        singleton = new ITextPDFImageWriter();
        GraphicFormatRegistry.registerType(new GraphicFormatPDF());
    }

    /**
     * Saves the canvas using the settings to the file.
     */
    public void exportGraphic(Canvas canvas, DiagramExportSettings settings, File outputFile, Properties metadata)
            throws ImageGenerationException {
        canvas.getController().hideMouseFromItems(true);
            	
        if (settings.usesAutoMode()) {
            // update information
            settings.setImageSize(canvas.getWidth(), canvas.getHeight());
        }
    
        try {
			int width = settings.getImageWidth();
            int height = settings.getImageHeight();
            
            // create document with the image's size and no margin
            Document document = new Document(new Rectangle(width,height),0,0,0,0);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.addTitle(metadata.getProperty("title"));
            document.addKeywords(metadata.getProperty("description"));
            document.addHeader("diagram history", metadata.getProperty("description"));
            document.open();
            
            DefaultFontMapper mapper = new DefaultFontMapper();
            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate tp = cb.createTemplate(width, height);
            Graphics2D g2 = tp.createGraphics(width, height, mapper);
            tp.setWidth(width);
            tp.setHeight(height);

            Rectangle2D bounds = new Rectangle2D.Double(0, 0, width, height);
            if(settings.forceColorIsSet()) {
                g2.setPaint(settings.getBackgroundColor());
                g2.fill(bounds);
            }
            
            AffineTransform transform = canvas.scaleToFit(g2, bounds);
            g2.transform(transform);

            canvas.paintCanvas(g2);

            g2.dispose();
            cb.addTemplate(tp, 0, 0);
            document.close();
        } catch (Exception e) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - writing PDF error: " + e.getMessage(), e);
		} finally {
			canvas.getController().hideMouseFromItems(false);
        }
    }
}