package docsearcher;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ManageIndexesDialog
    extends JDialog
    implements ActionListener
{
    JPanel[] panels;
    JLabel dirLabel =
        new JLabel("Adjust the index properties below as needed.");
    JButton okButton = new JButton("Apply");
    JButton cancelButton = new JButton("Cancel");
    JCheckBox[] sbd; // search by default
    JCheckBox[] del; // delete or remove
    JCheckBox[] upd; // update
    JCheckBox[] expi; // update
    JLabel[] descLabels;
    int numIndexes = 0;
    boolean returnBool = false;
    DocSearch monitor;
    Font f = new Font("Times", Font.BOLD, 16);
    int numPanels = 0;

    ManageIndexesDialog(DocSearch monitor,
                        String title,
                        boolean modal)
    {
        super(monitor, title, modal);

        //super(parent, "Generate Meta Tag Table", true);
        this.monitor = monitor;

        // accessibility info
        //
        okButton.setMnemonic(KeyEvent.VK_A);
        okButton.setToolTipText("Apply Changes");

        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText("Cancel");

        //
        dirLabel.setFont(f);
        if (!monitor.indexes.isEmpty())
        {
            numIndexes = monitor.indexes.size();
            sbd = new JCheckBox[numIndexes]; // search by default
            del = new JCheckBox[numIndexes]; // delete or remove
            upd = new JCheckBox[numIndexes]; // update now
            expi = new JCheckBox[numIndexes]; // export
            descLabels = new JLabel[numIndexes];
            Iterator iterator = monitor.indexes.iterator();
            DocSearcherIndex di;
            int i = 0;
            while (iterator.hasNext())
            {
                di = ((DocSearcherIndex)iterator.next());
                sbd[i] = new JCheckBox("Searched by default");
                sbd[i].setSelected(di.shouldBeSearched);
                del[i] = new JCheckBox("Remove");
                expi[i] = new JCheckBox("Export");
                expi[i].setToolTipText("To zip archive located in "
                                       + di.archiveDir);
                upd[i] =
                    new JCheckBox("Update Now (" + DateTimeUtils.getDaysOld(di)
                                  + " days old)");
                upd[i].setToolTipText("Updates the index for changes in"
                                      + di.indexerPath);
                descLabels[i] = new JLabel(di.desc);
                descLabels[i].setToolTipText("Index of content in "
                                             + di.indexerPath);
                i++;
            }
             // end of iteration
        }

        numPanels = 2 + numIndexes;
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++)
        {
            panels[i] = new JPanel();
        }

        //
        panels[0].add(dirLabel);

        //
        for (int i = 0; i < numIndexes; i++)
        {
            panels[1 + i].add(descLabels[i]);
            panels[1 + i].add(sbd[i]);
            panels[1 + i].add(del[i]);
            panels[1 + i].add(upd[i]);
            panels[1 + i].add(expi[i]);
        }

        //
        panels[numPanels - 1].add(okButton);
        panels[numPanels - 1].add(cancelButton);

        // now for the gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        JPanel allPanels = new JPanel();
        JScrollPane tagsPane = new JScrollPane(allPanels);
        tagsPane.setPreferredSize(new Dimension(730, 320));
        allPanels.setLayout(new GridLayout(1, numPanels - 2));
        gridbaglayout = new GridBagLayout();
        gridbagconstraints = new GridBagConstraints();
        allPanels.setLayout(gridbaglayout);

        // populate the scrollpane
        for (int i = 1; i < (numPanels - 1); i++)
        {
            //
            gridbagconstraints.fill = 1;
            gridbagconstraints.insets = new Insets(1, 1, 1, 1);
            gridbagconstraints.gridx = 0;
            gridbagconstraints.gridy = i;
            gridbagconstraints.gridwidth = 1;
            gridbagconstraints.gridheight = 1;
            gridbagconstraints.weightx = 0.0D;
            gridbagconstraints.weighty = 0.0D;
            gridbaglayout.setConstraints(panels[i], gridbagconstraints);
            allPanels.add(panels[i]);
        }
         // end for adding panels	

        getContentPane().setLayout(new GridLayout(1, 3));
        gridbaglayout = new GridBagLayout();
        gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);

        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(panels[0], gridbagconstraints);
        getContentPane().add(panels[0]);

        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(tagsPane, gridbagconstraints);
        getContentPane().add(tagsPane);

        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(panels[numPanels - 1], gridbagconstraints);
        getContentPane().add(panels[numPanels - 1]);
    }

    public void init()
    {
        pack();

        // center this dialog
        Rectangle frameSize = getBounds();
        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenD.width;
        int screenHeight = screenD.height;
        int newX = 0;
        int newY = 0;
        if (screenWidth > frameSize.width)
        {
            newX = (screenWidth - frameSize.width) / 2;
        }

        if (screenHeight > frameSize.height)
        {
            newY = (screenHeight - frameSize.height) / 2;
        }

        if ((newX != 0) || (newY != 0))
        {
            setLocation(newX, newY);
        }

    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if (s.equals("Apply"))
        {
            returnBool = true;
            this.setVisible(false);
        }

        if (s.equals("Cancel"))
        {
            returnBool = false;
            this.setVisible(false);
        }
        else
        {
            System.out.println("Action was: " + s);
        }
    }
}
