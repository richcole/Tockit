package docsearcher;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ImportDialog
    extends JDialog
    implements ActionListener
{
    DocSearch monitor;
    JButton okButton = new JButton("Open");
    JButton cancelButton = new JButton("Cancel");
    JLabel urlLabel =
        new JLabel("Docsearcher Index to import (URL or File; a zip) :");
    JButton fileBrowse;
    JTextField urlOrFile = new JTextField(25);
    JLabel dirLabel = new JLabel("Provide a url or file to import below.");
    JPanel bp = new JPanel();
    boolean confirmed = false;

    ImportDialog(DocSearch monitor,
                 String title,
                 boolean modal)
    {
        super(monitor, title, modal);
        this.monitor = monitor;
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        //System.out.println("icon dir is:"+monitor.iconDir);
        fileBrowse =
            new JButton(new ImageIcon(Utils.addFolder(monitor.iconDir,
                                                        "open.gif")));
        fileBrowse.setActionCommand("Browse Files");
        fileBrowse.addActionListener(this);
        fileBrowse.setToolTipText("Open Browser");
        bp.add(cancelButton);
        bp.add(okButton);

        // load up the GUI
        //
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setToolTipText("Open");

        //
        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText("Cancel");

        //
        // NOW PLACE THE GUI ONTO A GRIDBAG
        // put in the gridbag stuff
        getContentPane().setLayout(new GridLayout(3, 3));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 3;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 2.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(dirLabel, gridbagconstraints);
        getContentPane().add(dirLabel);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(urlLabel, gridbagconstraints);
        getContentPane().add(urlLabel);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 1;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(urlOrFile, gridbagconstraints);
        getContentPane().add(urlOrFile);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 2;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(fileBrowse, gridbagconstraints);
        getContentPane().add(fileBrowse);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 3;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(bp, gridbagconstraints);
        getContentPane().add(bp);
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
        System.out.println("action:" + s);
        if (s.equals("Open"))
        {
            confirmed = false;
            String chosenFi = urlOrFile.getText().trim();
            if (chosenFi.equals(""))
            {
                monitor.showMessage("Please Choose a File",
                                    "You need to specify a file or URL (zip) to import.");
            }
            else
            { // some text was there
                if (!chosenFi.toLowerCase().endsWith(".zip"))
                {
                    monitor.showMessage("Not a DocSearcher Archive",
                                        "DocSearcher archives are ZIP files.");
                }
                else if (!chosenFi.toLowerCase().startsWith("http:"))
                {
                    File testExist = new File(chosenFi);
                    if (!testExist.exists())
                    {
                        monitor.showMessage("File Does Not Exist",
                                            "Please select a valid DocSearcher Archive file.");
                    }
                    else
                    { // file exists
                        confirmed = true;
                        this.setVisible(false);
                    }
                }
                 // not a URL
                else
                { // a URL
                    confirmed = true;
                    this.setVisible(false);
                }
            }
        }
        else if (s.equals("Browse Files"))
        {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(DocSearch.userHome));
            int fileGotten = fdo.showDialog(this, "Select");
            if (fileGotten == JFileChooser.APPROVE_OPTION)
            {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                if (fNa.toLowerCase().endsWith(".zip"))
                {
                    urlOrFile.setText(fNa);
                }
                else
                {
                    monitor.showMessage("Not a DocSearcher Archive",
                                        "DocSearcher archives are ZIP files.");
                }
            }
        }
        else if (s.equals("Cancel"))
        {
            confirmed = false;
            this.setVisible(false);
        }
    }
}
