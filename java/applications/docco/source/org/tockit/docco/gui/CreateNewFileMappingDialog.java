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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.docco.documenthandler.DocumentHandlerRegistry;
import org.tockit.docco.filefilter.FileFilterFactory;
import org.tockit.docco.filefilter.FileFilterFactoryRegistry;
import org.tockit.docco.indexer.DocumentHandlerMapping;


// @todo needs relayouting and code cleanup
public class CreateNewFileMappingDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel docHandlerImplementationsModel;
	
	private DocumentHandlerMapping mapping;
	
	private JButton okButton;
	private JComboBox fileFilterFactoryChooser;
	private JTextField extensionField;

	private class FileFilterComboBoxCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, 
												int index, boolean isSelected, 
												boolean cellHasFocus) {
			
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if ( (value instanceof String) && ( ((String) value).startsWith("<")) ) { //$NON-NLS-1$
				setText((String) value);
			}
			else {
				FileFilterFactory fileFilter = (FileFilterFactory) value;
				String text = fileFilter.getDisplayName();
				setText(text);			
			}
			
			return this;
		}
	}	

	private class DocHandlerComboBoxCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, 
												int index, boolean isSelected, 
												boolean cellHasFocus) {
			
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if ( (value instanceof String) && ( ((String) value).startsWith("<")) ) { //$NON-NLS-1$
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

	public CreateNewFileMappingDialog(Dialog owner) 
										throws HeadlessException {
		super(owner, GuiMessages.getString("CreateNewFileMappingDialog.dialog.title"), true); //$NON-NLS-1$
		
		initAllComboBoxModels();
		
		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(createFileFilterPanel(), BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(owner);
		setVisible(true);
	}


	private void initAllComboBoxModels() {
		this.docHandlerImplementationsModel = new DefaultComboBoxModel();
		Iterator docHandlersIterator = DocumentHandlerRegistry.getDocumentHandlers().iterator();
		while (docHandlersIterator.hasNext()) {
			DocumentHandler curDocHandler = (DocumentHandler) docHandlersIterator.next();
			this.docHandlerImplementationsModel.addElement(curDocHandler);			
		}
	}

	private JPanel createFileFilterPanel () {
		JPanel fileFilterPanel = new JPanel(new GridBagLayout());
		
		Collection fileFilterFactoriesCollection = FileFilterFactoryRegistry.getFileFilters(); 
		FileFilterFactory[] fileFilterFactories = (FileFilterFactory[]) fileFilterFactoriesCollection.toArray(
									new FileFilterFactory[fileFilterFactoriesCollection.size()]);
		
		fileFilterFactoryChooser = new JComboBox(fileFilterFactories);
		fileFilterFactoryChooser.setRenderer(new FileFilterComboBoxCellRenderer());
		
		extensionField = new JTextField(5);
		
		fileFilterFactoryChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOkButtonStatus();
			}
		});
			
		extensionField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				setOkButtonStatus();
			}
		});

		final JComboBox docHandlersChooser = new JComboBox(this.docHandlerImplementationsModel);
		docHandlersChooser.setRenderer(new DocHandlerComboBoxCellRenderer());
		
		docHandlersChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOkButtonStatus();
			}
		});
									
		GridBagConstraints labelConstraint = new GridBagConstraints();
		labelConstraint.anchor = GridBagConstraints.WEST;
		labelConstraint.insets = new Insets(5, 5, 5, 5);
		labelConstraint.gridx = 0;
		labelConstraint.gridy = GridBagConstraints.RELATIVE;
		
		GridBagConstraints dataEntryConstraint = new GridBagConstraints();
		dataEntryConstraint.fill = GridBagConstraints.HORIZONTAL;
		dataEntryConstraint.insets = new Insets(5, 5, 5, 5);
		dataEntryConstraint.gridx = 1;
		dataEntryConstraint.weightx = 1;
		labelConstraint.gridy = GridBagConstraints.RELATIVE;
		
        fileFilterPanel.add(new JLabel(GuiMessages.getString("CreateNewFileMappingDialog.filterTypeChooser.label")),	labelConstraint); //$NON-NLS-1$
		fileFilterPanel.add(fileFilterFactoryChooser, dataEntryConstraint);
		fileFilterPanel.add(new JLabel(GuiMessages.getString("CreateNewFileMappingDialog.filterExpressionField.label")), labelConstraint); //$NON-NLS-1$
		fileFilterPanel.add(extensionField, dataEntryConstraint);
		fileFilterPanel.add(new JLabel(GuiMessages.getString("CreateNewFileMappingDialog.documentHandlerChooser.label")), labelConstraint); //$NON-NLS-1$
		fileFilterPanel.add(docHandlersChooser, dataEntryConstraint);
		
		dataEntryConstraint.weighty = 1;
		fileFilterPanel.add(new JPanel(), dataEntryConstraint);
		
		return fileFilterPanel;
	}

	
	private JPanel createButtonsPanel () {
		JPanel panel = new JPanel();

		okButton = new JButton(GuiMessages.getString("CreateNewFileMappingDialog.createMappingButton.label")); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (createMapping()) { 
					setVisible(false);
				}
			}
		});
		setOkButtonStatus();
			
		JButton cancelButton = new JButton(GuiMessages.getString("CreateNewFileMappingDialog.cancelButton.Label")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
								&& ( (String) selectedDocHandler).startsWith("<") )){ //$NON-NLS-1$
			docHandler = (DocumentHandler) selectedDocHandler;
		}

		if (extensionField.getText().length() > 0) {
			if (docHandler != null) {
				okButton.setEnabled(true);
				return;
			}
		}

		okButton.setEnabled(false);
	}
	
	private boolean createMapping() {
		DocumentHandler docHandler = (DocumentHandler) docHandlerImplementationsModel.getSelectedItem();
		try {
			FileFilterFactory fileFilterFactory = (FileFilterFactory) fileFilterFactoryChooser.getSelectedItem();
			mapping = new DocumentHandlerMapping(fileFilterFactory.createNewFilter(this.extensionField.getText()), docHandler);
			return true;
		}
		catch (Exception exception) {
			ErrorDialog.showError(this, exception, GuiMessages.getString("CreateNewFileMappingDialog.mappingCreationErrorDialog.title")); //$NON-NLS-1$
			return false;
		}
	}
	
	public DocumentHandlerMapping getCreatedMapping() {
		return this.mapping;
	}
}
