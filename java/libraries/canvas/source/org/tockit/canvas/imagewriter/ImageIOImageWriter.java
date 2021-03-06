/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.tockit.canvas.Canvas;

/**
 * This class can be used to save a Canvas to a bitmap file.
 */
public class ImageIOImageWriter implements ImageWriter {
    /**
     * A format representing PNG.
     */
    static protected class GraphicFormatPNG extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        @Override
		public String getName() {
            return "Portable Network Graphics";
        }

        /**
         * Implements GraphicFormat.getExtensions().
         */
        @Override
		public String[] getExtensions() {
            return new String[]{"png"};
        }

        /**
         * Implements GraphicFormat.getWriter().
         */
        @Override
		public ImageWriter getWriter() {
            return singleton;
        }
    }

    /**
     * A format representing JPG.
     */
    static protected class GraphicFormatJPG extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        @Override
		public String getName() {
            return "Joint Picture Expert Group";
        }

        /**
         * Implements GraphicFormat.getExtensions().
         */
        @Override
		public String[] getExtensions() {
            return new String[]{"jpg", "jpeg"};
        }

        /**
         * Implements GraphicFormat.getWriter().
         */
        @Override
		public ImageWriter getWriter() {
            return singleton;
        }
    }
	
    /**
     * The only instance of this class.
     */
    static private ImageIOImageWriter singleton;

    /**
     * We use a singleton approach, no public constructor.
     */
    private ImageIOImageWriter() {
        // no instances allowed
    }

    /**
     * Registers our graphic formats and sets up the instance.
     */
    static public void initialize() {
        singleton = new ImageIOImageWriter();
        GraphicFormatRegistry.registerType(new GraphicFormatPNG());
        GraphicFormatRegistry.registerType(new GraphicFormatJPG());
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
		GraphicFormat graphicFormat = settings.getGraphicFormat();
		BufferedImage image;
		if(graphicFormat instanceof GraphicFormatJPG) {
			image = new BufferedImage(settings.getImageWidth(), settings.getImageHeight(),
							BufferedImage.TYPE_INT_RGB);
		} else {
			image = new BufferedImage(settings.getImageWidth(), settings.getImageHeight(),
							BufferedImage.TYPE_INT_ARGB);
		}
		Graphics2D graphics2D = image.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle2D bounds = new Rectangle2D.Double(
                0, 0, settings.getImageWidth(), settings.getImageHeight());
		if(graphicFormat instanceof GraphicFormatJPG) {
			Paint paint = canvas.getBackgroundItem().getPaint();
			if(paint == null) {
				paint = settings.getBackgroundColor();
			}
			graphics2D.setPaint(paint);
			graphics2D.fill(bounds);
		}
        if(settings.forceColorIsSet()) {
            graphics2D.setPaint(settings.getBackgroundColor());
            graphics2D.fill(bounds);
        }

        AffineTransform transform = canvas.scaleToFit(graphics2D, bounds);
        graphics2D.transform(transform);
        // paint all items on canvas
        canvas.paintCanvas(graphics2D);
		try {
			// Save the image 
			ImageIO.write(image, graphicFormat.getExtensions()[0] , outputFile);
		} catch (FileNotFoundException e) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - not found ", e);
        } catch (IOException e) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - IO problem ", e);
		} finally {
			canvas.getController().hideMouseFromItems(false);
        }
    }
}