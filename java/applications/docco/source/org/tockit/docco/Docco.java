/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import java.io.PrintStream;
import java.util.Iterator;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import net.sourceforge.toscanaj.ToscanaJ;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.tockit.docco.gui.DoccoMainFrame;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.SkyBlue;

public class Docco {
	public static void main (String[] args) {
		ToscanaJ.testJavaVersion();
		boolean forceIndexAccess = false;
		boolean usePlatformLF = false;
        Options options = new Options();
        options.addOption("forceIndexAccess", false, "Forces the index to be opened, even if locks are present");
        options.addOption("usePlatformLF", false, "Uses the platform specific look and feel instead of the default");
        options.addOption("help", false, "Show this command line summary and exit");
        CommandLineParser parser = new BasicParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(options,args);
        } catch (ParseException e) {
            showUsage(options, System.err);
            System.exit(1);
        }
        if(cl.getArgs().length > 0) {
            showUsage(options, System.err);
            System.exit(1);
        }
        if(cl.hasOption("help")) {
            showUsage(options, System.out);
            System.exit(0);
        }
        if(cl.hasOption("forceIndexAccess")) {
            forceIndexAccess = true;
        }
        if(cl.hasOption("usePlatformLF")) {
            usePlatformLF = true;
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

    private static void showUsage(Options options, PrintStream stream) {
        stream.println("Usage:");
        stream.println("  Docco [Options]");
        stream.println();
        stream.println("where [Options] can be:");
        for (Iterator iter = options.getOptions().iterator(); iter.hasNext(); ) {
            Option option = (Option) iter.next();
            stream.println("  " + option.getOpt() + ": " + option.getDescription());
        }
    }
}
