/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe;

import org.tockit.crepe.gui.CrepeMainPanel;

public class Crepe {
    public static String VersionString = "0.1";

    public static void main(String[] args) {
        final CrepeMainPanel mainWindow;
        if (args.length == 1) {
            mainWindow = new CrepeMainPanel(args[0]);
        } else {
            mainWindow = new CrepeMainPanel();
        }

        mainWindow.setVisible(true);
    }
}
