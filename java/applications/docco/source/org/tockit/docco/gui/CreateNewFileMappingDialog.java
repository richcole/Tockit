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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Constructor;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.docco.GlobalConstants;
import org.tockit.docco.indexer.DocumentHandlerMapping;
import org.tockit.docco.indexer.DocumentHandlerRegistry;
import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.docco.filefilter.DoccoFileFilter;


public class CreateNewFileMappingDialog extends JDialog {
	
	private DocumentHandlerRegistry docHandlersRegistry;
	private DefaultComboBoxModel instantiatedFileFiltersModel;
	private DefaultComboBoxModel fileFilterImplementationsModel;
	private DefaultComboBoxModel docHandlerImplementationsModel;
	
	private DocumentHandlerMapping mapping;
	
	private JButton okButton;
	private JComboBox fileFiltersChooser;
	private JComboBox fileFilterImplementationsChooser;
	private JTextField extensionField;
	private JRadioButton useAvailableFileFilter;
	private JRadioButton createNewFileFilter;

	private class FileFilterComboBoxCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, 
												int index, boolean isSelected, 
												boolean cellHasFocus) {
			
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if ( (value instanceof String) && ( ((String) value).startsWith("<")) ) {
				setText((String) value);
			}
			else {
				DoccoFileFilter fileFilter = (DoccoFileFilter) value;
				String text = fileFilter.getDisplayName() + 
									" (" + fileFilter.getFilteringString() + ") ";
			
				setText(text);			
			}
			
			return this;
		}
	}	

	private class DocHandlerComboBoxCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, 
												int index, boolean isSelected, 
												boolean cellHasFocus) {
			
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if ( (value instanceof String) && ( ((String) value).startsWith("<")) ) {
				setText((String) value);
				return this;
			}
			else {
				DocumentHandler handler = (DocumentHandler) value;
				setText(handler.getDisplayName());
				setToolTipText(handler.getClass().toString());			
			}
			
			return this;
		}
	}	

	public CreateNewFileMappingDialog(Dialog owner, DocumentHandlerRegistry registry) 
										throws HeadlessException {
		super(owner, "Create new mapping", true);
		this.docHandlersRegistry = registry;
		
		initAllComboBoxModels();
		
		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(createFileFilterPanel(), BorderLayout.CENTER);
		mainPanel.add(createDocHandlersPanel(), BorderLayout.SOUTH);
		mainPanel.setBorder(BorderFactory.createEmptyBorder());
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}


	private void initAllComboBoxModels() {
		
		this.instantiatedFileFiltersModel = new DefaultComboBoxModel();
		this.instantiatedFileFiltersModel.addElement("<Choose File Filter>");
		Iterator it = docHandlersRegistry.getDocumentMappingList().iterator();
		while (it.hasNext()) {
			DocumentHandlerMapping curMapping = (DocumentHandlerMapping) it.next();
			this.instantiatedFileFiltersModel.addElement(curMapping.getFileFilter());
		}
		
		
		this.docHandlerImplementationsModel = new DefaultComboBoxModel();
		this.docHandlerImplementationsModel.addElement("<Choose Document Handler>");
		String[] docHandlers = GlobalConstants.DOC_HANDLER_IMPLEMENTATIONS;
		for (int i = 0; i < docHandlers.length; i++) {
			String curString = docHandlers[i];
			try {
				DocumentHandler curInstance = (DocumentHandler) Class.forName(curString).newInstance();
				this.docHandlerImplementationsModel.addElement(curInstance);
			}
			catch (Exception e) {
				e.printStackTrace();
				ErrorDialog.showError(this, e, 
									"Error finding or instantiating Document Handler",
									"Couldn't load Document Handler " + curString);
			}
		}
		
		
		this.fileFilterImplementationsModel = new DefaultComboBoxModel();
		this.fileFilterImplementationsModel.addElement("<Choose File Filter Implementation>");
		String[] fileFilters = GlobalConstants.FILE_FILTER_IMPLEMENTAIONS;
		for (int i = 0; i < fileFilters.length; i++) {
			String curString = fileFilters[i];
			this.fileFilterImplementationsModel.addElement(curString);
		}
	}

	private JPanel createFileFilterPanel () {
		JPanel fileFilterPanel = new JPanel(new GridBagLayout());
		
		useAvailableFileFilter = new JRadioButton();
		createNewFileFilter = new JRadioButton();

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(useAvailableFileFilter);
		buttonGroup.add(createNewFileFilter);
		useAvailableFileFilter.setSelected(true);

		
		fileFilterImplementationsChooser = new JComboBox(this.fileFilterImplementationsModel);
		
		extensionField = new JTextField(5);
		extensionField.setEnabled(false);
		
		final JLabel extensionLabel = new JLabel("and extension: ");
		extensionLabel.setEnabled(false);


		fileFiltersChooser = new JComboBox(this.instantiatedFileFiltersModel);
		fileFiltersChooser.setRenderer(new FileFilterComboBoxCellRenderer());

		useAvailableFileFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extensionField.setEnabled(false);
				extensionLabel.setEnabled(false);
				setOkButtonStatus();
			}
		});
		
		fileFiltersChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selectedItem = fileFiltersChooser.getSelectedItem();
				if ((selectedItem instanceof String) && ((String) selectedItem).startsWith("<")) {
				}
				else {
					useAvailableFileFilter.setSelected(true);
				}
				extensionField.setEnabled(false);
				extensionLabel.setEnabled(false);
				setOkButtonStatus();
			}
		});
		
		fileFilterImplementationsChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selectedItem = fileFilterImplementationsChooser.getSelectedItem();
				if ((selectedItem instanceof String) && ((String) selectedItem).startsWith("<")) {
					extensionField.setEnabled(false);
					extensionLabel.setEnabled(false);
				}
				else {
					createNewFileFilter.setSelected(true);
					extensionField.setEnabled(true);
					extensionLabel.setEnabled(true);
				}
				setOkButtonStatus();
			}
		});
			
		extensionField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
			}
			public void keyReleased(KeyEvent e) {
				setOkButtonStatus();
			}
		});

		int row = 0;	
		fileFilterPanel.add(useAvailableFileFilter,
								new GridBagConstraints(0, row, 	// gridx, gridy
								1, 1, 							// gridwidth, gridheight
								0, 0,  						// weightx, weighty
								GridBagConstraints.CENTER,	// anchor
								GridBagConstraints.BOTH,	// fill
								new Insets(5, 5, 5, 5),		// insets
								0, 0						// ipadx, ipady
								)); 
		fileFilterPanel.add(new JLabel("Use available file filter: "),
									new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
									 
		fileFilterPanel.add(fileFiltersChooser,
									new GridBagConstraints(2, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		
		row++;
		fileFilterPanel.add(createNewFileFilter,
									new GridBagConstraints(0, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		fileFilterPanel.add(new JLabel("Create new file filter with following settings: "),
									new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		fileFilterPanel.add(fileFilterImplementationsChooser,
									new GridBagConstraints(2, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		row++;
		fileFilterPanel.add(extensionLabel,
									new GridBagConstraints(1, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHEAST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 1, 5),
									0, 0
									));
		fileFilterPanel.add(extensionField,
									new GridBagConstraints(2, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 1, 5),
									0, 0
									));
									
		fileFilterPanel.setBorder(BorderFactory.createTitledBorder(
									BorderFactory.createEtchedBorder(),
									"Choose or create file filter"));									
		return fileFilterPanel;		
	}

	private JPanel createDocHandlersPanel() {
		final JComboBox docHandlersChooser = new JComboBox(this.docHandlerImplementationsModel);
		docHandlersChooser.setRenderer(new DocHandlerComboBoxCellRenderer());
		
		docHandlersChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selectedItem = docHandlersChooser.getSelectedItem();
				setOkButtonStatus();
			}
		});
									
		JPanel docHandlerPanel = new JPanel();									
		int row = 0;
		docHandlerPanel.add(new JLabel("Use the above file filter with document handler: "),
									new GridBagConstraints(0, row, 
									2, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		docHandlerPanel.add(docHandlersChooser,
									new GridBagConstraints(2, row, 
									1, 1, 
									0.3, 0.1,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		docHandlerPanel.setBorder(BorderFactory.createTitledBorder(
									BorderFactory.createEtchedBorder(),
									"Choose document handler"));
		
		return docHandlerPanel;
	}

	
	private JPanel createButtonsPanel () {
		JPanel panel = new JPanel();

		okButton = new JButton("Create mapping");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (createMapping()) { 
					setVisible(false);
				}
			}
		});
		setOkButtonStatus();
			
		JButton cancelButton = new JButton("Cancel");
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
		
		return panel;
	}
	
	private void setOkButtonStatus () {

		DocumentHandler docHandler = null;
		Object selectedDocHandler = docHandlerImplementationsModel.getSelectedItem();
		if (!((selectedDocHandler instanceof String) 
								&& ( (String) selectedDocHandler).startsWith("<") )){
			docHandler = (DocumentHandler) selectedDocHandler;
		}

		if (useAvailableFileFilter.isSelected()) {
			Object selectedFileFilter = fileFiltersChooser.getSelectedItem();
			if (!((selectedFileFilter instanceof String) && ((String) selectedFileFilter).startsWith("<"))) {
				if (docHandler != null) {
					okButton.setEnabled(true);
					return;
				}
			}
		}
		else {
			Object selectedFileFilterImplementation = fileFilterImplementationsChooser.getSelectedItem();
			if (! ( (selectedFileFilterImplementation instanceof String) 
						&& (((String) selectedFileFilterImplementation).startsWith("<")) )) {
				if (extensionField.getText().length() > 0) {
					if (docHandler != null) {
						okButton.setEnabled(true);
						return;
					}
				}
			}
		}
		okButton.setEnabled(false);
	}
	
	private boolean createMapping() {
		DocumentHandler docHandler = (DocumentHandler) docHandlerImplementationsModel.getSelectedItem();
		if (useAvailableFileFilter.isSelected()) {
			DoccoFileFilter fileFilter = (DoccoFileFilter) fileFiltersChooser.getSelectedItem();
			mapping = new DocumentHandlerMapping(fileFilter, docHandler);
			return true;
		}
		else {
			try {
				String fileFilterName = (String) fileFilterImplementationsChooser.getSelectedItem();
				Class fileFilterClass = Class.forName(fileFilterName);
			
				Class[] parameterTypes = { String.class };
				Constructor constructor = fileFilterClass.getConstructor(parameterTypes);
			
				Object[] args = { extensionField.getText() };
				DoccoFileFilter ff = (DoccoFileFilter) constructor.newInstance(args);
				
				mapping = new DocumentHandlerMapping(ff, docHandler);
				return true;
			}
			catch (Exception exception) {
				ErrorDialog.showError(this, exception, "Error creating mapping");
			}
		}
		return false;
	}
	
	public DocumentHandlerMapping getCreatedMapping() {
		return this.mapping;
	}


}
