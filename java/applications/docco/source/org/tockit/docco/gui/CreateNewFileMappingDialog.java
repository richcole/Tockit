/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


public class CreateNewFileMappingDialog extends JDialog {

	public CreateNewFileMappingDialog(Dialog owner) throws HeadlessException {
		super(owner, "Create new mapping", true);
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		
		JRadioButton useAvailableFF = new JRadioButton();
		JRadioButton createNewFF = new JRadioButton();
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(useAvailableFF);
		buttonGroup.add(createNewFF);

		int row = 0;
		
		mainPanel.add(useAvailableFF,
								new GridBagConstraints(0, row, 	// gridx, gridy
								1, 1, 							// gridwidth, gridheight
								0, 0,  						// weightx, weighty
								GridBagConstraints.CENTER,	// anchor
								GridBagConstraints.BOTH,	// fill
								new Insets(5, 5, 5, 5),		// insets
								0, 0						// ipadx, ipady
								)); 
		mainPanel.add(new JLabel("Use available file filter: "),
									new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
									 
		mainPanel.add(new JComboBox(),
									new GridBagConstraints(2, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		
		row++;
		mainPanel.add(createNewFF,
									new GridBagConstraints(0, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		mainPanel.add(new JLabel("Create new file filter with following settings: "),
									new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		mainPanel.add(new JComboBox(),
									new GridBagConstraints(2, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		row++;
		mainPanel.add(new JLabel("and extension: "),
									new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		mainPanel.add(new JTextField(5),
									new GridBagConstraints(2, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 1, 5),
									0, 0
									));
		row++;
		mainPanel.add(new JLabel("Use the above file filter with document handler: "),
									new GridBagConstraints(0, row, 
									2, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		mainPanel.add(new JComboBox(),
									new GridBagConstraints(2, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		
		
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}


}
