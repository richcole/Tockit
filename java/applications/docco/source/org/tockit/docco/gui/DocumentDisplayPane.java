/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package org.tockit.docco.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.lucene.document.DateField;
import org.tockit.docco.GlobalConstants;
import org.tockit.docco.query.HitReference;

public class DocumentDisplayPane extends JPanel {
	private JTextField sizeField;
    private JTextArea summaryArea;
    private JTextField dateField;
    private JTextField pathField;
    private JTextField authorField;
    private JTextField titleField;

    public DocumentDisplayPane() {
		super(new BorderLayout());
		JLabel titleLabel = new JLabel("Title:");
		this.titleField = new JTextField();
		this.titleField.setEditable(false);

		JLabel authorLabel = new JLabel("Author:");
		this.authorField = new JTextField();
		this.authorField.setEditable(false);

		JLabel pathLabel = new JLabel("File:");
		this.pathField = new JTextField();
		this.pathField.setEditable(false);

		JLabel dateLabel = new JLabel("Date:");
		this.dateField = new JTextField();
		this.dateField.setEditable(false);
		
		JLabel sizeLabel = new JLabel("Size:");
		this.sizeField = new JTextField();
		this.sizeField.setEditable(false);
		
		JLabel summaryLabel = new JLabel("Summary:");
		this.summaryArea = new JTextArea();
		this.summaryArea.setEditable(false);
		JScrollPane summaryPane = new JScrollPane(this.summaryArea);
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.add(titleLabel, new GridBagConstraints(
						0, 0, 1, 1, 0, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(this.titleField, new GridBagConstraints(
						1, 0, 3, 1, 1, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(authorLabel, new GridBagConstraints(
						0, 1, 1, 1, 0, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(this.authorField, new GridBagConstraints(
						1, 1, 3, 1, 1, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(pathLabel, new GridBagConstraints(
						0, 2, 1, 1, 0, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(this.pathField, new GridBagConstraints(
						1, 2, 3, 1, 1, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(dateLabel, new GridBagConstraints(
						0, 3, 1, 1, 0, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(this.dateField, new GridBagConstraints(
						1, 3, 1, 1, 1, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(sizeLabel, new GridBagConstraints(
						2, 3, 1, 1, 0, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(this.sizeField, new GridBagConstraints(
						3, 3, 1, 1, 1, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(summaryLabel, new GridBagConstraints(
						0, 4, 4, 1, 0, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
		mainPanel.add(summaryPane, new GridBagConstraints(
						0, 5, 4, 1, 1, 0,
						GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(5,5,5,5),
						0,0 ));
						
		this.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
	}
	
	public void displayDocument(HitReference reference) {
		this.titleField.setText(reference.getDocument().get(GlobalConstants.FIELD_DOC_TITLE));
		this.authorField.setText(reference.getDocument().get(GlobalConstants.FIELD_DOC_AUTHOR));
		this.pathField.setText(reference.getDocument().get(GlobalConstants.FIELD_DOC_PATH));
		Date date = DateField.stringToDate(reference.getDocument().get(GlobalConstants.FIELD_DOC_DATE));
		this.dateField.setText(DateFormat.getDateTimeInstance().format(date));
		long size = Long.parseLong(reference.getDocument().get(GlobalConstants.FIELD_DOC_SIZE));
        this.sizeField.setText(NumberFormat.getIntegerInstance().format(size) + " bytes");
		this.summaryArea.setText(reference.getDocument().get(GlobalConstants.FIELD_DOC_SUMMARY));
	}
	
	public void clearDisplay() {
		this.titleField.setText("");
		this.authorField.setText("");
		this.pathField.setText("");
		this.dateField.setText("");
		this.sizeField.setText("");
		this.summaryArea.setText("");
	}
}
