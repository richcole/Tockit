/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import query.HitReference;
import query.QueryWithResult;
import query.util.HitReferencesSet;
import query.util.HitReferencesSetImplementation;
import query.util.QueryWithResultSet;

import events.QueryEvent;
import events.QueryFinishedEvent;

public class DoccoMainFrame extends JFrame {
	private JTextField queryField = new JTextField(20);
	private JButton searchButton = new JButton("Submit");
	private JTextArea resultArea = new JTextArea(40, 80);
	
	private ListModel resListModel = new DefaultListModel();
	
	private DiagramView diagramView;
	
	private EventBroker eventBroker;
	
	int width = 900;
	int height = 700;
	
	private class QueryFinishedEventHandler implements EventBrokerListener {
		public void processEvent(Event event) {
			QueryWithResultSet queryResultSet = (QueryWithResultSet) event.getSubject();
			
			Concept[] concepts = createConcepts(queryResultSet);
			Point2D[] baseVectors = createBase(queryResultSet);
			
			WriteableDiagram2D diagram = createDiagram(concepts, baseVectors);
			
			diagramView.showDiagram(diagram);
		}

		public Point2D[] createBase(QueryWithResultSet queryResultSet) {
			final double scale = 200;
			
			int n = queryResultSet.size();
			Point2D[] baseVectors = new Point2D[n];
			for (int i = 0; i < baseVectors.length; i++) {
				double x = -1 + 2 * i / (n-1.0); // ranges from -1 to 1
				double y = (1 - Math.abs(x))* (-0.2) + 0.5;
				baseVectors[i] = new Point2D.Double(scale * x, scale * y);
			}
			return baseVectors;
		}

		public WriteableDiagram2D createDiagram(
			Concept[] concepts,
			Point2D[] baseVectors) {
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
	
	public DoccoMainFrame (EventBroker eventBroker) {
		super("Docco");
		this.eventBroker = eventBroker;
		
		this.eventBroker.subscribe(new QueryFinishedEventHandler(), QueryFinishedEvent.class, QueryWithResultSet.class);
		
		JComponent queryViewComponent = buildQueryViewComponent();
		queryViewComponent.setBorder(BorderFactory.createRaisedBevelBorder());

		JComponent viewsComponent = buildViewsComponent();
		viewsComponent.setBorder(BorderFactory.createRaisedBevelBorder());
		
		JComponent resultsComponent = buildResultAreaComponent();
		resultsComponent.setBorder(BorderFactory.createRaisedBevelBorder());
		
		getContentPane().add(queryViewComponent, BorderLayout.NORTH);
		getContentPane().add(viewsComponent, BorderLayout.CENTER);
		getContentPane().add(resultsComponent, BorderLayout.SOUTH);

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
		
		queryPanel.add(new JLabel("Search: "));
		queryPanel.add(this.queryField);
		queryPanel.add(this.searchButton);
		return queryPanel;
	}
	
	private JComponent buildViewsComponent() {
		this.diagramView = new DiagramView();
		this.diagramView.setConceptInterpreter(new DirectConceptInterpreter());
		ConceptInterpretationContext conceptInterpretationContext = 
					new ConceptInterpretationContext(new DiagramHistory(),new EventBroker());
		conceptInterpretationContext.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
		this.diagramView.setConceptInterpretationContext(
									conceptInterpretationContext);
		this.diagramView.setQuery(AggregateQuery.COUNT_QUERY);
		this.diagramView.setMinimumFontSize(12.0);

		JList resList = new JList(this.resListModel);
		JScrollPane scrollPane = new JScrollPane(resList);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
								   diagramView, scrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.9);

		return splitPane;
	}
	
	private JComponent buildResultAreaComponent() {
		JPanel resultsPanel = new JPanel();
		
		this.resultArea.setEditable(false);
		this.resultArea.setBorder(BorderFactory.createLoweredBevelBorder());

		JScrollPane scrollPane = new JScrollPane(this.resultArea);
		scrollPane.setPreferredSize(new Dimension(this.width - 40, this.height/4 - 40));
				
		resultsPanel.add(new JLabel("Search Results:"), BorderLayout.NORTH);
		resultsPanel.add(scrollPane, BorderLayout.CENTER);
		
		return resultsPanel;
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
			this.eventBroker.processEvent(new QueryEvent(this.queryField.getText(), true));
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
}