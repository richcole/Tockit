/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.gui;

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
import java.util.List;

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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tockit.docco.indexer.DocumentHandlerMapping;

public class FileMappingsEditingDialog extends JDialog {
	
	private DefaultListModel model;
	
	private List documentMappings;
	
	private JButton upButton = new JButton("Move Up");
	private JButton downButton = new JButton("Move Down");
	private JButton addButton = new JButton("Add");
	private JButton removeButton = new JButton("Remove");

	private JList mappingListView;
	
	private JLabel fileFilterDisplayLabel = new JLabel();
	private JLabel docHandlerDisplayLabel = new JLabel();
	
	private JButton okButton;
	
	
	private class MappingsListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, 
												int index, boolean isSelected, 
												boolean cellHasFocus) {
			
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			DocumentHandlerMapping mapping = (DocumentHandlerMapping) value;
			String text = mapping.getFileFilter().getDescription() + 
								" -- " + mapping.getHandler().getDisplayName();
			
			setText(text);

			return this;
		}
	}
	
	public FileMappingsEditingDialog(Frame parent, List documentMappings) 
													throws HeadlessException {
		super(parent, "Edit File Mappings Configuration", true);
		this.documentMappings = documentMappings;

		this.model = new DefaultListModel();
		loadListModelWithRegistryData(documentMappings);
		
		JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.add(createMappingEditingPanel(),new GridBagConstraints(0, 0, 
										1, 1, 
										1, 1,
										GridBagConstraints.WEST,
										GridBagConstraints.BOTH,
										new Insets(5, 5, 5, 5),
										0, 0
										)); 
		contentPane.add(createMappingDetailsDisplayPanel(),new GridBagConstraints(0, 1, 
										1, 1, 
										1, 0,
										GridBagConstraints.NORTH,
										GridBagConstraints.HORIZONTAL,
										new Insets(5, 5, 5, 5),
										0, 0
										)); 
		contentPane.add(createButtonsPanel(),new GridBagConstraints(0, 2, 
										1, 1, 
										1, 0,
										GridBagConstraints.NORTH,
										GridBagConstraints.HORIZONTAL,
										new Insets(5, 5, 5, 5),
										0, 0
										));
		this.setContentPane(contentPane);

		setManipulatorButtonsStatus();
		
		pack();
		
		setLocationRelativeTo(parent);
		setVisible(true);		
	}
	
	private void loadListModelWithRegistryData (List mappings) {
		Iterator it = mappings.iterator();
		while (it.hasNext()) {
			DocumentHandlerMapping cur = (DocumentHandlerMapping) it.next();
			this.model.addElement(cur);
		}
	}

	private JPanel createMappingDetailsDisplayPanel() {
		int row;
		JPanel displayDetailsPanel = new JPanel (new GridBagLayout());
		
		JLabel fileFilterLabel = new JLabel("File Filter:");
		JLabel docHandlerLabel = new JLabel("Document Handler:");
		
		row = 0;
		displayDetailsPanel.add(fileFilterLabel,new GridBagConstraints(0, row, 
										1, 1, 
										0, 0,
										GridBagConstraints.WEST,
										GridBagConstraints.NONE,
										new Insets(5, 5, 5, 5),
										0, 0
										)); 
		displayDetailsPanel.add(fileFilterDisplayLabel,new GridBagConstraints(1, row, 
										1, 1, 
										1, 0,
										GridBagConstraints.WEST,
										GridBagConstraints.HORIZONTAL,
										new Insets(5, 5, 5, 5),
										0, 0
										)); 
		
		row++;
		displayDetailsPanel.add(docHandlerLabel,new GridBagConstraints(0, row, 
											1, 1, 
											0, 0,
											GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(5, 5, 5, 5),
											0, 0
											)); 
		displayDetailsPanel.add(docHandlerDisplayLabel,new GridBagConstraints(1, row, 
											1, 1, 
											1, 0,
											GridBagConstraints.WEST,
											GridBagConstraints.HORIZONTAL,
											new Insets(5, 5, 5, 5),
											0, 0
											)); 
		
		displayDetailsPanel.setBorder(BorderFactory.createTitledBorder(
								BorderFactory.createEtchedBorder(),
								" File Type Details: "));
								
		return displayDetailsPanel;
	}

	private JPanel createMappingEditingPanel() {
		upButton.setToolTipText("Move selected mapping up in the list");
		downButton.setToolTipText("Move selected mapping down in the list");
		addButton.setToolTipText("Add new mapping");
		removeButton.setToolTipText("Remove selected mapping");
		
		upButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mappingListView.getSelectedIndex();
				if (index >= 1) {
					Object objectToMove = model.remove(index);
					model.add(index - 1, objectToMove);
					mappingListView.setSelectedIndex(index - 1);
					setSaveButtonsStatus(true);
				}
			}
		});
		
		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = mappingListView.getSelectedIndex();
				if (index + 1 < model.getSize()) {
					Object objectToMove = model.remove(index);
					model.add(index + 1, objectToMove);
					mappingListView.setSelectedIndex(index + 1);
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
				int selectedIndex = mappingListView.getSelectedIndex();
				model.remove(selectedIndex);
				if (selectedIndex >= model.getSize()) {
					mappingListView.setSelectedIndex(model.getSize() - 1);
				}
				else {
					mappingListView.setSelectedIndex(selectedIndex);
				}
				setSaveButtonsStatus(true);
			}
		});
		
		mappingListView = new JList(this.model);
		mappingListView.setCellRenderer(new MappingsListCellRenderer());
		mappingListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mappingListView.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				setManipulatorButtonsStatus();
				displayMappingDetails();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(mappingListView);
		
		
		JPanel editingPanel = new JPanel(new GridBagLayout());
		
		int row = 0;
		JLabel headingLabel = new JLabel( "Specify document handlers for different file types ");
		
		editingPanel.add(headingLabel ,new GridBagConstraints(0, row, 	// gridx, gridy
								2, 1, 							// gridwidth, gridheight
								0, 0,  						// weightx, weighty
								GridBagConstraints.NORTHWEST,	// anchor
								GridBagConstraints.NONE,	// fill
								new Insets(5, 5, 5, 5),		// insets
								0, 0						// ipadx, ipady
								)); 
		
		row++;
		editingPanel.add(scrollPane ,new GridBagConstraints(0, row, 	
								1, 4, 							
								1, 1,  						
								GridBagConstraints.CENTER,	
								GridBagConstraints.BOTH,	
								new Insets(5, 5, 5, 5),		
								0, 0						
								)); 
		
		editingPanel.add(upButton,new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTH,
									GridBagConstraints.HORIZONTAL,
									new Insets(5, 5, 5, 5),
									0, 0
									)); 
		row++;											
		editingPanel.add(downButton,new GridBagConstraints(1, row, 
									1, 1, 
									0, 1,
									GridBagConstraints.NORTH,
									GridBagConstraints.HORIZONTAL,
									new Insets(5, 5, 5, 5),
									0, 0
									)); 
		row++;
		editingPanel.add(addButton,new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.SOUTH,
									GridBagConstraints.HORIZONTAL,
									new Insets(5, 5, 5, 5),
									0, 0
									)); 
		row++;											
		editingPanel.add(removeButton,new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.SOUTH,
									GridBagConstraints.HORIZONTAL,
									new Insets(5, 5, 5, 5),
									0, 0
									)); 
		
		editingPanel.setBorder(BorderFactory.createTitledBorder(
								BorderFactory.createEtchedBorder(),
								"Edit File Filter Settings"));
		return editingPanel;
	}
	
	
	private JPanel createButtonsPanel () {
		JPanel panel = new JPanel();

		okButton = new JButton("OK");
		okButton.setToolTipText("Save changes and exit this dialog");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] mappingArray = model.toArray();
				documentMappings = Arrays.asList(mappingArray);
				setVisible(false);
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Close dialog without saving any changes that may have been made");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				documentMappings = null;
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
		if (this.mappingListView.getSelectedIndex() == -1) {
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
		}
		else {
			okButton.setEnabled(dataIsChanged);
		}		
	}
	
	
	private void displayMappingDetails () {
		if (this.mappingListView.getSelectedIndex() != -1) {
			DocumentHandlerMapping mapping = (DocumentHandlerMapping) this.mappingListView.getSelectedValue();
			this.fileFilterDisplayLabel.setText(mapping.getFileFilter().toSerializationString());
			this.docHandlerDisplayLabel.setText(mapping.getHandler().getDisplayName());
		}
		
	}
	
	private void createMapping() {
		CreateNewFileMappingDialog dialog = new CreateNewFileMappingDialog(this, documentMappings);
		DocumentHandlerMapping mapping = dialog.getCreatedMapping();
		if (mapping != null) {
			model.addElement(mapping);
		}
	}
	
    public List getDocumentMappings() {
        return documentMappings;
    }
}
