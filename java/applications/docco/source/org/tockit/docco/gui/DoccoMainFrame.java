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
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.LabelView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.CanvasBackground;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;


import org.tockit.docco.GlobalConstants;
import org.tockit.docco.events.QueryEvent;
import org.tockit.docco.events.QueryFinishedEvent;
import org.tockit.docco.query.HitReference;
import org.tockit.docco.query.QueryWithResult;
import org.tockit.docco.query.util.HitReferencesSet;
import org.tockit.docco.query.util.HitReferencesSetImplementation;
import org.tockit.docco.query.util.QueryWithResultSet;



public class DoccoMainFrame extends JFrame {
	private DocumentDisplayPane documentDisplayPane;
    private JTree hitList;
    private JTextField queryField = new JTextField(20);
	private JButton searchButton = new JButton("Submit");
	private JCheckBox showPhantomNodesCheckBox = new JCheckBox("Show phantom nodes");

	private DiagramView diagramView;
	
	private EventBroker eventBroker;
	
	int width = 900;
	int height = 700;

	/**
	 * @todo the code in here implements a more general notion of creating a lattice
	 * diagramm from a set of attribute/set pairs. This is not specific to Docco and
	 * could be reused. In math terms it models the mapping:
	 * 
	 *   { (m, m') | m \in M } --> B(G,M,I) with 
	 *                                G = \bigcup_{m \in M} m'
	 *                                I = { (g,m) | g \in m' }
	 */	
	private class QueryFinishedEventHandler implements EventBrokerListener {
		public void processEvent(Event event) {
			QueryWithResultSet queryResultSet = (QueryWithResultSet) event.getSubject();
			
			Concept[] concepts = createConcepts(queryResultSet);

			Diagram2D diagram;
			if(showPhantomNodesCheckBox.isSelected()) {
				Point2D[] baseVectors = createBase(queryResultSet);
				diagram = createDiagram(concepts, baseVectors);
			} else {
				final Concept[] finalConcepts = reduceConceptsToRealizedOnes(concepts);
				Lattice lattice = new Lattice(){
					public Concept[] getConcepts() {
	                    return finalConcepts;
	                }
	                public Concept getBottom() {
                        return finalConcepts[finalConcepts.length-1];
                    }
					public Concept getTop() {
                        return finalConcepts[0];
                    }
				};
				diagram = NDimLayoutOperations.createDiagram(lattice,"Query results",new DefaultDimensionStrategy());
			}
			
			diagramView.showDiagram(diagram);
		}

		/**
		 * Note: this method has the side effect of changing the upsets and downsets
		 * of the concepts involved. Not suited for reuse unless this is fixed.
		 */
        private Concept[] reduceConceptsToRealizedOnes(Concept[] concepts) {
			List realizedConcepts = new ArrayList();

			outerLoop: for (int i = 0; i < concepts.length; i++) {
				Concept concept = concepts[i];
				// we still assume the binary encoding of the intent in the concept numbering
				for (int j = i + 1; j < concepts.length; j++) {
					ConceptImplementation subconcept = (ConceptImplementation) concepts[j];
					if( (i & j) != i ){
						continue; // not a subconcept
					}
					if(concept.getExtentSize() == subconcept.getExtentSize()) { // not realized
						// move attribute contingent down. We know there is an infimum on the
						// set of concepts with the same extent, so that is ok.
						Iterator it = concept.getAttributeContingentIterator();
						while(it.hasNext()) {
							Attribute attribute = (Attribute) it.next();
							subconcept.addAttribute(attribute);
						}
						continue outerLoop;
					}
				}
				realizedConcepts.add(concept);
			}

			for (Iterator it = realizedConcepts.iterator();it.hasNext();) {
                ConceptImplementation concept = (ConceptImplementation) it.next();
				concept.getUpset().retainAll(realizedConcepts);
				concept.getDownset().retainAll(realizedConcepts);
            }
            
            return (Concept[]) realizedConcepts.toArray(new Concept[realizedConcepts.size()]);
        }

        /**
		 * Creates base vectors as described in Frank Vogt's book on page 61.
		 * 
		 * $w_m:=(2^i-2^{n-i-1},-2^i-2^{n-i-1})$ 
		 * 
		 * Slight changes: we use the CS coordinate system (i.e. inverted Y) and
		 * we scale things a bit.
		 */
		public Point2D[] createBase(QueryWithResultSet queryResultSet) {
			final double scalex = 30;
			final double scaley = 15;
			
			int n = queryResultSet.size();
			Point2D[] baseVectors = new Point2D[n];
			for (int i = 0; i < baseVectors.length; i++) {
				double x = (1<<i) - (1<<(n-i-1));
				double y = (1<<i) + (1<<(n-i-1));
				baseVectors[i] = new Point2D.Double(scalex * x, scaley * y);
			}
			return baseVectors;
		}

		public WriteableDiagram2D createDiagram(Concept[] concepts,	Point2D[] baseVectors) {
			WriteableDiagram2D diagram = new SimpleLineDiagram();
			DiagramNode[] nodes = new DiagramNode[concepts.length];
			for (int i = 0; i < concepts.length; i++) {
				double x = 0;
				double y = 0;
				for (int j = 0; j < baseVectors.length; j++) {
					int currentBit = 1<<j;
					if ((i & currentBit) == currentBit) {
						x += baseVectors[j].getX();
						y += baseVectors[j].getY();
					}
				}
				Point2D pos = new Point2D.Double(x,y);
				nodes[i] = new DiagramNode(diagram, String.valueOf(i), pos, concepts[i],
													new LabelInfo(), new LabelInfo(), null);
				diagram.addNode(nodes[i]);
			}
			
			for (int i = 0; i < concepts.length - 1; i++) {
				for (int j = 0; j < baseVectors.length; j++) {
					int currentBit = 1<<j;
					if( (i | currentBit ) != i ) {
						diagram.addLine(nodes[i], nodes[i | currentBit]);
					}
				}
			}
			
			return diagram;
		}

		public Concept[] createConcepts(QueryWithResultSet queryResultSet) {
			HitReferencesSet allObjects = new HitReferencesSetImplementation();
			for (Iterator iter = queryResultSet.iterator(); iter.hasNext();) {
				QueryWithResult queryWithResult = (QueryWithResult) iter.next();
				allObjects.addAll(queryWithResult.getResultSet());
			}
			
			QueryWithResult[] queryResults = queryResultSet.toArray();
			
			int n = queryResultSet.size();
			int numConcepts = 1<<n; // 2 to the power of n
			
			ConceptImplementation[] concepts = new ConceptImplementation[numConcepts];
			for (int i = 0; i < concepts.length; i++) {
				concepts[i] = new ConceptImplementation();
			}
			
			for (int i = 0; i < concepts.length; i++) {
				ConceptImplementation concept = concepts[i];
				HitReferencesSet objectContingent = 
							new HitReferencesSetImplementation(new HashSet(allObjects.toSet()));
				for (int j = 0; j < n; j++) {
					int currentBit = 1<<j;
					if (i == currentBit) {
						concept.addAttribute(new Attribute(queryResults[j].getQuery()));
					}
					HitReferencesSet currentHitReferences = queryResults[j].getResultSet();
					if ((i & currentBit) == currentBit) {
						objectContingent.retainAll(currentHitReferences);
					} else {
						objectContingent.removeAll(currentHitReferences);
					}
				}
				for (Iterator iter = objectContingent.iterator(); iter.hasNext();) {
					HitReference reference = (HitReference) iter.next();
					concept.addObject(reference);
				}
				for( int j =0; j < numConcepts; j ++) {
					if((i & j) == i) {
						concepts[i].addSubConcept(concepts[j]);
						concepts[j].addSuperConcept(concepts[i]);
					}
				}
			}
			return concepts;
		}
	}	
	
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
				hitList.setModel(null);
				return;
			}
			DiagramNode node = nodeView.getDiagramNode();
			Concept concept = node.getConcept();
			diagramView.setSelectedConcepts(new Concept[]{concept});
			
			fillTreeList(concept);
		}
	}

	private void fillTreeList(Concept concept) {
		Map pathToNodeMap = new Hashtable();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
		pathToNodeMap.put("",rootNode);
		
		String separator = File.separator;
		
		Iterator extentIterator = concept.getExtentIterator();
		int i = 0;
		while (extentIterator.hasNext()) {
			HitReference reference = (HitReference) extentIterator.next();
			String path = reference.getDocument().get(GlobalConstants.FIELD_DOC_PATH);
			StringTokenizer tokenizer = new StringTokenizer(path, separator);
			StringBuffer curPath = new StringBuffer();
			DefaultMutableTreeNode lastNode = rootNode;
			while(tokenizer.hasMoreTokens()) {
				String currentToken = tokenizer.nextToken();
				curPath.append(currentToken);
				if(tokenizer.hasMoreTokens()) {
					curPath.append(separator);
					DefaultMutableTreeNode curNode = (DefaultMutableTreeNode) pathToNodeMap.get(curPath.toString());
					if(curNode == null) {
						curNode = new DefaultMutableTreeNode(curPath.toString());
						lastNode.add(curNode);
						pathToNodeMap.put(curPath.toString(), curNode);
					}
					lastNode = curNode;
				} else {
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(reference);
					lastNode.add(newNode);
				}
			}
		}
		
		flattenResults(rootNode);
			
		this.hitList.setModel(new DefaultTreeModel(rootNode));
	}

	private void flattenResults(DefaultMutableTreeNode treeNode) {
		Enumeration children;
		boolean done; 
		do {
			children = treeNode.children();
			if(!children.hasMoreElements()) { // leaf
				return; 
			}
			DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) children.nextElement();
			done = true;
			if(!children.hasMoreElements()) { // single child
				treeNode.setUserObject(firstChild.getUserObject());
				treeNode.remove(firstChild);
				Enumeration grandchildren = firstChild.children();
				while (grandchildren.hasMoreElements()) {
					MutableTreeNode grandchild = (MutableTreeNode) grandchildren.nextElement();
					treeNode.add(grandchild);
				}
				done = false;
			}
		} while(!done);
		children = treeNode.children();
		while(children.hasMoreElements()) {
			flattenResults((DefaultMutableTreeNode) children.nextElement());
		}
	}

	public DoccoMainFrame (EventBroker eventBroker) {
		super("Docco");
		this.eventBroker = eventBroker;
		
		this.eventBroker.subscribe(new QueryFinishedEventHandler(), 
									QueryFinishedEvent.class, 
									QueryWithResultSet.class);
		
		JComponent queryViewComponent = buildQueryViewComponent();
		queryViewComponent.setBorder(BorderFactory.createRaisedBevelBorder());

		JComponent viewsComponent = buildViewsComponent();
		this.documentDisplayPane = new DocumentDisplayPane();
		
		JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, viewsComponent, documentDisplayPane);
		mainPane.setOneTouchExpandable(true);
		mainPane.setResizeWeight(0.9);
		
		getContentPane().add(queryViewComponent, BorderLayout.NORTH);
		getContentPane().add(mainPane, BorderLayout.CENTER);

		setSize(this.width, this.height);
		setBounds(new Rectangle(20, 20, this.width, this.height));

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	private JComponent buildQueryViewComponent() {
		JPanel queryPanel = new JPanel(new FlowLayout());

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
		
		this.showPhantomNodesCheckBox.setSelected(true);
		
		queryPanel.add(new JLabel("Search: "));
		queryPanel.add(this.queryField);
		queryPanel.add(this.searchButton);
		queryPanel.add(this.showPhantomNodesCheckBox);
		return queryPanel;
	}
	
	private JComponent buildViewsComponent() {
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
		ConceptInterpretationContext conceptInterpretationContext = 
					new ConceptInterpretationContext(new DiagramHistory(),new EventBroker());
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
		JScrollPane scrollPane = new JScrollPane(this.hitList);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
								   diagramView, scrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.9);

		return splitPane;
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
			this.eventBroker.processEvent(new QueryEvent(this.queryField.getText()));
			this.hitList.setModel(null);
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
