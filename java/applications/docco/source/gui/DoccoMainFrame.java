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

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import query.QueryWithResult;
import query.util.QueryWithResultSet;

import events.QueryEvent;
import events.QueryFinishedEvent;

public class DoccoMainFrame extends JFrame {
	private JTextField queryField = new JTextField(20);
	private JButton searchButton = new JButton("Submit");
	private JTextArea resultArea = new JTextArea(40, 80);
	
	private ListModel resListModel = new DefaultListModel();
	
	private EventBroker eventBroker;
	
	int width = 900;
	int height = 700;
	
	private class QueryFinishedEventHandler implements EventBrokerListener {
		public void processEvent(Event event) {
			QueryWithResultSet queryResultSet = (QueryWithResultSet) event.getSubject();
			StringBuffer str = new StringBuffer(1000000);
			Iterator it = queryResultSet.iterator();
			while (it.hasNext()) {
				QueryWithResult cur = (QueryWithResult) it.next();
				str.append("Query: " + cur.getQuery() + "\n");
				str.append("\t" + cur.getResultSet());
			}
			resultArea.setText(str.toString());
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
		JPanel diagramPanel = new JPanel();

		JList resList = new JList(this.resListModel);
		JScrollPane scrollPane = new JScrollPane(resList);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
								   diagramPanel, scrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(0.7);

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
