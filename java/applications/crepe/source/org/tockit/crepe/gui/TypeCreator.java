/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tockit.crepe.controller.ConfigurationManager;

public class TypeCreator extends JDialog {
    private static TypeCreator dialog = null;
    public static String newType = null;

    private TypeCreator(Frame frame) {
        super(frame, true);
        JLabel mainLabel = new JLabel();
        mainLabel.setText("Name new type:");

        final JTextField textField = new JTextField();

        //buttons
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newType = null;
                closeDialog();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                newType = null;
                closeDialog();
            }
        });

        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newType = textField.getText();
                closeDialog();
            }
        });
        getRootPane().setDefaultButton(okButton);

        JPanel buttonPanel = new JPanel();
        GridBagLayout buttonLayout = new GridBagLayout();
        buttonPanel.setLayout(buttonLayout);
        buttonPanel.add(cancelButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 1, 0));
        buttonPanel.add(okButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 1, 0));

        GridBagLayout gridBagLayout = new GridBagLayout();
        this.getContentPane().setLayout(gridBagLayout);
        this.getContentPane().add(mainLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(textField, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.getContentPane().add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 1, 0));
        this.getContentPane().add(new JPanel(), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
                , GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 1, 0));
        pack();
    }

    protected void closeDialog() {
        TypeCreator.dialog.setVisible(false);
        ConfigurationManager.storePlacement("TypeCreator", this);
    }

    public static void initialize(Component comp) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        dialog = new TypeCreator(frame);
    }

    public static String createNewTypeName(Component comp) {
        initialize(comp);
        dialog.setLocationRelativeTo(comp);
        ConfigurationManager.restorePlacement("TypeCreator", dialog,
                new Rectangle(50, 50, 335, 160));
        dialog.setVisible(true);
        return newType;
    }
}
