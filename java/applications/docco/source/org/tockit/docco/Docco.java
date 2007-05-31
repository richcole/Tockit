/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import java.io.PrintStream;
import java.text.MessageFormat;
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
	private static final String HELP_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.helpOption.name"); //$NON-NLS-1$
	private static final String INDEX_DIRECTORY_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.indexDirectoryOption.name"); //$NON-NLS-1$
	private static final String USE_PLATFORM_LF_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.usePlatformLnfOption.name"); //$NON-NLS-1$
	private static final String FORCE_INDEX_ACCESS_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.forceIndexAccessOption.name"); //$NON-NLS-1$

	public static void main (String[] args) {
		ToscanaJ.testJavaVersion();
        Options options = new Options();
        options.addOption(FORCE_INDEX_ACCESS_COMMAND_LINE_OPTION, false, CliMessages.getString("DoccoCommandLine.forceIndexAccessOption.description")); //$NON-NLS-1$
        options.addOption(USE_PLATFORM_LF_COMMAND_LINE_OPTION, false, CliMessages.getString("DoccoCommandLine.usePlatformLnfOption.description")); //$NON-NLS-1$
        options.addOption(INDEX_DIRECTORY_COMMAND_LINE_OPTION, true, CliMessages.getString("DoccoCommandLine.indexDirectoryOption.description")); //$NON-NLS-1$
        options.addOption(HELP_COMMAND_LINE_OPTION, false, CliMessages.getString("DoccoCommandLine.helpOption.description")); //$NON-NLS-1$
        CommandLineParser parser = new BasicParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(options,args);
        } catch (ParseException e) {
            showUsage(options, System.err);
            System.exit(1);
        }
        assert cl != null;
        
        if(cl.getArgs().length > 0) {
            showUsage(options, System.err);
            System.exit(1);
        }
        if(cl.hasOption(HELP_COMMAND_LINE_OPTION)) {
            showUsage(options, System.out);
            System.exit(0);
        }
        boolean forceIndexAccess = cl.hasOption(FORCE_INDEX_ACCESS_COMMAND_LINE_OPTION);
        boolean usePlatformLF = cl.hasOption(USE_PLATFORM_LF_COMMAND_LINE_OPTION);
        String indexDirectory = cl.getOptionValue(INDEX_DIRECTORY_COMMAND_LINE_OPTION);

		if(usePlatformLF) {
			try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e1) {
            	System.err.println(CliMessages.getString("DoccoCommandLine.failedToSetPlatformLnfMessage.text")); //$NON-NLS-1$
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
				System.err.println(CliMessages.getString("DoccoCommandLine.failedToSetPlasticLnfMessage.text")); //$NON-NLS-1$
			}
		}

		try {
			DoccoMainFrame mainFrame = new DoccoMainFrame(forceIndexAccess, indexDirectory);
			mainFrame.setVisible(true);
		}
		catch (Exception e) {
			ErrorDialog.showError(null, e, CliMessages.getString("DoccoCommandLine.errorDialog.title")); //$NON-NLS-1$
		}
	}

    private static void showUsage(Options options, PrintStream stream) {
        stream.println(CliMessages.getString("DoccoCommandLine.helpText.line1")); //$NON-NLS-1$
        stream.println(CliMessages.getString("DoccoCommandLine.helpText.line2")); //$NON-NLS-1$
        stream.println();
        stream.println(CliMessages.getString("DoccoCommandLine.helpText.line3")); //$NON-NLS-1$
        for (Iterator iter = options.getOptions().iterator(); iter.hasNext(); ) {
            Option option = (Option) iter.next();
            stream.println(MessageFormat.format(CliMessages.getString("DoccoCommandLine.helpText.optionFormat"), new Object[]{option.getOpt(),option.getDescription()})); //$NON-NLS-1$
        }
    }
}
