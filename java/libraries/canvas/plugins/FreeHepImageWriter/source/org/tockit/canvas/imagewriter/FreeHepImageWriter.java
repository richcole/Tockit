/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import java.awt.Dimension;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.graphicsio.PageConstants;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.ps.PSGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.graphicsio.swf.SWFGraphics2D;
import org.tockit.canvas.Canvas;

/**
 * This class can be used to save a Canvas to different graphic formats using the FreeHep library.
 */
public class FreeHepImageWriter implements ImageWriter {
	static protected class GraphicFormatPDF extends GraphicFormat {
		public ImageWriter getWriter() {
			return singleton;
		}

		public String getName() {
			return "Portable Document Format";
		}

		public String[] getExtensions() {
			return new String[]{"pdf"};
		}
	}
	
	static protected class GraphicFormatEMF extends GraphicFormat {
		public ImageWriter getWriter() {
			return singleton;
		}

		public String getName() {
			return "Extended Metafile Format";
		}

		public String[] getExtensions() {
			return new String[]{"emf", "wmf"};
		}
	}
	
	static protected class GraphicFormatPS extends GraphicFormat {
		public ImageWriter getWriter() {
			return singleton;
		}

		public String getName() {
			return "Postscript";
		}

		public String[] getExtensions() {
			return new String[]{"eps", "ps"};
		}
	}
	
	static protected class GraphicFormatPPM extends GraphicFormat {
		public ImageWriter getWriter() {
			return singleton;
		}

		public String getName() {
			return "UNIX Portable PixMap";
		}

		public String[] getExtensions() {
			return new String[]{"ppm", "PPM"};
		}
	}

	static protected class GraphicFormatSWF extends GraphicFormat {
		public ImageWriter getWriter() {
			return singleton;
		}

		public String getName() {
			return "Macromedia Flash";
		}

		public String[] getExtensions() {
			return new String[]{"swf", "SWF"};
		}
	}
	
	static protected class GraphicFormatSVG extends GraphicFormat {
		public ImageWriter getWriter() {
			return singleton;
		}

		public String getName() {
			return "Scalable Vector Graphics (compressed)";
		}

		public String[] getExtensions() {
			return new String[]{"svgz", "SVGZ"};
		}
	}
	
	
    /**
     * The only instance of this class.
     */
    static private FreeHepImageWriter singleton;

    /**
     * We use a singleton approach, no public constructor.
     */
    private FreeHepImageWriter() {
    }

    /**
     * Registers our graphic formats and sets up the instance.
     */
    static public void initialize() {
        singleton = new FreeHepImageWriter();
		GraphicFormatRegistry.registerType(new GraphicFormatPS());
		GraphicFormatRegistry.registerType(new GraphicFormatEMF());
		GraphicFormatRegistry.registerType(new GraphicFormatPPM());
		GraphicFormatRegistry.registerType(new GraphicFormatSWF());
		
		// fix funny default orientation for Postscript in FreeHEP lib (v1.2.2)
		Properties prop = PSGraphics2D.getDefaultProperties();
		prop.setProperty(PSGraphics2D.ORIENTATION, PageConstants.PORTRAIT);
    }
    
    static public void addPDF() {
		GraphicFormatRegistry.registerType(new GraphicFormatPDF());
    }

    static public void addSVG() {
		GraphicFormatRegistry.registerType(new GraphicFormatSVG());
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
		Dimension imageSize = new Dimension(settings.getImageWidth(), settings.getImageHeight());
		try {
			VectorGraphics graphics2D;
			if(graphicFormat instanceof GraphicFormatPDF) {
				graphics2D = new PDFGraphics2D(outputFile,imageSize);
			} else if(graphicFormat instanceof GraphicFormatEMF) {
				graphics2D = new EMFGraphics2D(outputFile,imageSize);
			} else if(graphicFormat instanceof GraphicFormatPS) {
				graphics2D = new PSGraphics2D(outputFile,imageSize);
			} else if(graphicFormat instanceof GraphicFormatPPM) {
				graphics2D = new ImageGraphics2D(outputFile,imageSize,"ppm");
			} else if(graphicFormat instanceof GraphicFormatSWF) {
				graphics2D = new SWFGraphics2D(outputFile,imageSize);
			} else if(graphicFormat instanceof GraphicFormatSVG) {
				graphics2D = new SVGGraphics2D(outputFile,imageSize);
			} else {
				throw new RuntimeException("Internal error -- unknown graphic format");
			}
			graphics2D.startExport();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Rectangle2D bounds = new Rectangle2D.Double(0, 0, settings.getImageWidth(), settings.getImageHeight());
			if(graphicFormat instanceof GraphicFormatPPM) {
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
			graphics2D.endExport();
		} catch (FileNotFoundException e) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - not found ", e);
		} catch (IOException e) {
            throw new ImageGenerationException("Error while generating '" +
                    outputFile.getPath() + "' - not found ", e);
		} finally {
			canvas.getController().hideMouseFromItems(false);
        }
    }
}