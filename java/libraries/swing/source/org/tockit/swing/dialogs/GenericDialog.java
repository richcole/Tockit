/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * A dialog displaying a content pane and ok/cancel buttons.
 * 
 * This class is used through the static method showDialog(Component, String, Component),
 * which takes beside the parent and title parameters a component used as the dialog's
 * content.
 */
public class GenericDialog extends JDialog {
    /**
     * Stores the information if the user clicked ok.
     */
    private boolean okClicked = false;
    
    /**
     * Creates the dialog for the static method.
     */
    private GenericDialog(Component parent, String title, Component selectionComponent) {
        super(JOptionPane.getFrameForComponent(parent));
        
        JPanel mainPane = new JPanel(new BorderLayout());
        
        mainPane.add(selectionComponent, BorderLayout.CENTER);

        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");
        
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        
        mainPane.add(buttonPane, BorderLayout.SOUTH);

        setTitle(title);
        setContentPane(mainPane);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okClicked = true;
                setVisible(false);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        
        pack();
    }
    
    /**
     * Shows a dialog containing the selection component and an ok/cancel pair of buttons.
     * 
     * This method can be used to turn any Component into a modal dialog with an 
     * ok and a cancel button. The return value is true if and only if the ok button 
     * was pressed -- if the dialog was closed by any other way it is false.
     * 
     * @param parent              the parent for the dialog
     * @param title               a title used for the dialog
     * @param selectionComponent  the main component for the dialog
     * 
     * @return true iff the ok button was pressed 
     */
    public static boolean showDialog(Component parent, String title, Component selectionComponent) {
        GenericDialog dialog = new GenericDialog(parent, title, selectionComponent);
        dialog.setModal(true);
        dialog.show();
        return dialog.okClicked;
    }
}
