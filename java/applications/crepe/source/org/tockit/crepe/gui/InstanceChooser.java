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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.tockit.cgs.model.Instance;
import org.tockit.cgs.model.KnowledgeBase;
import org.tockit.cgs.model.Type;
import org.tockit.crepe.controller.ConfigurationManager;

public class InstanceChooser extends JDialog {
    private static InstanceChooser dialog = null;
    public static Instance selectedInstance = null;

    private InstanceChooser(Frame frame, final KnowledgeBase knowledgeBase, Type type) {
        super(frame, true);
        JLabel modeLabel = new JLabel();
        modeLabel.setText("Instances:");

        final JList listView = new JList();
        Collection instanceIds = knowledgeBase.getInstancesForType(type);
        listView.setListData(instanceIds.toArray());

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listView.locationToIndex(e.getPoint());
                    selectedInstance = (Instance) listView.getModel().getElementAt(index);
                    closeDialog();
                }
            }
        };
        listView.addMouseListener(mouseListener);

        final JScrollPane scrollPane = new JScrollPane(listView);

        //buttons
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedInstance = null;
                closeDialog();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                selectedInstance = null;
                closeDialog();
            }
        });

        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedInstance = (Instance) listView.getSelectedValue();
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
        InstanceChooser.dialog.setVisible(false);
        ConfigurationManager.storePlacement("InstanceChooser", this);
    }

    public static void initialize(Component comp, KnowledgeBase knowledgeBase, Type type) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        dialog = new InstanceChooser(frame, knowledgeBase, type);
    }

    public static Instance chooseInstance(Component comp, KnowledgeBase knowledgeBase, Type type) {
        initialize(comp, knowledgeBase, type);
        dialog.setLocationRelativeTo(comp);
        ConfigurationManager.restorePlacement("InstanceChooser", dialog,
                new Rectangle(50, 50, 335, 160));
        dialog.setVisible(true);
        return selectedInstance;
    }
}
