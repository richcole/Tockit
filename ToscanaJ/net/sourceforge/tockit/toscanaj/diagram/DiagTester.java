package net.sourceforge.tockit.toscanaj.diagram;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import net.sourceforge.tockit.toscanaj.data.Diagram;
import net.sourceforge.tockit.toscanaj.data.DiagramNode;
import net.sourceforge.tockit.toscanaj.data.LabelInfo;

import net.sourceforge.tockit.toscanaj.diagram.DiagramView;

/**
 * This class is used for testing the diagram drawing code.
 *
 * @TODO change code to match refactored version (if further tests are needed)
 */
public class DiagTester extends JFrame {

    /**
     * The main view.
     */
    DiagramView _diagramView;

    /**
     * The default constructor creates the menu structure and adds a
     * DiagramView to the main window.
     */
    public DiagTester()
    {
        // create menu bar
        JMenuBar menuBar;
        menuBar = new JMenuBar();
        setJMenuBar( menuBar );

        // create Diagram menu
        JMenu diagramMenu;
        diagramMenu = new JMenu("Diagram");
        diagramMenu.setMnemonic( KeyEvent.VK_D );
        diagramMenu.setToolTipText( "Selects Diagrams" );
        menuBar.add( diagramMenu );

        // create Diagram->One
        JMenuItem diagramMenuOne;
        diagramMenuOne = new JMenuItem( "One" );
        diagramMenuOne.setMnemonic( KeyEvent.VK_O );
        diagramMenuOne.setToolTipText( "Creates and shows a Diagram" );
        diagramMenuOne.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent evt ) {
                showDiagramOne();
            }
        } );
        diagramMenu.add( diagramMenuOne );

        // create Diagram->Two
        JMenuItem diagramMenuTwo;
        diagramMenuTwo = new JMenuItem( "Two" );
        diagramMenuTwo.setMnemonic( KeyEvent.VK_T );
        diagramMenuTwo.setToolTipText( "Creates and shows a Diagram" );
        diagramMenuTwo.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent evt ) {
                showDiagramTwo();
            }
        } );
        diagramMenu.add( diagramMenuTwo );

        // create Diagram->Three
        JMenuItem diagramMenuThree;
        diagramMenuThree = new JMenuItem( "Three" );
        diagramMenuThree.setMnemonic( KeyEvent.VK_H );
        diagramMenuThree.setToolTipText( "Creates and shows a Diagram" );
        diagramMenuThree.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent evt ) {
                showDiagramThree();
            }
        } );
        diagramMenu.add( diagramMenuThree );

        diagramMenu.addSeparator();

        // create Diagram->Exit
        JMenuItem diagramMenuExit;
        diagramMenuExit = new JMenuItem( "Exit" );
        diagramMenuExit.setMnemonic( KeyEvent.VK_X );
        diagramMenuExit.setToolTipText( "Leave the program" );
        diagramMenuExit.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent evt ) {
                System.exit(0);
            }
        } );
        diagramMenu.add( diagramMenuExit );

        // get the main window
        _diagramView = new DiagramView();

        getContentPane().add( _diagramView );
    }

    /**
     * Creates a diagram and puts in in the display.
     */
    protected void showDiagramOne()
    {
        /*
        Diagram diagram = new Diagram();

        DiagramNode node1 = new Point2D.Double( 0, 0 );
        DiagramNode node2 = new Point2D.Double( -1, -1 );
        DiagramNode node3 = new Point2D.Double( 1, -1 );
        DiagramNode node4 = new Point2D.Double( 0, -2 );

        diagram.addNode( node1 );
        diagram.addNode( node2 );
        diagram.addNode( node3 );
        diagram.addNode( node4 );

        diagram.addLine( node1, node2 );
        diagram.addLine( node1, node3 );
        diagram.addLine( node2, node4 );
        diagram.addLine( node3, node4 );

        LabelInfo label;

        label = diagram.getAttributeLabel( 1 );
        label.addEntry( "One" );
        label.addEntry( "Two" );

        label = diagram.getAttributeLabel( 2 );
        label.addEntry( "blablabla" );
        label.addEntry( "auch nicht besser" );
        label.setTextAligment( LabelInfo.ALIGNCENTER );
        label.setOffset( new Point2D.Double( 0.3, 0.3 ) );

        label = diagram.getObjectLabel( 2 );
        label.addEntry( "A" );
        label.addEntry( "And another one" );
        label.setBackgroundColor( Color.green );
        label.setTextColor( Color.white );

        label = diagram.getObjectLabel( 3 );
        label.addEntry( "B" );
        label.addEntry( "Bnd bnother one" );
        label.setTextAligment( LabelInfo.ALIGNRIGHT );

        _diagramView.showDiagram( diagram );
        */
    }

    /**
     * Creates a diagram and puts in in the display.
     */
    protected void showDiagramTwo()
    {
        /*
        Diagram diagram = new Diagram();

        diagram.addNode( new Point2D.Double( 0, 0 ) );
        diagram.addNode( new Point2D.Double( -2, 1 ) );
        diagram.addNode( new Point2D.Double( 1,  1) );
        diagram.addNode( new Point2D.Double( -3, 2 ) );
        diagram.addNode( new Point2D.Double( -1, 2 ) );
        diagram.addNode( new Point2D.Double( -2, 3 ) );
        diagram.addNode( new Point2D.Double( 1, 3 ) );
        diagram.addNode( new Point2D.Double( 0, 4 ) );

        diagram.addLine( 0, 1 );
        diagram.addLine( 0, 2 );
        diagram.addLine( 1, 3 );
        diagram.addLine( 1, 4 );
        diagram.addLine( 2, 6 );
        diagram.addLine( 3, 5 );
        diagram.addLine( 4, 5 );
        diagram.addLine( 5, 7 );
        diagram.addLine( 6, 7 );

        _diagramView.showDiagram( diagram );
        */
    }

    /**
     * Creates a diagram and puts in in the display.
     */
    protected void showDiagramThree()
    {
        /*
        Diagram diagram = new Diagram();

        diagram.addNode( new Point2D.Double( 1.3247, -.234 ) );
        diagram.addNode( new Point2D.Double( 1.3247, 2.3465 ) );

        diagram.addLine( 0, 1 );

        _diagramView.showDiagram( diagram );
        */
    }

    public static void main( String[] args )
    {
        DiagTester window = new DiagTester();

        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        window.setTitle("Diagram");
        window.setSize(600,450);
        window.setVisible(true);
    }
}