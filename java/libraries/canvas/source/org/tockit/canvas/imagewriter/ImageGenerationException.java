/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

/**
 * Signals that an image could not be created.
 *
 * There is a large number of reasons why this can happen, the message string
 * and the embedded exception are used to indicate what exactly went wrong.
 *
 * @todo build hierarchy for this.
 */
public class ImageGenerationException extends Exception {
    public ImageGenerationException() {
    super();
    }

    public ImageGenerationException(String message) {
    super(message);
    }

    public ImageGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageGenerationException(Throwable cause) {
        super(cause);
    }
}
