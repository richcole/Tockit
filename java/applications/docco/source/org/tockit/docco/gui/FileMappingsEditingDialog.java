/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tockit.docco.indexer.DocumentHandlerMapping;
import org.tockit.docco.indexer.DocumentHandlerRegistry;

public class FileMappingsEditingDialog extends JDialog {
	
	private DefaultListModel model;
	
	private DocumentHandlerRegistry docHandlersRegistry;
	
	private JButton upButton = new JButton("Move Up");
	private JButton downButton = new JButton("Move Down");
	private JButton addButton = new JButton("Add");
	private JButton removeButton = new JButton("Remove");

	private JList jlist;
	
	private JLabel fileFilterDisplayLabel = new JLabel();
	private JLabel docHandlerDisplayLabel = new JLabel();
	
	private JButton okButton;
	private JButton applyButton;
	
	
	private class MappingsListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, 
												int index, boolean isSelected, 
												boolean cellHasFocus) {
			
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			DocumentHandlerMapping mapping = (DocumentHandlerMapping) value;
			String text = mapping.getFileFilter().getDisplayName() + 
								" (" + mapping.getFileFilter().getFilteringString() + ") " + 
								": " + mapping.getHandler().getDisplayName();
			
			setText(text);
			
			String tooltipText = "File Filter accepting: " + mapping.getFileFilter().getFilteringString() +
								" is mapped to document handler: " + mapping.getHandler().getDisplayName();
			setToolTipText(tooltipText);  
			return this;
		}
	}
	
	public FileMappingsEditingDialog(Frame parent, DocumentHandlerRegistry registry) 
													throws HeadlessException {
		super(parent, "Edit File Mappings Configuration", true);
		this.docHandlersRegistry = registry;

		this.model = new DefaultListModel();
		loadListModelWithRegistryData(registry);
		
		getContentPane().add(createMainPanel(), BorderLayout.CENTER);
		getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
		
		setManipulatorButtonsStatus();
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);		
	}
	
	private void loadListModelWithRegistryData (DocumentHandlerRegistry registry) {
		Iterator it = registry.getDocumentMappingList().iterator();
		while (it.hasNext()) {
			DocumentHandlerMapping cur = (DocumentHandlerMapping) it.next();
			this.model.addElement(cur);
		}
	}

	private JPanel createMainPanel() {
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		upButton.setToolTipText("Move selected mapping up in the list");
		downButton.setToolTipText("Move selected mapping down in the list");
		addButton.setToolTipText("Add new mapping");
		removeButton.setToolTipText("Remove selected mapping");
		
		upButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = jlist.getSelectedIndex();
				if (index >= 1) {
					Object objectToMove = model.remove(index);
					model.add(index - 1, objectToMove);
					jlist.setSelectedIndex(index - 1);
					setSaveButtonsStatus(true);
				}
			}
		});


		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = jlist.getSelectedIndex();
				if (index + 1 < model.getSize()) {
					Object objectToMove = model.remove(index);
					model.add(index + 1, objectToMove);
					jlist.setSelectedIndex(index + 1);
					setSaveButtonsStatus(true);
				}
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createMapping();
				setSaveButtonsStatus(true);
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = jlist.getSelectedIndex();
				model.remove(selectedIndex);
				if (selectedIndex >= model.getSize()) {
					jlist.setSelectedIndex(model.getSize() - 1);
				}
				else {
					jlist.setSelectedIndex(selectedIndex);
				}
				setSaveButtonsStatus(true);
			}
		});

		jlist = new JList(this.model);
		jlist.setCellRenderer(new MappingsListCellRenderer());
		jlist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				setManipulatorButtonsStatus();
				displayMappingDetails();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(jlist);
		Dimension d = new Dimension(150, 200);
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
		editingPanel.add(scrollPane ,new GridBagConstraints(0, row, 	
								1, 4, 							
								0.4, 0.2,  						
								GridBagConstraints.CENTER,	
								GridBagConstraints.BOTH,	
								new Insets(5, 5, 5, 5),		
								0, 0						
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
										GridBagConstraints.NORTHWEST,
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
											GridBagConstraints.NORTHWEST,
											GridBagConstraints.NONE,
											new Insets(1, 5, 10, 5),
											0, 0
											)); 

		displayDetailsPanel.setBorder(BorderFactory.createTitledBorder(
								BorderFactory.createEtchedBorder(),
								" File Type Details: "));


		JPanel buttonsPanel = new JPanel();
		applyButton = new JButton("Apply");
		applyButton.setToolTipText("Save changes");
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] mappingArray = model.toArray();
				docHandlersRegistry.setDocumentMappingList(Arrays.asList(mappingArray));
				setSaveButtonsStatus(false);
			}
		});
		
		JButton restoreDefault = new JButton("Restore Default");
		restoreDefault.setToolTipText("Overwrite currently displayed mappings with default mapping settings.");
		restoreDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				docHandlersRegistry.restoreDefaultMappingList();
				model.clear();
				loadListModelWithRegistryData(docHandlersRegistry);
			}
		});
					

		buttonsPanel.setLayout(new GridBagLayout());
		buttonsPanel.add(applyButton,new GridBagConstraints(0, 0, 
											1, 1, 
											0, 0,
											GridBagConstraints.EAST,
											GridBagConstraints.NONE,
											new Insets(10, 5, 10, 5),
											0, 0
											)); 
		buttonsPanel.add(restoreDefault,new GridBagConstraints(1, 0, 
											1, 1, 
											0, 0,
											GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(10, 5, 10, 5),
											0, 0
											)); 

		buttonsPanel.setBorder(BorderFactory.createEtchedBorder());

		mainPanel.add(editingPanel, BorderLayout.NORTH);
		mainPanel.add(displayDetailsPanel, BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

		return mainPanel;
	}
	
	private JPanel createButtonsPanel () {
		JPanel panel = new JPanel();

		okButton = new JButton("OK");
		okButton.setToolTipText("Save changes and exit this dialog");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] mappingArray = model.toArray();
				docHandlersRegistry.setDocumentMappingList(Arrays.asList(mappingArray));
				setVisible(false);
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Close dialog without saving any changes that may have been made");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel.add(Box.createHorizontalGlue());
		panel.add(okButton);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(cancelButton);
		
		setSaveButtonsStatus(false);
		
		return panel;
	}
	
	private void setManipulatorButtonsStatus() {
		if (this.jlist.getSelectedIndex() == -1) {
			this.upButton.setEnabled(false);
			this.downButton.setEnabled(false);
			this.removeButton.setEnabled(false);
		}
		else {
			this.upButton.setEnabled(true);
			this.downButton.setEnabled(true);
			this.removeButton.setEnabled(true);			
		}
		this.addButton.setEnabled(true);
	}
	
	private void setSaveButtonsStatus (boolean dataIsChanged) {
		if (dataIsChanged) {
			okButton.setEnabled(dataIsChanged);
			applyButton.setEnabled(dataIsChanged);
		}
		else {
			okButton.setEnabled(dataIsChanged);
			applyButton.setEnabled(dataIsChanged);
		}		
	}
	
	
	private void displayMappingDetails () {
		if (this.jlist.getSelectedIndex() != -1) {
			DocumentHandlerMapping mapping = (DocumentHandlerMapping) this.jlist.getSelectedValue();
			this.fileFilterDisplayLabel.setText(mapping.getFileFilter().getDisplayName() 
										+ ": " + mapping.getFileFilter().getFilteringString());
			this.docHandlerDisplayLabel.setText(mapping.getHandler().getDisplayName());
		}
		
	}
	
	private void createMapping() {
		CreateNewFileMappingDialog dialog = new CreateNewFileMappingDialog(this, docHandlersRegistry);
		DocumentHandlerMapping mapping = dialog.getCreatedMapping();
		if (mapping != null) {
			model.addElement(mapping);
		}
	}

}
