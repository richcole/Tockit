/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.exceptions;

/**
 * Superclass for all exceptions that are related to invalid layer access.
 * 
 * @todo this should be a checked exception
 */
public class InvalidLayerException extends RuntimeException {
    public InvalidLayerException() {
        super();
    }

    public InvalidLayerException(Throwable cause) {
        super(cause);
    }

    public InvalidLayerException(String message) {
        super(message);
    }

    public InvalidLayerException(String message, Throwable cause) {
    	super(message, cause);
    }
}
