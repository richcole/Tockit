/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import org.tockit.conscript.parser.DataFormatException;


public class SectionTypeNotSupportedException
    extends DataFormatException {

    public SectionTypeNotSupportedException() {
        super();
    }

    public SectionTypeNotSupportedException(String message) {
        super(message);
    }

    public SectionTypeNotSupportedException(
        String message,
        Throwable cause) {
        super(message, cause);
    }

    public SectionTypeNotSupportedException(Throwable cause) {
        super(cause);
    }

}