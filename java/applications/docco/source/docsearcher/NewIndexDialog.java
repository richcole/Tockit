package docsearcher;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

public class NewIndexDialog
    extends JDialog
    implements ActionListener
{
    final static String[] updateChoices =
    {
        "When I say so", "During Startup", "When Index > 1 Day Old",
        "When Index > 5 Days old", "When Index > 30 Days Old",
        "When Index > 60 Days Old", "When Index > 90 Days Old",
        "When Index > 180 Days Old", "When Index > 365 Days Old"
    };
    JPanel[] panels;
    JButton okButton = new JButton("Add New Index");
    JButton cancelButton = new JButton("Cancel");
    boolean returnBool = false;

    // start in
    JLabel startLabel = new JLabel("Start in folder");
    JLabel descLabel =
        new JLabel("Description for this new Index (should be short but meaningful):");
    JTextField descField = new JTextField(25);
    JTextField startField = new JTextField(25);
    JButton startInButton = new JButton("Select a Folder");

    // search depth
    JLabel searchDepthLabel =
        new JLabel("Search Depth (# of subfolder levels):");
    JComboBox sdChoice = new JComboBox();
    int numPanels = 6;
    JCheckBox searchByDefault = new JCheckBox("Searched by Default");
    JCheckBox isWeb = new JCheckBox("Web Server Indexing");
    JTextField replaceField = new JTextField(25);
    JButton selectFold = new JButton("Select folder by locating a file");
    JLabel replaceLabel = new JLabel("Match Pattern (folder)");
    JLabel matchLabel = new JLabel("Replace Pattern (URL)");
    JTextField matchField = new JTextField(45);
    DocSearch monitor;
    Font f = new Font("Times", Font.BOLD, 16);
    JLabel dirLabel = new JLabel("Select a start location and a depth below.");
    JPanel webPanel = new JPanel();
    CheckBoxListener cbl;
    JPanel indexFreqPanel = new JPanel();
    JComboBox indexFreq = new JComboBox(updateChoices);
    JLabel freqLabel = new JLabel("When should this index be updated?");
    JTabbedPane tabbedPane;
    JPanel archivePanel;
    JPanel archiveContentsPanel;
    JLabel archiveTitle =
        new JLabel("Archiving is a process that stores the DocSearcher Lucene indexes in zip files.");
    JLabel archiveTitle2 =
        new JLabel("These zip archives may be used as backups or for exporting to a search engine.");
    JLabel archiveLabel = new JLabel("Archive this index to:");
    JTextField archiveField = new JTextField(33);
    JButton archiveBrowseButton = new JButton("Browse to Folder");

    NewIndexDialog(DocSearch monitor,
                   String title,
                   boolean modal)
    {
        super(monitor, title, modal);

        //super(parent, "Generate Meta Tag Table", true);
        this.monitor = monitor;
        cbl = new CheckBoxListener();
        tabbedPane = new JTabbedPane();

        //
        archiveField.setText(monitor.getArchiveDir());

        // accessibility info
        searchByDefault.setSelected(true);
        sdChoice.setToolTipText("Select subfolder depth  (starting from the 'start in folder').");
        startField.setToolTipText("Examination will start in this folder, continuing for the number of subfolders selected.");
        searchByDefault.setToolTipText("Checking this means your this content will be part of the default searches.");

        //
        //
        okButton.setMnemonic(KeyEvent.VK_A);
        okButton.setToolTipText("Add this new index");

        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText("Cancel");

        //
        selectFold.setMnemonic(KeyEvent.VK_S);
        selectFold.setToolTipText("Select folder by locating a file");

        //
        selectFold.addActionListener(this);
        selectFold.setEnabled(false);

        //
        dirLabel.setFont(f);
        for (int i = 0; i <= 20; i++)
        {
            sdChoice.addItem(i + "");
        }

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        startInButton.addActionListener(this);
        archiveBrowseButton.addActionListener(this);
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++)
        {
            panels[i] = new JPanel();
        }

        //
        JPanel nWebPanel = new JPanel();
        JPanel cWebPanel = new JPanel();
        JPanel sWebPanel = new JPanel();

        //
        archivePanel = new JPanel();
        archiveContentsPanel = new JPanel();
        archiveContentsPanel.add(archiveLabel);
        archiveContentsPanel.add(archiveField);
        archiveContentsPanel.add(archiveBrowseButton);
        archivePanel.setLayout(new BorderLayout());
        archivePanel.setBorder(new TitledBorder("Options for archiving this new index"));
        archivePanel.add(archiveTitle, BorderLayout.NORTH);
        archivePanel.add(archiveTitle2, BorderLayout.CENTER);
        archivePanel.add(archiveContentsPanel, BorderLayout.SOUTH);

        //
        nWebPanel.add(isWeb);
        cWebPanel.add(matchLabel);
        cWebPanel.add(matchField);
        sWebPanel.add(replaceLabel);
        sWebPanel.add(replaceField);
        sWebPanel.add(selectFold);

        //
        isWeb.addActionListener(cbl);

        //
        //
        panels[1].add(startLabel);
        panels[1].add(startField);
        panels[1].add(startInButton);
        panels[2].add(searchDepthLabel);
        panels[2].add(sdChoice);
        panels[3].add(searchByDefault);

        //
        JPanel optsPane = new JPanel();
        optsPane.setLayout(new BorderLayout());
        optsPane.setBorder(new TitledBorder("Options for indexing files on a local hard drive"));
        optsPane.add(panels[1], BorderLayout.NORTH);
        optsPane.add(panels[2], BorderLayout.CENTER);
        optsPane.add(panels[3], BorderLayout.SOUTH);
        webPanel.setLayout(new BorderLayout());
        webPanel.add(nWebPanel, BorderLayout.NORTH);
        webPanel.add(cWebPanel, BorderLayout.CENTER);
        webPanel.add(sWebPanel, BorderLayout.SOUTH);

        //
        indexFreqPanel.setLayout(new BorderLayout());
        indexFreqPanel.setBorder(new TitledBorder("Index Updating Options"));
        JPanel freqP = new JPanel();
        JLabel notice =
            new JLabel("Note: You can change this setting later if you need to.");
        JLabel noLabel =
            new JLabel("Updating an index ensures searches reflect changes in your files.");
        freqP.add(freqLabel);
        freqP.add(indexFreq);
        indexFreqPanel.add(noLabel, BorderLayout.NORTH);
        indexFreqPanel.add(freqP, BorderLayout.CENTER);
        indexFreqPanel.add(notice, BorderLayout.SOUTH);

        //
        webPanel.setBorder(new TitledBorder("Web Server Index Properties"));
        tabbedPane.addTab("General Options", null, optsPane,
                          "Tell DocSearcher what files you want to be able to Search");
        tabbedPane.addTab("Advanced Options", null, webPanel,
                          "For a web server indexing.");
        tabbedPane.addTab("Updates", null, indexFreqPanel,
                          "Specify when an Index should be updated.");

        // archivePanel
        tabbedPane.addTab("Archiving", null, archivePanel,
                          "Specify a folder where this index is archived to.");
        panels[0].add(descLabel);
        panels[0].add(descField);
        panels[4].add(tabbedPane);
        panels[5].add(okButton);
        panels[5].add(cancelButton);
        panels[2].setBackground(Color.orange);
        panels[1].setBackground(Color.orange);
        panels[3].setBackground(Color.orange);
        searchByDefault.setBackground(Color.orange);

        // now for the gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        int curP = 0;
        for (int i = 0; i < numPanels; i++)
        {
            //
            if ((i == 0) || (i >= 4))
            {
                gridbagconstraints.fill = 1;
                gridbagconstraints.insets = new Insets(1, 1, 1, 1);
                gridbagconstraints.gridx = 0;
                gridbagconstraints.gridy = curP;
                gridbagconstraints.gridwidth = 1;
                gridbagconstraints.gridheight = 1;
                gridbagconstraints.weightx = 0.0D;
                gridbagconstraints.weighty = 0.0D;
                gridbaglayout.setConstraints(panels[i], gridbagconstraints);
                getContentPane().add(panels[i]);
                curP++;
            }
        }
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

        // end of centering the dialog
        cb();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if (s.equals("Add New Index"))
        {
            StringBuffer errBuf = new StringBuffer();
            boolean hasErr = false;
            if (descField.getText().trim().equals(""))
            {
                hasErr = true;
                errBuf.append("Missing a description.\n\n");
            }

            if (startField.getText().trim().equals(""))
            {
                hasErr = true;
                errBuf.append("Missing a start location (directory to index).\n\n");
            }

            if (isWeb.isSelected())
            {
                if ((matchField.getText().equals(""))
                        || (matchField.getText().equals("http://")))
                {
                    hasErr = true;
                    errBuf.append("Missing Web server URL (replacement pattern) info.\n\n");
                }
                else if (!matchField.getText().endsWith("/"))
                {
                    hasErr = true;
                    errBuf.append("Error: URL (replacement pattern) should end with a \"/\"\n\n");
                }

                if (replaceField.getText().equals(""))
                {
                    hasErr = true;
                    errBuf.append("Missing Match Pattern (folder).\n\n");
                }
                else if (!replaceField.getText().endsWith(DocSearch.pathSep))
                {
                    hasErr = true;
                    errBuf.append("Error: Match Pattern (folder) should end with a path separator ("
                                  + DocSearch.pathSep + ")\n\n");
                }
            }

            if (hasErr)
            {
                monitor.showMessage("Missing Information", errBuf.toString());
            }
            else
            {
                returnBool = true;
                this.setVisible(false);
            }
        }
        else if (s.equals("Select folder by locating a file"))
        {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(DocSearch.userHome));
            int fileGotten = fdo.showDialog(this, "Select a File");
            if (fileGotten == JFileChooser.APPROVE_OPTION)
            {
                File file = fdo.getCurrentDirectory();
                replaceField.setText(file.toString());
            }
        }
        else if (s.equals("Cancel"))
        {
            returnBool = false;
            this.setVisible(false);
        }
        else if (s.equals("Select a Folder"))
        {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(DocSearch.userHome));
            fdo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int fileGotten = fdo.showDialog(this, "Select");
            if (fileGotten == JFileChooser.APPROVE_OPTION)
            {
                File file = fdo.getSelectedFile();
                startField.setText(file.toString());
            }
        }
        else if (s.equals("Browse to Folder"))
        {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(DocSearch.userHome));
            fdo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int fileGotten = fdo.showDialog(this, "Select");
            if (fileGotten == JFileChooser.APPROVE_OPTION)
            {
                File file = fdo.getSelectedFile();
                archiveField.setText(file.toString());
            }
        }
        else
        {
            System.out.println("Action was: " + s);
        }
    }

    public void cb()
    {
        if (isWeb.isSelected())
        {
            replaceField.setEnabled(true);
            matchField.setEnabled(true);
            replaceField.setText(startField.getText());
            matchField.setText("http://");
            selectFold.setEnabled(true);
        }
        else
        {
            replaceField.setEnabled(false);
            matchField.setEnabled(false);
            replaceField.setText("na");
            matchField.setText("na");
            selectFold.setEnabled(false);
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
