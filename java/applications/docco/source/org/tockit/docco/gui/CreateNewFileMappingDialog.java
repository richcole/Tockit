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

import org.tockit.docco.indexer.DocumentHandlerMapping;
import org.tockit.docco.indexer.DocumentHandlerRegistry;
import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.docco.filefilter.FileFilterFactory;
import org.tockit.docco.filefilter.FileFilterFactoryRegistry;
import org.tockit.docco.documenthandler.DocumentHandlersRegistry;


// @todo needs relayouting and code cleanup
public class CreateNewFileMappingDialog extends JDialog {
	
	private DocumentHandlerRegistry docHandlersRegistry;
	private DefaultComboBoxModel docHandlerImplementationsModel;
	
	private DocumentHandlerMapping mapping;
	
	private JButton okButton;
	private JComboBox fileFilterFactoryChooser;
	private JTextField extensionField;

	private class FileFilterComboBoxCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, 
												int index, boolean isSelected, 
												boolean cellHasFocus) {
			
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if ( (value instanceof String) && ( ((String) value).startsWith("<")) ) {
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
		Iterator docHandlersIterator = DocumentHandlersRegistry.getIterator();
		while (docHandlersIterator.hasNext()) {
			DocumentHandler curDocHandler = (DocumentHandler) docHandlersIterator.next();
			this.docHandlerImplementationsModel.addElement(curDocHandler);			
		}
	}

	private JPanel createFileFilterPanel () {
		JPanel fileFilterPanel = new JPanel(new GridBagLayout());
		
		fileFilterFactoryChooser = new JComboBox(FileFilterFactoryRegistry.registry);
		
		extensionField = new JTextField(5);
		
		final JLabel extensionLabel = new JLabel("with filter expression: ");

		fileFilterFactoryChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		fileFilterPanel.add(new JLabel("Filter type: "),
									new GridBagConstraints(0, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		fileFilterPanel.add(fileFilterFactoryChooser,
									new GridBagConstraints(1, row, 
									1, 1, 
									1, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.HORIZONTAL,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		row++;
		fileFilterPanel.add(extensionLabel,
									new GridBagConstraints(0, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHEAST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 1, 5),
									0, 0
									));
		fileFilterPanel.add(extensionField,
									new GridBagConstraints(1, row, 
									1, 1, 
									1, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.HORIZONTAL,
									new Insets(5, 5, 1, 5),
									0, 0
									));

		final JComboBox docHandlersChooser = new JComboBox(this.docHandlerImplementationsModel);
		docHandlersChooser.setRenderer(new DocHandlerComboBoxCellRenderer());
		
		docHandlersChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selectedItem = docHandlersChooser.getSelectedItem();
				setOkButtonStatus();
			}
		});
									
		row++;
		fileFilterPanel.add(new JLabel("Use document handler: "),
									new GridBagConstraints(0, row, 
									1, 1, 
									0, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.NONE,
									new Insets(5, 5, 5, 5),
									0, 0
									));
		fileFilterPanel.add(docHandlersChooser,
									new GridBagConstraints(1, row, 
									1, 1, 
									1, 0,
									GridBagConstraints.NORTHWEST,
									GridBagConstraints.HORIZONTAL,
									new Insets(5, 5, 5, 5),
									0, 0
									));

		return fileFilterPanel;
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
								&& ( (String) selectedDocHandler).startsWith("<") )){
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
			ErrorDialog.showError(this, exception, "Error creating mapping");
			return false;
		}
	}
	
	public DocumentHandlerMapping getCreatedMapping() {
		return this.mapping;
	}


}
