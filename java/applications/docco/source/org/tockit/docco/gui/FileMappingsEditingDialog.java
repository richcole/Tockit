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
	
	public FileMappingsEditingDialog(Frame parent) throws HeadlessException {
		super(parent, "Edit File Mappins Configuration", true);
		
		JPanel mainPanel = createMainPanel();
		mainPanel.setBorder(BorderFactory.createEtchedBorder());
		
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);		
	}

	private JPanel createMainPanel() {
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		
		JLabel heading = new JLabel("Not fully layed out yet (ugly layout), and there is no functionality attached");
		
		int row = 0;
		mainPanel.add(heading,new GridBagConstraints(0, row, 	// gridx, gridy
								GridBagConstraints.REMAINDER, 1, // gridwidth, gridheight
								0.5, 0.5,  						// weightx, weighty
								GridBagConstraints.WEST,	// anchor
								GridBagConstraints.NONE,	// fill
								new Insets(10, 5, 10, 5),	// insets
								0, 0						// ipadx, ipady
								)); 
		
		DefaultListModel listModel = new DefaultListModel();
		JList jlist = new JList(listModel);
		JScrollPane scrollPane = new JScrollPane(jlist);
		Dimension d = new Dimension(200, 200);
		scrollPane.setPreferredSize(d);
		scrollPane.setMinimumSize(d);

		JButton upButton = new JButton("Move Up");
		JButton downButton = new JButton("Move Down");

		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");

		row++;
		mainPanel.add(scrollPane,new GridBagConstraints(0, row, 
									1, 4, 
									0.4, 0.2,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH,
									new Insets(5, 5, 5, 5),
									0, 0
									)); 
			
		mainPanel.add(upButton,new GridBagConstraints(1, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 1, 5),
									0, 0
									)); 
		row++;											
		mainPanel.add(downButton,new GridBagConstraints(1, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(0, 5, 60, 5),
									0, 0
									)); 
		row++;
		mainPanel.add(addButton,new GridBagConstraints(1, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE,
									new Insets(60, 5, 1, 5),
									0, 0
									)); 
		row++;											
		mainPanel.add(removeButton,new GridBagConstraints(1, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.SOUTHWEST,
									GridBagConstraints.NONE,
									new Insets(0, 5, 5, 5),
									0, 0
									)); 
		
		JLabel fileFilterLabel = new JLabel("File Filter:");
		JLabel fileFilterValue = new JLabel();

		row++;
		mainPanel.add(fileFilterLabel,new GridBagConstraints(0, row, 
										1, 1, 
										0, 0,
										GridBagConstraints.WEST,
										GridBagConstraints.NONE,
										new Insets(10, 5, 1, 5),
										0, 0
										)); 
		row++;											
		mainPanel.add(fileFilterValue,new GridBagConstraints(0, row, 
										1, 1, 
										0, 0,
										GridBagConstraints.CENTER,
										GridBagConstraints.NONE,
										new Insets(10, 5, 1, 5),
										0, 0
										)); 
		
		JLabel docHandlerLabel = new JLabel("Document Handler:");
		JLabel docHandlerValue = new JLabel();
		
		row++;
		mainPanel.add(docHandlerLabel,new GridBagConstraints(0, row, 
											1, 1, 
											0, 0,
											GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(1, 5, 10, 5),
											0, 0
											)); 
		row++;											
		mainPanel.add(docHandlerValue,new GridBagConstraints(0, row, 
											1, 1, 
											0, 0,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(1, 5, 10, 5),
											0, 0
											)); 
		return mainPanel;
	}
	
	private JPanel createButtonsPanel () {
		JPanel panel = new JPanel();
		JButton okButton = new JButton("Save");
		JButton cancelButton = new JButton("Cancel");
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel.add(Box.createHorizontalGlue());
		panel.add(okButton);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(cancelButton);
		
		return panel;
	}
	

}
