/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui;

import org.tockit.cgs.model.Relation;
import org.tockit.cgs.model.KnowledgeBase;
import org.tockit.crepe.controller.ConfigurationManager;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Collection;

public class RelationChooser extends JDialog {
    private static RelationChooser dialog = null;
    public static Relation selectedRelation = null;

    private RelationChooser(Frame frame, final KnowledgeBase knowledgeBase) {
        super(frame, true);
        JLabel modeLabel = new JLabel();
        modeLabel.setText("Relations:");

        final JList listView = new JList();
        Collection relationNames = knowledgeBase.getRelationNames();
        listView.setListData(relationNames.toArray());

        final JScrollPane scrollPane = new JScrollPane(listView);

        //buttons
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedRelation = null;
                closeDialog();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                selectedRelation = null;
                closeDialog();
            }
        });

        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedRelation = knowledgeBase.getRelation(
                                                listView.getSelectedValue().toString());
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
        this.getContentPane().add(modeLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(scrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                , GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        this.getContentPane().add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
                , GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 1, 0));
        pack();
    }

    protected void closeDialog() {
        RelationChooser.dialog.setVisible(false);
        ConfigurationManager.storePlacement("RelationChooser", this);
    }

    public static void initialize(Component comp, KnowledgeBase knowledgeBase) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        dialog = new RelationChooser(frame, knowledgeBase);
    }

    public static Relation chooseRelation(Component comp, KnowledgeBase knowledgeBase) {
        initialize(comp, knowledgeBase);
        dialog.setLocationRelativeTo(comp);
        ConfigurationManager.restorePlacement("RelationChooser", dialog,
                new Rectangle(50, 50, 335, 160));
        dialog.setVisible(true);
        return selectedRelation;
    }
}
