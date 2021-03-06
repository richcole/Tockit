/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.tree.DefaultTreeModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.canvas.imagewriter.ImageGenerationException;
import org.tockit.cgs.model.ConceptualGraph;
import org.tockit.cgs.model.Instance;
import org.tockit.cgs.model.KnowledgeBase;
import org.tockit.cgs.model.Link;
import org.tockit.cgs.model.Node;
import org.tockit.cgs.model.Relation;
import org.tockit.cgs.model.Type;
import org.tockit.crepe.Crepe;
import org.tockit.crepe.controller.ConfigurationManager;
import org.tockit.crepe.gui.eventhandlers.InstanceListUpdateHandler;
import org.tockit.crepe.gui.eventhandlers.RelationHierachyUpdateHandler;
import org.tockit.crepe.gui.eventhandlers.TypeHierachyUpdateHandler;
import org.tockit.crepe.gui.treeviews.RelationHierachyTreeNode;
import org.tockit.crepe.gui.treeviews.RelationHierarchyTreeView;
import org.tockit.crepe.gui.treeviews.TypeHierachyTreeNode;
import org.tockit.crepe.gui.treeviews.TypeHierarchyTreeView;
import org.tockit.crepe.view.GraphView;
import org.tockit.events.EventBroker;
import org.tockit.util.IdPool;

/**
 * Crepe extensions:
 * @todo allow dragging types into the instance view to create new instances
 * @todo add context menus to the hierarchies to add new types and relations
 * @todo allow creating infima by selecting two or more types and then calling an item from the context menu
 * @todo add undo history
 * @todo add CGIF export, extend query button to allow CGIF queries
 * @todo investigate how universal and absurd type shall be handled when exporting PIG/CGIF format
 * @todo add FCG export ???
 *
 * Refactorings:
 * @todo fix ConfigurationManager to use different properties
 */
public class CrepeMainPanel extends JFrame {

    /**
     * The central event broker for the main panel
     */
    private EventBroker eventBroker;

    /**
     * The maximum number of files in the most recently used files list.
     */
    static private final int MaxMruFiles = 8;

    public int maxArity = 3;

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
    private Action exportPigAction;
    private Action addNodeAction;
    private Action addLinkAction;
    private Action clearGraphAction;
    private Action queryPigAction = null;

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
    private KnowledgeBase knowledgeBase;
    private JSplitPane rightLowerSplitPane;
    private JSplitPane rightSplitPane;
    private JSplitPane mainSplitPane;

    /**
     * Simple initialisation constructor.
     */
    public CrepeMainPanel() {
        super("Crepe");
        eventBroker = new EventBroker();
        knowledgeBase = new KnowledgeBase(eventBroker);

        // set the default diagram export options: the very first format, auto mode, we can't get the size here
        Iterator it = GraphicFormatRegistry.getIterator();

        // if we have at least one format we use it, if not the settings stay null and the export options should
        // not be enabled
        if (it.hasNext()) {
            this.diagramExportSettings = new DiagramExportSettings((GraphicFormat) it.next(), 0, 0, true);
        }

        maxArity = ConfigurationManager.fetchInt("CrepeMainPanel", "maxArity", 3);

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
        knowledgeBase = new KnowledgeBase(this.eventBroker);
        ConceptualGraph graph = new ConceptualGraph(knowledgeBase);

        //create canon
        Type person = new Type(knowledgeBase, "Person");
        Type place = new Type(knowledgeBase, "Place");
        Type city = new Type(knowledgeBase, "City", new Type[]{place});
        Type bus = new Type(knowledgeBase, "Bus");
        Type rock = new Type(knowledgeBase, "Rock");
        Type hard = new Type(knowledgeBase, "Hard");
        Type animal = new Type(knowledgeBase, "Animal");
        Type mat = new Type(knowledgeBase, "Mat");
        Instance john = new Instance(knowledgeBase, "John", person);
        Instance boston = new Instance(knowledgeBase, "Boston", city);
        Relation go = new Relation(knowledgeBase, "go", new Type[]{person, city, bus});
        Relation between = new Relation(knowledgeBase, "between", new Type[]{person, rock, place});
        Relation attribute = new Relation(knowledgeBase, "attribute", new Type[]{place, hard});
        Relation on = new Relation(knowledgeBase, "on", new Type[]{animal, mat});

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
            case 3:
                Node animalNode = new Node(knowledgeBase, animal, null, null);
                Node matNode = new Node(knowledgeBase, mat, null, null);
                Link onLink = new Link(knowledgeBase, on, new Node[]{animalNode, matNode});
                graph.addNode(animalNode);
                graph.addNode(matNode);
                graph.addLink(onLink);
                break;
            default:
                throw new RuntimeException("no such graph");
        }
        showKnowledgeBase();
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
        graphView.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JTree typeHierarchyView = new TypeHierarchyTreeView(
                                new DefaultTreeModel(new TypeHierachyTreeNode(Type.getUniversal(), null)));
        typeHierarchyView.setRootVisible(true);
        new TypeHierachyUpdateHandler(typeHierarchyView, this.eventBroker);

        JTabbedPane tabPane = new JTabbedPane();
        JTree[] relationHierarchyViews = new JTree[maxArity];
        for (int i = 0; i < maxArity; i++) {
            relationHierarchyViews[i] = new RelationHierarchyTreeView(
                            new DefaultTreeModel(new RelationHierachyTreeNode(Relation.getUniversal(i + 1), null)));
            relationHierarchyViews[i].setRootVisible(true);
            tabPane.add(getArityName(i + 1), new JScrollPane(relationHierarchyViews[i]));
            new RelationHierachyUpdateHandler(relationHierarchyViews[i], i + 1, this.eventBroker);
        }

        JList instanceListView = new InstanceList();
        new InstanceListUpdateHandler(instanceListView, this.eventBroker);

        rightLowerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabPane, new JScrollPane(instanceListView));
        rightLowerSplitPane.setResizeWeight(0);
        int div = ConfigurationManager.fetchInt("CrepeMainPanel", "lowerRightDivider", 200);
        rightLowerSplitPane.setDividerLocation(div);

        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(typeHierarchyView), rightLowerSplitPane);
        rightSplitPane.setResizeWeight(0);
        div = ConfigurationManager.fetchInt("CrepeMainPanel", "upperRightDivider", 200);
        rightSplitPane.setDividerLocation(div);

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphView, rightSplitPane);
        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setResizeWeight(1);
        div = ConfigurationManager.fetchInt("CrepeMainPanel", "mainDivider", 400);
        mainSplitPane.setDividerLocation(div);

        contentPane.add(this.toolbar, BorderLayout.NORTH);
        contentPane.add(mainSplitPane, BorderLayout.CENTER);
        setContentPane(contentPane);

        // restore old position
        ConfigurationManager.restorePlacement("CrepeMainPanel", this, new Rectangle(10, 10, 600, 450));

        showKnowledgeBase();
    }

    private String getArityName(int arity) {
        if(arity == 1) {
            return "unary";
        }
        if(arity == 2) {
            return "binary";
        }
        if(arity == 3) {
            return "ternary";
        }
        return String.valueOf(arity) + "-ary";
    }

    private void createActions() {
        createFileActions();
        createGraphActions();
        final String pigFile = ConfigurationManager.fetchString("PIG", "filename", null);
        final String pigCommandLine = ConfigurationManager.fetchString("PIG", "command", null);
        if( (pigFile != null) && (pigCommandLine != null) ) {
            queryPigAction = new AbstractAction("Query") {
                public void actionPerformed(ActionEvent e) {
                    exportPig(new File(pigFile));
                    executeCommand(pigCommandLine);
                }
            };
        }
    }

    private void executeCommand(String command) {
        try {
            // add command shell on Win32 platforms
            String osName = System.getProperty("os.name");
            if (osName.equals("Windows NT")) {
                command = "cmd.exe /C " + command;
            } else if (osName.equals("Windows 95")) {
                command = "command.com /C " + command;
            }

            Runtime rt = Runtime.getRuntime();
            rt.exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        this.clearGraphAction = new AbstractAction("Clear Graph") {
            public void actionPerformed(ActionEvent e) {
                clearGraph();
            }
        };
        this.clearGraphAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
        this.clearGraphAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK + ActionEvent.ALT_MASK));
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
        Node newNode = new Node(knowledgeBase, Type.UNIVERSAL, null, null);
        graph.addNode(newNode);
        this.graphView.updateContents();
    }

    private void clearGraph() {
        ConceptualGraph graph = new ConceptualGraph(knowledgeBase);
        this.graphView.showGraph(graph);
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
        this.exportPigAction = new AbstractAction("Export PIG...") {
            public void actionPerformed(ActionEvent e) {
                exportPig();
            }
        };
    }

    private void exportPig() {
        final JFileChooser saveDialog = new JFileChooser();
        int rv = saveDialog.showSaveDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        exportPig(saveDialog.getSelectedFile());
    }

    private void exportPig(File file) {
        try {
            /// @todo escape quotes and backslashes in names
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            IdPool refIdPool = new IdPool();
            Collection types = this.knowledgeBase.getTypes();
            if(types.size() == 0) {
                throw new RuntimeException("Need types!");
            }
            boolean first = true;
            for (Iterator iterator = types.iterator(); iterator.hasNext();) {
                Type type = (Type) iterator.next();
                Type[] supertypes = type.getDirectSupertypes();
                for (int i = 0; i < supertypes.length; i++) {
                    Type supertype = supertypes[i];
                    if(first) {
                        first = false;
                    }
                    else {
                        writer.println(",");
                    }
                    writer.print("\"" + type.getName() + "\" is-a \"" + supertype.getName() + "\"");
                }
            }
            Collection relations = this.knowledgeBase.getRelations();
            for (Iterator iterator = relations.iterator(); iterator.hasNext();) {
                Relation relation = (Relation) iterator.next();
                Relation[] superrelations = relation.getDirectSupertypes();
                for (int i = 0; i < superrelations.length; i++) {
                    Relation superrelation = superrelations[i];
                    writer.println(",");
                    writer.print("\"" + relation.getName() + "\" is-a \"" + superrelation.getName() + "\"");
                }
            }
            Collection individuals = this.knowledgeBase.getInstances();
            for (Iterator iterator = individuals.iterator(); iterator.hasNext();) {
                Instance instance = (Instance) iterator.next();
                Type type = instance.getType();
                writer.println(",");
                writer.print("\"" + instance.getIdentifier() + "\" is-a \"" + type.getName() + "\"");
                refIdPool.reserveId(instance.getIdentifier());
            }
            Hashtable extraReferents = new Hashtable();
            Collection graphs = this.knowledgeBase.getGraphs();
            for (Iterator iterator = graphs.iterator(); iterator.hasNext();) {
                ConceptualGraph graph = (ConceptualGraph) iterator.next();
                Node[] nodes = graph.getNodes();
                for (int i = 0; i < nodes.length; i++) {
                    Node node = nodes[i];
                    if( node.getReferent() == null ) {
                        extraReferents.put(node, "__v" + refIdPool.getFreeId());
                        writer.println(",");
                        writer.print("\"" + extraReferents.get(node) + "\" is-a \"" + node.getType().getName() + "\"");
                    }
                }
                Link[] links = graph.getLinks();
                for (int i = 0; i < links.length; i++) {
                    Link link = links[i];
                    Node[] references = link.getReferences();
                    if(references.length == 1) {
                        writer.println(",");
                        writer.print("\"" + getReferentIdentifier(references[0], extraReferents) + "\" attr \"" +
                                     link.getType().getName() + "\""
                        );
                    }
                    else if (references.length == 2) {
                        writer.println(",");
                        writer.print("\"" + getReferentIdentifier(references[0], extraReferents) + "\" \"" +
                                       link.getType().getName() + "\" \"" +
                                       getReferentIdentifier(references[1], extraReferents) + "\"");
                    }
                    else {
                        /// @todo give feedback that we don't do that
                    }
                }
            }
            writer.println(".");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            /// @todo do something
        }
    }

    private String getReferentIdentifier(Node reference, Hashtable extraReferents) {
        String identifier;
        if(reference.getReferent() != null) {
            identifier = reference.getReferent().getIdentifier();
        }
        else {
            identifier = (String) extraReferents.get(reference);
        }
        return identifier;
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
        serializer.setNewlines(true);

        FileOutputStream out = null;
        try {
			out = new FileOutputStream(this.currentFile);
            serializer.output(doc, out);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
        	this.knowledgeBase.getXMLElement().detach();
        	if(out != null) {
	            try {
					out.close();
				} catch (IOException e) {
					// nothing we can do here
					e.printStackTrace();
				}
        	}
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
        graphMenu.add(new JMenuItem(clearGraphAction));
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

        exampleItem = new JMenuItem("The Cat is on the mat");
        exampleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTestGraph(3);
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

        fileMenu.addSeparator();
        fileMenu.add(this.exportPigAction);

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
        toolbar.add(this.clearGraphAction);
        if( this.queryPigAction != null ) {
            toolbar.addSeparator();
            toolbar.add(this.queryPigAction);
        }
    }

    /**
     * Close Main Window (Exit the program).
     */
    private void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("CrepeMainPanel", this);
        ConfigurationManager.storeInt("CrepeMainPanel", "mainDivider", mainSplitPane.getDividerLocation());
        ConfigurationManager.storeInt("CrepeMainPanel", "upperRightDivider", rightSplitPane.getDividerLocation());
        ConfigurationManager.storeInt("CrepeMainPanel", "lowerRightDivider", rightLowerSplitPane.getDividerLocation());
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
			SAXBuilder parser = new SAXBuilder();
            Document document = parser.build(kbFile);

            Element rootElement = document.getRootElement();
            rootElement.detach();
            this.knowledgeBase = new KnowledgeBase(rootElement, this.eventBroker);

            showKnowledgeBase();
            ///@todo we have to remember the file in the MRU menu

        } catch (Exception e) {
            ///@todo give feedback
            e.printStackTrace();
        }
    }

    private void showKnowledgeBase() {
        Iterator graphIds = this.knowledgeBase.getGraphIds().iterator();
        if (graphIds.hasNext()) {
            ConceptualGraph graph = this.knowledgeBase.getGraph(graphIds.next().toString());
            this.graphView.showGraph(graph);
        }
        else {
            ConceptualGraph graph = new ConceptualGraph(knowledgeBase);
            this.graphView.showGraph(graph);
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
                        this.graphView, this.diagramExportSettings, saveDialog.getSelectedFile(), new Properties());
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

