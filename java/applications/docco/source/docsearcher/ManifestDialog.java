package docsearcher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ManifestDialog
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
    DocSearch monitor;
    JButton okButton = new JButton("Import");
    JButton cancelButton = new JButton("Cancel");
    JPanel descPanel = new JPanel();
    JLabel desLabel = new JLabel("Description for this new Index :");
    JTextField descField = new JTextField(25);
    JPanel checkPanels = new JPanel();
    JCheckBox sbdBox = new JCheckBox("Searched by Default");
    JCheckBox isWebBox = new JCheckBox("Web Site Index");
    JLabel updateLabel = new JLabel("Update Policy: ");

    //
    JPanel indexFreqPanel = new JPanel();
    JComboBox indexFreq = new JComboBox(updateChoices);

    //
    JLabel dirLabel =
        new JLabel("Provide the descriptive information for this imported Index.");
    JPanel bp = new JPanel();
    boolean confirmed = false;
    JPanel propsPanel = new JPanel();

    ManifestDialog(DocSearch monitor,
                   String title,
                   boolean modal)
    {
        super(monitor, title, modal);
        this.monitor = monitor;
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        //
        indexFreqPanel.add(updateLabel);
        indexFreqPanel.add(indexFreq);

        //
        descPanel.add(desLabel);
        descPanel.add(descField);

        //
        checkPanels.add(sbdBox);
        checkPanels.add(isWebBox);

        //
        propsPanel.setLayout(new BorderLayout());
        propsPanel.setBorder(new TitledBorder("Index Properties"));
        propsPanel.add(descPanel, BorderLayout.NORTH);
        propsPanel.add(checkPanels, BorderLayout.CENTER);
        propsPanel.add(indexFreqPanel, BorderLayout.SOUTH);

        //
        bp.add(cancelButton);
        bp.add(okButton);

        // load up the GUI
        //
        okButton.setMnemonic(KeyEvent.VK_I);
        okButton.setToolTipText("Import");

        //
        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText("Cancel");

        //
        // NOW PLACE THE GUI ONTO A GRIDBAG
        // put in the gridbag stuff
        getContentPane().setLayout(new GridLayout(3, 1));
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
        gridbagconstraints.weightx = 1.0D;
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
        gridbagconstraints.weighty = 1.0D;
        gridbaglayout.setConstraints(propsPanel, gridbagconstraints);
        getContentPane().add(propsPanel);

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
        if (s.equals("Import"))
        {
            if (descField.getText().trim().equals(""))
            {
                monitor.showMessage("Missing a Description",
                                    "Please provide a description for this imported index.");
            }
            else
            {
                confirmed = true;
                this.setVisible(false);
            }
        }
        else if (s.equals("Cancel"))
        {
            confirmed = false;
            this.setVisible(false);
        }
    }
}
