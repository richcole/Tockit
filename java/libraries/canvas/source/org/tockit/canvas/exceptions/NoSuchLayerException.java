/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.exceptions;

/**
 * Thrown whenever a layer name is used that is unknown.
 *
 */
public class NoSuchLayerException extends InvalidLayerException {
    public NoSuchLayerException() {
        super();
    }

    public NoSuchLayerException(Throwable cause) {
        super(cause);
    }

    public NoSuchLayerException(String message) {
        super(message);
    }

    public NoSuchLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
