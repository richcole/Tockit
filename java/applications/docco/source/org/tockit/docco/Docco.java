/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import net.sourceforge.toscanaj.ToscanaJ;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.docco.gui.DoccoMainFrame;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.ExperienceBlue;

public class Docco {
	public static void main (String[] args) {
		ToscanaJ.testJavaVersion();
		boolean forceIndexAccess = false;
		boolean usePlatformLF = false;
		for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equalsIgnoreCase("-forceIndexAccess")) {
            	forceIndexAccess = true;
            } else if(arg.equalsIgnoreCase("-usePlatformLF")) {
            	usePlatformLF = true;
            } else {
            	System.err.println("Unknown command line parameter");
            	System.err.println("Options:");
            	System.err.println("  -forceIndexAccess  -  forces access to locked indexes");
            	System.err.println("  -usePlatformLF     -  use the look and feel of the OS");
            	System.exit(1);
            }
	    }

		if(usePlatformLF) {
			try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e1) {
            	System.err.println("Couldn't set platform look and feel");
            }
		} else {
			try {
				ExperienceBlue theme = new ExperienceBlue(){
					protected ColorUIResource getSecondary3() {
						 return new ColorUIResource(214,212,206);
					}
					protected ColorUIResource getPrimary1() {
						return new ColorUIResource(150,150,200);
					}
					protected ColorUIResource getPrimary3() {
						return new ColorUIResource(150,150,200);
					}
					public ColorUIResource getPrimaryControlHighlight() {
						return new ColorUIResource(230,230,255);
					}
					public ColorUIResource getPrimaryControlDarkShadow() {
						return new ColorUIResource(100,100,150);
					}
					public ColorUIResource getFocusColor() {
						return new ColorUIResource(50,50,80);
					}
				};
				PlasticLookAndFeel.setMyCurrentTheme(theme);
				UIManager.setLookAndFeel(new PlasticLookAndFeel());
			} catch (UnsupportedLookAndFeelException e1) {
				System.err.println("Couldn't set Plastic look and feel");
			}
		}

		try {
			DoccoMainFrame mainFrame = new DoccoMainFrame(forceIndexAccess);
			mainFrame.setVisible(true);
		}
		catch (Exception e) {
			ErrorDialog.showError(null, e, "Error");
		}
	}
}
