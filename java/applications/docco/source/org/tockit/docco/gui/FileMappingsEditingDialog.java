/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class FileMappingsEditingDialog extends JDialog {
	
	private DefaultListModel model = new DefaultListModel();
	private JButton upButton = new JButton("Move Up");
	private JButton downButton = new JButton("Move Down");
	private JButton addButton = new JButton("Add");
	private JButton removeButton = new JButton("Remove");
	private JLabel fileFilterDisplayLabel = new JLabel();
	private JLabel docHandlerDisplayLabel = new JLabel();

	private JButton okButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");
	
	
	public FileMappingsEditingDialog(Frame parent) throws HeadlessException {
		super(parent, "Edit File Mappins Configuration", true);
		
		getContentPane().add(createMainPanel(), BorderLayout.CENTER);
		getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);		
	}

	private JPanel createMainPanel() {
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JList jlist = new JList(model);
		JScrollPane scrollPane = new JScrollPane(jlist);
		Dimension d = new Dimension(200, 200);
		scrollPane.setPreferredSize(d);
		scrollPane.setMinimumSize(d);

		JPanel editingPanel = new JPanel(new GridBagLayout());
		
		int row = 0;
		JLabel headingLabel = new JLabel( "Specify document handlers for different file types ");

		editingPanel.add(headingLabel ,new GridBagConstraints(0, row, 	// gridx, gridy
								1, 1, 							// gridwidth, gridheight
								0.3, 0.3,  						// weightx, weighty
								GridBagConstraints.CENTER,	// anchor
								GridBagConstraints.BOTH,	// fill
								new Insets(5, 5, 5, 5),		// insets
								0, 0						// ipadx, ipady
								)); 

		row++;
		editingPanel.add(scrollPane ,new GridBagConstraints(0, row, 	// gridx, gridy
								1, 4, 							// gridwidth, gridheight
								0.4, 0.2,  						// weightx, weighty
								GridBagConstraints.CENTER,	// anchor
								GridBagConstraints.BOTH,	// fill
								new Insets(5, 5, 5, 5),		// insets
								0, 0						// ipadx, ipady
								)); 
		
		
		editingPanel.add(upButton,new GridBagConstraints(1, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 1, 5),
									0, 0
									)); 
		row++;											
		editingPanel.add(downButton,new GridBagConstraints(1, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(0, 5, 60, 5),
									0, 0
									)); 
		row++;
		editingPanel.add(addButton,new GridBagConstraints(1, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE,
									new Insets(60, 5, 1, 5),
									0, 0
									)); 
		row++;											
		editingPanel.add(removeButton,new GridBagConstraints(1, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE,
									new Insets(0, 5, 5, 5),
									0, 0
									)); 

		editingPanel.setBorder(BorderFactory.createTitledBorder(
								BorderFactory.createEtchedBorder(),
								" Edit File Filter Settings "));
		
		
		JPanel displayDetailsPanel = new JPanel (new GridBagLayout());
		
		JLabel fileFilterLabel = new JLabel("File Filter:");
		JLabel docHandlerLabel = new JLabel("Document Handler:");

		row = 0;
		displayDetailsPanel.add(fileFilterLabel,new GridBagConstraints(0, row, 
										1, 1, 
										0, 0,
										GridBagConstraints.NORTHWEST,
										GridBagConstraints.NONE,
										new Insets(10, 5, 1, 5),
										0, 0
										)); 
		displayDetailsPanel.add(fileFilterDisplayLabel,new GridBagConstraints(1, row, 
										GridBagConstraints.REMAINDER, 1, 
										0, 0,
										GridBagConstraints.CENTER,
										GridBagConstraints.NONE,
										new Insets(10, 5, 1, 5),
										0, 0
										)); 
		
		row++;
		displayDetailsPanel.add(docHandlerLabel,new GridBagConstraints(0, row, 
											1, 1, 
											0, 0,
											GridBagConstraints.NORTHWEST,
											GridBagConstraints.NONE,
											new Insets(1, 5, 10, 5),
											0, 0
											)); 
		displayDetailsPanel.add(docHandlerDisplayLabel,new GridBagConstraints(1, row, 
											GridBagConstraints.REMAINDER, 1, 
											0, 0,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(1, 5, 10, 5),
											0, 0
											)); 

		displayDetailsPanel.setBorder(BorderFactory.createTitledBorder(
								BorderFactory.createEtchedBorder(),
								" File Type Details: "));

		mainPanel.add(editingPanel, BorderLayout.CENTER);
		mainPanel.add(displayDetailsPanel, BorderLayout.SOUTH);
		
		return mainPanel;
	}
	
	private JPanel createButtonsPanel () {
		JPanel panel = new JPanel();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel.add(Box.createHorizontalGlue());
		panel.add(okButton);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(cancelButton);
		
		return panel;
	}
	

}
