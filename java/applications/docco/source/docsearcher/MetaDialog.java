package docsearcher;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

public class MetaDialog
    extends JDialog
    implements ActionListener
{
    DocSearch monitor;
    JButton okButton = new JButton("Run Report");
    JButton cancelButton = new JButton("Cancel");
    JPanel generalPanel = new JPanel();
    JPanel advancedPanel = new JPanel();
    CheckBoxListener cbl;
    JTabbedPane tabbedPane;

    //
    JPanel listPanel = new JPanel();
    JCheckBox listAll = new JCheckBox("List Meta Data for all files");

    //
    JPanel pathPanel = new JPanel();
    JCheckBox pathRequired =
        new JCheckBox("Require specific path or path text: ");
    JTextField pathField = new JTextField(10);
    JButton pathBrowseButton = new JButton("Browse paths...");

    //
    JPanel authPanel = new JPanel();
    JCheckBox authRequired = new JCheckBox("Require Specific Author Text");
    JTextField authField = new JTextField(25);

    //
    JPanel maxPanel = new JPanel();
    JLabel maxDocsLabel = new JLabel("Maximum # of Documents to retrieve: ");
    JComboBox maxDocs = new JComboBox();

    //
    JPanel reportPanel = new JPanel();
    JLabel reportFileLabel = new JLabel("Save Report to file: ");
    JTextField reportField = new JTextField(10);
    JButton browseReportFileButton = new JButton("Save As...");

    //
    JPanel datePanel = new JPanel();
    JCheckBox dateRequired = new JCheckBox("Max Age of Documents (in Days):");
    JTextField dateField = new JTextField(11);

    //
    JLabel dirLabel = new JLabel("Index to report on:");
    JPanel indexChoicePanel = new JPanel();
    JPanel bp = new JPanel();
    boolean confirmed = false;
    JComboBox indexChoice = new JComboBox();

    MetaDialog(DocSearch monitor,
               String title,
               boolean modal)
    {
        super(monitor, title, modal);
        this.monitor = monitor;

        //
        int numIs = monitor.indexes.size();
        DocSearcherIndex di;
        int indexToReportOn = -1;
        for (int i = 0; i < numIs; i++)
        {
            di = (DocSearcherIndex)monitor.indexes.get(i);
            if (i == 0)
            {
                pathField.setText(di.path);
            }

            indexChoice.addItem(di.desc);
        }

        indexChoicePanel.add(dirLabel);
        indexChoicePanel.add(indexChoice);

        //
        cbl = new CheckBoxListener();

        // set up the buttons
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        browseReportFileButton.addActionListener(this);
        pathBrowseButton.addActionListener(this);

        //
        tabbedPane = new JTabbedPane();

        //
        pathPanel.add(pathRequired);
        pathPanel.add(pathField);
        pathPanel.add(pathBrowseButton);

        //
        authPanel.add(authRequired);
        authPanel.add(authField);

        //
        listPanel.add(listAll);

        //
        generalPanel.setLayout(new BorderLayout());
        generalPanel.setBorder(new TitledBorder("General Options"));
        generalPanel.add(listAll, BorderLayout.NORTH);
        generalPanel.add(authPanel, BorderLayout.CENTER);
        generalPanel.add(pathPanel, BorderLayout.SOUTH);

        //
        tabbedPane.addTab("Listing, Author, Path", null, generalPanel,
                          "Tell DocSearcher what files you want obtain meta data on");

        //
        maxDocs.addItem("100");
        maxDocs.addItem("500");
        maxDocs.addItem("5000");
        maxDocs.addItem("10000");
        maxDocs.addItem("50000");
        maxDocs.addItem("100000");
        maxPanel.add(maxDocsLabel);
        maxPanel.add(maxDocs);

        //
        datePanel.add(dateRequired);
        datePanel.add(dateField);

        //
        reportPanel.add(reportFileLabel);
        reportPanel.add(reportField);
        reportPanel.add(browseReportFileButton);

        //
        advancedPanel.setLayout(new BorderLayout());
        advancedPanel.setBorder(new TitledBorder("Advanced Options"));
        advancedPanel.add(datePanel, BorderLayout.NORTH);
        advancedPanel.add(maxPanel, BorderLayout.CENTER);
        advancedPanel.add(reportPanel, BorderLayout.SOUTH);

        //
        tabbedPane.addTab("Date, Max Docs, Save Location", null, advancedPanel,
                          "Tell DocSearcher where to save the report and how many documents can be examined");

        //
        bp.add(okButton);
        bp.add(cancelButton);

        // load up the GUI
        //
        okButton.setMnemonic(KeyEvent.VK_R);
        okButton.setToolTipText("Open");

        //
        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText("Cancel");

        //
        // NOW PLACE THE GUI ONTO A GRIDBAG
        // put in the gridbag stuff
        getContentPane().setLayout(new GridLayout(1, 3));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(indexChoicePanel, gridbagconstraints);
        getContentPane().add(indexChoicePanel);

        //
        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(tabbedPane, gridbagconstraints);
        getContentPane().add(tabbedPane);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(bp, gridbagconstraints);
        getContentPane().add(bp);

        //
        pathRequired.addActionListener(cbl);
        authRequired.addActionListener(cbl);
        dateRequired.addActionListener(cbl);
        listAll.addActionListener(cbl);
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

        reportField.setText(Utils.addFolder(DocSearch.workingDir,
                                            "meta_rpt_file.htm"));
        authField.setText(System.getProperty("user.name"));
        dateField.setText("730");
        cb();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        System.out.println("action:" + s);
        if (s.equals("Run Report"))
        {
            // check everything
            confirmed = true;
            this.setVisible(false);
        }
         // end for ok
        else if (s.equals("Save As..."))
        {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(DocSearch.userHome));
            int fileGotten = fdo.showDialog(this, "Select");
            if (fileGotten == JFileChooser.APPROVE_OPTION)
            {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                if (!fNa.toLowerCase().endsWith(".htm"))
                {
                    fNa += ".htm";
                }

                reportField.setText(fNa);
            }
        }
        else if (s.equals("Browse paths..."))
        {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(DocSearch.userHome));
            fdo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int fileGotten = fdo.showDialog(this, "Select");
            if (fileGotten == JFileChooser.APPROVE_OPTION)
            {
                File file = fdo.getSelectedFile();
                pathField.setText(file.toString());
            }
        }
        else if (s.equals("Cancel"))
        {
            confirmed = false;
            this.setVisible(false);
        }
    }

    public void cb()
    {
        if (dateRequired.isSelected())
        {
            dateField.setEnabled(true);
        }
        else
        {
            dateField.setEnabled(false);
        }

        if (pathRequired.isSelected())
        {
            pathField.setEnabled(true);
            pathBrowseButton.setEnabled(true);
        }
        else
        {
            pathField.setEnabled(false);
            pathBrowseButton.setEnabled(false);
        }

        if (authRequired.isSelected())
        {
            authField.setEnabled(true);
        }
        else
        {
            authField.setEnabled(false);
        }
    }

    class CheckBoxListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String source = e.getActionCommand();
            cb();
        }
    }
}
