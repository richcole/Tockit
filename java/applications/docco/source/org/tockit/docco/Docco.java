/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import net.sourceforge.toscanaj.ToscanaJ;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.docco.gui.DoccoMainFrame;

public class Docco {
	public static void main (String[] args) {
		ToscanaJ.testJavaVersion();
		boolean forceIndexAccess = false;
		for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("-forceIndexAccess")) {
            	forceIndexAccess = true;
            } else {
            	System.err.println("Unknown command line parameter");
            	System.exit(1);
            }
        }
		try {
			DoccoMainFrame mainFrame = new DoccoMainFrame(forceIndexAccess);
			mainFrame.setVisible(true);
			/// @todo where should we call PluginLoader from?
			new PluginLoader();
		}
		catch (Exception e) {
			ErrorDialog.showError(null, e, "Error");
		}
	}
}
