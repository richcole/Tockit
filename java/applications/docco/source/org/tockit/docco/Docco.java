/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.docco.gui.DoccoMainFrame;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.SkyBlue;

public class Docco {
	public static void main (String[] args) {
		testJavaVersion();
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
				SkyBlue theme = new SkyBlue(){
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
					public ColorUIResource getHighlightedTextColor() {
                        return new ColorUIResource(255,255,255);
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


    /**
     * Tests if we are running at least JRE 1.4.0
     */
    public static void testJavaVersion() {
        String versionString = System.getProperty("java.class.version","44.0");
        if("48.0".compareTo(versionString) > 0) {
            JOptionPane.showMessageDialog(null,"This program requires a Java Runtime Environment\n" +
                "with version number 1.4.0 or above.\n\n" +
                "Up to date versions of Java can be found at\n" +
                "http://java.sun.com.",
                "Java installation too old", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
