package docsearcher;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Uses JFrame to create a simple browser with printing.  User passes String
 * URL (e.g., http://www.rbi.com)  to set opening location. User then may
 * follow  hyperlinks or type new preferred location into  provided
 * JTextField.   Scaling options are demonstrated in this  example. Scaling
 * options may be set from a  submenu in the File menu or by specified
 * KeyStroke.  A second menu track nn websites, which user can  reselect as
 * destination using mouse.
 */
public class JBrowser
    extends JFrame
{
    private static final int kNumSites = 20; //number of sites listed in JMenu "Last 20" 
    private static final int kDefaultX = 640;
    private static final int kDefaultY = 480;
    private static final int prefScale = 0;
    private static final String kScale2Label = "2X Scale";
    private static final String kScaleFitLabel = "Scale to Fit";
    private static final String kScaleHalfLabel = "1/2 Scale";
    private static final String kScaleOffLabel = "Scaling Off";
    private static final String kScaleXLabel = "Scale by Width";
    private static final String kScaleYLabel = "Scale by Length";
    private JEditorPane mainPane;
    private String path;
    private JButton goButton = new JButton("Go");
    private JComponentVista vista;
    private JMenu fileMenu = new JMenu("File", true);
    private JMenu prefMenu = new JMenu("Print Preferences", true);
    private JMenu siteMenu = new JMenu("Last 20", true);
    private JRadioButtonMenuItem scale2RadioBut =
        new JRadioButtonMenuItem(kScale2Label);
    private JRadioButtonMenuItem scaleFitRadioBut =
        new JRadioButtonMenuItem(kScaleFitLabel);
    private JRadioButtonMenuItem scaleHalfRadioBut =
        new JRadioButtonMenuItem(kScaleHalfLabel);
    private JRadioButtonMenuItem scaleOffRadioBut =
        new JRadioButtonMenuItem(kScaleOffLabel, true);
    private JRadioButtonMenuItem scaleXRadioBut =
        new JRadioButtonMenuItem(kScaleXLabel);
    private JRadioButtonMenuItem scaleYRadioBut =
        new JRadioButtonMenuItem(kScaleYLabel);
    private JTextField pathField = new JTextField(30);
    private Vector siteMIVector = new Vector();

    public JBrowser(String url)
    {
        super("JBrowser HTML Printing Demo");
        path = url;
        addSite(path);

        try
        {
            mainPane = new JEditorPane(path);
        }
        catch (IOException ex)
        {
            ex.printStackTrace(System.err);
            System.exit(1);
        }

        JMenuBar menuBar = new JMenuBar();
        JPanel navPanel = new JPanel();
        JMenuItem printMI = new JMenuItem("Print");
        JMenuItem exitMI = new JMenuItem("Exit");

        printMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                                                      Event.CTRL_MASK));
        exitMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                                                     Event.CTRL_MASK));

        scale2RadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                                                             Event.CTRL_MASK));
        scaleFitRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                                                               Event.CTRL_MASK));
        scaleHalfRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
                                                                Event.CTRL_MASK));
        scaleOffRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                                                               Event.CTRL_MASK));
        scaleXRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                                                             Event.CTRL_MASK));
        scaleYRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
                                                             Event.CTRL_MASK));

        printMI.addActionListener(new printMIListener());
        exitMI.addActionListener(new exitMIListener());

        scaleXRadioBut.addActionListener(new scaleXListener());
        scaleYRadioBut.addActionListener(new scaleYListener());
        scaleFitRadioBut.addActionListener(new scaleFitListener());
        scaleHalfRadioBut.addActionListener(new scaleHalfListener());

        scale2RadioBut.addActionListener(new scale2Listener());

        pathField.addActionListener(new pathFieldListener());
        pathField.setText(path);
        goButton.addActionListener(new pathFieldListener());

        ButtonGroup scaleSetGroup = new ButtonGroup();
        scaleSetGroup.add(scale2RadioBut);
        scaleSetGroup.add(scaleFitRadioBut);
        scaleSetGroup.add(scaleHalfRadioBut);
        scaleSetGroup.add(scaleOffRadioBut);
        scaleSetGroup.add(scaleXRadioBut);
        scaleSetGroup.add(scaleYRadioBut);

        prefMenu.add(scaleXRadioBut);
        prefMenu.add(scaleYRadioBut);
        prefMenu.add(scaleFitRadioBut);
        prefMenu.add(scaleHalfRadioBut);
        prefMenu.add(scale2RadioBut);
        prefMenu.addSeparator();
        prefMenu.add(scaleOffRadioBut);

        fileMenu.add(prefMenu);
        fileMenu.add(printMI);
        fileMenu.addSeparator();
        fileMenu.add(exitMI);

        menuBar.add(fileMenu);
        menuBar.add(siteMenu);
        menuBar.add(pathField);
        menuBar.add(goButton);

        mainPane.setEditable(false);
        mainPane.addHyperlinkListener(new linkListener());

        vista = new JComponentVista(mainPane, new PageFormat());

        //addWindowListener(new BasicWindowMonitor()); 
        setContentPane(new JScrollPane(mainPane));
        setVisible(true);

        setJMenuBar(menuBar);
        setSize(kDefaultX, kDefaultY);
        setVisible(true);
    }

    public static void main(String[] args)
    {
        new JBrowser(args[0]);
    }

    /*
     * addSite method takes the String url and adds it to the 
     * Vector siteMIVector and the JMenu siteMenu. 
     */
    public void addSite(String url)
    {
        boolean beenThere = false;

/* 
* Cycle through the contents of the siteMenu, comparing 
* their labels to the string to determine if there is 
* redundancy. 
*/
        for (int i = 0; (i < siteMenu.getItemCount()) && !beenThere; i++)
        {
            JMenuItem site = siteMenu.getItem(i);

/* 
* The String url, is compared to the labels of the 
* JMenuItems in already stored in siteMIVector. If 
* the string matches an existing label, the older 
* redundant element, at i, is removed. The new JMenuItem 
* site is inserted in the Vector at 0. The updateMenu 
* method is called to update the "Last nn" menu accordingly 
* and the "beenThere" boolean trigger is set TRUE. 
*/
            if (site.getText().equals(url))
            {
                siteMIVector.removeElementAt(i);
                siteMIVector.insertElementAt(site, 0);
                updateMenu(siteMenu);
                beenThere = true;
            }
        }

/* 
* If the new JMenuItem site has a unique string, then the 
* addSite method handles it as follows. 
*/
        if (!beenThere)
        {
/* 
* If the "Last nn" menu has reached kNumSites capacity, 
* the oldest JMenuItem is removed from the vector, enabling 
* storage for the new menu item and maintaining the specified 
* capacity of the "Last nn" menu. 
*/
            if (siteMenu.getItemCount() >= kNumSites)
            {
                siteMIVector.removeElementAt(siteMIVector.size() - 1);
            }

/* 
* A new JMenuItem is created and a siteMenuListener added. 
* It is added to the vector then the menu is updated. 
*/
            JMenuItem site = new JMenuItem(url);
            site.addActionListener(new siteMenuListener(url));
            siteMIVector.insertElementAt(site, 0);
            System.out.println("\n Connected to " + url);
            updateMenu(siteMenu);
        }
    }

    public void updateMenu(JMenu menu)
    {
        menu.removeAll();
        for (int i = 0; i < siteMIVector.size(); i++)
        {
            JMenuItem mi = (JMenuItem)siteMIVector.elementAt(i);
            menu.add(mi);
        }
    }

/* 
* 
* The ActionListener methods 
* 
*/
    public class exitMIListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            System.out.println("\n Killing JBrowser...");
            System.out.println(" ...AHHHHHHHHHhhhhhhh...ya got me...ugh");
            System.exit(0);
        }
    }

    public class linkListener
        implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent ev)
        {
            try
            {
                if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    mainPane.setPage(ev.getURL());
                    path = ev.getURL().toString();
                    pathField.setText(path);
                    addSite(path);
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace(System.err);
            }
        }
    }

    public class pathFieldListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            System.out.println("\n Switching from " + path + " to "
                               + pathField.getText() + ".");
            path = pathField.getText();
            try
            {
                mainPane.setPage(path);
            }
            catch (IOException ex)
            {
                ex.printStackTrace(System.err);
            }

            if (!path.equals(""))
            {
                addSite(path);
            }
        }
    }

    public class printMIListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPageable(vista);
            try
            {
                if (pj.printDialog())
                {
                    pj.print();
                }
            }
            catch (PrinterException e)
            {
                System.out.println(e);
            }
        }
    }

    public class scale2Listener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(mainPane, new PageFormat());
            vista.setScale(2.0, 2.0);
        }
    }

    public class scaleFitListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(mainPane, new PageFormat());
            vista.scaleToFit(false);
        }
    }

    public class scaleHalfListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(mainPane, new PageFormat());
            vista.setScale(0.5, 0.5);
        }
    }

    public class scaleOffListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(mainPane, new PageFormat());
        }
    }

    public class scaleXListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(mainPane, new PageFormat());
            vista.scaleToFitX();
        }
    }

    public class scaleYListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(mainPane, new PageFormat());
            vista.scaleToFitY();
        }
    }

    public class siteMenuListener
        implements ActionListener
    {
        private String site;

        public siteMenuListener(String url)
        {
            site = url;
        }

        public void actionPerformed(ActionEvent evt)
        {
            System.out.println("\n Switching from " + path + " to " + site
                               + ".");
            path = site;
            try
            {
                mainPane.setPage(path);
            }
            catch (IOException ex)
            {
                ex.printStackTrace(System.err);
            }

            if (!path.equals(""))
            {
                addSite(path);
            }

            pathField.setText(path);
        }
    }
}
