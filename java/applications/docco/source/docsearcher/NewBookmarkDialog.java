package docsearcher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NewBookmarkDialog
    extends JDialog
    implements ActionListener
{
    JPanel[] panels;
    JButton okButton = new JButton("Add");
    JButton cancelButton = new JButton("Cancel");
    boolean returnBool = false;

    // start in
    JLabel titleLabel = new JLabel("Description:");
    JTextField descField = new JTextField(25);

    // search depth
    JLabel locationLabel = new JLabel("URL:");
    JTextField locationField = new JTextField(45);
    int numPanels = 4;
    DocSearch monitor;
    Font f = new Font("Times", Font.BOLD, 16);
    JLabel dirLabel =
        new JLabel("Modify the title - if necessary and click the 'add' button.");

    NewBookmarkDialog(DocSearch monitor,
                      String title,
                      boolean modal)
    {
        super(monitor, title, modal);

        //super(parent, "Generate Meta Tag Table", true);
        this.monitor = monitor;
        locationField.setEditable(false);

        // accessibility info
        //
        okButton.setMnemonic(KeyEvent.VK_A);
        okButton.setToolTipText("Add");

        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText("Cancel");

        //
        dirLabel.setFont(f);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++)
        {
            panels[i] = new JPanel();
        }

        panels[0].add(dirLabel);

        //
        panels[1].add(titleLabel);
        panels[1].add(descField);

        panels[2].add(locationLabel);
        panels[2].add(locationField);

        panels[3].add(okButton);
        panels[3].add(cancelButton);
        panels[2].setBackground(Color.orange);
        panels[1].setBackground(Color.orange);

        // now for the gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        for (int i = 0; i < numPanels; i++)
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
            getContentPane().add(panels[i]);
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
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if (s.equals("Add"))
        {
            StringBuffer errBuf = new StringBuffer();
            boolean hasErr = false;
            if (descField.getText().trim().equals(""))
            {
                hasErr = true;
                errBuf.append("Missing a description.\n\nPlease provide a description for this new bookmark.");
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
