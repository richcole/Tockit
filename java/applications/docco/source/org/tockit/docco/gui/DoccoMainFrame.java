/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.border.BevelBorder;
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

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.dbviewer.BrowserLauncher;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.LabelView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.apache.lucene.queryParser.ParseException;
import org.tockit.canvas.CanvasBackground;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.plugin.PluginLoader;
import org.tockit.plugin.PluginLoaderException;


import org.tockit.docco.ConfigurationManager;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.documenthandler.DocumentHandlerRegistry;
import org.tockit.docco.fca.DiagramGenerator;
import org.tockit.docco.index.Index;
import org.tockit.docco.indexer.Indexer;
import org.tockit.docco.query.HitReference;
import org.tockit.docco.query.QueryDecomposer;
import org.tockit.docco.query.QueryEngine;
import org.tockit.docco.query.util.QueryWithResultSet;

public class DoccoMainFrame extends JFrame {
    private int indexingPriority;
    private File lastDirectoryIndexed;
    private static final int LOWEST_PRIORITY = Thread.MIN_PRIORITY;
	private static final int LOW_PRIORITY = (Thread.MIN_PRIORITY + Thread.NORM_PRIORITY)/2;
    private static final int MEDIUM_PRIORITY = Thread.NORM_PRIORITY;
	private static final int HIGH_PRIORITY = (Thread.NORM_PRIORITY + Thread.MAX_PRIORITY)/2;
    private static final int HIGHEST_PRIORITY = Thread.MAX_PRIORITY;

    private static final int VISIBLE_TREE_DEPTH = 2;
    private static final int DEFAULT_VERTICAL_DIVIDER_LOCATION = 600;
    private static final int DEFAULT_FRAME_WIDTH = 900;
    private static final int DEFAULT_FRAME_HEIGHT = 700;

	private static final String WINDOW_TITLE = "Docco";
    
	private static final String CONFIGURATION_SECTION_NAME = "DoccoMainPanel";
	private static final String CONFIGURATION_VERTICAL_DIVIDER_LOCATION = "verticalDivider";
	private static final String CONFIGURATION_INDEX_LOCATION = "indexLocation";
	private static final String CONFIGURATION_LAST_INDEX_DIR = "lastIndexDir";
	private static final String CONFIGURATION_SHOW_PHANTOM_NODES_NAME = "showPhantomNodes";
	private static final String CONFIGURATION_SHOW_CONTINGENT_ONLY_NAME = "showContingentOnly";
	private static final String CONFIGURATION_INDEXING_PRIORITY_NAME = "indexingPriority";
	private static final String DEFAULT_INDEX_DIR = System.getProperty("user.dir") + 
											System.getProperty("file.separator") + 
											".doccoIndex";
	
	private QueryEngine queryEngine;
	private List indexes = new ArrayList();

    private DocumentDisplayPane documentDisplayPane;
    private JTree hitList;
    private JTextField queryField = new JTextField();
	private JButton searchButton = new JButton("Submit");
	private JCheckBoxMenuItem showPhantomNodesCheckBox;
	private JCheckBoxMenuItem showContingentOnlyCheckBox;
	private JLabel statusBarMessage;
	private DiagramView diagramView;
	private Concept selectedConcept;
	private JSplitPane viewsSplitPane;
	
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
				selectedConcept = null;
				hitList.setModel(null);
				return;
			}
			DiagramNode node = nodeView.getDiagramNode();
			selectedConcept = node.getConcept();
			diagramView.setSelectedConcepts(new Concept[]{selectedConcept});
			
			fillTreeList();
		}
	}

	private void fillTreeList() {
		if(this.selectedConcept == null) {
			this.hitList.setModel(null);
			return;
		}
		Map pathToNodeMap = new Hashtable();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
		pathToNodeMap.put("",rootNode);
		
		Iterator iterator;
		if(this.showContingentOnlyCheckBox.isSelected()) {
			iterator = this.selectedConcept.getObjectContingentIterator();
		} else {
			iterator = this.selectedConcept.getExtentIterator();
		}

		while (iterator.hasNext()) {
			HitReference reference = (HitReference) iterator.next();
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
								DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getParent();
								String userObjectString = getUserObject().toString();
                                return userObjectString.substring(parent.getUserObject().toString().length(), userObjectString.length() - 1);
                            }
						};
						lastNode.add(curNode);
						pathToNodeMap.put(curPath.toString(), curNode);
					}
					lastNode = curNode;
				} else {
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(reference){
						public String toString() {
							String path = super.toString();
                            return path.substring(path.lastIndexOf(File.separator) + 1);
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
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createViewMenu());
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(createHelpMenu());
		this.setJMenuBar(menuBar);
		
		JComponent viewsComponent = buildViewsComponent();
		this.documentDisplayPane = new DocumentDisplayPane();
		this.documentDisplayPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(viewsComponent, BorderLayout.CENTER);
		mainPane.add(this.documentDisplayPane, BorderLayout.SOUTH);
								
		this.statusBarMessage = new JLabel("Ready!");

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(mainPane, BorderLayout.CENTER);
		contentPane.add(this.statusBarMessage, BorderLayout.SOUTH);
		
		setContentPane(contentPane);

		loadDefaultSettings();
		/// @todo where should we call PluginLoader from?
		loadPlugins();
		
		this.indexingPriority = ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, 
															  CONFIGURATION_INDEXING_PRIORITY_NAME, MEDIUM_PRIORITY);	
		String lastDirectory = ConfigurationManager.fetchString(CONFIGURATION_SECTION_NAME, CONFIGURATION_LAST_INDEX_DIR,
										                        null);
		if(lastDirectory != null) {
			this.lastDirectoryIndexed = new File(lastDirectory);
		}
		
        openIndexes(forceIndexAccess);

		this.setVisible(true);
		ConfigurationManager.restorePlacement(
					CONFIGURATION_SECTION_NAME,
					this,
					new Rectangle(10, 10, DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT));

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeMainPanel();
			}
		});
	}

	private void loadPlugins() {
		/// @todo this should be read from config manager?...
		String pluginsDirName = "plugins";

		String pluginsBaseDir = System.getProperty("user.dir") + File.separator;
		
		File pluginsDirFile1 = new File(pluginsBaseDir + pluginsDirName);
		File pluginsDirFile2 = new File(pluginsBaseDir +
										"docco" +
										File.separator + 
										pluginsDirName);
		
		File[] pluginsBaseFiles = { pluginsDirFile1, pluginsDirFile2	};
		
		try {
			PluginLoader.Error[] errors = PluginLoader.loadPlugins(pluginsBaseFiles);
			if (errors.length > 0) {
				String errorMsg = "";
				for (int i = 0; i < errors.length; i++) {
					PluginLoader.Error error = errors[i];
					errorMsg += "Plugin location: " + error.getPluginLocation().getAbsolutePath();
					errorMsg += "\n";
					errorMsg += "Error: " + error.getException().getMessage();
					errorMsg += "\n\n";
					error.getException().printStackTrace();
				}
				JOptionPane.showMessageDialog(this, "There were errors loading plugins: \n" + errorMsg,
											"Error loading plugins", 
											JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (PluginLoaderException e) {
			ErrorDialog.showError(this, e, "Error loading plugins");
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
        String indexDirectoryLocation = ConfigurationManager.fetchString(CONFIGURATION_SECTION_NAME,
        														CONFIGURATION_INDEX_LOCATION,
																DEFAULT_INDEX_DIR);
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
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('v');
        
        this.showPhantomNodesCheckBox = new JCheckBoxMenuItem("Show all possible combinations");
        this.showPhantomNodesCheckBox.setMnemonic('p');
        this.showPhantomNodesCheckBox.setSelected(
        				ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, 
        											  CONFIGURATION_SHOW_PHANTOM_NODES_NAME, 1) == 1
        );
        this.showPhantomNodesCheckBox.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		/// @todo this is a bit brute force and will be confusing if the text field has changed since
        		/// the last query
        		doQuery();
        	}
        });
        viewMenu.add(this.showPhantomNodesCheckBox);
        
        this.showContingentOnlyCheckBox = new JCheckBoxMenuItem("Show matches only once");
        this.showContingentOnlyCheckBox.setMnemonic('o');
		this.showContingentOnlyCheckBox.setSelected(
						ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, 
													  CONFIGURATION_SHOW_CONTINGENT_ONLY_NAME, 0) == 1
	    );
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
        viewMenu.add(this.showContingentOnlyCheckBox);
        return viewMenu;
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
            }
            public void menuCanceled(MenuEvent e) {
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

		fileMenu.addSeparator();
                
        for (Iterator iter = this.indexes.iterator(); iter.hasNext();) {
            Index currentIndex = (Index) iter.next();
			addIndexMenu(fileMenu, currentIndex);
        }

		fileMenu.addSeparator();
		
		final JMenuItem updateAllItem = new JMenuItem("Update All");
		updateAllItem.setMnemonic('u');
		updateAllItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for (Iterator iter = indexes.iterator(); iter.hasNext();) {
                    Index index = (Index) iter.next();
                    index.updateIndex();
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
				System.exit(0);
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
    	final JFrame outerThis = this;
    	
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
				try {
					currentIndex.updateIndex();
				} catch (Exception ex) {
					ErrorDialog.showError(outerThis, ex, "Error updating the index");
				}
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
    }

    private void createNewIndex(){
		try {
            JFileChooser fileDialog = new JFileChooser(this.lastDirectoryIndexed);
            fileDialog.setDialogTitle("Select directory to index");
            fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileDialog.setMultiSelectionEnabled(false);
            
			final List documentMappings = new ArrayList(Arrays.asList(GlobalConstants.DEFAULT_MAPPINGS));

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
			this.lastDirectoryIndexed = fileDialog.getSelectedFile().getParentFile();

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
													GlobalConstants.FIELD_QUERY_BODY, 
													GlobalConstants.DEFAULT_ANALYZER);
			List activeIndexesList = new ArrayList();
			for (Iterator iter = this.indexes.iterator(); iter.hasNext();) {
                Index currentIndex = (Index) iter.next();
                if(currentIndex.isActive()) {
                	activeIndexesList.add(currentIndex);
                }
            }
            this.queryEngine =	new QueryEngine(((Index[]) activeIndexesList.toArray(new Index[activeIndexesList.size()])),
												GlobalConstants.FIELD_QUERY_BODY,
												GlobalConstants.DEFAULT_ANALYZER,
												queryDecomposer);
			this.diagramView.showDiagram(null);
			this.hitList.setModel(null);
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

		/**
		 * @todo check if this could be done more elegant, e.g. by listening to properties
		 */		
		this.queryField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {
				setSearchEnabledStatus();
			}
			public void keyPressed(KeyEvent arg0) {
			}
			public void keyReleased(KeyEvent arg0) {
			}
		});
		
		this.queryField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				doQuery();
			}
		});

		setSearchEnabledStatus();		

		this.searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				doQuery();
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
								new Insets(5,15,5,15), 0, 0));
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
				Concept concept = nodeView.getDiagramNode().getConcept();
				StringBuffer tooltip = new StringBuffer();
				Iterator it = concept.getIntentIterator();
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
		boolean showContingentsOnly = ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, 
									                                CONFIGURATION_SHOW_CONTINGENT_ONLY_NAME, 0) == 1;
		if(showContingentsOnly) {
			conceptInterpretationContext.setObjectDisplayMode(ConceptInterpretationContext.CONTINGENT);
		} else {
			conceptInterpretationContext.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
		}
		this.diagramView.setConceptInterpretationContext(
									conceptInterpretationContext);
		this.diagramView.setQuery(AggregateQuery.COUNT_QUERY);
		this.diagramView.setMinimumFontSize(12.0);
		
		this.diagramView.getController().getEventBroker().subscribe(new SelectionEventHandler(),
									CanvasItemSelectedEvent.class,
									NodeView.class);
		this.diagramView.getController().getEventBroker().subscribe(new SelectionEventHandler(),
									CanvasItemSelectedEvent.class,
									LabelView.class);
		this.diagramView.getController().getEventBroker().subscribe(new SelectionEventHandler(),
									CanvasItemSelectedEvent.class,
									CanvasBackground.class);
		this.diagramView.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

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
		viewsSplitPane.setDividerLocation(ConfigurationManager.fetchInt( 
										CONFIGURATION_SECTION_NAME, 
										CONFIGURATION_VERTICAL_DIVIDER_LOCATION, 
										DEFAULT_VERTICAL_DIVIDER_LOCATION));
		viewsSplitPane.setResizeWeight(0.9);

		return viewsSplitPane;
	}
	
	private void setSearchEnabledStatus() {
		if (this.queryField.getText().length() <= 0 ) {
			this.searchButton.setEnabled(false);
		}
		else {
			this.searchButton.setEnabled(true);
		}
	}
	
	private void doQuery() {
		if (this.searchButton.isEnabled()) {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			QueryWithResultSet results;
            try {
                results = this.queryEngine.executeQueryUsingDecomposer(this.queryField.getText());
				Diagram2D diagram = DiagramGenerator.createDiagram(results, this.showPhantomNodesCheckBox.isSelected());
				this.diagramView.showDiagram(diagram);
            } catch (ParseException e) {
            	ErrorDialog.showError(this, e, "Couldn't parse query");
            	this.diagramView.showDiagram(null);
            } catch (IOException e) {
				ErrorDialog.showError(this, e, "Error querying");
				this.diagramView.showDiagram(null);
            }
			this.hitList.setModel(null);
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
		ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME, this);
		
		ConfigurationManager.storeFloat(CONFIGURATION_SECTION_NAME,	"minLabelFontSize",
								(float) this.diagramView.getMinimumFontSize());
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_VERTICAL_DIVIDER_LOCATION,
								this.viewsSplitPane.getDividerLocation());
								
		// store menu settings
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_SHOW_PHANTOM_NODES_NAME,
								this.showPhantomNodesCheckBox.isSelected()?1:0);
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_SHOW_CONTINGENT_ONLY_NAME,
								this.showContingentOnlyCheckBox.isSelected()?1:0);
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_INDEXING_PRIORITY_NAME,
								this.indexingPriority);
		
		if(this.lastDirectoryIndexed != null) {
			ConfigurationManager.storeString(CONFIGURATION_SECTION_NAME, CONFIGURATION_LAST_INDEX_DIR,
											 this.lastDirectoryIndexed.getPath());
		}

		ConfigurationManager.saveConfiguration();
		System.exit(0);
	}
}