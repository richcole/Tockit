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
import java.io.IOException;
import java.net.URL;
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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.tockit.canvas.CanvasBackground;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;


import org.tockit.docco.ConfigurationManager;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.fca.DiagramGenerator;
import org.tockit.docco.index.Index;
import org.tockit.docco.indexer.Indexer;
import org.tockit.docco.query.HitReference;
import org.tockit.docco.query.QueryDecomposer;
import org.tockit.docco.query.QueryEngine;
import org.tockit.docco.query.util.QueryWithResultSet;



public class DoccoMainFrame extends JFrame {
	private JLabel statusBarMessage;
    private static final int VISIBLE_TREE_DEPTH = 2;
    private static final int DEFAULT_VERTICAL_DIVIDER_LOCATION = 600;
	private static final int DEFAULT_HORIZONTAL_DIVIDER_LOCATION = 500;
    private static final int DEFAULT_FRAME_WIDTH = 900;
    private static final int DEFAULT_FRAME_HEIGHT = 700;
	private static final String DEFAULT_INDEX_NAME = "default";

	private static final String WINDOW_TITLE = "Docco";
    
	private static final String CONFIGURATION_SECTION_NAME = "DoccoMainPanel";
	private static final String CONFIGURATION_VERTICAL_DIVIDER_LOCATION = "verticalDivider";
	private static final String CONFIGURATION_HORIZONTAL_DIVIDER_LOCATION = "horizontalDivider";
	private static final String CONFIGURATION_INDEX_NAME = "indexName";
	private static final String CONFIGURATION_LAST_INDEX_DIR = "lastIndexDir";
	
	private QueryEngine queryEngine;
	private Index index;

    private DocumentDisplayPane documentDisplayPane;
    private JTree hitList;
    private JTextField queryField = new JTextField();
	private JButton searchButton = new JButton("Submit");
	private JCheckBoxMenuItem showPhantomNodesCheckBox;
	private JCheckBoxMenuItem showContingentOnlyCheckBox;

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
		super("Docco");
		
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
		
		String indexLocation = GlobalConstants.INDEX_DIR + 
								ConfigurationManager.fetchString(CONFIGURATION_SECTION_NAME,
											CONFIGURATION_INDEX_NAME,
											DEFAULT_INDEX_NAME);

		Indexer.CallbackRecipient callbackRecipient = new Indexer.CallbackRecipient(){
			public void showFeedbackMessage(String message) {
				statusBarMessage.setText(message);
			}
		};

		if(IndexReader.indexExists(indexLocation)) {
			try {
				if(IndexReader.isLocked(indexLocation)) {
					if(!forceIndexAccess) {
						JOptionPane.showMessageDialog(this, "The index is locked. You can run only one instance of Docco at one time.\n" +
													  "If you want to override this error run Docco with the '-forceIndexAccess' option.",
													  "Index locked", JOptionPane.ERROR_MESSAGE);
						System.exit(1);
					}
					try {
						IndexReader.unlock(FSDirectory.getDirectory(indexLocation, false));
					} catch (IOException e) {
						// we just ignore that here -- Lucene throws exceptions about lock files that can't be deleted since
						// they are not there
					}
				}
				this.index = new Index(indexLocation, callbackRecipient);
				createQueryEngine();
				this.index.start();
			} catch (IOException e) {
				ErrorDialog.showError(this, e, "Couldn't access index -- program will exit");
				System.exit(1);
			}
		} else {
			createNewIndex();
			if(!IndexReader.indexExists(indexLocation)) {
				System.exit(1);
			}
		}

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
        this.showPhantomNodesCheckBox.setSelected(true);
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
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        
		JMenuItem newIndexItem = new JMenuItem("Create New Index...");
		newIndexItem.setMnemonic('n');
		newIndexItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				createNewIndex();
			}
		});
		fileMenu.add(newIndexItem);

		JMenuItem addFilesItem = new JMenuItem("Add Files Into Index...");
		addFilesItem.setMnemonic('a');
		addFilesItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				addFilesIntoIndex();
			}
		});
		fileMenu.add(addFilesItem);

		JMenuItem updateIndexItem = new JMenuItem("Update Index");
		updateIndexItem.setMnemonic('u');
		updateIndexItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updateIndex();
			}
		});
		fileMenu.add(updateIndexItem);

		fileMenu.addSeparator();

		JMenuItem editFileMappings = new JMenuItem("Edit File Mappings...");
		editFileMappings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				editFileMappings();
			}
		});
		fileMenu.add(editFileMappings);


		fileMenu.addSeparator();
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('x');
		exitItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);

        return fileMenu;
    }
    
    private void createNewIndex(){
		try {
			if(IndexReader.indexExists(this.index.getIndexLocation())) {
				int result = JOptionPane.showConfirmDialog(this, "This will delete the existing index. Continue?", "Delete Index?",
				                                           JOptionPane.OK_CANCEL_OPTION);
				if(result != JOptionPane.OK_OPTION) {
					return;
				}
			}
			
			this.index.stop();
			createDirPath(new File(this.index.getIndexLocation()));
            IndexWriter writer = new IndexWriter(
            						this.index.getIndexLocation(),
            						GlobalConstants.DEFAULT_ANALYZER,
            						true);
            writer.close();
            
			this.index.start();
        } catch (IOException e) {
			ErrorDialog.showError(this, e, "There has been an error creating a new index");
        }

		createQueryEngine();
		
		addFilesIntoIndex();
		
		this.diagramView.showDiagram(null);
    }

    private void addFilesIntoIndex() {
		JFileChooser fileDialog = new JFileChooser(getIndexLocation());
		fileDialog.setDialogTitle("Select directories and files to index");
		fileDialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileDialog.setMultiSelectionEnabled(true);
		int rv = fileDialog.showDialog(this, "Index");
		if(rv != JFileChooser.APPROVE_OPTION) {
			return;
		}
		ConfigurationManager.storeString(CONFIGURATION_SECTION_NAME, CONFIGURATION_LAST_INDEX_DIR,
									fileDialog.getSelectedFile().getAbsolutePath());

		this.index.addFilesToIndex(fileDialog.getSelectedFiles());
    }
    
    private void updateIndex() {
    	if(this.index.isWorking()) {
    		int result = JOptionPane.showConfirmDialog(this, "Do you want to stop the current indexing process?", 
						                			   "Indexer running", JOptionPane.YES_NO_OPTION);
			if(result != JOptionPane.YES_OPTION) {
				return;
			}
    	}
    	try {
            this.index.updateIndex();
        } catch (IOException e) {
        	ErrorDialog.showError(this, e, "Error updating the index");
        }
    }

	private void editFileMappings() {
		FileMappingsEditingDialog fileMappingsEditingDialog = 
						new FileMappingsEditingDialog(this, this.index.getDocHandlersRegistry());

		// @todo store changed info in config manager?
	}


	private String getIndexLocation() {
		return ConfigurationManager.fetchString(CONFIGURATION_SECTION_NAME,
											CONFIGURATION_LAST_INDEX_DIR,
											null);
	}

    private void createQueryEngine() {
		try {
			QueryDecomposer queryDecomposer = new QueryDecomposer(
													GlobalConstants.FIELD_QUERY_BODY, 
													GlobalConstants.DEFAULT_ANALYZER);
			this.queryEngine =	new QueryEngine(
												this.index.getIndexLocation(),
												GlobalConstants.FIELD_QUERY_BODY,
												GlobalConstants.DEFAULT_ANALYZER,
												queryDecomposer);
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
		conceptInterpretationContext.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
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
		// shut down indexer
		try {
            this.index.close();
        } catch (Exception e) {
        	ErrorDialog.showError(this, e, "Could not shut down indexer");
        }
		
		// store current position
		ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME, this);
		
		ConfigurationManager.storeFloat(CONFIGURATION_SECTION_NAME,	"minLabelFontSize",
								(float) this.diagramView.getMinimumFontSize());
		ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, CONFIGURATION_VERTICAL_DIVIDER_LOCATION,
								this.viewsSplitPane.getDividerLocation());
		ConfigurationManager.storeString(CONFIGURATION_SECTION_NAME, CONFIGURATION_INDEX_NAME, 
								DEFAULT_INDEX_NAME);
		
		ConfigurationManager.saveConfiguration();
		System.exit(0);
	}
	
	private void createDirPath(File file) {
		if (!file.exists()) {
			File parent = file.getParentFile();
			if (!parent.exists()) {
				createDirPath(parent);
			}
			else {
				file.mkdir();
			}
		}
	}
}