/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.canvas.imagewriter;
 
import org.tockit.plugin.Plugin;


public class ITextPDFImageWriterLoader implements Plugin {
    public void load() {
    	ITextPDFImageWriter.initialize();
    }
}
