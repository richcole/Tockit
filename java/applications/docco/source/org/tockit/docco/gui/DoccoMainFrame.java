/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.toscanaj.controller.diagram.AttributeAdditiveNodeMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.NodeMovementEventListener;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DiagramToContextConverter;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.dbviewer.BrowserLauncher;
import net.sourceforge.toscanaj.gui.action.ExportDiagramAction;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.LabelView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.apache.lucene.queryParser.ParseException;
import org.tockit.canvas.CanvasBackground;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemEventFilter;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.documenthandler.DocumentHandlerRegistry;
import org.tockit.docco.fca.DiagramGenerator;
import org.tockit.docco.index.Index;
import org.tockit.docco.indexer.Indexer;
import org.tockit.docco.query.HitReference;
import org.tockit.docco.query.QueryDecomposer;
import org.tockit.docco.query.QueryEngine;
import org.tockit.docco.query.util.QueryWithResultSet;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.events.filters.EventFilter;
import org.tockit.events.filters.EventTypeFilter;
import org.tockit.events.filters.SubjectTypeFilter;
import org.tockit.plugin.PluginLoader;
import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * @TODO the results shown when selected nodes in nested diagrams are sometimes wrong (too many)
 */
public class DoccoMainFrame extends JFrame {
    private int indexingPriority;
    private File lastDirectoryIndexed;
    private static final int LOWEST_PRIORITY = Thread.MIN_PRIORITY;
	private static final int LOW_PRIORITY = (Thread.MIN_PRIORITY + Thread.NORM_PRIORITY)/2;
    private static final int MEDIUM_PRIORITY = Thread.NORM_PRIORITY;
	private static final int HIGH_PRIORITY = (Thread.NORM_PRIORITY + Thread.MAX_PRIORITY)/2;
    private static final int HIGHEST_PRIORITY = Thread.MAX_PRIORITY;

    private static final int MAXIMUM_QUERY_HISTORY_LENGTH = 10;
    private static final int VISIBLE_TREE_DEPTH = 2;
    private static final int DEFAULT_VERTICAL_DIVIDER_LOCATION = 600;
    private static final int DEFAULT_FRAME_WIDTH = 900;
    private static final int DEFAULT_FRAME_HEIGHT = 700;

	private static final String WINDOW_TITLE = "Docco";
    
    private static final ExtendedPreferences preferences = 
                            ExtendedPreferences.userNodeForClass(DoccoMainFrame.class);
	private static final String CONFIGURATION_VERTICAL_DIVIDER_LOCATION = "verticalDivider";
	private static final String CONFIGURATION_INDEX_LOCATION = "indexLocation";
	private static final String CONFIGURATION_LAST_INDEX_DIR = "lastIndexDir";
	private static final String CONFIGURATION_SHOW_PHANTOM_NODES_NAME = "showPhantomNodes";
	private static final String CONFIGURATION_SHOW_CONTINGENT_ONLY_NAME = "showContingentOnly";
	private static final String CONFIGURATION_INDEXING_PRIORITY_NAME = "indexingPriority";
    private static final String CONFIGURATION_QUERY_HISTORY = "queryHistory";
	private static final String DEFAULT_INDEX_DIR = System.getProperty("user.dir") + 
											System.getProperty("file.separator") + 
											".doccoIndex";
	
	private QueryEngine queryEngine;
	private List indexes = new ArrayList();

    private DocumentDisplayPane documentDisplayPane;
    private JTree hitList;
    private JComboBox queryField = new JComboBox();
	private JButton searchButton = new JButton("Submit");
	private JButton nestedSearchButton = new JButton("Nest");
	private JCheckBoxMenuItem showPhantomNodesCheckBox;
	private JCheckBoxMenuItem showContingentOnlyCheckBox;
	private JLabel statusBarMessage;
	private DiagramView diagramView;
	private Concept[] selectedConcepts;
	private JSplitPane viewsSplitPane;
    private DiagramExportSettings diagramExportSettings;
    private JMenuItem printMenuItem;
    private ExportDiagramAction exportDiagramAction;
    private JMenuItem printSetupMenuItem;
    private PageFormat pageFormat = new PageFormat();
    
	private class SelectionEventHandler implements EventBrokerListener {
		public void processEvent(Event event) {
			Object subject = event.getSubject();
			NodeView nodeView;
			if(subject instanceof NodeView) {
				nodeView = (NodeView) subject;
			} else if(subject instanceof LabelView) {
				LabelView labelView = (LabelView) subject;
				nodeView = labelView.getNodeView();
			} else {
				diagramView.setSelectedConcepts(null);
				selectedConcepts = null;
                fillTreeList();
				return;
			}
			DiagramNode node = nodeView.getDiagramNode();
			selectedConcepts = node.getConceptNestingList();
			diagramView.setSelectedConcepts(node.getConceptNestingList());
			fillTreeList();
		}
	}

    private void fillTreeList() {
        boolean allShown = false;
		if(this.selectedConcepts == null) {
            Diagram2D diagram = this.diagramView.getDiagram();
            if(diagram == null) {
                this.hitList.setModel(null);
                return;
            }
            this.selectedConcepts = new Concept[]{diagram.getTopConcept()};
            allShown = true;
		}
		Map pathToNodeMap = new Hashtable();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
		pathToNodeMap.put("",rootNode);

		Concept[] concepts = this.selectedConcepts;
		
		ConceptInterpretationContext context = createInterpretationContextForConcepts(concepts);
		if(allShown) {
		    context.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
		}
		
		ConceptInterpreter interpreter = new DirectConceptInterpreter();
		Iterator iterator = interpreter.getObjectSetIterator(this.selectedConcepts[0], context);

		while (iterator.hasNext()) {
			FCAElement object = (FCAElement) iterator.next();
            HitReference reference = (HitReference) (object).getData();
			String path = reference.getDocument().get(GlobalConstants.FIELD_DOC_PATH);
			StringTokenizer tokenizer = new StringTokenizer(path, File.separator);
			StringBuffer curPath = new StringBuffer();
			DefaultMutableTreeNode lastNode = rootNode;
			while(tokenizer.hasMoreTokens()) {
				String currentToken = tokenizer.nextToken();
				curPath.append(currentToken);
				if(tokenizer.hasMoreTokens()) {
					curPath.append(File.separator);
					DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) pathToNodeMap.get(curPath.toString());
					if(curNode == null) {
						curNode = new DefaultMutableTreeNode(curPath.toString()){
							public String toString() {
								DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) getParent();
								String userObjectString = getUserObject().toString();
                                return userObjectString.substring(parentNode.getUserObject().toString().length(), userObjectString.length() - 1);
                            }
						};
						lastNode.add(curNode);
						pathToNodeMap.put(curPath.toString(), curNode);
					}
					lastNode = curNode;
				} else {
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(reference){
						public String toString() {
							String result = super.toString();
                            return result.substring(result.lastIndexOf(File.separator) + 1);
                        }
					};
					lastNode.add(newNode);
				}
			}
		}
		
		// flatten all children of the root node, make copy to avoid concurrent modification troubles
		DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[rootNode.getChildCount()];
		int count = 0;
		Enumeration childrenEnum = rootNode.children();
		while (childrenEnum.hasMoreElements()) {
			children[count++] = (DefaultMutableTreeNode) childrenEnum.nextElement();
		}
		for (int i = 0; i < children.length; i++) {
			DefaultMutableTreeNode child = children[i];
			flattenResults(child);
		}
			
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		this.hitList.setModel(treeModel);
		unfoldTree(treeModel);
        
        if(allShown) {
            this.selectedConcepts = null;
        }
	}

	private ConceptInterpretationContext createInterpretationContextForConcepts(Concept[] concepts) {
        DiagramHistory diagramHistory = new DiagramHistory();
		Diagram2D diagram = this.diagramView.getDiagram();
		
		ConceptInterpretationContext context = new ConceptInterpretationContext(diagramHistory, new EventBroker());
		context.setObjectDisplayMode(this.showContingentOnlyCheckBox.isSelected());

		if(concepts.length == 2) {
		    NestedLineDiagram nestedDiagram = (NestedLineDiagram) diagram;
		    diagramHistory.addDiagram(nestedDiagram.getInnerDiagram());
		    diagramHistory.addDiagram(nestedDiagram.getOuterDiagram());
		    diagramHistory.setNestingLevel(1);
		    context = context.createNestedContext(concepts[1]);
		} else {
		    diagramHistory.addDiagram(diagram);
		}
        return context;
    }

    private void flattenResults(DefaultMutableTreeNode treeNode) {
		Enumeration childrenEnum;
		boolean done; 
		do {
			childrenEnum = treeNode.children();
			if(!childrenEnum.hasMoreElements()) { // leaf
				return; 
			}
			DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) childrenEnum.nextElement();
			done = true;
			if(!childrenEnum.hasMoreElements() && !firstChild.isLeaf()) { // single directory child
				// we need to copy references to the grandchildren first, since modification
				// of the children breaks the enumeration
				MutableTreeNode[] grandchildren = new MutableTreeNode[firstChild.getChildCount()];
				int count = 0;
				Enumeration grandchildrenEnum = firstChild.children();
				while (grandchildrenEnum.hasMoreElements()) {
					grandchildren[count++] = (MutableTreeNode) grandchildrenEnum.nextElement();
				}
				for (int i = 0; i < grandchildren.length; i++) {
					MutableTreeNode grandchild = grandchildren[i];
					treeNode.add(grandchild);
				}
				treeNode.setUserObject(firstChild.getUserObject());
				treeNode.remove(firstChild);
				done = false;
			}
		} while(!done);
		// same story as above: copy first
		DefaultMutableTreeNode[] children = new DefaultMutableTreeNode[treeNode.getChildCount()];
		int count = 0;
		childrenEnum = treeNode.children();
		while (childrenEnum.hasMoreElements()) {
			children[count++] = (DefaultMutableTreeNode) childrenEnum.nextElement();
		}
		for (int i = 0; i < children.length; i++) {
			DefaultMutableTreeNode child = children[i];
			flattenResults(child);
		}
	}
	
	private void unfoldTree(DefaultTreeModel treeModel) {
		List q = new LinkedList();
		q.add(treeModel.getRoot());
		
		while (! q.isEmpty()) {
			DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) q.remove(0);
			if (curNode.getLevel() < VISIBLE_TREE_DEPTH) {
				Enumeration children = curNode.children();
				while (children.hasMoreElements())  {
					TreeNode curChild = (TreeNode) children.nextElement();
					q.add(curChild);
				}
			}
			TreeNode[] pathToRoot = treeModel.getPathToRoot(curNode);
			hitList.expandPath(new TreePath(pathToRoot));
		}
	}
	
	public DoccoMainFrame(boolean forceIndexAccess) {
		super(WINDOW_TITLE);
		
		JComponent viewsComponent = buildViewsComponent();
		this.documentDisplayPane = new DocumentDisplayPane();
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(viewsComponent, BorderLayout.CENTER);
		mainPane.add(this.documentDisplayPane, BorderLayout.SOUTH);
								
        this.diagramExportSettings = new DiagramExportSettings();

        JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createViewMenu());
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(createHelpMenu());
		this.setJMenuBar(menuBar);
		
		this.statusBarMessage = new JLabel("Ready!");

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(mainPane, BorderLayout.CENTER);
		contentPane.add(this.statusBarMessage, BorderLayout.SOUTH);
		
		setContentPane(contentPane);

		loadDefaultSettings();
		/// @todo where should we call PluginLoader from?
		loadPlugins();
		
		this.indexingPriority = preferences.getInt(CONFIGURATION_INDEXING_PRIORITY_NAME, MEDIUM_PRIORITY);	
		String lastDirectory = preferences.get(CONFIGURATION_LAST_INDEX_DIR, null);
		if(lastDirectory != null) {
			this.lastDirectoryIndexed = new File(lastDirectory);
		}
		
        openIndexes(forceIndexAccess);

        this.setVisible(true);
        preferences.restoreWindowPlacement(this,
					new Rectangle(10, 10, DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT));

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeMainPanel();
			}
		});
        
        if(this.indexes.size() == 0) {
            createNewIndex();
        }
	}

	private void loadPlugins() {
		/// @todo this should be read from config manager?...
		String pluginsDirName = "plugins";

		String pluginsBaseDir = System.getProperty("user.dir") + File.separator;
		
		
		try {
			PluginLoader.Error[] errors = PluginLoader.loadPlugins(new File(pluginsBaseDir + pluginsDirName));
			if (errors.length > 0) {
				String errorMsg = "";
				for (int i = 0; i < errors.length; i++) {
					PluginLoader.Error error = errors[i];
					errorMsg += "Plugin location:\n\t" + error.getPluginLocation().getAbsolutePath();
					errorMsg += "\n";
					errorMsg += "Error:\n\t" + error.getException().getMessage();
					errorMsg += "\n\n";
					error.getException().printStackTrace();
				}
				JOptionPane.showMessageDialog(this, "There were errors loading plugins: \n" + errorMsg,
											"Error loading plugins", 
											JOptionPane.WARNING_MESSAGE);
			}
		}
		catch (FileNotFoundException e) {
			// no plugins -- we don't care
		}
	}
	
	private void loadDefaultSettings () {
		try {
			DocumentHandlerRegistry.registerDefaults();
		} catch (Exception e) {
			ErrorDialog.showError(this, e, "Error loading default Document Handlers");
		}
	}

    private void openIndexes(boolean forceIndexAccess) {
        File indexDirectory = getIndexDirectory();
        File[] indexLocations = indexDirectory.listFiles(new FileFilter(){
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
		Indexer.CallbackRecipient callbackRecipient = new Indexer.CallbackRecipient(){
			public void showFeedbackMessage(String message) {
				statusBarMessage.setText(message);
			}
		};
        for (int i = 0; i < indexLocations.length; i++) {
            File file = indexLocations[i];
			try {
                Index index = Index.openIndex(file.getName(), indexDirectory, callbackRecipient);
                this.indexes.add(index);
            } catch (Exception e) {
            	ErrorDialog.showError(this, e, "Error opening index");
            }
        }
		createQueryEngine();
		//@todo add force access into Index class
//        			if(!forceIndexAccess) {
//        				JOptionPane.showMessageDialog(this, "The index is locked. You can run only one instance of Docco at one time.\n" +
//        											  "If you want to override this error run Docco with the '-forceIndexAccess' option.",
//        											  "Index locked", JOptionPane.ERROR_MESSAGE);
//        				System.exit(1);
//        			}
//        			try {
//        				IndexReader.unlock(FSDirectory.getDirectory(indexLocation, false));
//        			} catch (IOException e) {
//        				// we just ignore that here -- Lucene throws exceptions about lock files that can't be deleted since
//        				// they are not there
//        			}
//        		}
    }

    private File getIndexDirectory() {
        String indexDirectoryLocation = preferences.get(CONFIGURATION_INDEX_LOCATION, DEFAULT_INDEX_DIR);
		File indexDirectory = new File(indexDirectoryLocation);
		if (!indexDirectory.exists()) {
			indexDirectory.mkdir();
		}
        return indexDirectory;
    }

    private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		
		final DoccoMainFrame outerThis = this;
        
		JMenuItem howtoItem = new JMenuItem("How to use Docco...");
		howtoItem.setMnemonic('h');
		howtoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				URL fileUrl = this.getClass().getClassLoader().getResource("doc/index.html");
				HtmlDisplayDialog.show(outerThis, "Docco Online Help", fileUrl, new Dimension(500, 700));
			}
		});
		helpMenu.add(howtoItem);

		JMenuItem aboutItem = new JMenuItem("About Docco...");
		aboutItem.setMnemonic('a');
		aboutItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				URL fileUrl = this.getClass().getClassLoader().getResource("doc/about.html");
				HtmlDisplayDialog.show(outerThis, "About Docco", fileUrl, new Dimension(600, 400));
			}
		});
		helpMenu.add(aboutItem);

		return helpMenu;
    }

    private JMenu createViewMenu() {
        JMenu diagramMenu = new JMenu("Diagram");
        diagramMenu.setMnemonic('d');
        
        this.showPhantomNodesCheckBox = new JCheckBoxMenuItem("Show all possible combinations");
        this.showPhantomNodesCheckBox.setMnemonic('p');
        this.showPhantomNodesCheckBox.setSelected(preferences.getBoolean(CONFIGURATION_SHOW_PHANTOM_NODES_NAME, true));
        this.showPhantomNodesCheckBox.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		/// @todo this is a bit brute force and will be confusing if the text field has changed since
        		/// the last query
        		doQuery(false);
        	}
        });
        diagramMenu.add(this.showPhantomNodesCheckBox);
        
        this.showContingentOnlyCheckBox = new JCheckBoxMenuItem("Show matches only once");
        this.showContingentOnlyCheckBox.setMnemonic('o');
		this.showContingentOnlyCheckBox.setSelected(preferences.getBoolean(CONFIGURATION_SHOW_CONTINGENT_ONLY_NAME, true));
        this.showContingentOnlyCheckBox.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		if(showContingentOnlyCheckBox.isSelected()) {
        			diagramView.setDisplayType(ConceptInterpretationContext.CONTINGENT);
        		} else {
        			diagramView.setDisplayType(ConceptInterpretationContext.EXTENT);
        		}
        		fillTreeList();
        	}
        });
        diagramMenu.add(this.showContingentOnlyCheckBox);
        
        diagramMenu.addSeparator();

        // we add the export options only if we can export at all
        /// @todo reduce duplicate code with ToscanaJMainPanel
        if (this.diagramExportSettings != null) {
            Frame frame = JOptionPane.getFrameForComponent(this);
            this.exportDiagramAction =
                new ExportDiagramAction(
                    frame,
                    this.diagramExportSettings,
                    this.diagramView,
                    KeyEvent.VK_E,
                    KeyStroke.getKeyStroke(
                        KeyEvent.VK_E,
                        ActionEvent.CTRL_MASK));
            diagramMenu.add(this.exportDiagramAction);
            this.exportDiagramAction.setEnabled(false);
            diagramMenu.addSeparator();
        }

        // menu item PRINT
        this.printMenuItem = new JMenuItem("Print...");
        this.printMenuItem.setMnemonic(KeyEvent.VK_P);
        this.printMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        this.printMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                printDiagram();
            }
        });
        this.printMenuItem.setEnabled(false);
        diagramMenu.add(this.printMenuItem);

        // menu item PRINT SETUP
        this.printSetupMenuItem = new JMenuItem("Print Setup...");
        this.printSetupMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                pageFormat = PrinterJob.getPrinterJob().pageDialog(pageFormat);
                printDiagram();
            }
        });
        this.printSetupMenuItem.setEnabled(true);
        diagramMenu.add(this.printSetupMenuItem);

        return diagramMenu;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("Indexing");
        fileMenu.setMnemonic('i');
		fileMenu.addMenuListener(new MenuListener(){
            public void menuSelected(MenuEvent e) {
            	fileMenu.removeAll();
                fillFileMenu(fileMenu);
            }
            public void menuDeselected(MenuEvent e) {
            	// nothing to do
            }
            public void menuCanceled(MenuEvent e) {
            	// nothing to do
            }
		});

        return fileMenu;
    }
    
	private void fillFileMenu(final JMenu fileMenu) {
		final JMenuItem newIndexItem = new JMenuItem("Index Directory...");
		newIndexItem.setMnemonic('i');
		newIndexItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				createNewIndex();
			}
		});
		fileMenu.add(newIndexItem);

		if(this.indexes.size() != 0) {
			fileMenu.addSeparator();
                
			for (Iterator iter = this.indexes.iterator(); iter.hasNext();) {
				Index currentIndex = (Index) iter.next();
				addIndexMenu(fileMenu, currentIndex);
			}
		}

		fileMenu.addSeparator();
		
		final JMenuItem updateAllItem = new JMenuItem("Update All");
		updateAllItem.setMnemonic('u');
		updateAllItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for (Iterator iter = indexes.iterator(); iter.hasNext();) {
                    Index index = (Index) iter.next();
                    updateIndex(index);
                }
            }
		});
		fileMenu.add(updateAllItem);
                
		final JMenu indexingPriorityMenu = new JMenu("Indexing Priority");
		indexingPriorityMenu.setMnemonic('p');
		final JRadioButtonMenuItem highestPriorityMenuItem = 
					new JRadioButtonMenuItem("Highest (fast indexing, computer might get less responsive)");
		highestPriorityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				indexingPriority = HIGHEST_PRIORITY;
				updateIndexPriorities();
			}
		});
		highestPriorityMenuItem.setSelected(indexingPriority == HIGHEST_PRIORITY);
		indexingPriorityMenu.add(highestPriorityMenuItem);
		final JRadioButtonMenuItem highPriorityMenuItem = 
					new JRadioButtonMenuItem("High");
		highPriorityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				indexingPriority = HIGH_PRIORITY;
				updateIndexPriorities();
			}
		});
		highPriorityMenuItem.setSelected(indexingPriority == HIGH_PRIORITY);
		indexingPriorityMenu.add(highPriorityMenuItem);
		final JRadioButtonMenuItem mediumPriorityMenuItem = 
					new JRadioButtonMenuItem("Medium");
		mediumPriorityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				indexingPriority = MEDIUM_PRIORITY;
				updateIndexPriorities();
			}
		});
		mediumPriorityMenuItem.setSelected(indexingPriority == MEDIUM_PRIORITY);
		indexingPriorityMenu.add(mediumPriorityMenuItem);
		final JRadioButtonMenuItem lowPriorityMenuItem = 
					new JRadioButtonMenuItem("Low");
		lowPriorityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				indexingPriority = LOW_PRIORITY;
				updateIndexPriorities();
			}
		});
		lowPriorityMenuItem.setSelected(indexingPriority == LOW_PRIORITY);
		indexingPriorityMenu.add(lowPriorityMenuItem);
		final JRadioButtonMenuItem lowestPriorityMenuItem = 
					new JRadioButtonMenuItem("Lowest (slow indexing, but computer stays responsive)");
		lowestPriorityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				indexingPriority = LOWEST_PRIORITY;
				updateIndexPriorities();
			}
		});
		lowestPriorityMenuItem.setSelected(indexingPriority == LOWEST_PRIORITY);
		indexingPriorityMenu.add(lowestPriorityMenuItem);
		fileMenu.add(indexingPriorityMenu);
                
		fileMenu.addSeparator();
		final JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('x');
		exitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
                closeMainPanel();
			}
		});
		fileMenu.add(exitItem);
	}

    protected void updateIndexPriorities() {
    	for (Iterator iter = this.indexes.iterator(); iter.hasNext();) {
            Index index = (Index) iter.next();
            index.setPriority(this.indexingPriority);
        }
    }

    private void addIndexMenu(final JMenu fileMenu, final Index currentIndex) {
    	final JMenu currentIndexMenu = new JMenu(currentIndex.getName());
		final JCheckBoxMenuItem indexActiveItem = new JCheckBoxMenuItem("Active");
		indexActiveItem.setMnemonic('a');
		indexActiveItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				currentIndex.setActive(!currentIndex.isActive());
				createQueryEngine();
			}
		});
		indexActiveItem.setSelected(currentIndex.isActive());
		currentIndexMenu.add(indexActiveItem);
		
		final JMenuItem updateIndexItem = new JMenuItem("Update Index");
		updateIndexItem.setMnemonic('u');
		updateIndexItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updateIndex(currentIndex);
			}
		});
		updateIndexItem.setEnabled(!currentIndex.isWorking());
		currentIndexMenu.add(updateIndexItem);
		
		final JMenuItem editFileMappingsItem = new JMenuItem("Edit File Mappings ...");
		editFileMappingsItem.setMnemonic('f');
		editFileMappingsItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				List result = editDocumentMappings(currentIndex.getDocumentMappings());
				if(result != null) {
					currentIndex.setDocumentMappings(result);
				}
			}
		});
		currentIndexMenu.add(editFileMappingsItem);
		
		final JMenuItem deleteIndexItem = new JMenuItem("Delete Index");
		deleteIndexItem.setMnemonic('d');
		deleteIndexItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				deleteIndex(currentIndex);
			}
		});
		currentIndexMenu.add(deleteIndexItem);
        fileMenu.add(currentIndexMenu);
    }

    protected void deleteIndex(Index currentIndex) {
    	// TODO this next dialog will have yes/no buttons, which are (a) not clear, and (b) will be internationalized
    	// we should use "Delete" and "Abort" instead
    	int rv = JOptionPane.showConfirmDialog(this, "A deleted index can be restored only by re-running\n" +
    		                                         "the whole indexing process. Are you sure you want to\n" +
    		                                         "delete the index '" + currentIndex.getName() + "'?", 
    		                                   "Delete Index?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    	if(rv != JOptionPane.YES_OPTION) {
    		return;
    	}
    	
    	this.indexes.remove(currentIndex);
    	try {
            currentIndex.delete();
        } catch (IOException e) {
        	ErrorDialog.showError(this, e, "Index not fully deleted");
        }
		createQueryEngine();
    }

    private void createNewIndex(){
		try {
            JFileChooser fileDialog = new JFileChooser(this.lastDirectoryIndexed);
            fileDialog.setDialogTitle("Select directory to index");
            fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileDialog.setMultiSelectionEnabled(false);
            
			final List documentMappings = new ArrayList(DocumentHandlerRegistry.getDefaultMappings());

            JPanel optionsPanel = new JPanel(new GridBagLayout());
            JTextField nameField = new JTextField("Index " + (this.indexes.size() + 1));
            JButton mappingsButton = new JButton("Edit Mappings...");
            mappingsButton.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e) {
                    List result = editDocumentMappings(documentMappings);
                    if(result != null) {
                    	documentMappings.clear();
                    	documentMappings.addAll(result);
                    }
                }
            });
            
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(0,6,0,0);
            
            constraints.gridy = 0;
            optionsPanel.add(new JLabel("Index name:"), constraints);
            
            constraints.gridy++;
            optionsPanel.add(nameField, constraints);
            
            constraints.gridy++;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.fill = GridBagConstraints.NONE;
            constraints.weighty = 1;
            optionsPanel.add(mappingsButton, constraints);
            
            fileDialog.setAccessory(optionsPanel);
            int rv = fileDialog.showDialog(this, "Index");
            if(rv != JFileChooser.APPROVE_OPTION) {
            	return;
            }
			this.lastDirectoryIndexed = fileDialog.getSelectedFile();

            // @todo the next bit should be in the file chooser
			String indexName = nameField.getText();
			for (Iterator iter = this.indexes.iterator(); iter.hasNext();) {
                Index index = (Index) iter.next();
                if(index.getName().equals(indexName)) {
                	JOptionPane.showMessageDialog(this, "Index name already in use, please choose another name.",
                	                              "Duplicate Name", JOptionPane.ERROR_MESSAGE);
                	createNewIndex();
                	return;
                }
            }
            							
			File inputDir = fileDialog.getSelectedFile().getCanonicalFile();
			Indexer.CallbackRecipient callbackRecipient = new Indexer.CallbackRecipient(){
				public void showFeedbackMessage(String message) {
					statusBarMessage.setText(message);
				}
			};
			File indexLocation = getIndexDirectory();
            this.indexes.add(Index.createIndex(indexName, indexLocation, inputDir, documentMappings, callbackRecipient));
        } catch (IOException e) {
			ErrorDialog.showError(this, e, "There has been an error creating a new index");
        }

		createQueryEngine();
    }

    private List editDocumentMappings(List documentMappings) {
        FileMappingsEditingDialog dialog = new FileMappingsEditingDialog(this, documentMappings);
        return dialog.getDocumentMappings();
	}

    private void createQueryEngine() {
		try {
			QueryDecomposer queryDecomposer = new QueryDecomposer(
													GlobalConstants.FIELD_QUERY_BODY);
			List activeIndexesList = new ArrayList();
			for (Iterator iter = this.indexes.iterator(); iter.hasNext();) {
                Index currentIndex = (Index) iter.next();
                if(currentIndex.isActive()) {
                	activeIndexesList.add(currentIndex);
                }
            }
            this.queryEngine =	new QueryEngine(((Index[]) activeIndexesList.toArray(new Index[activeIndexesList.size()])),
												queryDecomposer);
			this.diagramView.showDiagram(null);
			this.hitList.setModel(null);
            setMenuStates();
		} catch (IOException e1) {
			ErrorDialog.showError(this, e1, "Index not found");
			String[] options = new String[]{"Recreate Index", "Exit Program"};
			Object result = JOptionPane.showInputDialog(this, "There seems to be some error with the existing index.\n" +
										"It probably needs to be recreated.", "Index Problem",
										JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			if(result != options[0]) {
				System.exit(1);
			}
			createNewIndex();
		}
	}
	
	private JComponent buildQueryViewComponent() {
		JPanel queryPanel = new JPanel(new GridBagLayout());

        List queryHistory = preferences.getStringList(CONFIGURATION_QUERY_HISTORY);
        this.queryField = new JComboBox(queryHistory.toArray());
        
		Component editorComponent = this.queryField.getEditor().getEditorComponent();
        editorComponent.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
                setSearchEnabledStatus();
			}
		});

        this.queryField.getEditor().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doQuery(false);
            }
        });

        this.queryField.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    setSearchEnabledStatus();
                }
            }
        });
        
        this.queryField.setEditable(true);
        this.queryField.setSelectedItem(null);

		setSearchEnabledStatus();		

		this.searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				doQuery(false);
			}
		});
		this.nestedSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				doQuery(true);
			}
		});
		
		queryPanel.add(new JLabel("Search:"), 
							new GridBagConstraints( 0, 0, 1, 1, 0, 0,
													GridBagConstraints.WEST, GridBagConstraints.NONE,
													new Insets(5,5,5,5), 0, 0));
		queryPanel.add(this.queryField, 
							new GridBagConstraints( 1, 0, 1, 1, 1, 0,
								GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
								new Insets(5,5,5,5), 0, 0));
		queryPanel.add(this.searchButton, 
							new GridBagConstraints( 2, 0, 1, 1, 0, 0,
								GridBagConstraints.EAST, GridBagConstraints.NONE,
								new Insets(5,15,5,5), 0, 0));
		queryPanel.add(this.nestedSearchButton, 
							new GridBagConstraints( 3, 0, 1, 1, 0, 0,
								GridBagConstraints.EAST, GridBagConstraints.NONE,
								new Insets(5,5,5,15), 0, 0));
		return queryPanel;
	}
	
	private JComponent buildViewsComponent() {
		JComponent queryViewComponent = buildQueryViewComponent();
		queryViewComponent.setBorder(BorderFactory.createMatteBorder(0,0,1,0,SystemColor.controlDkShadow));

		this.diagramView = new DiagramView(){
			public String getToolTipText(MouseEvent me) {
				Point2D canvasPos = getCanvasCoordinates(me.getPoint());
				CanvasItem item = getCanvasItemAt(canvasPos);
				NodeView nodeView = null;
				if(item instanceof NodeView) {
					nodeView = (NodeView) item;
				}
				if(item instanceof LabelView) {
					LabelView labelView = (LabelView) item;
					nodeView = labelView.getNodeView();
				}
				if(nodeView == null) {
					return null;
				}
                DiagramNode node = nodeView.getDiagramNode();
				Concept[] concepts;
                if (node instanceof NestedDiagramNode) {
                    concepts = new Concept[]{node.getConcept()};
                } else {
                    concepts = node.getConceptNestingList();
                }
				ConceptInterpretationContext context = createInterpretationContextForConcepts(concepts);
				ConceptInterpreter interpreter = new DirectConceptInterpreter();

				StringBuffer tooltip = new StringBuffer();
				Iterator it = interpreter.getIntentIterator(node.getConcept(), context);
				if(!it.hasNext()) {
					return null;
				}
				while(it.hasNext()) {
					tooltip.append(it.next().toString());
					if(it.hasNext()) {
						tooltip.append("; ");
					}
				}
				return tooltip.toString();
			}
		};
		this.diagramView.setToolTipText("dummy to enable tooltips");
		this.diagramView.setConceptInterpreter(new DirectConceptInterpreter());
		ConceptInterpretationContext conceptInterpretationContext = new ConceptInterpretationContext(new DiagramHistory(),new EventBroker());
		boolean showContingentsOnly = preferences.getBoolean(CONFIGURATION_SHOW_CONTINGENT_ONLY_NAME, true);
		if(showContingentsOnly) {
			conceptInterpretationContext.setObjectDisplayMode(ConceptInterpretationContext.CONTINGENT);
		} else {
			conceptInterpretationContext.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
		}
		this.diagramView.setConceptInterpretationContext(
									conceptInterpretationContext);
		this.diagramView.setQuery(AggregateQuery.COUNT_QUERY);
		this.diagramView.setMinimumFontSize(12.0);
		
		EventBroker eventBroker = this.diagramView.getController().getEventBroker();
        eventBroker.subscribe(new SelectionEventHandler(),
									CanvasItemSelectedEvent.class,
									NodeView.class);
		eventBroker.subscribe(new SelectionEventHandler(),
									CanvasItemSelectedEvent.class,
									LabelView.class);
        eventBroker.subscribe(new SelectionEventHandler(),
                                    CanvasItemSelectedEvent.class,
                                    CanvasBackground.class);
        eventBroker.subscribe(new AttributeAdditiveNodeMovementEventListener(), 
                new EventFilter[] {
                        new SubjectTypeFilter(NodeView.class),
                        new EventTypeFilter(CanvasItemDraggedEvent.class),
                        new CanvasItemEventFilter(0, // do not test BUTTON1_DOWN since that would skip the drop event
                                                  InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | 
                                                  InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK |
                                                  InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK)
                });
        eventBroker.subscribe(new NodeMovementEventListener(), 
                new EventFilter[] {
                        new SubjectTypeFilter(NodeView.class),
                        new EventTypeFilter(CanvasItemDraggedEvent.class),
                        new CanvasItemEventFilter(InputEvent.SHIFT_DOWN_MASK,
                                                  InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | 
                                                  InputEvent.ALT_GRAPH_DOWN_MASK |
                                                  InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK)
                });

		// create a JTree with some model containing at least two elements. Otherwise the layout
		// is broken. True even for the JTree(Object[]) constructor. And the default constructor
		// is so not funny that it is funny again. Swing is always good for bad surprises.
		
		/// @todo we should use session management instead -- at the moment the width is still
		/// too small
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode someChildNode = new DefaultMutableTreeNode("child");
		rootNode.add(someChildNode);
		this.hitList = new JTree(rootNode);
		this.hitList.setModel(null);
		DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.hitList.setSelectionModel(selectionModel);
		this.hitList.setRootVisible(false);
		this.hitList.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = hitList.getSelectionPath();
				if(path == null) {
					documentDisplayPane.clearDisplay();
					return;
				}
				DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) path.getLastPathComponent();
				Object lastObject = lastNode.getUserObject();
				if(lastObject instanceof HitReference) {
					HitReference reference = (HitReference) lastObject;
					documentDisplayPane.displayDocument(reference);
				} else {
					documentDisplayPane.clearDisplay();
				}
			}
		});
		final DoccoMainFrame finalThis = this; 
		this.hitList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() != 2) {
					return;
				}
				if(e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) hitList.getLastSelectedPathComponent();
				if(node.getUserObject() instanceof HitReference) {
					HitReference reference = (HitReference) node.getUserObject();
					try {
						BrowserLauncher.openURL(reference.getDocument().get(GlobalConstants.FIELD_DOC_PATH));
					} catch (IOException ex) {
						ErrorDialog.showError(finalThis,ex,"Could not open document");
					}
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(this.hitList);

		JPanel leftPane = new JPanel(new BorderLayout());
		leftPane.add(queryViewComponent, BorderLayout.NORTH);
		leftPane.add(this.diagramView, BorderLayout.CENTER);
				
		viewsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
										   leftPane, scrollPane);
		viewsSplitPane.setOneTouchExpandable(true);
		viewsSplitPane.setDividerLocation(preferences.getInt(CONFIGURATION_VERTICAL_DIVIDER_LOCATION, 
										                    DEFAULT_VERTICAL_DIVIDER_LOCATION));
		viewsSplitPane.setResizeWeight(0.9);

		return viewsSplitPane;
	}
	
	private void setSearchEnabledStatus() {
		String queryString = getQueryString();
        if(queryString.length() == 0 ) {
            this.searchButton.setEnabled(false);
            this.nestedSearchButton.setEnabled(false);
            return;
        }
        this.searchButton.setEnabled(true);
        this.nestedSearchButton.setEnabled(this.diagramView.getDiagram() != null);
	}
	
	private String getQueryString() {
        String queryString = this.queryField.getEditor().getItem().toString();
        if(queryString == null) {
            queryString = "";
        }
        return queryString;
    }

    private void doQuery(boolean nestDiagram) {
        addEntryToQueryHistory();
		if(getQueryString().length() != 0) {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			QueryWithResultSet results;
            try {
                results = this.queryEngine.executeQueryUsingDecomposer(getQueryString());
				Diagram2D diagram = DiagramGenerator.createDiagram(results, this.showPhantomNodesCheckBox.isSelected());
                insertDiagramIntoView(diagram, nestDiagram);
            } catch (ParseException e) {
            	ErrorDialog.showError(this, e, "Couldn't parse query");
            	this.diagramView.showDiagram(null);
            } catch (IOException e) {
				ErrorDialog.showError(this, e, "Error querying");
				this.diagramView.showDiagram(null);
            }
            this.selectedConcepts = null;
            fillTreeList();
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            setMenuStates();
        }
	}
	
	private void insertDiagramIntoView(Diagram2D diagram, boolean nestDiagram) {
        Diagram2D oldDiagram = this.diagramView.getDiagram();
        DiagramHistory diagramHistory = new DiagramHistory();

        if(nestDiagram && oldDiagram != null) {
            if(diagram instanceof WriteableDiagram2D) {
                // attach event broker so we can use DiagramChangeEvents to update nested diagrams
                // this happens internally iff the event broker is set
                ((WriteableDiagram2D)diagram).setEventBroker(new EventBroker());
            }
            
        	// before nesting make sure apposition is ok by synchronizing object sets to their join
        	Iterator oldObjectSetIterator = oldDiagram.getTopConcept().getExtentIterator();
        	Set oldObjects = new HashSet();
        	while (oldObjectSetIterator.hasNext()) {
        		oldObjects.add(oldObjectSetIterator.next());
        	}
        	Iterator newObjectSetIterator = diagram.getTopConcept().getExtentIterator();
        	while (newObjectSetIterator.hasNext()) {
        		Object object = newObjectSetIterator.next();
        		if(oldObjects.contains(object)) {
        			// remove the common ones from the old set
        			oldObjects.remove(object);
        		} else {
        			// add the ones that are only in new to the top node of the old diagram if its intent is empty
        			// if the intent is not empty, the diagram needs to be recreated to have a matching concept.
        		    // Since the new diagram will have a concept with empty intent, this should happen only once.
        			// note that if there is intent which would match, the object should be in both diagrams in the
        			// first place
        			if(oldDiagram.getTopConcept().getIntentSize() == 0) {
        				((ConceptImplementation)oldDiagram.getTopConcept()).addObject(object);
        			} else {
        			    oldDiagram = extendDiagram(oldDiagram, object);
        			}
        		}
        	}
        	// now add the ones that are in the old diagram but not found in the new one to
        	// the new top concept
        	// this is again only happening if there is no intent attached to the top node, else
        	// we have to create a new diagram
        	for (Iterator iter = oldObjects.iterator(); iter.hasNext();) {
                if (diagram.getTopConcept().getIntentSize() == 0) {
                    ((ConceptImplementation) diagram.getTopConcept()).addObject(iter.next());
                } else {
                    diagram = extendDiagram(diagram, iter.next());
                }
            }
            assert oldDiagram.getTopConcept().getExtentSize() == diagram.getTopConcept().getExtentSize();
        	// nest the results
        	diagram = new NestedLineDiagram(oldDiagram, diagram);
            diagramHistory.addDiagram(oldDiagram);
            diagramHistory.addDiagram(diagram);
            diagramHistory.setNestingLevel(1);
        } else {
            diagramHistory.addDiagram(diagram);
            diagramHistory.setNestingLevel(0);
        }
        this.diagramView.showDiagram(diagram);
        ConceptInterpretationContext context = new ConceptInterpretationContext(diagramHistory, new EventBroker());
        this.diagramView.setConceptInterpretationContext(context);
    }

    private Diagram2D extendDiagram(Diagram2D oldDiagram, Object newObject) {
        ContextImplementation context = (ContextImplementation) DiagramToContextConverter.getContext(oldDiagram);
        context.getObjects().add(newObject);
        LatticeGenerator lgen = new GantersAlgorithm();
        return NDimLayoutOperations.createDiagram(lgen.createLattice(context),
                                                  oldDiagram.getTitle(),
                                                  new DefaultDimensionStrategy());
    }

    private void addEntryToQueryHistory() {
        Vector items = new Vector();
        String queryString = getQueryString();
        items.add(queryString);
        ComboBoxModel comboBoxModel = this.queryField.getModel();
        for(int i = 0; i < comboBoxModel.getSize(); i++) {
            Object item = comboBoxModel.getElementAt(i);
            if(!item.equals(queryString)) {
                items.add(item);
            }
            if(items.size() == MAXIMUM_QUERY_HISTORY_LENGTH) {
                break;
            }
        }
        this.queryField.setModel(new DefaultComboBoxModel(items));
    }

    private void setMenuStates() {
        boolean diagramAvailable = this.diagramView.getDiagram() != null;
        this.printMenuItem.setEnabled(diagramAvailable);
        if(this.exportDiagramAction != null) {
            this.exportDiagramAction.setEnabled(diagramAvailable);
        }
    }

    private void closeMainPanel() {
		// shut down indexers
		try {
			for (Iterator iter = this.indexes.iterator(); iter.hasNext();) {
                Index currentIndex = (Index) iter.next();
				currentIndex.shutdown();
            }
        } catch (Exception e) {
        	ErrorDialog.showError(this, e, "Could not shut down indexer");
        }
		
		// store current position
		preferences.storeWindowPlacement(this);
		
		preferences.putDouble("minLabelFontSize", this.diagramView.getMinimumFontSize());
		preferences.putInt(CONFIGURATION_VERTICAL_DIVIDER_LOCATION,
								this.viewsSplitPane.getDividerLocation());
								
		// store menu settings
		preferences.putInt(CONFIGURATION_SHOW_PHANTOM_NODES_NAME,
								this.showPhantomNodesCheckBox.isSelected()?1:0);
		preferences.putInt(CONFIGURATION_SHOW_CONTINGENT_ONLY_NAME,
								this.showContingentOnlyCheckBox.isSelected()?1:0);
		preferences.putInt(CONFIGURATION_INDEXING_PRIORITY_NAME,
								this.indexingPriority);
		
        // store other info
		if(this.lastDirectoryIndexed != null) {
			preferences.put(CONFIGURATION_LAST_INDEX_DIR,
											 this.lastDirectoryIndexed.getPath());
		}
        Collection queryHistory = new Vector();
        ComboBoxModel comboBoxModel = this.queryField.getModel();
        for(int i = 0; i < comboBoxModel.getSize(); i++) {
            queryHistory.add(comboBoxModel.getElementAt(i));
        }
        preferences.putStringList(CONFIGURATION_QUERY_HISTORY, queryHistory);

		System.exit(0);
	}

    /**
     * Prints the diagram using the current settings.
     *
     * If we don't have a diagram at the moment we just return.
     */
    protected void printDiagram() {
        if (this.diagramView.getDiagram() != null) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob.printDialog()) {
                try {
                    printJob.setPrintable(this.diagramView, pageFormat);
                    printJob.print();
                } catch (Exception e) {
                    ErrorDialog.showError(this, e, "Printing failed");
                }
            }
        }
    }

    private void updateIndex(final Index index) {
        try {
            if(index.isLocked()) {
                int result = JOptionPane.showOptionDialog(this, 
                        "The index is locked. This can occur if Docco was not closed properly,\n" +
                		"or if some other program accesses the index. You can remove the lock,\n" +
                		"but that might affect other programs you run on the same index.", 
                		"Index '" + index.getName() + "' locked",
                		JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, 
                		new String[]{"Remove lock", "Do not index"}, "Do not index");
                if(result != 0) {
                    return;
                }
                index.removeLock();
            }
        	index.updateIndex();
        } catch (Exception ex) {
        	ErrorDialog.showError(this, ex, "Error updating the index");
        }
    }
}