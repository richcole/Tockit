/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.tockit.canvas.Canvas;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Saves a Canvas as PDF document.
 */
public class PDFImageWriter implements ImageWriter {
    /**
     * A format representing PDF.
     */
    static protected class GraphicFormatPDF extends GraphicFormat {
        public String getName() {
            return "Portable Document Format";
        }

        public String[] getExtensions() {
            String[] retVal = new String[1];
            retVal[0] = "pdf";
            return retVal;
        }

        public ImageWriter getWriter() {
            return singleton;
        }
    }

    /**
     * The only instance of this class.
     */
    static private PDFImageWriter singleton;

    /**
     * We use a singleton approach, no public constructor.
     */
    private PDFImageWriter() {
    }

    /**
     * Registers our graphic format and sets up the instance.
     */
    static public void initialize() {
        singleton = new PDFImageWriter();
        GraphicFormatRegistry.registerType(new GraphicFormatPDF());
    }

    /**
     * Saves the canvas using the settings to the file.
     */
    public void exportGraphic(Canvas canvas, DiagramExportSettings settings, File outputFile, Properties metadata)
            throws ImageGenerationException {
        if (settings.usesAutoMode()) {
            // update information
            settings.setImageSize(canvas.getWidth(), canvas.getHeight());
        }

        int imageHeight = settings.getImageHeight();
        int imageWidth = settings.getImageWidth();
        
        Document document = new Document(new Rectangle(imageWidth, imageHeight),0,0,0,0);
        try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
			document.open();
            
			PdfContentByte cb = writer.getDirectContent();
			Graphics2D g2d = cb.createGraphicsShapes(imageWidth, imageHeight);

			Rectangle2D bounds = new Rectangle2D.Double(
											0, 0, imageWidth, imageHeight);
			AffineTransform transform = canvas.scaleToFit(g2d, bounds);
			g2d.transform(transform);

			canvas.paintCanvas(g2d);
            
			g2d.dispose();
        }
        catch(DocumentException de) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - writing PDF error: " + de.getMessage(), de);
        }
        catch(IOException ioe) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - IO problem: " + ioe.getMessage(), ioe);
        }

        document.close();
    }
}