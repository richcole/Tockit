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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.adapters.DOMAdapter;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.*;

/**
 * @todo fix ConfigurationManager to use different properties
 */
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
    private Action saveFileAction;
    private Action saveFileAsAction;
    private Action exportGraphicAction;
    private Action addNodeAction;
    private Action addLinkAction;

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
     * Stores the file of the currently open file.
     */
    private File currentFile = null;

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
    private KnowledgeBase knowledgeBase = new KnowledgeBase();

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

    private void showTestGraph(int number) {
        knowledgeBase = new KnowledgeBase();
        ConceptualGraph graph = new ConceptualGraph(knowledgeBase);

        //create canon
        Type person = new Type(knowledgeBase, "Person");
        Type place = new Type(knowledgeBase, "Place");
        Type city = new Type(knowledgeBase, "City");
        city.addDirectSupertype(place);
        Type bus = new Type(knowledgeBase, "Bus");
        Type rock = new Type(knowledgeBase, "Rock");
        Type hard = new Type(knowledgeBase, "Hard");
        Instance john = new Instance(knowledgeBase, "John", person);
        Instance boston = new Instance(knowledgeBase, "Boston", city);
        Relation go = new Relation(knowledgeBase, "go", new Type[]{person, city, bus});
        Relation between = new Relation(knowledgeBase, "between", new Type[]{person, rock, place});
        Relation attribute = new Relation(knowledgeBase, "attribute", new Type[]{place, hard});

        switch (number) {
            case 1:
                Node johnNode = new Node(knowledgeBase, person, john, null);
                Node bostonNode = new Node(knowledgeBase, city, boston, null);
                Node busNode = new Node(knowledgeBase, bus, null, null);
                Link goLink = new Link(knowledgeBase, go, new Node[]{johnNode, bostonNode, busNode});
                graph.addNode(johnNode);
                graph.addNode(bostonNode);
                graph.addNode(busNode);
                graph.addLink(goLink);
                break;
            case 2:
                Node personNode = new Node(knowledgeBase, person, null, null);
                Node rockNode = new Node(knowledgeBase, rock, null, null);
                Node placeNode = new Node(knowledgeBase, place, null, null);
                Node hardNode = new Node(knowledgeBase, hard, null, null);
                Link betweenLink = new Link(knowledgeBase, between, new Node[]{personNode, rockNode, placeNode});
                Link attributeLink = new Link(knowledgeBase, attribute, new Node[]{placeNode, hardNode});
                graph.addNode(personNode);
                graph.addNode(rockNode);
                graph.addNode(placeNode);
                graph.addNode(hardNode);
                graph.addLink(betweenLink);
                graph.addLink(attributeLink);
                break;
            default:
                throw new RuntimeException("no such graph");
        }
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
        graphView.showGraph(new ConceptualGraph(knowledgeBase));
        contentPane.add(this.toolbar, BorderLayout.NORTH);
        contentPane.add(graphView, BorderLayout.CENTER);
        setContentPane(contentPane);

        // restore old position
        ConfigurationManager.restorePlacement("CrepeMainPanel", this, new Rectangle(10, 10, 600, 450));
    }

    private void createActions() {
        createFileActions();
        createGraphActions();
    }

    private void createGraphActions() {
        this.addNodeAction = new AbstractAction("Add Node") {
            public void actionPerformed(ActionEvent e) {
                addNode();
            }
        };
        this.addNodeAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
        this.addNodeAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
        this.addLinkAction = new AbstractAction("Add Link...") {
            public void actionPerformed(ActionEvent e) {
                addLink();
            }
        };
        this.addLinkAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
        this.addLinkAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
    }

    private void addLink() {
        ConceptualGraph graph = this.graphView.getGraphShown();
        Relation relation = RelationChooser.chooseRelation(this, knowledgeBase);
        if(relation == null) {
            return;
        }
        Type[] signature = relation.getSignature();
        Node[] nodes = new Node[signature.length];
        for (int i = 0; i < signature.length; i++) {
            Type type = signature[i];
            nodes[i] = new Node(knowledgeBase, type, null, null);
            graph.addNode(nodes[i]);
        }
        Link newLink = new Link(knowledgeBase, relation, nodes);
        graph.addLink(newLink);
        this.graphView.updateContents();
    }

    private void addNode() {
        ConceptualGraph graph = this.graphView.getGraphShown();
        Node newNode = new Node(knowledgeBase, knowledgeBase.UNIVERSAL, null, null);
        graph.addNode(newNode);
        this.graphView.updateContents();
    }

    private void createFileActions() {
        this.openFileAction = new AbstractAction("Open...") {
            public void actionPerformed(ActionEvent e) {
                openKnowledgeBase();
            }
        };
        this.openFileAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
        this.openFileAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        this.saveFileAction = new AbstractAction("Save") {
            public void actionPerformed(ActionEvent e) {
                saveKnowledgeBase(false);
            }
        };
        this.saveFileAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
        this.saveFileAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        this.saveFileAsAction = new AbstractAction("Save As...") {
            public void actionPerformed(ActionEvent e) {
                saveKnowledgeBase(true);
            }
        };
        this.saveFileAsAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
        this.exportGraphicAction = new AbstractAction("Export Diagram...") {
            public void actionPerformed(ActionEvent e) {
                exportImage();
            }
        };
        this.exportGraphicAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
        this.exportGraphicAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
    }

    private void saveKnowledgeBase(boolean forceDialog) {
        if (forceDialog || this.currentFile == null) {
            final JFileChooser saveDialog;
            if (this.currentFile != null) {
                // use position of last file for dialog
                saveDialog = new JFileChooser(this.currentFile);
            } else {
                saveDialog = new JFileChooser();
            }
            if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.currentFile = saveDialog.getSelectedFile();
            } else {
                return;
            }
        }
        Document doc = new Document(this.knowledgeBase.getXMLElement());

        XMLOutputter serializer = new XMLOutputter();

        serializer.setIndent("  "); // use two space indent
        serializer.setNewlines(false);

        try {
            FileOutputStream out = new FileOutputStream(this.currentFile);
            serializer.output(doc, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println(e);
        }
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
        buildFileMenu();
        buildGraphMenu();
        buildKnowledgeBaseMenu();
        buildExamplesMenu();
        menubar.add(Box.createHorizontalGlue());
        buildHelpMenu();
        this.menubar.updateUI();
    }

    private void buildGraphMenu() {
        JMenu graphMenu = new JMenu("Graph");
        graphMenu.setMnemonic(KeyEvent.VK_G);
        graphMenu.add(new JMenuItem(addNodeAction));
        graphMenu.add(new JMenuItem(addLinkAction));
        menubar.add(graphMenu);
    }

    private void buildKnowledgeBaseMenu() {
        JMenu kbMenu = new JMenu("Knowledge Base");
        kbMenu.setMnemonic(KeyEvent.VK_K);
        JMenuItem menuItem;
        menuItem = new JMenuItem("Edit Types...");
        menuItem.setEnabled(false);
        kbMenu.add(menuItem);
        menuItem = new JMenuItem("Edit Relations...");
        menuItem.setEnabled(false);
        kbMenu.add(menuItem);
        menubar.add(kbMenu);
    }

    private void buildHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutItem = new JMenuItem("About Crepe");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutItem);
        menubar.add(helpMenu);
    }

    private void buildExamplesMenu() {
        JMenu examplesMenu = new JMenu("Examples");
        examplesMenu.setMnemonic(KeyEvent.VK_E);

        JMenuItem exampleItem;
        exampleItem = new JMenuItem("John goes to Boston by bus");
        exampleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTestGraph(1);
            }
        });
        examplesMenu.add(exampleItem);

        exampleItem = new JMenuItem("Person between rock and hard place");
        exampleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTestGraph(2);
            }
        });
        examplesMenu.add(exampleItem);

        menubar.add(examplesMenu);
    }

    private void buildFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        menubar.add(fileMenu);
        fileMenu.add(this.openFileAction);
        fileMenu.add(this.saveFileAction);
        fileMenu.add(this.saveFileAsAction);

        // we add the export options only if we can export at all
        if (this.diagramExportSettings != null) {
            fileMenu.addSeparator();
            fileMenu.add(exportGraphicAction);

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
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeMainPanel();
            }
        });
        fileMenu.add(exitMenuItem);
    }

    /**
     *  Build the ToolBar.
     */
    private void buildToolBar() {
        toolbar = new JToolBar();
        toolbar.setFloatable(true);
        toolbar.add(this.openFileAction);
        toolbar.add(this.saveFileAction);
        toolbar.add(this.saveFileAsAction);
        toolbar.addSeparator();
        toolbar.add(this.addNodeAction);
        toolbar.add(this.addLinkAction);
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
            openDialog = new JFileChooser();
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
    protected void openKnowledgeBase(File kbFile) {
        // store current file
        this.currentFile = kbFile;
        // recreate the menus
        buildMenuBar();
        recreateMruMenu();

        try {
            FileInputStream in;
            in = new FileInputStream(this.currentFile);

            // parse schema with Xerxes
            DOMAdapter domAdapter = new org.jdom.adapters.XercesDOMAdapter();
            org.w3c.dom.Document w3cdoc = domAdapter.getDocument(in, false);

            // create JDOM document
            DOMBuilder builder =
                    new DOMBuilder("org.jdom.adapters.XercesDOMAdapter");
            Document document = builder.build(w3cdoc);

            Element rootElement = document.getRootElement();
            rootElement.detach();
            this.knowledgeBase = new KnowledgeBase(rootElement);

            Iterator graphIds = this.knowledgeBase.getGraphIds().iterator();
            if (graphIds.hasNext()) {
                ConceptualGraph graph = this.knowledgeBase.getGraph(graphIds.next().toString());
                this.graphView.showGraph(graph);
            }
            else {
                this.graphView.showGraph(new ConceptualGraph(knowledgeBase));
            }
            ///@todo we have to remember the file in the MRU menu
        } catch (Exception e) {
            ///@todo give feedback
            e.printStackTrace();
        }
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
//            this.diagramExportSettings.setImageSize(this.diagramView.getWidth(), this.diagramView.getHeight());
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

