/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.tockit.docco.documenthandler.DocumentHandlerRegistry;
import org.tockit.docco.gui.DoccoMainFrame;
import org.tockit.docco.index.Index;
import org.tockit.docco.indexer.Indexer;
import org.tockit.plugin.PluginLoader;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.SkyBlue;

/**
 * Main class to start Docco.
 */
public class Docco {
	// CLI exit codes
	/**
	 * Signals normal program execution.
	 */
	private static final int EXIT_CODE_OK = 0;
    /**
     * Signals an error in the command line options given by the user.
     */
	private static final int EXIT_CODE_CLI_OPTIONS_WRONG = 1;
	/**
	 * Signals an error loading the document handlers in non-GUI mode.
	 */
	private static final int EXIT_CODE_ERROR_LOADING_DOCUMENT_HANDLERS = 2;
	/**
	 * Signals an error loading the plugins in non-GUI mode.
	 */
	private static final int EXIT_CODE_ERROR_LOADING_PLUGINS = 3;
	/**
	 * Signals an error opening an index in non-GUI mode.
	 */
	private static final int EXIT_CODE_ERROR_OPENING_INDEX = 4;
	
	private static final String USE_PLATFORM_LF_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.usePlatformLnfOption.name"); //$NON-NLS-1$
	private static final String INDEX_DIRECTORY_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.indexDirectoryOption.name"); //$NON-NLS-1$
	private static final String FORCE_INDEX_ACCESS_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.forceIndexAccessOption.name"); //$NON-NLS-1$
	private static final String UPDATE_INDEXES_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.updateIndexesOption.name"); //$NON-NLS-1$
	private static final String HELP_COMMAND_LINE_OPTION = CliMessages.getString("DoccoCommandLine.helpOption.name"); //$NON-NLS-1$

	public static void main (String[] args) {
		ToscanaJ.testJavaVersion();
        Options options = new Options();
        options.addOption(USE_PLATFORM_LF_COMMAND_LINE_OPTION, false, CliMessages.getString("DoccoCommandLine.usePlatformLnfOption.description")); //$NON-NLS-1$
        options.addOption(INDEX_DIRECTORY_COMMAND_LINE_OPTION, true, CliMessages.getString("DoccoCommandLine.indexDirectoryOption.description")); //$NON-NLS-1$
        options.addOption(FORCE_INDEX_ACCESS_COMMAND_LINE_OPTION, false, CliMessages.getString("DoccoCommandLine.forceIndexAccessOption.description")); //$NON-NLS-1$
        options.addOption(UPDATE_INDEXES_COMMAND_LINE_OPTION, false, CliMessages.getString("DoccoCommandLine.updateIndexesOption.description")); //$NON-NLS-1$
        options.addOption(HELP_COMMAND_LINE_OPTION, false, CliMessages.getString("DoccoCommandLine.helpOption.description")); //$NON-NLS-1$
        CommandLineParser parser = new BasicParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(options,args);
        } catch (ParseException e) {
            showUsage(options, System.err);
            System.exit(EXIT_CODE_CLI_OPTIONS_WRONG);
        }
        assert cl != null;
        
        if(cl.getArgs().length > 0) {
            showUsage(options, System.err);
            System.exit(EXIT_CODE_CLI_OPTIONS_WRONG);
        }
        if(cl.hasOption(HELP_COMMAND_LINE_OPTION)) {
            showUsage(options, System.out);
            System.exit(EXIT_CODE_OK);
        }
        boolean usePlatformLF = cl.hasOption(USE_PLATFORM_LF_COMMAND_LINE_OPTION);
        String indexDirectory = cl.getOptionValue(INDEX_DIRECTORY_COMMAND_LINE_OPTION);
        boolean forceIndexAccess = cl.hasOption(FORCE_INDEX_ACCESS_COMMAND_LINE_OPTION);
        boolean updateIndexesOnly = cl.hasOption(UPDATE_INDEXES_COMMAND_LINE_OPTION);
        
        if(updateIndexesOnly) {
        	// we just update the indexes and don't start the GUI
        	updateIndexes(indexDirectory, forceIndexAccess, false);
        	System.exit(EXIT_CODE_OK);
        }

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

	private static void updateIndexes(String indexDirectory, boolean forceIndexAccess, boolean useCallback) {
		loadPlugins();
		try {
			DocumentHandlerRegistry.registerDefaults();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(EXIT_CODE_ERROR_LOADING_DOCUMENT_HANDLERS);
		}
		if(indexDirectory == null) {
			indexDirectory = DoccoMainFrame.DEFAULT_INDEX_DIR;
		}
		List indexes = openIndexes(new File(indexDirectory), forceIndexAccess, useCallback);
		for (Iterator iter = indexes.iterator(); iter.hasNext();) {
		    Index index = (Index) iter.next();
            System.out.println(MessageFormat.format(CliMessages.getString("DoccoCommandLine.currentIndexMessage.text"), new Object[]{index.getName()}));
		    index.updateIndex(); 
		    while(index.isWorking()) { // do all indexes sequentially
		    	try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }
		}
		System.out.println(CliMessages.getString("DoccoCommandLine.updatesFinishedMessage.text")); //$NON-NLS-1$
	}

	private static void loadPlugins() {
		// TODO this is copy and paste from the DoccoMainFrame class -- somehow the error handling of
		// the plugin loader is not very nice for reuse, a callback mechanism would be better.
		String pluginsDirName = "plugins"; //$NON-NLS-1$
		String pluginsBaseDir = System.getProperty("user.dir") + File.separator; //$NON-NLS-1$
		try {
			PluginLoader.Error[] errors = PluginLoader.loadPlugins(new File(pluginsBaseDir + pluginsDirName));
			if (errors.length > 0) {
				String errorMsg = ""; //$NON-NLS-1$
				for (int i = 0; i < errors.length; i++) {
					PluginLoader.Error error = errors[i];
					errorMsg = MessageFormat.format(CliMessages.getString("DoccoCommandLine.pluginLoadingError.entryText"),  //$NON-NLS-1$
							new Object[]{error.getPluginLocation().getAbsolutePath(),error.getException().getMessage()});
					error.getException().printStackTrace();
				}
				System.err.println(CliMessages.getString("DoccoCommandLine.pluginLoadingError.header") + "\n" + errorMsg); //$NON-NLS-1$ //$NON-NLS-2$
				System.exit(EXIT_CODE_ERROR_LOADING_PLUGINS);
			}
		}
		catch (FileNotFoundException e) {
			System.err.println(CliMessages.getString("DoccoCommandLine.pluginDirectoryNotFoundError.text")); //$NON-NLS-1$
			System.exit(EXIT_CODE_ERROR_LOADING_PLUGINS);
		}
	}

    private static List openIndexes(File indexDirectory, boolean forceIndexAccess, boolean useCallback) {
    	// TODO copy and paste from DoccoMainFrame class
    	List retVal = new ArrayList();
        File[] indexLocations = indexDirectory.listFiles(new FileFilter(){
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
		Indexer.CallbackRecipient callbackRecipient;
		if(useCallback) {
			callbackRecipient = new Indexer.CallbackRecipient(){
				public void showFeedbackMessage(String message) {
					System.out.println("  " + message); //$NON-NLS-1$
				}
			};
		} else {
			callbackRecipient = Indexer.NullRecipient;
		}
        for (int i = 0; i < indexLocations.length; i++) {
            File file = indexLocations[i];
			try {
                Index index = Index.openIndex(file.getName(), indexDirectory, callbackRecipient, forceIndexAccess);
                retVal.add(index);
            } catch (Exception e) {
            	e.printStackTrace();
            	System.exit(EXIT_CODE_ERROR_OPENING_INDEX);
            }
        }
        return retVal;
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
