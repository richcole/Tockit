/**
 *  This class provides the main GUI panel with menus and a toolbar
 *  for ToscanaJ.
 *
 *  28/03/2001
 *
 *  @author Nataliya Roberts, Thomas Tilley
 *
 */

package net.sourceforge.tockit.toscanaj.gui;

import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JList;

import net.sourceforge.tockit.toscanaj.data.ConceptualSchema;
import net.sourceforge.tockit.toscanaj.data.DatabaseInfo;
import net.sourceforge.tockit.toscanaj.diagram.DiagramView;
import net.sourceforge.tockit.toscanaj.gui.DatabaseChooser;
import net.sourceforge.tockit.toscanaj.parser.*;

// debugging
import net.sourceforge.tockit.toscanaj.data.*;
import java.awt.geom.*;
//import org.jdom.*;
import org.jdom.DataConversionException;

public class MainPanel extends JFrame implements ActionListener {


    private JToolBar toolbar = null;
    private JMenuBar menubar = null;

	// buttons list
    private JButton newButton      = null;
    private JButton openButton     = null;
    private JButton saveButton     = null;
    private JButton contentsButton = null;


	// menu items list
	// FILE menu
	private JMenuItem newMenuItem		= null;
	private JMenuItem openMenuItem		= null;
	private JMenuItem closeMenuItem		= null;
	private JMenuItem saveMenuItem		= null;
	private JMenuItem saveAsMenuItem	= null;
	private JMenuItem printPrevMenuItem	= null;
	private JMenuItem printMenuItem		= null;
	private JMenuItem printSetupMenuItem= null;
	private JMenuItem exitMenuItem		= null;

	// THEMES menu
	private JMenuItem zoomOutMenuItem	= null;
	private JMenuItem managMenuItem		= null;

	// DIAGRAM menu
	private	ButtonGroup diagrGroup1		= null;
	private	ButtonGroup diagrGroup2		= null;
	private JMenuItem redrawMenuItem	= null;
	private JRadioButtonMenuItem exDocMenuItem		= null;
	private JRadioButtonMenuItem spDocMenuItem		= null;
	private JRadioButtonMenuItem allDocMenuItem	= null;
	private JMenuItem numDocMenuItem	= null;
	private JMenuItem persDistMenuItem	= null;
	private JMenuItem listDocMenuItem	= null;
	private JMenuItem moveLabMenuItem	= null;

	private int currentSelectedIndex;

    // icon images
    private static final String OPEN_ICON          = "open.gif";
    private static final String NEW_ICON           = "new.gif";
    private static final String SAVE_ICON          = "save.gif";
    private static final String SAVE_ICON_DISABLED = "saveDisabled.gif";
    private static final String CONTENTS_ICON      = "contents.gif";
    private static final String CLEAR_ICON          = "clear.gif";

    /**
     * The main model member.
     */
    private ConceptualSchema conceptualSchema;

    /**
     * The diagram viewing area.
     */
    private DiagramView diagramView;

    // specify the location of icon images - this is platform safe
    // because the String is converted into a URL so the "/" is OK for
    // all platforms.
    private static final String IMAGE_PATH         = "resource/icons/";

    // flag to indicate if the save icon and menu options should be
    // enabled
    public boolean fileIsOpen = false;

    /**
     *  default constructor
     */

    public MainPanel() {

		super("ToscanaJ 0.1");
		buildPanel();

    }


    /**
     *  build the GUI
     */

    private void buildPanel() {

		buildMenuBar();
		setJMenuBar(menubar);

		buildToolBar();

        //Lay out the content pane.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        diagramView = new DiagramView();

//        contentPane.add(toolbar, BorderLayout.NORTH);
        contentPane.add(diagramView, BorderLayout.CENTER);

        setContentPane( contentPane );
        contentPane.setPreferredSize(new Dimension(400, 400));
    }


    /**
     *  build the MenuBar
     */

    private void buildMenuBar() {

		// create menu bar

		menubar = new JMenuBar();

		// create the FILE menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menubar.add(fileMenu);

		// menu item NEW
	 	newMenuItem = new JMenuItem("New",
						new ImageIcon(IMAGE_PATH + NEW_ICON));
		newMenuItem.setMnemonic(KeyEvent.VK_N);
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newMenuItem.addActionListener(this);
		fileMenu.add(newMenuItem);

		// menu item OPEN
	 	openMenuItem = new JMenuItem("Open...",
						new ImageIcon(IMAGE_PATH + OPEN_ICON));
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openMenuItem.addActionListener(this);
		fileMenu.add(openMenuItem);

		// menu item CLOSE
		/*
	 	closeMenuItem = new JMenuItem("Close",
						new ImageIcon(IMAGE_PATH + CLEAR_ICON));
		closeMenuItem.setMnemonic(KeyEvent.VK_C);
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		closeMenuItem.setEnabled(fileIsOpen);
		closeMenuItem.addActionListener(this);
		fileMenu.add(closeMenuItem);
		*/

		// separator
		fileMenu.addSeparator();

		/*

		// menu item SAVE
	 	saveMenuItem = new JMenuItem("Save",
						new ImageIcon(IMAGE_PATH + SAVE_ICON));
		saveMenuItem.setMnemonic(KeyEvent.VK_S);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.addActionListener(this);
		saveMenuItem.setEnabled(fileIsOpen);
		fileMenu.add(saveMenuItem);

		// menu item SAVE AS
	 	saveAsMenuItem = new JMenuItem("Save As",
						new ImageIcon(IMAGE_PATH + CLEAR_ICON));
		saveAsMenuItem.addActionListener(this);
		saveAsMenuItem.setEnabled(fileIsOpen);
		fileMenu.add(saveAsMenuItem);

		// separator
		fileMenu.addSeparator();
		*/

		// menu item PRINT PREVIEW
	 	printPrevMenuItem = new JMenuItem("Print Preview");
		printPrevMenuItem.addActionListener(this);
		printPrevMenuItem.setEnabled(fileIsOpen);
		fileMenu.add(printPrevMenuItem);

		// menu item PRINT
	 	printMenuItem = new JMenuItem("Print");
		printMenuItem.setMnemonic(KeyEvent.VK_P);
		printMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		printMenuItem.addActionListener(this);
		printMenuItem.setEnabled(fileIsOpen);
		fileMenu.add(printMenuItem);

		// menu item PRINT SETUP
	 	printSetupMenuItem = new JMenuItem("Print Setup");
		printSetupMenuItem.addActionListener(this);
		printSetupMenuItem.setEnabled(fileIsOpen);
		fileMenu.add(printSetupMenuItem);

		// separator
		fileMenu.addSeparator();

		// recent edited files should come here
		JMenuItem dummyMenu = new JMenuItem("recent files here");
		dummyMenu.setEnabled(false);
		fileMenu.add(dummyMenu);

		// separator
		fileMenu.addSeparator();

		// menu item EXIT
	 	exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		exitMenuItem.setMnemonic(KeyEvent.VK_E);
		exitMenuItem.addActionListener(this);
		fileMenu.add(exitMenuItem);

		// create the THEMES menu
		JMenu themesMenu = new JMenu("Themes");
		themesMenu.setMnemonic(KeyEvent.VK_T);
		menubar.add(themesMenu);

		// menu item ZOOM OUT
	 	zoomOutMenuItem = new JMenuItem("Zoom Out");
		zoomOutMenuItem.addActionListener(this);
		zoomOutMenuItem.setEnabled(fileIsOpen);
		themesMenu.add(zoomOutMenuItem);

		// separator
		themesMenu.addSeparator();

		// menu item MANAGER
	 	managMenuItem = new JMenuItem("Manager");
		managMenuItem.addActionListener(this);
		managMenuItem.setEnabled(fileIsOpen);
		themesMenu.add(managMenuItem);

		// create the DIAGRAM menu
		JMenu diagrMenu = new JMenu("Diagram");
		diagrMenu.setMnemonic(KeyEvent.VK_D);
		menubar.add(diagrMenu);


		// menu item REDRAW
	 	redrawMenuItem = new JMenuItem("Redraw");
		redrawMenuItem.addActionListener(this);
		//redrawMenuItem.setEnabled(fileIsOpen);
		diagrMenu.add(redrawMenuItem);

		// separator
		diagrMenu.addSeparator();

		// menu radio buttons group:
		diagrGroup1 = new ButtonGroup();

		// radio button menu item EXACT DOCUMENTS

		// set checked by default. maybe this default should
		// be handled differently?....
		// use ButtonModel?

        exDocMenuItem = new JRadioButtonMenuItem("Exact Documents");
        exDocMenuItem.setSelected(true);
        exDocMenuItem.addActionListener(this);
        diagrGroup1.add(exDocMenuItem);
        diagrMenu.add(exDocMenuItem);

		// radio button menu item SPECIAL DOCUMENTS
		spDocMenuItem = new JRadioButtonMenuItem("Special Documents");
        spDocMenuItem.addActionListener(this);
        diagrGroup1.add(spDocMenuItem);
        diagrMenu.add(spDocMenuItem);

		// radio button menu item ALL DOCUMENTS
		allDocMenuItem = new JRadioButtonMenuItem("All Documents");
        allDocMenuItem.addActionListener(this);
        diagrGroup1.add(allDocMenuItem);
        diagrMenu.add(allDocMenuItem);

		// separator
		diagrMenu.addSeparator();

		// menu radio buttons group:
		diagrGroup2 = new ButtonGroup();

		// radio button menu item NUMBER OF DOCUMENTS

		// this item is checked by default. not sure
		// how to hadle this in ActionListener :(

        numDocMenuItem = new JRadioButtonMenuItem("Number Of Documents");
        numDocMenuItem.setSelected(true);
        numDocMenuItem.addActionListener(this);
        diagrGroup2.add(numDocMenuItem);
        diagrMenu.add(numDocMenuItem);

		// radio button menu item PERCEPTUAL DISTRIBUTION
        persDistMenuItem = new JRadioButtonMenuItem("Perceptual Distribution");
        persDistMenuItem.addActionListener(this);
        diagrGroup2.add(persDistMenuItem);
        diagrMenu.add(persDistMenuItem);

		// radio button menu item LIST OF DOCUMENTS
        listDocMenuItem = new JRadioButtonMenuItem("List Of Documents");
        listDocMenuItem.addActionListener(this);
        diagrGroup2.add(listDocMenuItem);
        diagrMenu.add(listDocMenuItem);

		/*
		// separator
		diagrMenu.addSeparator();

		// menu item MOVE LABELS
	 	moveLabMenuItem = new JMenuItem("Move Labels");
		moveLabMenuItem.addActionListener(this);
		//moveLabMenuItem.setEnabled(fileIsOpen);
		diagrMenu.add(moveLabMenuItem);
		*/




    }


    /**
     *  build the ToolBar
     */

    private void buildToolBar() {
		toolbar = new JToolBar();
		toolbar.setFloatable(true);

        // New button
        newButton = new JButton(new ImageIcon(IMAGE_PATH + NEW_ICON));
        newButton.setToolTipText("New");
		newButton.addActionListener(this);
        toolbar.add(newButton);

        // Open button
        openButton = new JButton(new ImageIcon(IMAGE_PATH + OPEN_ICON));
        openButton.setToolTipText("Open");
		openButton.addActionListener(this);
        toolbar.add(openButton);

        // Save button
        saveButton = new JButton(new ImageIcon(IMAGE_PATH + SAVE_ICON));
        saveButton.setToolTipText("Save");
		saveButton.addActionListener(this);
		saveButton.setEnabled(fileIsOpen);
        toolbar.add(saveButton);

        // add separator
        toolbar.addSeparator();

        // Contents button
        contentsButton = new JButton(new ImageIcon(IMAGE_PATH + CONTENTS_ICON));
        contentsButton.setToolTipText("Contents");
		contentsButton.addActionListener(this);
        toolbar.add(contentsButton);


    }

	/**
	 * Enable or disable relevant buttons and menus depending
	 * on boolean isOpen (referring to the face if any file/s is
	 * open ).
	 */

	public void resetButtons ( boolean isOpen) {

		// menues

		//closeMenuItem.setEnabled (isOpen);
		//saveMenuItem.setEnabled (isOpen);
		//saveAsMenuItem.setEnabled (isOpen);

		printPrevMenuItem.setEnabled (isOpen);
		printMenuItem.setEnabled (isOpen);
		printSetupMenuItem.setEnabled (isOpen);

		zoomOutMenuItem.setEnabled(isOpen);
		managMenuItem.setEnabled(isOpen);


		redrawMenuItem.setEnabled (isOpen);
		exDocMenuItem.setEnabled (isOpen);
		spDocMenuItem.setEnabled (isOpen);
		allDocMenuItem.setEnabled (isOpen);
		numDocMenuItem.setEnabled (isOpen);
		persDistMenuItem.setEnabled (isOpen);
		listDocMenuItem.setEnabled (isOpen);
		//moveLabMenuItem.setEnabled (isOpen);

		// buttons
		//saveButton.setEnabled (isOpen);
	}

	/**
	 * Close Main Window (Exit the program).
	 */
	private void closeMainPanel () {
		System.exit(0);

	}

	public void actionPerformed (ActionEvent ae) {
		Object actionSource = ae.getSource();
		//System.out.println ("actionSource = " + actionSource);
		//System.out.println ("action command = " + ae.getActionCommand());

		// Button actions
		if (actionSource == newButton) {
			System.out.println("Action for new button");	//stub
		}
		if (actionSource == openButton) {
			openSchemaFile();
		}
		if (actionSource == saveButton) {
			System.out.println("Action for save button"); // stub
		}
		if (actionSource == contentsButton) {
			System.out.println("Action for contents button"); // stub
		}

		// Menus actions

		// menu FILE
		if (actionSource == newMenuItem) {
			System.out.println("Action for menu item: new "); // stub
		}
		if (actionSource == openMenuItem) {
			openSchemaFile();
		}
		if (actionSource == closeMenuItem) {
			System.out.println("Action for menu item: close "); // stub
		}
		if (actionSource == saveMenuItem) {
			System.out.println("Action for menu item: save "); // stub
		}
		if (actionSource == saveAsMenuItem) {
			System.out.println("Action for menu item: saveAs "); // stub
		}
		if (actionSource == printPrevMenuItem) {
			System.out.println("Action for menu item: Print Preview "); // stub
		}
		if (actionSource == printMenuItem) {
			System.out.println("Action for menu item: Print "); // stub
		}
		if (actionSource == printSetupMenuItem) {
			System.out.println("Action for menu item: Print Setup "); // stub
		}
		if (actionSource == exitMenuItem) {
			System.out.println("Action for menu item: exit ");
			closeMainPanel();
		}

		// menu THEMES
		if (actionSource == zoomOutMenuItem) {
			System.out.println("Action for menu item: Zoom Out "); // stub
		}
		if (actionSource == managMenuItem) {
			System.out.println("Action for menu item: Manager "); // stub
			chooseDiagramView();
		}

		// menu DIAGRAM
		/*
		if (actionSource == diagrGroup1) {
			System.out.println("GOT diagrGroup1 action!!!");
		}
		*/
		if (actionSource == redrawMenuItem) {
			// this one is checked by default
			System.out.println("Acton for menu item: Redraw ");
		}

		// radio group 1
		if (actionSource == exDocMenuItem) {
			System.out.println("Acton for menu group: Exact Documents ");
		}
		if (actionSource == spDocMenuItem) {
			System.out.println("Acton for menu group: Special Documents ");
		}
		if (actionSource == allDocMenuItem) {
			System.out.println("Acton for menu group: All Documents ");
		}
		// end of radio group 1

		// radio group 2
		if (actionSource == numDocMenuItem) {
			System.out.println("Acton for menu group: Number Of Documents ");
		}
		if (actionSource == persDistMenuItem) {
			System.out.println("Acton for menu group: Perceptual Distribution");
		}
		if (actionSource == listDocMenuItem) {
			System.out.println("Acton for menu group: List Of Documents ");
		}
		// end of radio group 2

		if (actionSource == moveLabMenuItem) {
			System.out.println("Acton for menu group: Move Labels");
		}
	}

    /**
     * Open a file and parse it to create ConceptualSchema.
     */
    protected void openSchemaFile() {
        // get file containing schema
        File schemaFile;
        final JFileChooser openDialog =
                        new JFileChooser( System.getProperty( "user.dir" ) );
        int rv=openDialog.showOpenDialog( this );
        if( rv != JFileChooser.APPROVE_OPTION )
        {
            return;
        }
        schemaFile = openDialog.getSelectedFile();

        // parse it
        try
        {
            conceptualSchema = CSXParser.parse(schemaFile);
        }
        catch( FileNotFoundException e)
        {
            JOptionPane.showMessageDialog( this,
                    "Couldn't access the file.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE );
            System.err.println( e.getMessage() );
            return;
        }
        catch( IOException e)
        {
            JOptionPane.showMessageDialog( this,
                    "Some error happened when parsing the file:\n" +
                        e.getMessage(),
                    "File/XML error",
                    JOptionPane.ERROR_MESSAGE );
            System.err.println( e.getMessage() );
            return;
        }
        catch( DataFormatException e)
        {
            JOptionPane.showMessageDialog( this,
                    "Some error happened when parsing the file:\n" +
                        e.getMessage(),
                    "CSX error",
                    JOptionPane.ERROR_MESSAGE );
            System.err.println( e.getMessage() );
            return;
        }

        // if database should be used, but is not given --> ask user
        if( conceptualSchema.usesDatabase() &&
            ( conceptualSchema.getDatabaseInfo() == null ) )
        {
            DatabaseChooser.initialize( this, new DatabaseInfo() );
            DatabaseInfo dbInfo = DatabaseChooser.showDialog( this );
            if( dbInfo == null )
            {
                return;
            }
            else
            {
                conceptualSchema.setDatabaseInformation( dbInfo );
            }
        }

        // if there is at least one diagram, open the first
        if( conceptualSchema.getNumberOfDiagrams() != 0 )
        {
	    currentSelectedIndex = 0;
            diagramView.showDiagram( conceptualSchema.getDiagram( 0 ) );
        }

        // enable relevant buttons and menus
        fileIsOpen = true;
	resetButtons(fileIsOpen);
    }

	/**
	 * Select different diagram view
	 */
	public void chooseDiagramView(){
		// shouldn't need this check as relevant menu should
		// be disabled if there is no schema open
		if (conceptualSchema == null)
			return;
		int diagrNum = conceptualSchema.getNumberOfDiagrams();
		Vector diagrVector = new Vector (diagrNum);
		for (int i = 0; i < diagrNum; i++) {
			Diagram diagram = conceptualSchema.getDiagram(i);
			String diagrTitle = diagram.getTitle();
			diagrVector.addElement (diagrTitle);
		}

		// popup dialog window
		final JList list = new JList(diagrVector);
		list.setSelectedIndex(currentSelectedIndex);
		String title = "Choose Diagram Veiew";
		JFrame f = new JFrame(title);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                 System.exit(0);
            }
        });
		final JDialog chooseDialog = new JDialog (f, title);
		chooseDialog.setLocationRelativeTo(f);

		//buttons
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				chooseDialog.setVisible(false);
			}
		});
		JButton okButton = new JButton("OK");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				currentSelectedIndex = list.getSelectedIndex();
            	diagramView.showDiagram( conceptualSchema.getDiagram( currentSelectedIndex ) );
				chooseDialog.setVisible(false);
			}
		});
		JPanel buttonPane = new JPanel (new GridLayout(1,2));
		buttonPane.add(cancelButton);
		buttonPane.add(okButton);

		// contentPane
		Container contentPane = chooseDialog.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(list, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		chooseDialog.pack();
		chooseDialog.setVisible(true);


	}

    /**
     *  main method for testing
     */

    public static void main(String [] args) {

	MainPanel test = new MainPanel();

        test.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        test.setSize(600,450);
        test.pack();
        test.setVisible(true);
    }

}