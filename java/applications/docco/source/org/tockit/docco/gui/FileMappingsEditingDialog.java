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

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tockit.docco.indexer.DocumentHandlerMapping;
import org.tockit.docco.indexer.DocumentHandlersRegistery;
import org.tockit.docco.indexer.documenthandler.DocumentHandler;
import org.tockit.docco.indexer.filefilter.DoccoFileFilter;

public class FileMappingsEditingDialog extends JDialog {
	
	private DocHandlersRegisteryListModel model;
	
	private JButton upButton = new JButton("Move Up");
	private JButton downButton = new JButton("Move Down");
	private JButton addButton = new JButton("Add");
	private JButton removeButton = new JButton("Remove");

	private JList jlist;
	
	private JLabel fileFilterDisplayLabel = new JLabel();
	private JLabel docHandlerDisplayLabel = new JLabel();
	
	
	
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
	
	private class DocHandlersRegisteryListModel extends AbstractListModel {
		private DocumentHandlersRegistery registery;
		
		public DocHandlersRegisteryListModel (DocumentHandlersRegistery registery) {
			this.registery = registery;
		}

		public int getSize() {
			return registery.getDocumentMappingCollection().size();
		}

		public Object getElementAt(int index) {
			return registery.getMappingAt(index);
		}
		
		public void moveMapping (int fromIndex, int toIndex) {
			registery.moveMapping(fromIndex, toIndex);
			if (fromIndex < toIndex) {
				fireContentsChanged(registery.getMappingAt(toIndex), fromIndex, toIndex);	
			}
			else {
				fireContentsChanged(registery.getMappingAt(toIndex), toIndex, fromIndex);	
			}
		}
		
		public void removeMappingAt (int index) {
			if (index < model.getSize()) {
				DocumentHandlerMapping mapping = (DocumentHandlerMapping) model.getElementAt(jlist.getSelectedIndex());
				registery.removeMapping(mapping);
				fireIntervalRemoved(mapping, 0, getSize());
			}			
		}
		
		public void addMapping (DoccoFileFilter fileFilter, DocumentHandler docHandler) {
			registery.register(fileFilter, docHandler);
			System.out.println("adding new doc handler mapping");
			// @todo assumption here is that a new element is always added in the end.
			fireIntervalAdded(registery.getMappingAt(getSize() - 1), 0, getSize());
		}
	}

	
	public FileMappingsEditingDialog(Frame parent, DocumentHandlersRegistery registery) 
													throws HeadlessException {
		super(parent, "Edit File Mappins Configuration", true);
		this.model = new DocHandlersRegisteryListModel(registery);
		
		getContentPane().add(createMainPanel(), BorderLayout.CENTER);
		getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
		
		setButtonsStatus();
		
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);		
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
					model.moveMapping(index, index -1);
					jlist.setSelectedIndex(index - 1);
					saveMappings();
				}
			}
		});


		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = jlist.getSelectedIndex();
				if (index + 1 < model.getSize()) {
					model.moveMapping(index, index + 1);
					jlist.setSelectedIndex(index + 1);
					saveMappings();
				}
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createMapping();
			}
		});
		
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					model.removeMappingAt(jlist.getSelectedIndex());
					setButtonsStatus();
			}
		});

		jlist = new JList(this.model);
		jlist.setCellRenderer(new MappingsListCellRenderer());
		jlist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				setButtonsStatus();
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

		mainPanel.add(editingPanel, BorderLayout.CENTER);
		mainPanel.add(displayDetailsPanel, BorderLayout.SOUTH);
		
		return mainPanel;
	}
	
	private JPanel createButtonsPanel () {
		JPanel panel = new JPanel();
			
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel.add(Box.createHorizontalGlue());
		panel.add(cancelButton);
		
		return panel;
	}
	
	private void saveMappings () {
		// @todo store changed info in config manager?

	}	
	
	private void setButtonsStatus() {
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
	
	
	private void displayMappingDetails () {
		
		if (this.jlist.getSelectedIndex() != -1) {
			DocumentHandlerMapping mapping = (DocumentHandlerMapping) this.jlist.getSelectedValue();
			this.fileFilterDisplayLabel.setText(mapping.getFileFilter().getDisplayName() 
										+ ": " + mapping.getFileFilter().getFilteringString());
			this.docHandlerDisplayLabel.setText(mapping.getHandler().getDisplayName());
		}
		
	}
	
	private void createMapping() {
		CreateNewFileMappingDialog dialog = new CreateNewFileMappingDialog(this, model.registery);
		DocumentHandlerMapping mapping = dialog.getCreatedMapping();
		if (mapping != null) {
			model.addMapping(mapping.getFileFilter(), mapping.getHandler());
		}
	}

}
