/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

import java.io.File;
import java.util.Properties;

import org.tockit.canvas.Canvas;

/**
 * Interface for implementing image writers.
 *
 * To add an image writer you should do these things:
 * - implement this interface by writing your encoding code
 * - turn off the mouse-over effects by calling 
 *   CanvasController.hideMouseFromItems(boolean)
 * - use the singleton approach to get one static instance
 * - add at least one static inner class extending GraphicFormat that defines
 *   what your image writer can handle, pointing getWriter() to your singleton
 * - write a static method initialize() that creates your singleton and calls
 *   GraphicFormatRegistry.register(GraphicFormat) for all formats you support
 * - call the latter at program startup
 *
 * Don't try to implement the registration in a generic static block, this won't
 * be called unless something else of the class is used. You don't have to supply
 * any other way to get your singleton than by calling GraphicFormat.getWriter().
 */
public interface ImageWriter {
    /**
     * Saves the canvas using the settings to the file.
     * 
     * Important: when implementing the method CanvasController.hideMouseFromItems(boolean)
     * has to be called to turn of potential mouse over effects.
     */
    void exportGraphic(Canvas canvas, DiagramExportSettings settings, File outputFile, Properties metadata)
            throws ImageGenerationException;
}