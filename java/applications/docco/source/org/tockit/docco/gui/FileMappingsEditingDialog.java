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
		
		
		GridBagLayout gridbag = new GridBagLayout();
		JPanel mainPanel = new JPanel(gridbag);
		
		JLabel heading = new JLabel("Not fully layed out yet (ugly layout), and there is no functionality attached");
		
		gridbag.setConstraints(heading,new GridBagConstraints(0, 0, 	// gridx, gridy
											GridBagConstraints.REMAINDER, 1, // gridwidth, gridheight
											0.5, 0.5,					// weightx, weighty
											GridBagConstraints.WEST,	// anchor
											GridBagConstraints.NONE,	// fill
											new Insets(0, 0, 0, 0),	// insets
											0, 0						// ipadx, ipady
											)); 
		mainPanel.add(heading);
		
		DefaultListModel listModel = new DefaultListModel();
		JList jlist = new JList(listModel);
		JScrollPane scrollPane = new JScrollPane(jlist);
		gridbag.setConstraints(scrollPane,new GridBagConstraints(0, 1, 
											1, 8, 
											0.5, 0.5,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		mainPanel.add(scrollPane);
		
		JButton upButton = new JButton("Move Up");
		JButton downButton = new JButton("Move Down");
		gridbag.setConstraints(upButton,new GridBagConstraints(1, 1, 
											1, 1, 
											0.5, 0.5,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		gridbag.setConstraints(downButton,new GridBagConstraints(1, 2, 
											1, 1, 
											0.5, 0.5,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		mainPanel.add(upButton);
		mainPanel.add(downButton);
		
		
		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		gridbag.setConstraints(addButton,new GridBagConstraints(1, 7, 
											1, 1, 
											0.5, 0.5,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		gridbag.setConstraints(removeButton,new GridBagConstraints(1, 8, 
											1, 1, 
											0.5, 0.5,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		mainPanel.add(addButton);
		mainPanel.add(removeButton);
		
		
		JLabel fileFilterLabel = new JLabel("File Filter:");
		JLabel fileFilterValue = new JLabel();
		gridbag.setConstraints(fileFilterLabel,new GridBagConstraints(0, 9, 
											1, 1, 
											0.5, 0.5,
											GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		gridbag.setConstraints(fileFilterValue,new GridBagConstraints(0, 10, 
											1, 1, 
											0.5, 0.5,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		mainPanel.add(fileFilterLabel);
		mainPanel.add(fileFilterValue);
		
		
		JLabel docHandlerLabel = new JLabel("Document Handler:");
		JLabel docHandlerValue = new JLabel();
		gridbag.setConstraints(docHandlerLabel,new GridBagConstraints(0, 11, 
											1, 1, 
											0.5, 0.5,
											GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		gridbag.setConstraints(docHandlerValue,new GridBagConstraints(0, 12, 
											1, 1, 
											0.5, 0.5,
											GridBagConstraints.CENTER,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0),
											0, 0
											)); 
		mainPanel.add(docHandlerLabel);
		mainPanel.add(docHandlerValue);
		
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
