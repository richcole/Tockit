/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.arakhne.neteditor.io.PostscriptWriter;
import org.arakhne.neteditor.io.VectorialExporter;
import org.tockit.canvas.Canvas;

/**
 * Saves a Canvas as EPS graphic.
 */
public class PostscriptImageWriter implements ImageWriter {
    /**
     * A format representing EPS.
     */
    static protected class GraphicFormatEPS extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        public String getName() {
            return "Encapsulated Postscript";
        }

        /**
         * Implements GraphicFormat.getExtensions().
         */
        public String[] getExtensions() {
            return new String[]{"eps", "ps"};
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
    static private PostscriptImageWriter singleton;

    /**
     * We use a singleton approach, no public constructor.
     */
    private PostscriptImageWriter() {
    }

    /**
     * Registers our graphic format and sets up the instance.
     */
    static public void initialize() {
        singleton = new PostscriptImageWriter();
        GraphicFormatRegistry.registerType(new GraphicFormatEPS());
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

        Rectangle bounds = new Rectangle(
                0, 0, settings.getImageWidth(), settings.getImageHeight());

		try {
			VectorialExporter ve = new PostscriptWriter();
			AffineTransform affineTransform = canvas.scaleToFit(ve,bounds);
			ve.setTargetFile(new FileOutputStream(outputFile), outputFile);
			ve.setDrawingArea(bounds);
			ve.setTransform(affineTransform);
			ve.exportProlog();
			canvas.paintCanvas(ve);
			ve.dispose() ;
        } catch (Exception e) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - writing EPS error: " + e.getMessage(), e);
        }
    }
}