/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.exceptions;

/**
 * Thrown in case an illegal layer name is used.
 * 
 * Illegal names are the null object and an empty string.
 */
public class IllegalLayerNameException extends InvalidLayerException {
    public IllegalLayerNameException() {
        super();
    }

    public IllegalLayerNameException(Throwable cause) {
        super(cause);
    }

    public IllegalLayerNameException(String message) {
        super(message);
    }

    public IllegalLayerNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
