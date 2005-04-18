/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.tockit.canvas.Canvas;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Saves a Canvas as SVG graphic.
 */
public class BatikImageWriter implements ImageWriter {
    /**
     * A format representing SVG.
     */
    static protected class GraphicFormatSVG extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        public String getName() {
            return "Scalable Vector Graphics";
        }

        /**
         * Implements GraphicFormat.getExtensions().
         */
        public String[] getExtensions() {
            String[] retVal = new String[1];
            retVal[0] = "svg";
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
     * A format representing compressed SVG.
     */
    static protected class GraphicFormatSVGZ extends GraphicFormat {
        /**
         * Implements GraphicFormat.getName().
         */
        public String getName() {
            return "Scalable Vector Graphics (compressed)";
        }

        /**
         * Implements GraphicFormat.getExtensions().
         */
        public String[] getExtensions() {
            String[] retVal = new String[1];
            retVal[0] = "svgz";
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
    static private BatikImageWriter singleton;

    /**
     * We use a singleton approach, no public constructor.
     */
    private BatikImageWriter() {
        // no instances allowed
    }

    /**
     * Registers our graphic format and sets up the instance.
     */
    static public void initialize() {
        singleton = new BatikImageWriter();
        GraphicFormatRegistry.registerType(new GraphicFormatSVG());
        GraphicFormatRegistry.registerType(new GraphicFormatSVGZ());
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
        // use Batik
        // Get a DOMImplementation
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document
        Document document = domImpl.createDocument(null, "svg", null);
		
        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
		
        svgGenerator.setSVGCanvasSize(new Dimension(settings.getImageWidth(), settings.getImageHeight()));
        Rectangle2D bounds = new Rectangle2D.Double(
                0, 0, settings.getImageWidth(), settings.getImageHeight());
        if(settings.forceColorIsSet()) {
            svgGenerator.setPaint(settings.getBackgroundColor());
            svgGenerator.fill(bounds);
        }
        
        AffineTransform transform = canvas.scaleToFit(svgGenerator, bounds);
        svgGenerator.transform(transform);

        // render the graphic into the DOM
        canvas.paintCanvas(svgGenerator);
	
		Element svgRoot = svgGenerator.getRoot();
		//Get title and desc and insert into the svg document
			String title = metadata.getProperty("title");
			String description= metadata.getProperty("description");
			if( title != null && description!= null ) {
				Element titleElement = document.createElement("title");
				Element descElement = document.createElement("desc");
				titleElement.appendChild(document.createTextNode(title));
				descElement.appendChild(document.createTextNode(description));
				// Insert the title and description element to top of the existing document	 
				svgRoot.insertBefore(descElement,svgRoot.getFirstChild());
				svgRoot.insertBefore(titleElement,svgRoot.getFirstChild());
			}
		
        // Finally, stream out SVG to the standard output using UTF-8
        // character to byte encoding
        boolean useCSS = true; // we want to use CSS style attribute
        try {
            OutputStream outStream = new FileOutputStream(outputFile);
            if (settings.getGraphicFormat() instanceof GraphicFormatSVGZ) {
                outStream = new GZIPOutputStream(outStream);
            }
            Writer out = new OutputStreamWriter(outStream, "UTF-8");
			svgGenerator.stream(svgRoot, out, useCSS);
		    outStream.close();
        } catch (Exception e) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - writing SVG error: " + e.getMessage(), e);
		} finally {
			canvas.getController().hideMouseFromItems(false);
        }
    }
}