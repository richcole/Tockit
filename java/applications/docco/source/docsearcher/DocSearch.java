package docsearcher;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.DateFilter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

public class DocSearch
    extends JFrame
    implements ActionListener
{
    final static String startDir = System.getProperty("user.dir");
    final static String[] searchOpts =
    { "body", "title", "summary", "body", "keywords" };
    final static String[] fileTypesToFind =
    {
        Messages.getString("DocSearch.Web_Pages"),
        Messages.getString("DocSearch.Open/Star_Office_Files"),
        Messages.getString("DocSearch.MS_Office_Files"),
        Messages.getString("DocSearch.RTF_Files"),
        Messages.getString("DocSearch.PDF_Files"),
        Messages.getString("DocSearch.TEXT_Files"),
        Messages.getString("DocSearch.java_source_code")
    };
    final static String[] fileTypesToGet =
    { "htm", "sxc sxd sxi sxp sxw", "doc xls", "rtf", "pdf", "txt", "java" };
    final static String fileString = "file:///";
    final static String userHome = System.getProperty("user.home");
    final public static String pathSep = System.getProperty("file.separator");
    final static String searchTips = Messages.getString("DocSearch.searchTips");
    final static String aboutString =
        Messages.getString("DocSearch.aboutString");
    public final static GenericFilter wf = new GenericFilter();
    public final static FolderFilter ff = new FolderFilter();

    // printing vars
    private static final int kDefaultX = 640;
    private static final int kDefaultY = 480;
    private static final int prefScale = 0;
    private static final String kScale2Label =
        Messages.getString("DocSearch.scale2X");
    private static final String kScaleFitLabel =
        Messages.getString("DocSearch.scaleToFit");
    private static final String kScaleHalfLabel =
        Messages.getString("DocSearch.scaleHalf");
    private static final String kScaleOffLabel =
        Messages.getString("DocSearch.scaleOff");
    private static final String kScaleXLabel =
        Messages.getString("DocSearch.scaleWidth");
    private static final String kScaleYLabel =
        Messages.getString("DocSearch.scaleLength");
    int osType = 0;
    boolean useGui = true;
    String currentIndex = "";
    String defaultSaveFolder = "";
    String[] startA = { "" };
    public String workingDir = Utils.addFolder(userHome, ".docSearcher");
    String indexFile = Utils.addFolder(workingDir, "index_list.htm");
    String indexDir = Utils.addFolder(workingDir, "indexes");
    String iconsDir = Utils.addFolder(userHome, "icons");
    String bookmarksFile = Utils.addFolder(workingDir, "bookmarks.htm");
    String blankFile = "";
    String lastSearch = Messages.getString("DocSearch.lastSearch");
    String iconDir = "";
    boolean hasErr = false;
    boolean isLoading = true;
    boolean hasIcons = true;
    String errString = Messages.getString("DocSearch.unknownError");
    public String wordTextFile =
        Utils.addFolder(workingDir, "temp_word_file.txt");
    public String excelTextFile =
        Utils.addFolder(workingDir, "temp_excel_file.txt");
    public String rtfTextFile =
        Utils.addFolder(workingDir, "temp_rtf_file.txt");
    public String pdfTextFile =
        Utils.addFolder(workingDir, "temp_pdf_file.txt");
    public String ooTextFile = Utils.addFolder(workingDir, "temp_oo_file.xml");
    public String ooMetaTextFile =
        Utils.addFolder(workingDir, "temp_oo_meta_file.xml");
    public String ooTextOnlyFile =
        Utils.addFolder(workingDir, "temp_oo_text_file.txt");
    public String archiveDir = Utils.addFolder(workingDir, "archives");
    public String contentDir = Utils.addFolder(startDir, "content");

    // GUI items
    String curStatusString = Messages.getString("DocSearch.loading");
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu reportsMenu;
    JMenu helpMenu;
    JMenu bookMarkMenu;
    JMenu indexMenu;
    JOptionPane pane;
    JMenuItem mi;
    JComboBox searchField = new JComboBox();
    JLabel searchLabel = new JLabel(Messages.getString("DocSearch.searchFor"));
    JButton searchButton = new JButton(Messages.getString("DocSearch.search"));
    JPanel[] panels;
    int numPanels = 1;
    java.util.List fileList;
    java.util.List results;
    Searcher searcher;
    Hits hits;
    Query query;
    Hyperactive hl = new Hyperactive();
    String[] colors;
    JScrollPane scrollPane;
    JEditorPane editorPane;
    JToolBar toolbar;
    JButton[] iconButtons;
    boolean hasStartPage = false;
    String startPageString = "";
    private JComponentVista vista;
    private JMenu prefMenu =
        new JMenu(Messages.getString("DocSearch.printPreferences"), true);
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
    printMIListener pml = new printMIListener();
    JRadioButton keywords =
        new JRadioButton(Messages.getString("DocSearch.keywords"));
    JRadioButton phrase =
        new JRadioButton(Messages.getString("DocSearch.phrase"));
    JLabel searchInLabel = new JLabel(Messages.getString("DocSearch.searchIn"));
    JLabel searchTypeLabel =
        new JLabel(Messages.getString("DocSearch.searchType"));
    JComboBox searchIn = new JComboBox();
    JPanel sizeAndTypePanel;
    JPanel fileTypePanel;
    JCheckBox useType;
    JComboBox fileType;
    JLabel fileTypeLabel;
    JPanel sizePanel;
    JCheckBox useSize;
    JLabel sizeFromLabel;
    JLabel sizeToLabel;
    JTextField sizeToField;
    JTextField sizeFromField;
    ButtonGroup bg;
    LinkedList backList = new LinkedList();
    LinkedList forwardList = new LinkedList();
    int forwardPos = -1;
    int backwardPos = -1;
    public String curPage = Messages.getString("DocSearch.home"); // home always means the last searchresults
    ArrayList indexes = new ArrayList();
    ArrayList bookmarksList = new ArrayList();
    JLabel dirLabel = new JLabel(Messages.getString("DocSearch.statusUp"));
    WordProps wp; // object to retrieve MS Word Document Properties
    ExcelProps ep; // MS Excel object
    PdfToText pp;
    RtfToText rp;
    OoToText op;

    // GUI items for advanced searching
    JPanel metaPanel;
    JPanel datePanel;
    JCheckBox useDate;
    JTextField fromField;
    JLabel fromLabel;
    JLabel toLabel;
    JTextField toField;
    CheckBoxListener cbl;
    JPanel authorPanel;
    JCheckBox useAuthor;
    JTextField authorField;
    JLabel authorLabel;
    JTabbedPane tabbedPane;
    int maxNumHitsShown = 250;
    Index idx;

    DocSearch()
    {
        super(Messages.getString("DocSearch.title"));

        idx = new Index(this);

        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("linux") != -1)
        {
            osType = 1;
        }
        else if (os.indexOf("windows") != -1)
        {
            osType = 0;
        }
        else if (os.indexOf("nix") != -1)
        {
            osType = 2;
        }
        else if (os.indexOf("mac") != -1)
        {
            osType = 3;
        }
        else
        {
            osType = -1;
        }

        switch (osType)
        {
        case 0: // windows

            //System.out.println("Up and running on Windows("+os+")");
            try
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                SwingUtilities.updateComponentTreeUI(this);
            }
            catch (Exception eN)
            {
                System.out.println(eN.toString());
            }

            break;

        case 1: // Linux

            //System.out.println("Up and running on Linux("+os+")");
            break;

        case 2: // Unix variant

            //System.out.println("Up and running on Unix("+os+")");
            break;

        case 3: // MAC

            //System.out.println("Up and running on Linux("+os+")");
            break;

        case -1: //UNKNOWN

            //System.out.println("Up and running on unrecognized OS ("+os+")");
            break;
        }

        colors = new String[2];
        colors[0] = "ffeffa";
        colors[1] = "fdffda";

        startPageString = Utils.addFolder(startDir, "start_page.htm");
        File startPageFile = new File(startPageString);
        if (startPageFile.exists())
        {
            hasStartPage = true;
        }

        defaultSaveFolder = Utils.addFolder(workingDir, "saved_searches");

        iconDir = Utils.addFolder(startDir, "icons");
        File iconFolder = new File(iconDir);
        searchField.setEditable(true);
        searchField.addItem("");

        bg = new ButtonGroup();
        bg.add(phrase);
        bg.add(keywords);
        keywords.setSelected(true);
        
        keywords.setToolTipText("Use if the order of the words is NOT important");
        phrase.setToolTipText("Use if the order of the words IS important");

        int iconInt = 2;
        searchIn.addItem("Title and Document Body");
        searchIn.addItem("Title");
        searchIn.addItem("Summary");
        searchIn.addItem("Document Body");
        searchIn.addItem("Keywords");
        searchIn.setSelectedIndex(3);

        searchField.setPreferredSize(new Dimension(370, 22));

        if (iconFolder.exists())
        {
            int numIcons = 11;
            toolbar = new JToolBar();
            iconButtons = new JButton[numIcons];

            iconButtons[0] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir, "open.gif")));
            iconButtons[0].setToolTipText("Open saved search results");
            iconButtons[0].setActionCommand("Open");
            iconButtons[0].addActionListener(this);
            iconButtons[0].setMnemonic(KeyEvent.VK_O);

            iconButtons[1] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir, "save.gif")));
            iconButtons[1].setToolTipText("Save the current page");
            iconButtons[1].setActionCommand("Save");
            iconButtons[1].addActionListener(this);
            iconButtons[1].setMnemonic(KeyEvent.VK_S);

            iconButtons[2] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir,
                                                          "open_in_browser.gif")));
            iconButtons[2].setToolTipText("Open Current in External Browser");
            iconButtons[2].setActionCommand("Open in Browser");
            iconButtons[2].addActionListener(this);
            iconButtons[2].setMnemonic(KeyEvent.VK_E);

            iconButtons[3] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir, "back.gif")));
            iconButtons[3].setToolTipText("Go Back");
            iconButtons[3].setActionCommand("back");
            iconButtons[3].addActionListener(this);
            iconButtons[3].setMnemonic(KeyEvent.VK_B);

            iconButtons[4] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir, "home.gif")));
            iconButtons[4].setToolTipText("Home");
            iconButtons[4].setActionCommand("Home");
            iconButtons[4].addActionListener(this);
            iconButtons[4].setMnemonic(KeyEvent.VK_H);

            iconButtons[5] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir, "forward.gif")));
            iconButtons[5].setToolTipText("Forward");
            iconButtons[5].setActionCommand("forward");
            iconButtons[5].addActionListener(this);
            iconButtons[5].setMnemonic(KeyEvent.VK_F);

            iconButtons[6] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir, "refresh.gif")));
            iconButtons[6].setToolTipText("Refresh - reloads the current page");
            iconButtons[6].setActionCommand("Refresh");
            iconButtons[6].addActionListener(this);
            iconButtons[6].setMnemonic(KeyEvent.VK_L);

            iconButtons[7] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir,
                                                          "search_results.gif")));
            iconButtons[7].setToolTipText("Search Results");
            iconButtons[7].setActionCommand("Search Results");
            iconButtons[7].addActionListener(this);
            iconButtons[7].setMnemonic(KeyEvent.VK_R);

            iconButtons[8] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir,
                                                          "bookmark.gif")));
            iconButtons[8].setToolTipText("Bookmark this page");
            iconButtons[8].setActionCommand("bookmark");
            iconButtons[8].addActionListener(this);
            iconButtons[8].setMnemonic(KeyEvent.VK_M);

            iconButtons[9] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir, "print.gif")));
            iconButtons[9].setToolTipText("Print");
            iconButtons[9].setActionCommand("Print");
            iconButtons[9].addActionListener(pml);
            iconButtons[9].setMnemonic(KeyEvent.VK_P);

            iconButtons[numIcons - 1] =
                new JButton(new ImageIcon(Utils.addFolder(iconDir, "quite.gif")));
            iconButtons[numIcons - 1].setToolTipText("Exit DocSearcher");
            iconButtons[numIcons - 1].setActionCommand("Exit");
            iconButtons[numIcons - 1].addActionListener(this);
            iconButtons[numIcons - 1].setMnemonic(KeyEvent.VK_X);

            for (int i = 0; i < numIcons; i++)
            {
                // add the icons to the toolbar
                toolbar.add(iconButtons[i]);
                if ((i == 1) || (i == 2) || (i == 5) || (i >= 7))
                {
                    toolbar.addSeparator();
                }

                toolbar.setFloatable(false);
            }

            iconButtons[5].setEnabled(false); // forward
            iconButtons[3].setEnabled(false); // back

            Image iconImage =
                Toolkit.getDefaultToolkit().getImage(Utils.addFolder(iconDir,
                                                                     "ds.gif"));
            this.setIconImage(iconImage);
        }
        else
        {
            hasIcons = false;
            iconInt = 1;
        }

        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");

        // print gui items
        JMenuItem printMI = new JMenuItem("Print");
        printMI.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
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
        printMI.addActionListener(pml);

        scaleXRadioBut.addActionListener(new scaleXListener());
        scaleYRadioBut.addActionListener(new scaleYListener());
        scaleFitRadioBut.addActionListener(new scaleFitListener());
        scaleHalfRadioBut.addActionListener(new scaleHalfListener());
        scale2RadioBut.addActionListener(new scale2Listener());

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

        bookMarkMenu = new JMenu("Bookmarks");
        mi = new JMenuItem("Add a bookmark");
        mi.addActionListener(this);
        bookMarkMenu.add(mi);

        mi = new JMenuItem("Clear all bookmarks");
        mi.addActionListener(this);
        bookMarkMenu.add(mi);

        bookMarkMenu.addSeparator();

        // add and loaded bookmarks
        indexMenu = new JMenu("Index");
        mi = new JMenuItem("Create a New Index");
        mi.addActionListener(this);
        indexMenu.add(mi);
        mi = new JMenuItem("Manage Indexes");
        mi.addActionListener(this);
        indexMenu.add(mi);

        mi = new JMenuItem("Re-index all content");
        mi.addActionListener(this);
        indexMenu.add(mi);

        indexMenu.addSeparator();
        mi = new JMenuItem("Import a DocSearcher Index");
        mi.addActionListener(this);
        indexMenu.add(mi);

        fileMenu.addSeparator();
        mi = new JMenuItem("Exit");
        mi.addActionListener(this);
        fileMenu.add(mi);

        helpMenu = new JMenu("Help");
        mi = new JMenuItem("Search Tips");
        mi.addActionListener(this);
        helpMenu.add(mi);
        mi = new JMenuItem("About DocSearcher");
        mi.addActionListener(this);
        helpMenu.add(mi);
        reportsMenu = new JMenu("Reports");
        mi = new JMenuItem("Meta Data Report");
        mi.addActionListener(this);
        reportsMenu.add(mi);
        mi = new JMenuItem("Search Log Report (for DocSearcher Servlet Only!)");
        mi.addActionListener(this);
        reportsMenu.add(mi);
        menuBar.add(fileMenu);
        menuBar.add(indexMenu);
        menuBar.add(bookMarkMenu);
        menuBar.add(reportsMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        results = new ArrayList();

        editorPane = new JEditorPane("text/html", lastSearch);
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(hl);
        if (hasStartPage)
        {
            try
            {
                editorPane.setPage(fileString + startPageString);
            }
            catch (Exception eL)
            {
                editorPane.setText(lastSearch);
            }
        }

        scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(1024, 720));
        scrollPane.setMinimumSize(new Dimension(900, 670));
        scrollPane.setMaximumSize(new Dimension(1980, 1980));

        // create panels
        // add printing stuff
        vista = new JComponentVista(editorPane, new PageFormat());

        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        bottomPanel.add(searchTypeLabel);
        bottomPanel.add(keywords);
        bottomPanel.add(phrase);
        bottomPanel.add(searchInLabel);
        bottomPanel.add(searchIn);

        searchButton.setMnemonic(KeyEvent.VK_A);

        // GUI items for advanced searching
        metaPanel = new JPanel();
        datePanel = new JPanel();
        useDate = new JCheckBox("Use Date Properties");
        fromField = new JTextField(11);
        fromLabel = new JLabel("From:");
        toLabel = new JLabel("To:");
        toField = new JTextField(11);
        cbl = new CheckBoxListener();
        authorPanel = new JPanel();
        useAuthor = new JCheckBox("Use Author Properties");
        authorField = new JTextField(31);
        authorLabel = new JLabel("Author:");
        tabbedPane = new JTabbedPane();
        authorPanel.add(useAuthor);
        authorPanel.add(authorLabel);
        authorPanel.add(authorField);

        // combine stuff
        datePanel.add(useDate);
        datePanel.add(fromLabel);
        datePanel.add(fromField);
        datePanel.add(toLabel);
        datePanel.add(toField);

        metaPanel.setLayout(new BorderLayout());
        metaPanel.setBorder(new TitledBorder("Date Range and Author Options"));
        metaPanel.add(datePanel, BorderLayout.NORTH);
        metaPanel.add(authorPanel, BorderLayout.SOUTH);

        useDate.addActionListener(cbl);
        useAuthor.addActionListener(cbl);

        fromField.setText(DateTimeUtils.getLastYear());
        toField.setText(DateTimeUtils.getToday());
        authorField.setText(System.getProperty("user.name"));

        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++)
        {
            panels[i] = new JPanel();
        }

        // add giu to panels
        panels[0].setLayout(new BorderLayout());
        panels[0].add(topPanel, BorderLayout.NORTH);
        panels[0].add(bottomPanel, BorderLayout.SOUTH);
        panels[0].setBorder(new TitledBorder("Search Criteria"));
        searchButton.addActionListener(this);

        sizeAndTypePanel = new JPanel();
        fileTypePanel = new JPanel();
        useType = new JCheckBox("Use File Type Criteria");
        useType.addActionListener(cbl);
        fileType = new JComboBox(fileTypesToFind);
        fileTypeLabel = new JLabel("Search only these types of files:");
        fileTypePanel.add(useType);
        fileTypePanel.add(fileTypeLabel);
        fileTypePanel.add(fileType);

        sizePanel = new JPanel();
        useSize = new JCheckBox("Use File Size Criteria");
        useSize.addActionListener(cbl);
        sizeFromLabel = new JLabel("From (K Bytes):");
        sizeToLabel = new JLabel("To (K Bytes):");
        sizeFromField = new JTextField(10);
        sizeFromField.setText("0");
        sizeToField = new JTextField(10);
        sizeToField.setText("100");
        sizePanel.add(useSize);
        sizePanel.add(sizeFromLabel);
        sizePanel.add(sizeFromField);
        sizePanel.add(sizeToLabel);
        sizePanel.add(sizeToField);

        sizeAndTypePanel.setLayout(new BorderLayout());
        sizeAndTypePanel.setBorder(new TitledBorder("Document Type and File Size"));
        sizeAndTypePanel.add(fileTypePanel, BorderLayout.NORTH);
        sizeAndTypePanel.add(sizePanel, BorderLayout.SOUTH);

        // set up the tabbed pane
        tabbedPane.addTab("General Options", null, panels[0],
                          "General Search Criteria");
        tabbedPane.addTab("Date and Author", null, metaPanel,
                          "Options for selecting a range of dates or Author.");
        tabbedPane.addTab("Type and Size of Document", null, sizeAndTypePanel,
                          "Options for selecting specific file types and sizes.");

        // gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels + iconInt + 1));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        int start = 0;
        if (hasIcons)
        {
            gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
            gridbagconstraints.insets = new Insets(1, 1, 1, 1);
            gridbagconstraints.gridx = 0;
            gridbagconstraints.gridy = 0;
            gridbagconstraints.gridwidth = 1;
            gridbagconstraints.gridheight = 1;
            gridbagconstraints.weightx = 1.0D;
            gridbagconstraints.weighty = 0.0D;
            gridbaglayout.setConstraints(toolbar, gridbagconstraints);
            getContentPane().add(toolbar);
            start++;
        }

        for (int i = 0; i < numPanels; i++)
        {
            if (i == 0)
            {
                gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagconstraints.insets = new Insets(1, 1, 1, 1);
                gridbagconstraints.gridx = 0;
                gridbagconstraints.gridy = i + start;
                gridbagconstraints.gridwidth = 1;
                gridbagconstraints.gridheight = 1;
                gridbagconstraints.weightx = 1.0D;
                gridbagconstraints.weighty = 0.0D;
                gridbaglayout.setConstraints(tabbedPane, gridbagconstraints);
                getContentPane().add(tabbedPane);
            }
            else
            {
                gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagconstraints.insets = new Insets(1, 1, 1, 1);
                gridbagconstraints.gridx = 0;
                gridbagconstraints.gridy = i + start;
                gridbagconstraints.gridwidth = 1;
                gridbagconstraints.gridheight = 1;
                gridbagconstraints.weightx = 1.0D;
                gridbagconstraints.weighty = 0.0D;
                gridbaglayout.setConstraints(panels[i], gridbagconstraints);
                getContentPane().add(panels[i]);
            }
        }

        // now add the results area
        gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = iconInt;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbaglayout.setConstraints(scrollPane, gridbagconstraints);
        getContentPane().add(scrollPane);
        JPanel statusP = new JPanel();
        statusP.add(dirLabel);

        // now add the status label
        gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = numPanels + iconInt;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(statusP, gridbagconstraints);
        getContentPane().add(statusP);

        //
        File testArchDir = new File(archiveDir);
        if (!testArchDir.exists())
        {
            boolean madeDir = testArchDir.mkdir();
            if (!madeDir)
            {
                System.out.println("Error Creating archive Directory");
            }
            else
            {
                System.out.println("Created directory: " + archiveDir);
            }
        }
        loadIndexes();
    }
    public void init()
    {
        // GUI BUILDING
        // close window item
        this.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent windowevent)
                {
                    doExit();
                }
            });

        // center on the screen
        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenD.width;
        int screenHeight = screenD.height;
        setSize(640, 480);
        int newX = 0;
        int newY = 0;
        if (screenWidth > kDefaultX)
        {
            newX = (screenWidth - kDefaultX) / 2;
        }

        if (screenHeight > kDefaultY)
        {
            newY = (screenHeight - kDefaultY) / 2;
        }

        if ((newX != 0) || (newY != 0))
        {
            setLocation(newX, newY);
        }

        // now determine if we need to create an index
        if (!hasIndex())
        {
            curStatusString = "Initializing Document index...";
            String contentFolderS = Utils.addFolder(startDir, "content");
            File testCF = new File(contentFolderS);
            if (testCF.exists())
            {
                try
                {
                    DocSearcherIndex di =
                        new DocSearcherIndex(contentFolderS, "default index",
                                             true, 20,
                                             Utils.addFolder(indexDir, "default"),
                                             false, "", "", 0, archiveDir);
                    createNewIndex(di);
                }
                catch (Exception eS)
                {
                    showMessage("Error creating default index", eS.toString());
                }
            }
        }

        // HANDLE ERRORS
        isLoading = false;
        if (hasErr)
        {
            showMessage("Initialization Error", errString);
        }

        // ensure that the word and excel scratch files are saved as blanks
        StringBuffer blankBuf = new StringBuffer();
        blankBuf.append("      ");
        saveFile(excelTextFile, blankBuf);
        saveFile(wordTextFile, blankBuf);

        // set up our fields;
        cb();
    }

    public void checkUpdates()
    {
        int numDis = indexes.size();
        if (numDis > 0)
        {
            setStatus("Please wait...Check indexes for updates...");
            Iterator it = indexes.iterator();
            DocSearcherIndex di;
            int curDays = 0;
            while (it.hasNext())
            {
                di = (DocSearcherIndex)it.next();
                curDays = DateTimeUtils.getDaysOld(di);
                setStatus("Checking for updates to " + di.desc);
                switch (di.indexPolicy)
                {
                case 0: // When I say so
                    break;

                case 1: // During Startup
                    idx.updateIndex(di);

                    break;

                case 2: // When Index > 1 Day Old
                    if (curDays > 1)
                    {
                        idx.updateIndex(di);
                    }

                    break;

                case 3: // When Index > 5 Days old
                    if (curDays > 5)
                    {
                        idx.updateIndex(di);
                    }

                    break;

                case 4: // When Index > 30 Days Old
                    if (curDays > 30)
                    {
                        idx.updateIndex(di);
                    }

                    break;

                case 5: // When Index > 60 Days Old
                    if (curDays > 60)
                    {
                        idx.updateIndex(di);
                    }

                    break;

                case 6: // When Index > 90 Days Old
                    if (curDays > 90)
                    {
                        idx.updateIndex(di);
                    }

                    break;

                case 7: // When Index > 180 Days Old
                    if (curDays > 180)
                    {
                        idx.updateIndex(di);
                    }

                    break;

                case 8: // When Index > 365 Days Old
                    if (curDays > 365)
                    {
                        idx.updateIndex(di);
                    }

                    break;

                default: // whatever

                    break;
                }
            }
        }

        setStatus("Update checks complete.");
    }

    public void handleEventCommand(String s)
    {
        try
        {
            // we run validation in a thread so as not to interfere
            // with repaints of GUI
            if (s.equals("Exit"))
            {
                doExit();
            }
            else if (s.equals("Meta Data Report"))
            {
                doMetaReport();
            }
            else if (s.equals("Search Log Report (for DocSearcher Servlet Only!)"))
            {
                getSeachLogReport();
            }
            else if (s.equals("Create a New Index"))
            {
                doNewIndex();
            }
            else if (s.equals("Re-index all content"))
            {
                rebuildIndexes();
            }
            else if (s.equals("Manage Indexes"))
            {
                doManageIndexes();
            }
            else if (s.equals("Import a DocSearcher Index"))
            {
                getImportInfo();
            }
            else if (s.equals("forward"))
            {
                doForward();
            }
            else if (s.equals("Open"))
            {
                doOpen();
            }
            else if (s.equals("Save"))
            {
                doSave();
            }
            else if (s.equals("Open in Browser"))
            {
                doExternal(curPage);
            }
            else if (s.equals("back"))
            {
                doBack();
            }
            else if ((s.equals("bookmark")) || (s.equals("Add a bookmark")))
            {
                doBookmark();
            }
            else if (s.equals("Clear all bookmarks"))
            {
                bookmarksList.clear();
                File bmf = new File(bookmarksFile);
                boolean wasCleared = true;
                String errString = "";
                if (bmf.exists())
                {
                    try
                    {
                        bmf.delete();
                    }
                    catch (Exception eF)
                    {
                        wasCleared = false;
                        errString = eF.toString();
                    }
                }

                if (!wasCleared)
                {
                    showMessage("Error Removing Bookmarks", errString);
                }
                else
                {
                    showMessage("Bookmarks Cleared",
                                "When you restart DocSearcher you should only see\nthe bookmarks you add after this point.");
                }
            }
            else if (s.equals("Topics"))
            {
                showMessage("help", "Help topics....");
            }
            else if (s.equals("About DocSearcher"))
            {
                showMessage("About DocSearcher", aboutString);
            }
            else if (s.equals("Search"))
            {
                setStatus("Searching.... Please wait.");
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if (searchField.getSelectedItem() != null)
                {
                    String searchText =
                        searchField.getSelectedItem().toString().trim();
                    if (searchText.length() == 0)
                    {
                        showMessage("Error",
                                    "You must provide either key words or phrases\nin order for a search to be performed.");
                    }
                    else
                    {
                        doSearch(searchText);
                    }
                }
                else
                {
                    showMessage("Error",
                                "You must provide either key words or phrases\nin order for a search to be performed.");
                }

                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                setStatus("Search Complete.");
            }
            else if (s.equals("Refresh"))
            {
                setPage(curPage);
            }
            else if (s.equals("Search Results"))
            {
                editorPane.setContentType("text/html");
                editorPane.setText(lastSearch);
                editorPane.select(0, 0);
                iconButtons[5].setEnabled(false); // forward
                iconButtons[3].setEnabled(false); // back

                backList = new LinkedList();
                forwardList = new LinkedList();
                forwardPos = -1;
                backwardPos = -1;
                curPage = "results";
            }
            else if (s.equals("Home"))
            {
                editorPane.setContentType("text/html");
                if (hasStartPage)
                {
                    try
                    {
                        setPage(fileString + startPageString);
                    }
                    catch (Exception eL)
                    {
                        // error message
                        editorPane.setText(lastSearch);
                    }

                    curPage = "home";
                }
                else
                {
                    editorPane.setText(lastSearch);
                    editorPane.select(0, 0);
                    curPage = "results";
                }

                iconButtons[5].setEnabled(false); // forward
                iconButtons[3].setEnabled(false); // back

                backList = new LinkedList();
                forwardList = new LinkedList();
                forwardPos = -1;
                backwardPos = -1;
            }
            else if (s.equals("Search Tips"))
            {
                showMessage("Search Tips", searchTips);
            }
            else
            {
                if ((s.startsWith("file:/")) || (s.startsWith("http")))
                {
                    setPage(s);
                    forwardPos = -1;
                    backwardPos = -1;
                    backList = new LinkedList();
                    forwardList = new LinkedList();
                    iconButtons[5].setEnabled(false); // forward
                    iconButtons[3].setEnabled(false); // back
                }
            }
        }
        catch (Exception eF)
        {
            System.out.println("Action thread was stopped!\n" + eF.toString());
            eF.printStackTrace();
        }
    }


    public void actionPerformed(ActionEvent actionevent)
    {
        String a = actionevent.getActionCommand();
        //Runner actThread = new Runner(a, this);
        //SwingUtilities.invokeLater(actThread);
	GuiThread g=new GuiThread(this, a);
	g.start();
    }

    public boolean hasIndex()
    {
        boolean returnBool = true;
        File indexFolder = new File(indexDir);
        if (!indexFolder.exists())
        {
            returnBool = false;
        }
        else if (useGui)
        {
            setStatus("Index Folder " + indexDir + " exists...");
        }

        return returnBool;
    }

    public void setStatus(String toSet)
    {
        if ((!isLoading) && (useGui))
        {
            dirLabel.setText(toSet);
            System.out.println("STATUS:" + toSet);
        }
        else
        {
            System.out.println(toSet);
        }
    }

    public void showMessage(String title,
                            String details)
    {
        MessageRunner mesThread = new MessageRunner(title, details, this);
        try
        {
            SwingUtilities.invokeLater(mesThread);
        }
        catch (Exception eM)
        {
            eM.printStackTrace();
        }
    }

    public void showMessageDialog(String title,
                                  String body)
    {
        if ((!isLoading) && (useGui))
        {
            int messageType = JOptionPane.INFORMATION_MESSAGE;
            if (title.toLowerCase().indexOf("error") != -1)
            {
                messageType = JOptionPane.ERROR_MESSAGE;
            }

            pane = new JOptionPane((Object)body, messageType);
            JDialog dialog = pane.createDialog(this, title);
            dialog.setVisible(true);
        }
        else
        {
            System.out.println(" * * * " + title + " * * *\n\t" + body);
        }
    }

    public void saveFile(String fileName,
                         StringBuffer content)
    {
        String saveStr = "";
        boolean error = false;
        try
        {
            File saveFile;
            saveFile = new File(fileName);
            FileWriter writer = new FileWriter(saveFile);
            saveStr = content.toString();
            int saveStrLen = saveStr.length();
            for (int i = 0; i < saveStrLen; i++)
            {
                writer.write((int)saveStr.charAt(i));
            }

            writer.close();
        }
        catch (Exception eF)
        {
            setStatus("File Save Error - Error Reported was:\n" + eF.toString()
                      + "\n\nFor file " + fileName);
            error = true;
        }

        if (!error)
        {
            if (useGui)
            {
                setStatus("Save Successful -File : " + fileName
                          + "\n- was saved .");
            }
        }
    }

    public void saveFile(String fileName,
                         String savePath,
                         StringBuffer content)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String saveStr = "";
        boolean error = false;
        try
        {
            File saveFile;
            if (!savePath.equals(""))
            {
                saveFile = new File(savePath, fileName);
            }
            else
            {
                saveFile = new File(fileName);
            }

            FileWriter writer = new FileWriter(saveFile);
            saveStr = content.toString();
            int saveStrLen = saveStr.length();
            for (int i = 0; i < saveStrLen; i++)
            {
                writer.write((int)saveStr.charAt(i));
            }

            writer.close();
        }
        catch (Exception eF)
        {
            setStatus("File Save Error - Error Reported was:\n" + eF.toString()
                      + "\n\nFor file " + fileName + "\n- in directory "
                      + savePath + ".\n\nContent of file :\n\n" + saveStr);
            error = true;
        }

        if (!error)
        {
            setStatus("Save Successful -File : " + fileName
                      + "\n- was saved in directory:\n " + savePath + ".");
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /********************************************************
     *  PRINTER CLASSES
     ********************************************************/
    public String getChangeFromFloat(double aDouble)
    {
        String returnString = "";
        String tempString = "" + aDouble;
        int tempLen = tempString.length();
        StringBuffer newSb = new StringBuffer();
        char curChar = ' ';
        int pastDot = 0;
        boolean foundDot = false;
        for (int i = 0; i < tempLen; i++)
        {
            curChar = tempString.charAt(i);
            if (curChar == '.')
            {
                foundDot = true;
            }
            else if (foundDot)
            {
                pastDot++;
            }

            newSb.append(curChar);
            if (pastDot >= 1)
            {
                break;
            }
        }

        returnString = newSb.toString();

        return returnString;
    }

    public String getPercentFromFloat(double aDouble)
    {
        String returnString = "";
        double newDouble = 100.0 * aDouble;
        String tempString = "" + newDouble;
        int tempLen = tempString.length();
        StringBuffer newSb = new StringBuffer();
        char curChar = ' ';
        int pastDot = 0;
        boolean foundDot = false;
        for (int i = 0; i < tempLen; i++)
        {
            curChar = tempString.charAt(i);
            if (curChar == '.')
            {
                foundDot = true;
            }
            else if (foundDot)
            {
                pastDot++;
            }

            newSb.append(curChar);
            if (pastDot >= 1)
            {
                newSb.append(" %");

                break;
            }
        }

        returnString = newSb.toString();

        return returnString;
    }

    public String getKStyle(String toGet)
    {
        String returnString = "";
        try
        {
            double l = Double.parseDouble(toGet);
            double newD = l / 1024.0;
            returnString = getChangeFromFloat(newD) + " k";
        }
        catch (Exception eN)
        {
            returnString = toGet;
        }

        return returnString;
    }

    public String getSearchedIndexes()
    {
        StringBuffer rb = new StringBuffer();

        // iterate over the di s
        int numIndexes = 0;
        if (!indexes.isEmpty())
        {
            numIndexes = indexes.size();

            // add the items
            Iterator iterator = indexes.iterator();
            DocSearcherIndex curI;
            if (numIndexes > 0)
            {
                rb.append("<ul>");
            }
            while (iterator.hasNext())
            {
                curI = ((DocSearcherIndex)iterator.next());
                if (curI.shouldBeSearched)
                {
                    rb.append("<li><font color = blue>");
                    rb.append(curI.desc);
                    rb.append("</font></li>");
                }
            }

            if (numIndexes > 0)
            {
                rb.append("</ul>");
            }
        }

        if (numIndexes == 0)
        {
            return "<p align = left><b>none</b></p>";
        }
        else
        {
            return rb.toString();
        }
    }

    public ArrayList[] screenForSize(Hits hits,
                                     int lowerSize,
                                     int upperSize)
    {
        ArrayList[] returnList = new ArrayList[2];
        returnList[0] = new ArrayList();
        returnList[1] = new ArrayList();
        int tempSize = 0;
        for (int i = 0; i < hits.length(); i++)
        {
            try
            {
                tempSize = Integer.parseInt(hits.doc(i).get("size"));
                if ((tempSize >= lowerSize) && (tempSize <= upperSize))
                {
                    returnList[0].add(hits.doc(i));
                    returnList[1].add(("" + hits.score(i)));
                }
            }
            catch (Exception eR)
            {
                setStatus("Error obtaining document size.." + eR.toString());
            }
        }

        return returnList;
    }

    public class printMIListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setJobName("docSearcher page");
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
                showMessage("Error Printing", e.toString());
                e.printStackTrace();
            }
        }
    }

    public class scale2Listener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.setScale(2.0, 2.0);
        }
    }

    public class scaleFitListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.scaleToFit(false);
        }
    }

    public class scaleHalfListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.setScale(0.5, 0.5);
        }
    }

    public class scaleOffListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(editorPane, new PageFormat());
        }
    }

    public class scaleXListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.scaleToFitX();
        }
    }

    public class scaleYListener
        implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.scaleToFitY();
        }
    }

    public void doSearch(String searchText)
    {
        ArrayList[] sizeList = new ArrayList[2];
        sizeList[0] = new ArrayList();
        sizeList[1] = new ArrayList();
        String findText = "";
        if (phrase.isSelected()) {
            if (searchText.indexOf("\"") == -1) searchText = "\""+searchText+"\"";
        }
        // for each di - search and add the results
        int grandTotalHits = 0;
        int selectedFields = searchIn.getSelectedIndex();
        String sField = searchOpts[selectedFields];
        String tempSearchText = searchText;
        StringBuffer searchedIndexes = new StringBuffer();
        searchedIndexes.append("<ul>");
        String htmlTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "htm.gif")+"\" border = \"0\" alt = \"Web Page Document\">";
        String wordTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "doc.gif")+"\" border = \"0\" alt = \"MS Word Document\">";
        String excelTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "xls.gif")+"\" border = \"0\" alt = \"MS Excel Document\">";
        String pdfTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "pdf.gif")+"\" border = \"0\" alt = \"PDF Document\">";
        String textTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "txt.gif")+"\" border = \"0\" alt = \"Text Document\">";
        String rtfTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "rtf.gif")+"\" border = \"0\" alt = \"RTF Document\">";
        String ooImpressTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "ooimpress-32x32.png")+"\" border = \"0\" alt = \"Open Office Impress Document\">";
        String ooWriterTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "oowriter-32x32.png")+"\" border = \"0\" alt = \"Open Office Writer Document\">";
        String ooCalcTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "oocalc-32x32.png")+"\" border = \"0\" alt = \"Open Office Calc Document\">";
        String ooDrawTag = "<img src = \""+fileString+Utils.addFolder(iconDir, "oo.gif")+"\" border = \"0\" alt = \"Open Office Draw Document\">";
        StringBuffer hitBuf;
        StringBuffer tempBuf = new StringBuffer();
        StringBuffer bodyBuf = new StringBuffer();
        String curTitle = "";
        String curSize = "";
        String curAuthor = "";
        String curDate = "";
        String curFile = "";
        String curType = "";
        String curSummary = "";
        int numHits = 0;
        hitBuf = new StringBuffer();
        hitBuf.append("<html><head><title>Results of search for ");
        hitBuf.append(searchText);
        hitBuf.append("</title><body><h1 align = center>Search Results for <strong><font color = blue>");
        hitBuf.append(searchText);
        hitBuf.append("</font></strong></h1>");
        if (!useGui) System.out.println("Results of search for "+searchText);
        int curColor = 0;
        int curTypeInt = 0;
        int numIndexes = 0;
        if (!indexes.isEmpty()) try {
            numIndexes = indexes.size();
            // add the items
            Iterator iterator  =  indexes.iterator();
            DocSearcherIndex curI;
            while (iterator.hasNext()) {
                curI = ((DocSearcherIndex)iterator.next());
                tempBuf = new StringBuffer();
                if (curI.shouldBeSearched) {
                    searcher = new IndexSearcher(curI.indexerPath);
                    if ((searchText.indexOf("(")!= -1) ||
                        (searchText.indexOf("[")!= -1)) findText = searchText;
                    else {
                        if (selectedFields == 0) {
                            findText = "+(+body:("+tempSearchText+") OR +title:("+tempSearchText+"))";
                        } // body and title
                        else findText = "+"+sField+":("+searchText+")";
                        if (useAuthor.isSelected()) {
                            findText += " AND +author:("+authorField.getText()+")";
                        }
                        if (useType.isSelected()) {
                            findText += " AND +type:("+fileTypesToGet[fileType.getSelectedIndex()]+")";
                        }
                    } // end for a regular search
                    setStatus("SEARCH STRING:"+findText+"...");
                    query = QueryParser.parse(findText, sField, new StandardAnalyzer());
                    if (useDate.isSelected()) {
                        DateFilter df = new DateFilter("mod_date",
                                                       DateTimeUtils.getJDateFromString(fromField.getText()),
                                                       DateTimeUtils.getJDateFromString(toField.getText()));
                        hits = searcher.search(query, df);
                    }
                    else hits = searcher.search(query);
                    if (useSize.isSelected()) {
                        int fromInt = 1;
                        int toInt = 2;
                        try {
                            fromInt = Integer.parseInt(sizeFromField.getText())*1024;
                            toInt = Integer.parseInt(sizeToField.getText())*1024;
                        }
                        catch (Exception eN) {
                            setStatus("Error parsing numbers for files size:"+eN.toString());
                        }
                        sizeList = screenForSize(hits, fromInt, toInt);
                        numHits = sizeList[0].size();
                    }
                    else numHits = hits.length();     // NOT A SIZE QUERY
                    searchedIndexes.append("<li> <font color = blue>");
                    searchedIndexes.append(curI.desc);
                    searchedIndexes.append("</font> (<b>");
                    searchedIndexes.append(numHits);
                    searchedIndexes.append("</b> documents)</li>");
                    if (!useGui) System.out.println("Index: "+curI.desc);
                    float curScore;
                    grandTotalHits += numHits;
                    tempBuf.append("<p align = center><b>");
                    tempBuf.append(numHits);
                    tempBuf.append("</b> Document(s) Found in index <b>");
                    tempBuf.append(curI.desc);
                    tempBuf.append("</b></p>");
                    curColor = 0;
                    curTypeInt = 0;
                    for (int i = 0;i<numHits;i++) {
                        if (i>maxNumHitsShown) {
                            setStatus("Maximum Number of hits ("+maxNumHitsShown+") exceeded ("+numHits+").");
                            break;
                        }
                        //
                        if (!useSize.isSelected()) {
                            curTitle = convertTextToHTML(hits.doc(i).get("title"));
                            curSize = hits.doc(i).get("size");
                            if (!curI.isWeb) curFile = hits.doc(i).get("path");
                            else curFile = hits.doc(i).get("URL");
                            curType = hits.doc(i).get("type");
                            curAuthor = hits.doc(i).get("author");
                            if (curAuthor.equals("")) curAuthor = "Unknown";
                            curDate = hits.doc(i).get("mod_date");
                            if (curDate.equals("")) curDate = "Unknown";
                            else curDate = DateTimeUtils.getDateFormatNormal(curDate);
                            curTypeInt = Utils.getTypeInt(curType);
                            curScore = hits.score(i);
                            curSummary = convertTextToHTML(hits.doc(i).get("summary"));
                        }
                        else {
                            curTitle = convertTextToHTML(((Document)sizeList[0].get(i)).get("title"));
                            curSize = ((Document)sizeList[0].get(i)).get("size");
                            if (!curI.isWeb) curFile = ((Document)sizeList[0].get(i)).get("path");
                            else curFile = ((Document)sizeList[0].get(i)).get("URL");
                            curType = ((Document)sizeList[0].get(i)).get("type");
                            curAuthor = ((Document)sizeList[0].get(i)).get("author");
                            if (curAuthor.equals("")) curAuthor = "Unknown";
                            curDate = ((Document)sizeList[0].get(i)).get("mod_date");
                            if (curDate.equals("")) curDate = "Unknown";
                            else curDate = DateTimeUtils.getDateFormatNormal(curDate);
                            curTypeInt = Utils.getTypeInt(curType);
                            curScore = 1;
                            try {
                                curScore = Float.parseFloat(((String)sizeList[1].get(i)));
                            }
                            catch (Exception eF) {
                                setStatus("Error parsing score:"+eF.toString());
                            }
                            curSummary = ((Document)sizeList[0].get(i)).get("summary");
                        }
                        //
                        // add it to our page - doc size title score
                        tempBuf.append("<p align = left>");
                        if (!curI.isWeb) {
                            tempBuf.append("<a href = \"file:///");
                            tempBuf.append(curFile);
                            tempBuf.append("\">");
                        }
                        else {
                            tempBuf.append("<a href = \"");
                            tempBuf.append(curFile);
                            tempBuf.append("\">");
                        }
                        if (hasIcons) {
                            switch (curTypeInt) {
                            case 0: // html
                                tempBuf.append(htmlTag);
                                break;
                            case 2: // ms word
                                tempBuf.append(wordTag);
                                break;
                            case 3: // ms word
                                tempBuf.append(excelTag);
                                break;
                            case 4: // pdf
                                tempBuf.append(pdfTag);
                                break;
                            case 5: // rtf
                                tempBuf.append(rtfTag);
                                break;
                            case 6: // open office writer
                                tempBuf.append(ooWriterTag);
                                break;
                            case 7: // open office impress
                                tempBuf.append(ooImpressTag);
                                break;
                            case 8: // open office calc
                                tempBuf.append(ooCalcTag);
                                break;
                            case 9: // open office draw
                                tempBuf.append(ooDrawTag);
                                break;
                            default: // text
                                tempBuf.append(textTag);
                                break;
                            }
                        }
                        else tempBuf.append(curType);
                        tempBuf.append(" ");
                        tempBuf.append(curTitle);
                        tempBuf.append("</a><br>");
                        tempBuf.append(curSummary);
                        tempBuf.append("<font color = green><br><em>");
                        tempBuf.append(curDate);
                        tempBuf.append(", ");
                        tempBuf.append(getKStyle(curSize));
                        tempBuf.append(" K bytes, ");
                        tempBuf.append(curAuthor);
                        tempBuf.append(", <b>");
                        tempBuf.append(getPercentFromFloat(curScore));
                        tempBuf.append("</b></em></font><br><font color = gray>");
                        tempBuf.append(curFile);
                        tempBuf.append("</font>");
                        tempBuf.append("</p>");
                        if (!useGui) {
                            System.out.println("\n\n* "+curTitle+"\n"+
                                               curSummary+"\n"+curDate+", "+getKStyle(curSize)+
                                               " K bytes, "+curAuthor+", "+getPercentFromFloat(curScore)+
                                               "\n"+curFile);
                        }
                    } // end for hits
                    // now add our results
                    // add the footer
                    bodyBuf.append(tempBuf.toString());
                } // end if shouldbesearched
                else {
                    tempBuf.append("<p align = left>Index <b>");
                    tempBuf.append(curI.desc);
                    tempBuf.append("</b> was not searched.</p>");
                    bodyBuf.append(tempBuf.toString());
                }
            } // end while hasmore indexes
            // finish up the page
            searchedIndexes.append("</ul>");
            hitBuf.append("<p align = left><strong>");
            hitBuf.append(grandTotalHits);
            hitBuf.append("</strong> total documents found in Index(es):</p>");
            hitBuf.append(searchedIndexes.toString());
            hitBuf.append(bodyBuf);
            hitBuf.append("</body></html>");
            if (!useGui) System.out.println("\nTotal hits: "+grandTotalHits);
            lastSearch = hitBuf.toString();
            if (useGui) {
                editorPane.setText(lastSearch);
                editorPane.select(0,0);
                int numS = searchField.getItemCount();
                boolean inThere = false;
                for (int i = 1;i<numS;i++) if (((String)searchField.getItemAt(i)).equals(searchText)) inThere = true;
                if (!inThere) searchField.addItem(searchText);
                searchField.setSelectedIndex(searchField.getItemCount()-1);
                vista  =  new JComponentVista(editorPane, new PageFormat());
                forwardPos = -1;
                backwardPos = -1;
                backList = new LinkedList();
                addToBackList(curPage);
                curPage = "results";
                forwardList = new LinkedList();
            }
        } // end for try
        catch (IOException ie) {
            showMessage("Error Performing Search", "It appears you may have a corrupted index:\n"+ie.toString()+"\n\nYou may need to delete some indexes, and re-create them.\n\nIndexes are stored in your :\n\t"+indexDir+"  directory.\n\nThe easiest way to remove a corrupt index is to\nclick \"Indexes\", then \"Manage Indexes\",\nthen check the remove box as needed.\n\n If the problem persists - you may want to delete your:\n\t "+workingDir+" directory.\n\nCurrupt indexes are typically caused by a lack of user \npermissions to index the files in certain directories \n- or problems with hidden directories.");
            ie.printStackTrace();
        }
        catch (NullPointerException ne) {
            showMessage("Error Performing Search", "It appears you may have a corrupted index.\n\nYou may need to delete some indexes, and re-create them.\n\nIndexes are stored in your :\n\t"+indexDir+"  directory.\n\nThe easiest way to remove a corrupt index is to\nclick \"Indexes\", then \"Manage Indexes\",\nthen check the remove box as needed.\n\n If the problem persists - you may want to delete your : \n\t"+workingDir+" directory.\n\nCurrupt indexes are typically caused by a lack of user \npermissions to index the files in certain directories \n- or problems with hidden directories.");
            ne.printStackTrace();
        }
        catch (Exception eS) {
            showMessage("Search Error", eS.toString());
            eS.printStackTrace();
        }
        else showMessage("No indexes", "Currently there are no indexes to search.\n\nPlease create a new index.");
    } // end for doSearch

    public String convertTextToHTML(String conv)
    {
        String returnString = conv;

        // lowerCase
        returnString = Utils.replaceAll("& ", returnString, "&amp;"); // and symbol
        returnString = Utils.replaceAll("\n", returnString, "&nbsp;"); // spacer
        returnString = Utils.replaceAll("<", returnString, "&lt;"); // less than
        returnString = Utils.replaceAll(">", returnString, "&gt;"); // greater than
        returnString = Utils.replaceAll("\"", returnString, "&quot;"); // quot

        return returnString;
    }

    public StringBuffer loadFile(String fileToLoad)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        StringBuffer returnBuffer = new StringBuffer();
        try
        {
            //System.out.println("Loading text from file"+fileToLoad);
            File readFile = new File(fileToLoad);
            if ((readFile.exists()) && (!readFile.isDirectory()))
            {
                FileReader filereader = new FileReader(fileToLoad);
                int i;
                while ((i = filereader.read()) != -1)
                {
                    if (i == -1)
                    {
                        break;
                    }
                    else
                    {
                        returnBuffer.append((char)i);
                    }
                }

                filereader.close();
            }
            else
            {
                returnBuffer.append(" ** FILE NOT FOUND OR FILE IS A DIRECTORY ["
                                    + fileToLoad + "] **\n");
            }
        }
        catch (Exception eF)
        {
            showMessage("Error Loading File",
                        "Unable to load file:" + fileToLoad
                        + "\n\nError Reported:" + eF.toString());
            eF.printStackTrace();
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        return returnBuffer;
    }

    public boolean setPage(String pageToSet)
    {
        try
        {
            editorPane.setPage(fileString + blankFile);
        }
        catch (Exception eR)
        {
            setStatus("Error setting up refresh page: " + eR.toString());
        }

        boolean returnBool = true;
        String lowerLink = pageToSet.toLowerCase();
        String loadString = pageToSet;
        if (pageToSet.equals("home"))
        {
            setStatus("Loading home:*" + pageToSet + "*");
            editorPane.setContentType("text/html");
            if (hasStartPage)
            {
                try
                {
                    editorPane.setPage(fileString + startPageString);
                }
                catch (Exception eL)
                {
                    // error message
                    editorPane.setText(lastSearch);
                }
            }
            else
            {
                editorPane.setText(lastSearch);
            }

            editorPane.select(0, 0);
            curPage = "home";
        }
        else if (pageToSet.equals("results"))
        {
            System.out.println("Loading Results:*" + pageToSet + "*");
            editorPane.setContentType("text/html");
            editorPane.setText(lastSearch);
            editorPane.select(0, 0);
            curPage = "results";
        }
        else
        {
            if (Utils.isHtml(lowerLink))
            {
                editorPane.setContentType("text/html");
            }
            else if (Utils.isText(lowerLink))
            {
                editorPane.setContentType("text/plain");
            }
            else if (Utils.isRtf(lowerLink))
            {
                editorPane.setContentType("text/rtf");
            }

            try
            {
                // set the page
                editorPane.setPage(pageToSet);
                setStatus("Loaded " + pageToSet);
                curPage = pageToSet;
            }
             // end for try
            catch (Exception eP)
            {
                returnBool = false;
                showMessage("Error Loading Page",
                            "Location :\n\n" + pageToSet
                            + "\n-didn't load. \n\nThe following error was reported:\n\n"
                            + eP.toString());
                eP.printStackTrace();
            }
        }

        if (returnBool)
        {
            curPage = pageToSet;
        }

        return returnBool;
    }

    public void doBack()
    {
        int backSize = backList.size();
        String oldPage = curPage;
        String reopenPage = "";
        if (backwardPos < (backSize - 1))
        {
            // go back
            backwardPos++;
            reopenPage = (String)backList.get(backwardPos);
            System.out.println("Trying to go BACK to " + reopenPage + " pos "
                               + backwardPos + " of " + backSize);
            if (setPage(reopenPage))
            {
                addToForwardList(oldPage);
                if (forwardPos > -1)
                {
                    forwardPos--;
                }
            }
            else
            {
                backwardPos--;
            }

            if (backwardPos == (backSize - 1))
            {
                iconButtons[3].setEnabled(false);
            }
        }
        else
        {
            iconButtons[3].setEnabled(false); // back button
        }
    }

    public void doForward()
    {
        int fwdSize = forwardList.size();
        String oldPage = curPage;
        String reopenPage = "";
        if (forwardPos < (fwdSize - 1))
        {
            // go back
            forwardPos++;
            reopenPage = (String)forwardList.get(forwardPos);
            System.out.println("Trying to go FWD to " + reopenPage
                               + " position " + forwardPos + " of " + fwdSize);
            if (setPage(reopenPage))
            {
                //addToBackList(oldPage);
                if (backwardPos > -1)
                {
                    backwardPos--;
                }

                //addToBackList(oldPage);
            }
            else
            {
                forwardPos--;
            }

            if ((forwardPos == (fwdSize - 1)))
            {
                iconButtons[5].setEnabled(false);
            }
        }
        else
        {
            iconButtons[5].setEnabled(false); // forward button
        }
    }

    public void addToForwardList(String toAdd)
    {
        System.out.println("Adding " + toAdd + " to fwdlist");
        forwardList.add(0, toAdd);
        iconButtons[5].setEnabled(true); // forward button
    }

    public void addToBackList(String toAdd)
    {
        if ((toAdd.startsWith("file://")) && (!toAdd.startsWith("file:///")))
        {
            toAdd = "file:///" + toAdd.substring(6, toAdd.length());
            System.out.println("New URL is:" + toAdd);
        }

        System.out.println("Adding " + toAdd + " to backlist");
        backList.add(0, toAdd);
	if (hasIcons) {
        	iconButtons[3].setEnabled(true); // backward button
        	iconButtons[5].setEnabled(false); // forward button
		}
    }

    public void doOpen()
    {
        setStatus("Doing open action.");
        JFileChooser fdo = new JFileChooser();
        fdo.setCurrentDirectory(new File(defaultSaveFolder));
        int fileGotten = fdo.showDialog(this, "Open");
        if (fileGotten == JFileChooser.APPROVE_OPTION)
        {
            File file = fdo.getSelectedFile();
            String fileName = file.toString();
            System.out.println("Saving document " + fileName);

            // get document stream and save it
            if (!fileName.startsWith("http"))
            {
                setPage(fileString + fileName);
            }
            else
            {
                setPage(fileName);
            }
        }
         // end if approved
    }

    public void doSave()
    {
        setStatus("Doing save action.");

        //defaultSaveFolder
        JFileChooser fds = new JFileChooser();
        fds.setDialogTitle("Save");
        boolean aWebPage = false;
        String saveName = "";
        if (curPage.equals("results"))
        {
            saveName = "results.htm";
        }
        else if (curPage.equals("home"))
        {
            saveName = "home.htm";
        }
        else
        {
            if (Utils.isHtml(curPage))
            {
                aWebPage = true;
            }

            saveName = Utils.getNameOnly(curPage);
        }

        saveName = Utils.addFolder(defaultSaveFolder, saveName);
        fds.setCurrentDirectory(new File(defaultSaveFolder));
        fds.setSelectedFile(new File(saveName));
        int fileGotten = fds.showDialog(this, "Save");
        if (fileGotten == JFileChooser.APPROVE_OPTION)
        {
            File file = fds.getSelectedFile();
            String fileName = file.toString();
            setStatus("Saving document " + fileName);

            // get document stream and save it
            String saveText = editorPane.getText();
            int textLen = saveText.length();
            char curChar = ' ';
            try
            {
                File saveFile = new File(fileName);
                FileWriter filewriter = new FileWriter(saveFile);
                PrintWriter pw = new PrintWriter(filewriter);
                for (int i = 0; i < textLen; i++)
                {
                    curChar = saveText.charAt(i);
                    pw.print(curChar);
                }
                 // end for writing text

                filewriter.close();
                pw.close();
            }
            catch (Exception eR)
            {
                showMessage("Error Saving File",
                            "The following error was \nencountered while attempting to \nsave file: \n\t"
                            + fileName);
            }
        }
         // end if approved
    }

    public void doExit()
    {
        try
        {
            saveIndexes();
        }
        catch (Exception eR)
        {
            showMessage("Error while attempting to save index information",
                        eR.toString());
        }
        finally
        {
            System.exit(0);
        }
    }

    public void saveIndexes()
    {
        if (!indexes.isEmpty())
        {
            int numIndexes = indexes.size();
            StringBuffer sB = new StringBuffer();
            sB.append("<html><head><title>DocSearcher Index Listing</title>");
            sB.append("<body><h1>DocSearcher Index Listing</h1><p align = left>");
            sB.append("Listed below are the paths and whether they are to be searched by default.</p>");
            sB.append("<table border = 1>");

            // add the items
            Iterator iterator = indexes.iterator();
            DocSearcherIndex curI;
            while (iterator.hasNext())
            {
                curI = ((DocSearcherIndex)iterator.next());
                sB.append("<tr>");
                sB.append("<td>");
                sB.append(curI.desc);
                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.path);
                sB.append("</td>");
                sB.append("<td>");
                if (curI.shouldBeSearched)
                {
                    sB.append("0");
                }
                else
                {
                    sB.append("1");
                }

                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.depth + "");
                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.indexerPath + "");
                sB.append("</td>");

                // now the isWeb stuff and date
                sB.append("<td>");
                if (curI.isWeb)
                {
                    sB.append("true");
                }
                else
                {
                    sB.append("false");
                }

                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.match);
                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.replace);
                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.lastIndexed);
                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.indexPolicy + "");
                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.archiveDir);
                sB.append("</td>");
                sB.append("</tr>");
            }
             // end of iteration

            // close up the html
            sB.append("</table></body></html>");
            saveFile("index_list.htm", workingDir, sB);

            // save the file
        }
         // end for not empty

        if (!bookmarksList.isEmpty())
        {
            int numIndexes = bookmarksList.size();
            StringBuffer sB = new StringBuffer();
            sB.append("<html><head><title>DocSearcher Bookmark Listing</title>");
            sB.append("<body><h1>DocSearcher Bookmark Listing</h1><p align = left>");
            sB.append("Listed below are the bookmarks for DocSearcher.</p>");
            sB.append("<table border = 1>");

            // add the items
            Iterator iterator = bookmarksList.iterator();
            SimpleBookmark curI;
            while (iterator.hasNext())
            {
                curI = ((SimpleBookmark)iterator.next());
                sB.append("<tr>");
                sB.append("<td>");
                sB.append(curI.desc);
                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.urlString);
                sB.append("</td>");
                sB.append("</tr>");
            }

            sB.append("</table></body></html>");
            saveFile("bookmarks.htm", workingDir, sB);
        }
         // end for bookmarks not empty
    }

    public void loadIndexes()
    {
        // check for indexFile
        int numIndexes = 0;
        File testIndex = new File(indexFile);
        StringBuffer errS = new StringBuffer();
        boolean loadErr = false;
        DocSearcherIndex curI;
        File tempFile;
        String tempFileString = "";
        String tempIndexerPath = "";
        String tempDesc = "";
        int tempDepth = 0;
        int updatePolicy = 0;
        String updateStr = "";
        int tempSbd = 0;
        boolean tempBool = false;
        boolean tempWebBool = false;
        String tempIsWeb = "";
        String tempReplace = "";
        String tempMatch = "";
        String tempDateStr = "";
        String tempArch = "";
        if (testIndex.exists())
        {
            Table tempTable = new Table(11, 100);
            tempTable.htmlLoad(indexFile, "");
            int numI = tempTable.colSize();

            // parse it
            for (int i = 0; i < numI; i++)
            {
                //
                try
                {
                    tempDesc = tempTable.inItem(0, i);
                    tempFileString = tempTable.inItem(1, i);
                    tempSbd = Integer.parseInt(tempTable.inItem(2, i));
                    tempIndexerPath = tempTable.inItem(4, i);

                    // isWeb content
                    tempIsWeb = tempTable.inItem(5, i);
                    tempWebBool = false;
                    tempReplace = "";
                    tempMatch = "";
                    tempDateStr = "";
                    if (tempIsWeb != null)
                    {
                        if (tempIsWeb.equals("true"))
                        {
                            tempWebBool = true;
                            tempMatch = tempTable.inItem(6, i);
                            tempReplace = tempTable.inItem(7, i);
                        }
                    }

                    tempDateStr = tempTable.inItem(8, i);
                    updateStr = tempTable.inItem(9, i);
                    if (updateStr == null)
                    {
                        updatePolicy = 0;
                    }
                    else
                    {
                        updatePolicy = Integer.parseInt(updateStr);
                    }

                    if (tempDateStr == null)
                    {
                        tempDateStr = DateTimeUtils.getToday();
                    }

                    if (tempSbd == 1)
                    {
                        tempBool = false;
                    }
                    else
                    {
                        tempBool = true;
                    }

                    tempArch = tempTable.inItem(10, i);
                    if (tempArch == null)
                    {
                        tempArch = getArchiveDir();
                    }

                    tempDepth = Integer.parseInt(tempTable.inItem(3, i));
                    tempFile = new File(tempFileString);
                    if (tempFileString.toLowerCase().endsWith(".zip"))
                    {
                        curI =
                            new DocSearcherIndex(tempFileString, tempDesc,
                                                 tempBool, tempDepth,
                                                 tempIndexerPath, tempWebBool,
                                                 tempMatch, tempReplace,
                                                 tempDateStr, updatePolicy,
                                                 tempArch);
                        indexes.add(curI);
                        if (useGui)
                        {
                            setStatus("Index " + curI.desc + " is "
                                      + DateTimeUtils.getDaysOld(curI)
                                      + " days old (" + curI.lastIndexed + ")");
                        }

                        numIndexes++;
                    }
                    else if (tempFile.exists())
                    {
                        curI =
                            new DocSearcherIndex(tempFileString, tempDesc,
                                                 tempBool, tempDepth,
                                                 tempIndexerPath, tempWebBool,
                                                 tempMatch, tempReplace,
                                                 tempDateStr, updatePolicy,
                                                 tempArch);
                        indexes.add(curI);
                        setStatus("Index " + curI.desc + " is "
                                  + DateTimeUtils.getDaysOld(curI)
                                  + " days old (" + curI.lastIndexed + ")");
                        numIndexes++;
                    }
                    else
                    {
                        loadErr = true;
                        errS.append("Folder : " + tempFileString
                                    + "\n\tNo longer exists.\n\n");
                    }
                     // end for file doesn't exist
                }
                catch (Exception eN)
                {
                    loadErr = true;
                    errS.append(eN.toString() + "\n\n");
                }
            }
        }

        if (numIndexes == 0)
        {
            loadErr = true;
            errS.append("No indexes have been created yet.");
        }

        // now load the bookmarks
        // from the bookmarksFile
        File testBMF = new File(bookmarksFile);
        if (testBMF.exists())
        {
            Table tempTable = new Table(2, 200);
            tempTable.htmlLoad(bookmarksFile, "");
            int numI = tempTable.colSize();

            // parse it
            for (int i = 0; i < numI; i++)
            {
                addNewBookmark(new SimpleBookmark(tempTable.inItem(1, i),
                                                  tempTable.inItem(0, i)));
            }
        }

        if (loadErr)
        {
            showMessage("Error loading indexes", errS.toString());
        }
        else
        {
            setStatus("Found " + numIndexes + " total indexes.");
        }
    }

    public void checkDefaults()
    {
        File workingDirFile = new File(workingDir);
        if (!workingDirFile.exists())
        {
            workingDirFile.mkdir();
        }
         // end for creating the working directory

        File defaultSaveFolderFile = new File(defaultSaveFolder);
        if (!defaultSaveFolderFile.exists())
        {
            defaultSaveFolderFile.mkdir();
        }
         // end for creating the working directory

        blankFile = Utils.addFolder(workingDir, "blank_page.htm");
        File blankPageFile = new File(blankFile);
        if (!blankPageFile.exists())
        {
            StringBuffer bp = new StringBuffer();
            bp.append("<html><head><title>Loading</title></head><body><h1>Loading....</h1></body></html>");
            saveFile("blank_page.htm", workingDir, bp);
        }

        File indexFolder = new File(indexDir);
        if (!indexFolder.exists())
        {
            setStatus("Index folder doesn't exist : " + indexDir);
            setStatus("Creating index...");
            indexFolder.mkdir();
        }
    }

    public void createNewIndex(DocSearcherIndex di)
        throws IOException
    {
        setStatus("Indexing (" + di.indexerPath + ") Please wait.");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        checkDefaults();
        StringBuffer failedBuf = new StringBuffer();
        failedBuf.append("\nThe Following files failed to index properly:");
        String indexDirNew = di.indexerPath;
        File indexFolder = new File(indexDirNew);
        int addedSuccessfully = 0;
        StringBuffer noRobotsBuf = new StringBuffer();
        noRobotsBuf.append("\nFiles that weren't indexed due to Robots, NOINDEX meta tag content:");
        int numNoIndex = 0;
        boolean newIndex = false;
        if (!indexFolder.exists())
        {
            setStatus("Index folder doesn't exist : " + indexDirNew);
            setStatus("Creating index...");
            indexFolder.mkdir();
            newIndex = true;
        }

        // BUILD THE INDEX
        File contentFolder = new File(di.path);
        int totalFolders = 1;
        int totalFiles = 0;
        int numErrors = 0;
        String urlStr = "";
        String dateStr = "";
        File tempDFile;
        int curSize = 1;
        if (contentFolder.exists())
        {
            ArrayList folderList = new ArrayList();
            folderList.add(di.path); // add in our contentDir
            String curFolderString = di.path;
            String[] filesString;
            String[] foldersString;
            File curFolderFile;
            String curFold = "";
            String curFi = "";
            int curItemNo = 0;
            int lastItemNo = 0;
            int numFiles = 0;
            int numFolders = 0;
            int curSubNum = 0;
            int startSubNum = Utils.countSLash(di.path);
            int maxSubNum = startSubNum + di.depth;

            // creating the index
            IndexWriter writer =
                new IndexWriter(indexDirNew, new StandardAnalyzer(), newIndex);
            do
            {
                // create our folder file
                curFolderString = (String)folderList.get(curItemNo);
                curFolderFile = new File(curFolderString);
                curSubNum = Utils.countSLash(curFolderString);
                try
                {
                    // handle any subfolders --> add them to our folderlist
                    foldersString = curFolderFile.list(ff);
                    numFolders = foldersString.length;
                    for (int i = 0; i < numFolders; i++)
                    {
                        // add them to our folderlist
                        curFold =
                            curFolderString + pathSep + foldersString[i]
                            + pathSep;
                        curFold =
                            Utils.replaceAll(pathSep + pathSep, curFold, pathSep);
                        folderList.add(curFold);
                        lastItemNo++;
                        totalFolders++;

                        // debug output
                        setStatus("Found folder " + curFold);
                    }
                     // end for having more than 0 folder
                }
                catch (Exception eF)
                {
                    setStatus("Error accessing folder information: "
                              + curFolderString + " : " + eF.toString());
                    eF.printStackTrace();
                }

                // add our files
                try
                {
                    filesString = curFolderFile.list(wf);
                    numFiles = filesString.length;
                    for (int i = 0; i < numFiles; i++)
                    {
                        // add them to our folderlist
                        curFi = curFolderString + pathSep + filesString[i];
                        curFi =
                            Utils.replaceAll(pathSep + pathSep, curFi, pathSep);
                        setStatus("Please wait: Indexing (" + curFi
                                  + ") - file # " + curSize);
                        curSize++;
                        addedSuccessfully =
                            idx.addDocToIndex(curFi, writer, di);
                        switch (addedSuccessfully)
                        {
                        case 1: // error
                            numErrors++;
                            if (numErrors < 8)
                            {
                                failedBuf.append("\n");
                                failedBuf.append(curFi);
                            }

                            break;

                        case 2: // meta robots = noindex
                            numNoIndex++;
                            if (numNoIndex < 8)
                            {
                                noRobotsBuf.append("\n");
                                noRobotsBuf.append(curFi);
                            }

                            break;

                        default: // OK
                            totalFiles++;

                            break;
                        } // end of switch
                    }
                     // end for files
                }
                 // end of trying to get files
                catch (Exception eI)
                {
                    setStatus("Error " + eI.toString() + "during indexing.");
                    eI.printStackTrace();
                }

                // increment our curItem
                folderList.set(curItemNo, null); // remove memory overhead as you go!
                curItemNo++;
                if (curSubNum >= maxSubNum)
                {
                    break;
                }
            }
            while (curItemNo <= lastItemNo);

            writer.close(); // close the writer
            indexes.add(di);
        }
        else
        {
            hasErr = true;
            errString = "Content Directory Not Found. Expected: " + contentDir;
        }
         // end for content dir Missing

        if (hasErr)
        {
            showMessage("Error creating Index", errString);
        }
        else
        {
            StringBuffer resultsBuf = new StringBuffer();
            resultsBuf.append("Added to index \"");
            resultsBuf.append(di.desc);
            resultsBuf.append("\" ");
            resultsBuf.append(curSize);
            resultsBuf.append(" files from ");
            resultsBuf.append(totalFolders);
            resultsBuf.append(" folders\n\nstarting in folder:\n\n\t");
            resultsBuf.append(di.path);
            resultsBuf.append("\n\nfor a depth of ");
            resultsBuf.append(di.depth);
            resultsBuf.append(" subfolders.");
            if (numErrors > 0)
            {
                resultsBuf.append("\n" + numErrors);
                resultsBuf.append(" files were not successfully indexed.");
                resultsBuf.append(noRobotsBuf.toString());
            }

            if (numNoIndex > 0)
            {
                resultsBuf.append("\n\n" + numNoIndex);
                resultsBuf.append(" files were not indexed because of meta data (robots = noindex) constraints:");
                resultsBuf.append(failedBuf.toString());
            }

            showMessage("Index Created", resultsBuf.toString());
        }

        setStatus("Indexing Complete.");
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void doNewIndex()
    {
        NewIndexDialog nid =
            new NewIndexDialog(this, "Create a New Index", true);
        nid.init();
        nid.setVisible(true);
        if (nid.returnBool)
        {
            //
            String descD = nid.descField.getText();
            String noUnd = Utils.replaceAll(" ", descD, "_");
            boolean isWeb = nid.isWeb.isSelected();
            String replace = nid.replaceField.getText();
            String match = nid.matchField.getText();
            int policy = nid.indexFreq.getSelectedIndex();
            try
            {
                createNewIndex(new DocSearcherIndex(nid.startField.getText(),
                                                    descD,
                                                    nid.searchByDefault
                                                    .isSelected(),
                                                    nid.sdChoice
                                                    .getSelectedIndex(),
                                                    Utils.addFolder(indexDir,
                                                                    noUnd),
                                                    isWeb, match, replace,
                                                    policy,
                                                    nid.archiveField.getText()));
            }
            catch (Exception eS)
            {
                showMessage("An Error Occurred while creating your index.",
                            eS.toString());
            }
        }
    }

    public String getTitle()
    {
        StringBuffer title = new StringBuffer();
        String ret = "";
        String saveText = editorPane.getText();
        int textLen = saveText.length();
        char curChar = ' ';
        boolean inTag = false;
        boolean inTitle = false;
        StringBuffer tagBuf = new StringBuffer();
        String lowerTag = "";
        try
        {
            for (int i = 0; i < textLen; i++)
            {
                curChar = saveText.charAt(i);
                if (curChar == '<')
                {
                    inTag = true;
                }
                else if (curChar == '>')
                {
                    tagBuf.append(curChar);
                    lowerTag = tagBuf.toString().toLowerCase();
                    if (curChar == '\n')
                    {
                        curChar = ' ';
                    }

                    if (lowerTag.startsWith("<title"))
                    {
                        inTitle = true;
                    }
                    else if (lowerTag.startsWith("</head"))
                    {
                        break;
                    }
                    else if (lowerTag.startsWith("</body"))
                    {
                        break;
                    }

                    inTag = false;
                    tagBuf = new StringBuffer();
                }
                 // end for end of a tag

                if ((!inTag) && (inTitle) && (curChar != '>'))
                {
                    title.append(curChar);
                }

                if (inTag)
                {
                    tagBuf.append(curChar);
                }
            }

            ret = title.toString().trim();
            if (ret.length() <= 0)
            {
                ret = Utils.getNameOnly(curPage);
            }
        }
        catch (Exception eP)
        {
            return Utils.getNameOnly(curPage);
        }

        return ret;
    }

    public void addNewBookmark(SimpleBookmark sbm)
    {
        bookmarksList.add(sbm);
        JMenuItem bmi = new JMenuItem(sbm.desc);
        bmi.setActionCommand(sbm.urlString);
        bmi.addActionListener(this);
        bookMarkMenu.add(bmi);
    }

    public void rebuildIndexes()
    {
        if (!indexes.isEmpty())
        {
            Iterator iterator = indexes.iterator();
            DocSearcherIndex di;
            while (iterator.hasNext())
            {
                di = ((DocSearcherIndex)iterator.next());
                idx.updateIndex(di);
            }
        }

        setStatus("Indexes rebuilt.");
    }

    public String getBrowserFile()
    {
        File testExist;
        String returnString = "";
        switch (osType)
        {
        case 0: // windows
            returnString =
                "C:\\Program Files\\Microsoft Internet\\Iexplore.exe";
            testExist = new File(returnString);
            if (!testExist.exists())
            {
                returnString =
                    "C:\\Program Files\\Microsoft\\Internet Explorer\\Iexplore.exe";
                testExist = new File(returnString);
                if (!testExist.exists())
                {
                    returnString =
                        "C:\\Program Files\\Plus!\\Microsoft Internet\\Iexplore.exe";
                    testExist = new File(returnString);
                    if (!testExist.exists())
                    {
                        returnString =
                            "C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE";
                    }
                }
                 // end for file still doesn't exist
            }
             // file doesn't exist

            break;

        case 1: // Linux
            returnString = "/usr/bin/konqueror";
            testExist = new File(returnString);
            if (!testExist.exists())
            {
                returnString = "/usr/bin/mozilla";
                testExist = new File(returnString);
                if (!testExist.exists())
                {
                    returnString = "/usr/bin/netscape";
                }
                 // end for no mozilla
            }
             // end for no konqueror

            break;

        case 2: // Unix variant
            returnString = "/usr/bin/netscape";

            break;

        case 3: // MAC
            returnString = "/usr/bin/Iexplore";

            break;

        case -1: //UNKNOWN
            returnString = "/usr/bin/netscape";

            break;
        }

        testExist = new File(returnString);
        if (!testExist.exists())
        {
            returnString = "NOT FOUND";
        }

        return returnString;
    }

    public void doExternal(String externalLink)
    {
        String lowerL = externalLink.toLowerCase();
        if ((curPage.equals("home")) || (curPage.equals("results")))
        {
            if ((lowerL.endsWith(".doc")) || (lowerL.endsWith(".xls"))
                    || (lowerL.endsWith(".ppt")) || (lowerL.endsWith(".pdf")))
            {
                System.out.println("Doing external viewer action.");
                if (externalLink.indexOf(" ") != -1)
                {
                    externalLink = "\"" + externalLink + "\"";
                }

                String execString = getBrowserFile() + " " + externalLink;
                try
                {
                    Runtime.getRuntime().exec(execString);
                }
                catch (Exception eR)
                {
                    showMessage("Error using External Viewer",
                                "The command:\n\n\t" + execString
                                + "\n\nproduced the following error:\n\n"
                                + eR.toString());
                }
            }
             // end for office file
            else
            {
                showMessage("Not Externally Viewable.",
                            "You can open documents found in a search via an external viewer \n- however you cannot externally view the search results \nunless you first save them and then open the page.");
            }
        }
        else
        {
            System.out.println("Doing external viewer action.");
            if (externalLink.indexOf(" ") != -1)
            {
                externalLink = "\"" + externalLink + "\"";
            }

            String execString = getBrowserFile() + " " + externalLink;
            try
            {
                Runtime.getRuntime().exec(execString);
            }
            catch (Exception eR)
            {
                showMessage("Error using External Viewer",
                            "The command:\n\n\t" + execString
                            + "\n\nproduced the following error:\n\n"
                            + eR.toString());
            }
        }
    }

    public void doBookmark()
    {
        System.out.println("Doing bookmark action.");
        if ((curPage.equals("home")) || (curPage.equals("results")))
        {
            showMessage("Not bookmarkable.",
                        "Search Results must be saved.\n\nInstead of bookmarking - click on the save icon.");
        }
         // end for not bookmarkable
        else
        {
            // obtain title - if there was one
            String nbt = getTitle();
            NewBookmarkDialog nbd =
                new NewBookmarkDialog(this, "Add a new bookmark", true);
            nbd.descField.setText(nbt);
            String toAdd = curPage;
            if ((toAdd.startsWith("file://"))
                    && (!toAdd.startsWith("file:///")))
            {
                toAdd = "file:///" + toAdd.substring(6, toAdd.length());
                System.out.println("New URL is:" + toAdd);
            }

            nbd.locationField.setText(toAdd);
            nbd.init();
            nbd.setVisible(true);
            if (nbd.returnBool)
            {
                addNewBookmark(new SimpleBookmark(nbd.locationField.getText(),
                                                  nbd.descField.getText()));
            }
        }
    }

    public void doManageIndexes()
    {
        if (!indexes.isEmpty())
        {
            int numIndexes = indexes.size();
            ManageIndexesDialog min =
                new ManageIndexesDialog(this, "Index Properties", true);
            min.init();
            min.setVisible(true);
            if (min.returnBool)
            {
                // proceed to make the changes
                ArrayList newIndex = new ArrayList();
                DocSearcherIndex di;
                for (int i = 0; i < numIndexes; i++)
                {
                    // set searched
                    if (!min.del[i].isSelected())
                    {
                        di = ((DocSearcherIndex)indexes.get(i));
                        di.shouldBeSearched = min.sbd[i].isSelected();
                        if (min.upd[i].isSelected())
                        {
                            idx.updateIndex(di);
                        }

                        if (min.expi[i].isSelected())
                        {
                            doExport(di);
                        }

                        newIndex.add(di);
                    }
                    else
                    {
                        // recursively delete the content
                        //in the selected index
                        di = ((DocSearcherIndex)indexes.get(i));
                        deleteRecursive(di.indexerPath);
                    }
                     // end for deleting and index
                }
                indexes = newIndex;
            }
        }
        else
        {
            showMessage("Error: No indexes", "Please create a new index.");
        }
    }

    public void deleteRecursive(String folderToDelete)
    {
        int curFoldNum = 0;
        File curFolderFile;
        String curFold = "";
        String[] subFolds;
        int numSubFolds = 0;
        int totalFolds = 0;
        int numFiles = 0;
        String curFolderString = "";
        String curFileString = "";
        File testFile;
        try
        {
            // first obtain a list of all folders
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            ArrayList allFold = new ArrayList();
            allFold.add(folderToDelete);
            setStatus("Removing index files...");
            do
            {
                // get list of sub folders
                curFolderString = (String)allFold.get(curFoldNum);
                curFolderFile = new File(curFolderString);
                subFolds = curFolderFile.list(ff);
                numSubFolds = subFolds.length;
                for (int y = 0; y < numSubFolds; y++)
                {
                    curFold = curFolderString + pathSep + subFolds[y] + pathSep;
                    curFold =
                        Utils.replaceAll(pathSep + pathSep, curFold, pathSep);
                    allFold.add(curFold);
                    totalFolds++;
                }

                curFoldNum++;
            }
            while (curFoldNum < totalFolds);

            // next get a list of all files
            ArrayList allFiles = new ArrayList();
            Iterator foldIt = allFold.iterator();
            String[] filesString;
            while (foldIt.hasNext())
            {
                curFolderString = (String)foldIt.next();
                curFolderFile = new File(curFolderString);

                // get the files
                filesString = curFolderFile.list();
                numFiles = filesString.length;
                for (int y = 0; y < numFiles; y++)
                {
                    // add the files
                    curFileString = curFolderString + pathSep + filesString[y];
                    curFileString =
                        Utils.replaceAll(pathSep + pathSep, curFileString,
                                         pathSep);
                    testFile = new File(curFileString);
                    if (!testFile.isDirectory())
                    {
                        allFiles.add(curFileString);

                        //System.out.println("will delete "+curFileString);
                    }
                }
            }
             // end for iterating

            // delete all files
            Iterator fileIt = allFiles.iterator();
            while (fileIt.hasNext())
            {
                curFileString = (String)fileIt.next();
                testFile = new File(curFileString);
                testFile.delete();
            }
             // end while deleteing

            // delete all folders
            int numFoldTotal = allFiles.size();
            for (int y = numFoldTotal - 1; y >= 0; y--)
            {
                curFolderString = (String)allFiles.get(y);
                curFolderFile = new File(curFolderString);
                System.out.println("Deleting dir: " + curFolderString);
                curFolderFile.delete();
            }

            // delete last folder
            curFolderFile = new File(folderToDelete);
            curFolderFile.delete();
        }
         // end for trrying recursive delete
        catch (Exception eR)
        {
            showMessage("Error removing index",
                        "The following error was encountered:\n\n"
                        + eR.toString());
        }
        finally
        {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            setStatus("Index removed.");
        }
    }

    public void cb()
    {
        if (useAuthor.isSelected())
        {
            authorField.setEnabled(true);
        }
        else
        {
            authorField.setEnabled(false);
        }

        if (useDate.isSelected())
        {
            fromField.setEnabled(true);
            toField.setEnabled(true);
        }
        else
        {
            fromField.setEnabled(false);
            toField.setEnabled(false);
        }

        if (useSize.isSelected())
        {
            sizeFromField.setEnabled(true);
            sizeToField.setEnabled(true);
        }
        else
        {
            sizeFromField.setEnabled(false);
            sizeToField.setEnabled(false);
        }

        if (useType.isSelected())
        {
            fileType.setEnabled(true);
        }
        else
        {
            fileType.setEnabled(false);
        }
    }

    void doZipArchiveUpdate(DocSearcherIndex di)
    {
        // try and obtain meta data from the file
        // based on this new meta data see if we can obtain new index
        // fourth column
        String tempArchiveDir = Utils.getFolderOnly(di.path);
        String metaFileName = Utils.addFolder(tempArchiveDir, "archives.htm");
        String tempManifestFileName =
            Utils.addFolder(workingDir, "temp_manifest.htm");
        boolean okToUpdate = true;
        if (metaFileName.toLowerCase().startsWith("http:"))
        {
            okToUpdate = downloadURLToFile(metaFileName, tempManifestFileName);
            metaFileName = tempManifestFileName;
        }

        if (okToUpdate)
        {
            // load the meta data
            try
            {
                Table tempTable = new Table(6, 100);
                tempTable.htmlLoad(metaFileName, "");
                String newIndexDate = di.lastIndexed;
                okToUpdate = tempTable.loadOK;
                if (okToUpdate)
                {
                    // search for our new date
                    int numArchs = tempTable.colSize();
                    boolean foundArch = false;
                    int matchInt = 0;
                    for (int i = 0; i < numArchs; i++)
                    {
                        if (tempTable.inItem(0, i).equals(di.desc))
                        {
                            matchInt = i;
                            foundArch = true;

                            break;
                        }
                    }
                     // end for iterating

                    if (foundArch)
                    {
                        if (!tempTable.inItem(1, matchInt).equals(di.lastIndexed))
                        {
                            newIndexDate = tempTable.inItem(1, matchInt);
                            String downloadFileName =
                                Utils.addFolder(tempArchiveDir,
                                                tempTable.inItem(2, matchInt));
                            if (downloadFileName.toLowerCase().startsWith("http:"))
                            {
                                String tempZipFileName =
                                    Utils.addFolder(workingDir,
                                                    "temp_zip_download.zip");
                                okToUpdate =
                                    downloadURLToFile(downloadFileName,
                                                      tempZipFileName);
                                downloadFileName = tempZipFileName;
                            }

                            if (okToUpdate)
                            {
                                // now delete recursively the directory
                                // and extract the new zip
                                setStatus("Deleting path: " + di.indexerPath);
                                deleteRecursive(di.indexerPath);
                                File newIndP = new File(di.indexerPath);
                                boolean madeFold = newIndP.mkdir();
                                boolean finalSuccess = true;
                                if (madeFold)
                                {
                                    setStatus("Unzipping " + downloadFileName
                                              + " to " + di.indexerPath);
                                    UnZippHandler uz =
                                        new UnZippHandler(downloadFileName,
                                                          di.indexerPath);
                                    try
                                    {
                                        uz.unZip();
                                        setStatus("The archive was successfully updated.");
                                        di.lastIndexed = newIndexDate;
                                    }
                                    catch (Exception eZ)
                                    {
                                        finalSuccess = false;
                                        showMessage("Error updating archive",
                                                    "The following error was encountered:\n\n "
                                                    + eZ.toString());
                                    }
                                }
                                else
                                {
                                    showMessage("Error creating folder",
                                                "Unable to re-create folder "
                                                + di.indexerPath);
                                }
                            }
                             // end if ok to update
                        }
                         // end if dates different
                        else
                        {
                            showMessage("No Update Available for archive.",
                                        "Last update was " + di.lastIndexed
                                        + " (same as archive).");
                        }
                    }
                     // end if found arch
                    else
                    {
                        showMessage("Unable to locate meta data",
                                    "...in file: " + metaFileName);
                    }
                }
                else
                {
                    setStatus("Unable to locate meta data in file: "
                              + metaFileName);
                }
            }
             // end for trying to update Zip
            catch (Exception metaE)
            {
                setStatus("Error retrieving meta data from " + metaFileName);
                okToUpdate = false;
            }
        }
    }

    public String getArchiveDir()
    {
        return archiveDir;
    }

    public void doExport(DocSearcherIndex di)
    {
        // zip contents and place in archive Dir
        String archiveZipFileName = Utils.replaceAll(" ", di.desc, "_");
        if (!archiveZipFileName.toLowerCase().endsWith(".zip"))
        {
            archiveZipFileName += ".zip";
        }

        String content = di.path;
        if (di.isWeb)
        {
            content = di.match;
        }

        String zipFileNameOnly = archiveZipFileName;
        archiveZipFileName = Utils.addFolder(di.archiveDir, archiveZipFileName);
        ZippHandler zh = new ZippHandler(archiveZipFileName, di.indexerPath);
        boolean zipSuccess = true;
        String errMsg = "";
        setStatus("Archiving Index " + di.desc + " to zip: "
                  + archiveZipFileName + ", please wait...");
        try
        {
            zh.zip();
        }
        catch (Exception eZ)
        {
            errMsg = eZ.toString();
            zipSuccess = false;
        }
        finally
        {
            setStatus("Archiving finished.");
        }

        if (zipSuccess)
        {
            showMessage("Archive Successful",
                        di.desc + " was archived to file :\n"
                        + archiveZipFileName);
        }
        else
        {
            showMessage("Error archiving index", errMsg);
        }

        // OK now update the archive table
        updateArchiveTable(di.desc, di.lastIndexed, zipFileNameOnly,
                           di.archiveDir, content);
    }

    public void updateArchiveTable(String desc,
                                   String lastIndexed,
                                   String zipFileName,
                                   String archDir,
                                   String content)
    {
        boolean hasErr = false;
        String errMsg = "";
        try
        {
            String archivesFileName = Utils.addFolder(archDir, "archives.htm");
            setStatus("Updateing archives table: " + archivesFileName);
            File textArchiveIndex = new File(archivesFileName);
            if (textArchiveIndex.exists())
            {
                // read and update the file
                Table tempTable = new Table(6, 100);
                tempTable.htmlLoad(archivesFileName, "");
                tempTable.captionStr =
                    "DocSearcher Lucene Search Index Archive Listing";
                int numI = tempTable.colSize();
                String tempDesc = "";
                int foundAtNum = numI;

                // parse it
                for (int i = 1; i < numI; i++)
                {
                    tempDesc = tempTable.inItem(0, i);
                    if (tempDesc.equals(desc))
                    {
                        foundAtNum = i;

                        break;
                    }
                }
                 // end for iterating

                //
                tempTable.add(desc, 0, foundAtNum);
                tempTable.add(lastIndexed, 1, foundAtNum);
                tempTable.add(zipFileName, 2, foundAtNum);
                tempTable.add(content, 3, foundAtNum);

                // save it
                int k = tempTable.colSize();
                int l = tempTable.rowSize();
                tempTable.fpSave(archivesFileName, k, l);
            }
            else
            {
                // create a new archive index
                Table tempTable = new Table(6, 102);
                tempTable.captionStr =
                    "DocSearcher Lucene Search Index Archive Listing";

                //
                // add the header
                tempTable.add("Description", 0, 0);
                tempTable.add("Date of Indexing", 1, 0);
                tempTable.add("Archive Zip File", 2, 0);
                tempTable.add("Directory or Content", 3, 0);

                // add the data
                tempTable.add(desc, 0, 1);
                tempTable.add(lastIndexed, 1, 1);
                tempTable.add(zipFileName, 2, 1);
                tempTable.add(content, 3, 1);

                // save it
                System.out.println("Saving changes to " + archivesFileName);

                // save it
                int k = tempTable.colSize();
                int l = tempTable.rowSize();
                tempTable.fpSave(archivesFileName, k, l);
            }
        }
         // end for try
        catch (Exception eT)
        {
            hasErr = true;
            errMsg = eT.toString();
        }

        if (hasErr)
        {
            showMessage("Error updating the archive tables", errMsg);
        }
        else
        {
            setStatus("Archives table updated. ");
        }
    }

    public boolean downloadURLToFile(String urlString,
                                     String fileToSaveAs)
    {
        byte curBint;
        boolean returnBool = true;
        int numBytes = 0;
        int curI = 0;
        FileOutputStream dos;
        InputStream urlStream;
        int lastPercent = 0;
        int curPercent = 0;
        try
        {
            URL url = new URL(urlString);
            File saveFile = new File(fileToSaveAs);
            dos = new FileOutputStream(saveFile);
            URLConnection conn = (URLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            urlStream = conn.getInputStream();
            int totalSize = conn.getContentLength();
            while (curI != -1)
            {
                curI = urlStream.read();
                curBint = (byte)curI;
                if (curI == -1)
                {
                    break;
                }

                dos.write(curBint);
                numBytes++;
                if (totalSize > 0)
                {
                    curPercent = (numBytes * 100) / totalSize;
                    if (curPercent != lastPercent)
                    {
                        setStatus(curPercent + " % Downloaded (file "
                                  + urlString + " bytes total " + totalSize
                                  + ")");
                        lastPercent = curPercent;
                    }
                }
                 // end if total size not zero
            }

            urlStream.close();
            dos.close();
        }
        catch (Exception eF)
        {
            showMessage("File Download Error",
                        "Download Error Message Was\n\n:" + eF.toString());
            returnBool = false;
            eF.printStackTrace();
        }

        return returnBool;
    }

    public void doImport(String zipFileString,
                         String description,
                         String dateIndexed,
                         boolean searchedByDefault,
                         boolean isWeb,
                         int indexPolicy)
    {
        // may be just a URL
        boolean successFulDownload = true;
        if (dateIndexed.equals(""))
        {
            dateIndexed = DateTimeUtils.getToday();
        }

        //
        boolean zipSuccess = true;
        String errMsg = "";
        String indexFolderName = Utils.replaceAll(" ", description, "_");
        String loadString = zipFileString;
        if (loadString.toLowerCase().startsWith("http://"))
        {
            // download zip and load the downloaded zip file
            String nameOnlyStr = Utils.getNameOnly(loadString);
            loadString = Utils.addFolder(archiveDir, nameOnlyStr);
            successFulDownload = downloadURLToFile(zipFileString, loadString);
            if (!successFulDownload)
            {
                errMsg = "Failed to download " + zipFileString;
                zipSuccess = false;
            }
        }
         // end for downloading the zip

        indexFolderName = Utils.addFolder(indexDir, indexFolderName);
        File newIndexFolder = new File(indexFolderName);
        boolean dirMade = newIndexFolder.mkdir();
        if ((dirMade) && (successFulDownload))
        {
            UnZippHandler uz = new UnZippHandler(loadString, indexFolderName);
            setStatus("Importing " + description + ", please wait...");
            try
            {
                uz.unZip();

                // add it in there
                DocSearcherIndex di =
                    new DocSearcherIndex(zipFileString, description,
                                         searchedByDefault, 0, indexFolderName,
                                         isWeb, "", "", indexPolicy, archiveDir);
                indexes.add(di);
            }
            catch (Exception eZ)
            {
                errMsg = eZ.toString();
                zipSuccess = false;
            }
            finally
            {
                setStatus("Import finished.");
            }

            if (zipSuccess)
            {
                showMessage("Import Successful ", description
                            + " was imported.");
            }
            else
            {
                showMessage("Error Importing index: " + description, errMsg);
            }
        }
        else
        {
            showMessage("Error importing index",
                        "Unable to create folder " + indexFolderName
                        + "\n\nPlease delete this folder and ensure you have proper permissions to this volume.");
        }
    }

    public void getImportInfo()
    {
        ImportDialog id =
            new ImportDialog(this, "Import DocSearcher Index", true);
        id.init();
        id.setVisible(true);
        if (id.confirmed)
        {
            String importZipFileName = id.urlOrFile.getText().trim();
            String importFolderOnly = Utils.getFolderOnly(importZipFileName);
            String importManifestName =
                Utils.addFolder(importFolderOnly, "archives.htm");
            String zipArchNameOnly = Utils.getNameOnly(importZipFileName);
            boolean foundManifest = false;
            boolean foundManifestData = false;
            boolean doImport = true;

            //
            String zipFileString = importZipFileName;
            String description = "Unknown";
            String dateIndexed = "";
            boolean searchedByDefault = true;
            boolean isWeb = true;
            int indexPolicy = 0;

            //
            if (importManifestName.toLowerCase().startsWith("http:"))
            {
                // convert URL to file
                String tempManifestFileName =
                    Utils.addFolder(workingDir, "temp_manifest.htm");
                foundManifest =
                    downloadURLToFile(importManifestName, tempManifestFileName);
                if (foundManifest)
                {
                    importManifestName = tempManifestFileName;
                }
            }
             // end for manifest
            else
            {
                File testManifestFile = new File(importManifestName);
                if (testManifestFile.exists())
                {
                    foundManifest = true;
                }
            }
             // end for not a URL

            if (foundManifest)
            {
                // try and retrieve the manifest data
                Table tempTable = new Table(11, 100);
                tempTable.htmlLoad(importManifestName, "");
                int numI = tempTable.colSize();
                int tempSbd = 0;
                String tempArchFile = "";
                String tempDesc = "";
                String tempDateIndexed = "";

                // parse it
                for (int i = 1; i < numI; i++)
                {
                    //
                    try
                    {
                        tempDesc = tempTable.inItem(0, i);
                        tempArchFile = tempTable.inItem(2, i);
                        tempDateIndexed = tempTable.inItem(1, i);
                        if (zipArchNameOnly.equals(tempArchFile))
                        {
                            description = tempDesc;
                            dateIndexed = tempDateIndexed;
                            foundManifestData = true;
                            setStatus("Archive description found: "
                                      + description);

                            break;
                        }
                        else
                        {
                            System.out.println(zipArchNameOnly
                                               + " not equal to "
                                               + tempArchFile);
                        }
                    }
                    catch (Exception eR)
                    {
                        setStatus("Error parsing manifest file: "
                                  + eR.toString());
                    }
                }
                 // end for iterating over manifest file entries
            }

            if (!foundManifestData)
            {
                // show a dialog to obtain archive meta data
                // IF dialog is cancelled ; doImport  =  false
                ManifestDialog md =
                    new ManifestDialog(this, "Import Index Properties", true);
                md.init();
                md.setVisible(true);
                if (md.confirmed)
                {
                    description = md.descField.getText().trim();
                    dateIndexed = DateTimeUtils.getToday();
                    isWeb = md.isWebBox.isSelected();
                    searchedByDefault = md.sbdBox.isSelected();
                    indexPolicy = md.indexFreq.getSelectedIndex();
                }
                else
                {
                    doImport = false;
                }
            }

            if (doImport)
            {
                // create our new index!
                doImport(zipFileString, description, dateIndexed,
                         searchedByDefault, isWeb, indexPolicy);
            }
        }
         // end for confirmed
    }

    public static void main(String[] args)
    {
        String commandString = "";
        String indexString = "";
        boolean hasCommands = false;
        if (args.length > 0)
        {
            commandString = args[0];
            if (commandString != null)
            {
                if (args.length > 1)
                {
                    indexString = args[1];
                    if (indexString == null)
                    {
                        indexString = "";
                    }
                }

                hasCommands = true;
            }
        }

        final DocSearch sw;
        DocSplashViewer splash =
            new DocSplashViewer("splash.gif",
                                "DocSearcher 3.0 is Loading; Please wait...");
        if (!hasCommands)
        {
            splash.display();
        }

        sw = new DocSearch();
        if (args.length > 0)
        {
            sw.useGui = false;
        }

        splash.setMonitor(sw);
        sw.init();
        splash.close();
        if (!hasCommands)
        {
            sw.setVisible(true);
            //Runnable checkUPD =
            //    new Runnable()
            //    {
            //        public void run()
            //        {
                        sw.checkUpdates();
            //        }
            //    };
            //SwingUtilities.invokeLater(checkUPD);
        }
        else
        {
            if ((commandString.equals("update")) && (indexString.equals("")))
            {
                sw.checkUpdates();
                System.exit(0);
            }
            else
            {
                sw.doCommand(commandString, indexString);
            }
        }
    }

    public void doCommand(String commandString,
                          String indexString)
    {
        System.out.println("command: " + commandString + ", index: "
                           + indexString);

        if (commandString.equals("list"))
        {
            int numIndexes = indexes.size();
            if (numIndexes > 0)
            {
                DocSearcherIndex di;
                Iterator it = indexes.iterator();
                int countNum = 1;
                while (it.hasNext())
                {
                    di = (DocSearcherIndex)it.next();
                    System.out.println(countNum + ". " + di.desc);
                    countNum++;
                }
            }
            else
            {
                System.out.println("No Indexes Found.");
            }
        }
        else if (commandString.equals("update"))
        {
            if (indexString.trim().equals(""))
            {
                System.out.println("You need to specify an index.");
            }
            else
            {
                int numIndexes = indexes.size();
                boolean foundMatch = false;
                if (numIndexes > 0)
                {
                    DocSearcherIndex di;
                    Iterator it = indexes.iterator();
                    while (it.hasNext())
                    {
                        di = (DocSearcherIndex)it.next();
                        if (di.desc.equals(indexString))
                        {
                            idx.updateIndex(di);
                            foundMatch = true;

                            break;
                        }
                    }
                     // end for match

                    if (!foundMatch)
                    {
                        System.out.println(indexString
                                           + " didn't match any indexes.");
                    }
                }
                else
                {
                    System.out.println("No Indexes Found.");
                }
            }
             // end for we have an index
        }
         // end for update
        else if (commandString.equals("export"))
        {
            if (indexString.trim().equals(""))
            {
                System.out.println("You need to specify an index.");
            }
            else
            {
                int numIndexes = indexes.size();
                boolean foundMatch = false;
                if (numIndexes > 0)
                {
                    DocSearcherIndex di;
                    Iterator it = indexes.iterator();
                    while (it.hasNext())
                    {
                        di = (DocSearcherIndex)it.next();
                        if (di.desc.equals(indexString))
                        {
                            doExport(di);
                            foundMatch = true;

                            break;
                        }
                    }
                     // end for match

                    if (!foundMatch)
                    {
                        System.out.println(indexString
                                           + " didn't match any indexes.");
                    }
                }
                else
                {
                    System.out.println("No Indexes Found.");
                }
            }
             // end for we have an index
        }
        else if (commandString.startsWith("search:"))
        {
            String searchT = commandString.substring(7, commandString.length());
            setStatus("SEARCHING FOR: " + searchT + "...");
            if (indexString.trim().equals(""))
            {
                doSearch(searchT);
            }
            else
            {
                int numIndexes = indexes.size();
                if (numIndexes > 0)
                {
                    DocSearcherIndex di;
                    boolean foundMatch = false;
                    Iterator it = indexes.iterator();
                    while (it.hasNext())
                    {
                        di = (DocSearcherIndex)it.next();
                        if (di.desc.equals(indexString))
                        {
                            di.shouldBeSearched = true;
                            foundMatch = true;
                        }
                        else
                        {
                            di.shouldBeSearched = false;
                        }
                    }
                     // end for match

                    if (!foundMatch)
                    {
                        System.out.println(indexString
                                           + " didn't match any indexes.");
                    }
                    else
                    {
                        doSearch(searchT);
                    }
                }
                else
                {
                    System.out.println("No Indexes Found.");
                }
            }

            // proceed to do search
        }
        else if (commandString.equals("analyze_log"))
        {
            try
            {
                LogAnalysis.doLogAnalysis(this, indexString);
            }
            catch (Exception eF)
            {
                System.out.println("Error analyzing log file (" + indexString
                                   + "): " + eF.toString());
            }
        }
        else
        {
            // list the command line help
            System.out.println("\nCOMMAND LINE USAGE: \njava -jar DocSearch.jar [\"action\"]  [\"index\" or log file name] \n\t ... where actions can be:");
            System.out.println("\n\t update : Which means update an index");
            System.out.println("\n\t export : Which means export an index to a zip file");
            System.out.println("\n\t list : Which lists the indexes");
            System.out.println("\n\t analyze_log : Which analyzes search log data (from a servlet)");
            System.out.println("\n\t \"Search:text to find\" : Which performs a search and outputs \n\t\tthe text result to the console.");
        }

        System.exit(0);
    }

    public void getSeachLogReport()
    {
        JFileChooser fdo = new JFileChooser();
        fdo.setCurrentDirectory(new File(workingDir));
        int fileGotten =
            fdo.showDialog(this, Messages.getString("DocSearch.select"));
        if (fileGotten == JFileChooser.APPROVE_OPTION)
        {
            File file = fdo.getSelectedFile();
            try
            {
                LogAnalysis.doLogAnalysis(this, file.toString());
            }
            catch (Exception eF)
            {
                setStatus(Messages.getString("DocSearch.statusLogError")
                          + eF.toString());
            }
        }
    }

    protected void doMetaReport()
    {
        MetaReport mr = new MetaReport();
        mr.getMetaReport(this);
    }

    class Hyperactive
        implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent e)
        {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
                JEditorPane pane = (JEditorPane)e.getSource();
                if (e instanceof HTMLFrameHyperlinkEvent)
                {
                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent)e;
                    HTMLDocument doc = (HTMLDocument)pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent(evt);
                }
                else
                {
                    try
                    {
                        String urlString = e.getURL().toString();
                        if (!Utils.isViewAble(urlString))
                        {
                            doExternal(urlString);
                        }
                        else
                        {
                            System.out.println("Loading: " + urlString);
                            pane.setPage(e.getURL());
                            addToBackList(curPage);
                            curPage = urlString;
                        }
                    }
                    catch (Throwable t)
                    {
                        showMessage("Error Loading Page",
                                    "The following error was reported:\n\n"
                                    + t.toString()
                                    + "\n\nIt may be that the link is broken or network problems occurred.");
                        t.printStackTrace();
                    }
                }
            }
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
