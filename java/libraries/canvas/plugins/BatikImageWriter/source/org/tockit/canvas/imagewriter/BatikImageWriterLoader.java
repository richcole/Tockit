/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.canvas.imagewriter;
 
import org.tockit.plugin.Plugin;


public class BatikImageWriterLoader implements Plugin {
    public void load() {
        BatikImageWriter.initialize();
    }
}
