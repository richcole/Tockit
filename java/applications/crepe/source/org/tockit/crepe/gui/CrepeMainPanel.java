/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui;

import org.tockit.events.EventBroker;
import org.tockit.canvas.imagewriter.*;
import org.tockit.crepe.controller.ConfigurationManager;
import org.tockit.crepe.Crepe;
import org.tockit.crepe.view.GraphView;
import org.tockit.cgs.model.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.*;

public class CrepeMainPanel extends JFrame implements ActionListener {

    /**
     * The central event broker for the main panel
     */
    private EventBroker eventBroker;

    /**
     * The maximum number of files in the most recently used files list.
     */
    static private final int MaxMruFiles = 8;

    /**
     * Our toolbar.
     */
    private JToolBar toolbar = null;

    /**
     * The main menu.
     */
    private JMenuBar menubar = null;

    // the actions used in the UI
    private Action openFileAction;
    private Action exportGraphAction;

    // menu items list
    // FILE menu
    private JMenuItem exportDiagramSetupMenuItem = null;
    private JMenuItem printMenuItem = null;
    private JMenuItem printSetupMenuItem = null;
    private JMenu mruMenu = null;
    private JMenuItem exitMenuItem = null;

    /**
     * Keeps a list of most recently files.
     */
    private List mruList = new LinkedList();

    /**
     * Stores the file name of the currently open file.
     */
    private String currentFile = null;

    /**
     * Stores the last file where an image was exported to.
     */
    private File lastImageExportFile = null;

    /**
     * The last setup for page format given by the user.
     */
    private PageFormat pageFormat = new PageFormat();
    private DiagramExportSettings diagramExportSettings;
    private GraphView graphView;

    /**
     * Simple initialisation constructor.
     */
    public CrepeMainPanel() {
        super("Crepe");
        eventBroker = new EventBroker();

        // register all image writers we want to support
        org.tockit.canvas.imagewriter.BatikImageWriter.initialize();
        org.tockit.canvas.imagewriter.JimiImageWriter.initialize();
        // set the default diagram export options: the very first format, auto mode, we can't get the size here
        Iterator it = GraphicFormatRegistry.getIterator();

        // if we have at least one format we use it, if not the settings stay null and the export options should
        // not be enabled
        if (it.hasNext()) {
            this.diagramExportSettings = new DiagramExportSettings((GraphicFormat) it.next(), 0, 0, true);
        }
        // then build the panel (order is important for checking if we want export options)
        buildPanel();

        showTestGraph();

        // restore the old MRU list
        mruList = ConfigurationManager.fetchStringList("CrepeMainPanel", "mruFiles", MaxMruFiles);
        // set up the menu for the MRU files
        recreateMruMenu();
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile = new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                openKnowledgeBase(schemaFile);
            }
        }
        // restore the last image export position
        String lastImage = ConfigurationManager.fetchString("CrepeMainPanel", "lastImageExport", null);
        if (lastImage != null) {
            this.lastImageExportFile = new File(lastImage);
        }

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    private void showTestGraph() {
        Type person = new Type("Person");
        Type city = new Type("City");
        Type vehicle = new Type("Vehicle");
        Type bus = new Type("Bus");
        bus.addSupertype(vehicle);
        Instance john = new Instance("John", person);
        Instance boston = new Instance("Boston", city);
        Instance greyhound = new Instance("Greyhound", bus);
        Node johnNode = new Node(person, john, null);
        Node bostonNode = new Node(city, boston, null);
        Node busNode = new Node(bus, greyhound, null);
        Relation go = new Relation("go", new Type[]{person, city, vehicle});
        Link goLink = new Link(go, new Node[]{johnNode, bostonNode, busNode});
        ConceptualGraph graph = new ConceptualGraph();
        graph.addNode(johnNode);
        graph.addNode(bostonNode);
        graph.addNode(busNode);
        graph.addLink(goLink);

        graphView.showGraph(graph);
    }

    /**
     * This constructor opens the file given as url in the parameter.
     *
     * Used when opening Crepe with a file name on the command line.
     */
    public CrepeMainPanel(String schemaFileURL) {
        // do the normal initialisation first
        this();
        // open the file
        openKnowledgeBase(new File(schemaFileURL));
    }

    /**
     * Build the GUI.
     */
    private void buildPanel() {
        createActions();
        buildMenuBar();
        setJMenuBar(menubar);
        buildToolBar();

        //Lay out the content pane.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        graphView = new GraphView(eventBroker);
        contentPane.add(this.toolbar, BorderLayout.NORTH);
        contentPane.add(graphView, BorderLayout.CENTER);
        setContentPane(contentPane);

        // restore old position
        ConfigurationManager.restorePlacement("CrepeMainPanel", this, new Rectangle(10, 10, 600, 450));
    }

    private void createActions() {
        this.openFileAction = new AbstractAction("Open...") {
            public void actionPerformed(ActionEvent e) {
                openKnowledgeBase();
            }
        };
        this.openFileAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
        this.openFileAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        this.exportGraphAction = new AbstractAction("Export Diagram...") {
            public void actionPerformed(ActionEvent e) {
                exportImage();
            }
        };
    }

    /**
     *  build the MenuBar
     */
    private void buildMenuBar() {
        if (menubar == null) {
            // create menu bar
            menubar = new JMenuBar();
        } else {
            menubar.removeAll();
        }

        // create the FILE menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        menubar.add(fileMenu);
        fileMenu.add(this.openFileAction);

        // we add the export options only if we can export at all
        if (this.diagramExportSettings != null) {
            fileMenu.add(exportGraphAction);

            // create the export diagram save options submenu
            this.exportDiagramSetupMenuItem = new JMenuItem("Export Diagram Setup...");
            this.exportDiagramSetupMenuItem.setMnemonic(KeyEvent.VK_S);
            this.exportDiagramSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
            this.exportDiagramSetupMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showImageExportOptions();
                }
            });
            fileMenu.add(exportDiagramSetupMenuItem);
        }

        // separator
        fileMenu.addSeparator();

        // menu item PRINT
        printMenuItem = new JMenuItem("Print...");
        printMenuItem.setMnemonic(KeyEvent.VK_P);
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        printMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printGraph();
            }
        });
        fileMenu.add(printMenuItem);

        // menu item PRINT SETUP
        printSetupMenuItem = new JMenuItem("Print Setup...");
        printSetupMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pageFormat = PrinterJob.getPrinterJob().pageDialog(pageFormat);
                printGraph();
            }
        });
        printSetupMenuItem.setEnabled(true);
        fileMenu.add(printSetupMenuItem);

        // separator
        fileMenu.addSeparator();

        // recent edited files will be in this menu
        mruMenu = new JMenu("Reopen");
        mruMenu.setMnemonic(KeyEvent.VK_R);
        fileMenu.add(mruMenu);

        // separator
        fileMenu.addSeparator();

        // menu item EXIT
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);

        // create a help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menubar.add(Box.createHorizontalGlue());
        menubar.add(helpMenu);

        JMenuItem aboutItem = new JMenuItem("About Crepe");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutItem);
        this.menubar.updateUI();
    }

    /**
     *  Build the ToolBar.
     */
    private void buildToolBar() {
        toolbar = new JToolBar();
        toolbar.setFloatable(true);
        toolbar.add(this.openFileAction);
    }

    /**
     * Close Main Window (Exit the program).
     */
    private void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("CrepeMainPanel", this);
        // save the MRU list
        ConfigurationManager.storeStringList("CrepeMainPanel", "mruFiles", this.mruList);
        // store last image export position
        if (this.lastImageExportFile != null) {
            ConfigurationManager.storeString("CrepeMainPanel", "lastImageExport", this.lastImageExportFile.getPath());
        }
        // and save the whole configuration
        ConfigurationManager.saveConfiguration();

        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) {
    }

    /**
     * Open a schema using the file open dialog.
     */
    protected void openKnowledgeBase() {
        final JFileChooser openDialog;
        if (this.currentFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.currentFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        openKnowledgeBase(openDialog.getSelectedFile());
    }

    /**
     * Open a file and parse it to create ConceptualSchema.
     */
    protected void openKnowledgeBase(File schemaFile) {
        // store current file
        try {
            this.currentFile = schemaFile.getCanonicalPath();
        } catch (IOException e) { // could not resolve canonical path
            e.printStackTrace();
            this.currentFile = schemaFile.getAbsolutePath();
            /// @todo what could be done here?
        }
        // recreate the menus
        buildMenuBar();
        recreateMruMenu();
    }

    /**
     * Recreates the menu of most recently used files and enables it if it is not
     * empty.
     */
    private void recreateMruMenu() {
        this.mruMenu.removeAll();
        boolean empty = true; // will be used to check if we have at least one entry
        if (this.mruList.size() > 0) {
            ListIterator it = mruList.listIterator(mruList.size() - 1);
            while (it.hasPrevious()) {
                String cur = (String) it.previous();
                if (cur.equals(currentFile)) {
                    // don't enlist the current file
                    continue;
                }
                empty = false;
                JMenuItem mruItem = new JMenuItem(cur);
                mruItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JMenuItem menuItem = (JMenuItem) e.getSource();
                        openKnowledgeBase(new File(menuItem.getText()));
                    }
                });
                this.mruMenu.add(mruItem);
            }
        }
        // we have now at least one file
        this.mruMenu.setEnabled(!empty);
    }

    /**
     * Ask the user for a file name and then exports the current diagram as graphic to it.
     *
     * If there is no diagram to print we will just return.
     *
     * @see #showImageExportOptions()
     */
    protected void exportImage() {
            if (this.lastImageExportFile == null) {
                this.lastImageExportFile = new File(System.getProperty("user.dir"));
            }
            final JFileChooser saveDialog = new JFileChooser(this.lastImageExportFile);
            int rv = saveDialog.showSaveDialog(this);
            if (rv == JFileChooser.APPROVE_OPTION) {
                this.lastImageExportFile = saveDialog.getSelectedFile();
                if (this.diagramExportSettings.usesAutoMode()) {
                    GraphicFormat format = GraphicFormatRegistry.getTypeByExtension(saveDialog.getSelectedFile());
                    if (format != null) {
                        this.diagramExportSettings.setGraphicFormat(format);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Sorry, no type with this extension known.\n" +
                                "Please use either another extension or try\n" +
                                "manual settings.",
                                "Export failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                try {
                    this.diagramExportSettings.getGraphicFormat().getWriter().exportGraphic(
                            this.graphView, this.diagramExportSettings, saveDialog.getSelectedFile());
                } catch (ImageGenerationException e) {
                    ErrorDialog.showError(this, e, "Exporting image error");
                } catch (OutOfMemoryError e) {
                    ErrorDialog.showError(this, "Out of memory", "Not enough memory available to export\n" +
                            "the diagram in this size");
                }
            }
    }

    /**
     * Shows the dialog to change the image export options.
     *
     * If the dialog is closed by pressing ok, the settings will be stored and an
     * export will be initiated.
     */
    protected void showImageExportOptions() {
        if (this.diagramExportSettings.usesAutoMode()) {
            this.diagramExportSettings.setImageSize(this.graphView.getWidth(), this.graphView.getHeight());
        }
        DiagramExportSettingsDialog.initialize(this, this.diagramExportSettings);
        DiagramExportSettings rv = DiagramExportSettingsDialog.showDialog(this);
        if (rv != null) {
            this.diagramExportSettings = rv;
            // probably the user wants to export now -- just give him the chance if possible
            exportImage();
        }
    }

    /**
     * Prints the diagram using the current settings.
     *
     * If we don't have a diagram at the moment we just return.
     */
    protected void printGraph() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        if (printJob.printDialog()) {
            try {
                printJob.setPrintable(this.graphView, pageFormat);
                printJob.print();
            } catch (Exception PrintException) {
                PrintException.printStackTrace();
            }
        }
    }

    protected void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "This is Crepe " + Crepe.VersionString + ".\n\n",
                "About Crepe",
                JOptionPane.PLAIN_MESSAGE);
    }
}

