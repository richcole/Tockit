/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
import javax.swing.JTextField;
import javax.swing.ListModel;

public class DoccoMainFrame extends JFrame {
	JTextField queryField = new JTextField(20);
	JButton searchButton = new JButton("Submit");
	
	ListModel resListModel = new DefaultListModel();
	
	int width = 900;
	int height = 700;
	
	public DoccoMainFrame () {
		super("Docco");
		
		JComponent queryViewComponent = buildQueryViewComponent();
		queryViewComponent.setBorder(BorderFactory.createRaisedBevelBorder());
		JComponent viewsComponent = buildViewsComponent();
		viewsComponent.setBorder(BorderFactory.createRaisedBevelBorder());
		
		getContentPane().add(queryViewComponent, BorderLayout.NORTH);
		getContentPane().add(viewsComponent, BorderLayout.CENTER);

		setSize(this.width, this.height);
		setBounds(new Rectangle(20, 20, this.width, this.height));		
	}
	
	private JComponent buildQueryViewComponent() {
		JPanel queryPanel = new JPanel(new FlowLayout());
		
		this.queryField.setFocusable(true);
		
		this.queryField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent event) {
			}
			public void keyPressed(KeyEvent event) {
			}
			public void keyReleased(KeyEvent event) {
				if (queryField.getText().length() > 0) {
					if (event.getKeyCode() == KeyEvent.VK_ENTER) {
						doQuery();
					}
				}
			}
		});
		
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
	
	private void doQuery() {
		System.out.println("should execute a query here");
	}
}
