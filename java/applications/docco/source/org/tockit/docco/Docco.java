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
	public Docco () {
		try {
			DoccoMainFrame mainFrame = new DoccoMainFrame();
			mainFrame.setVisible(true);
		}
		catch (Exception e) {
			ErrorDialog.showError(null, e, "Error");
		}
	}
	
	public static void main (String[] args) {
		ToscanaJ.testJavaVersion();
		new Docco();
	}

}
