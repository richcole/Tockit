package docsearcher;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ProgressDialogDS
    extends JDialog
{
    public final static int ONE_SECOND = 1000;
    JProgressBar progressBar;
    private Timer timer;
    JLabel dirLabel = new JLabel("Progress...");
    DocSearch monitor;
    int cur = 0;
    boolean isDone = false;

    ProgressDialogDS(DocSearch monitor,
                     String title,
                     boolean modal)
    {
        super(monitor, title, modal);
        this.monitor = monitor;
        progressBar = new JProgressBar(0, 70000);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        JPanel panelTop = new JPanel();
        JPanel panel = new JPanel();
        panelTop.add(dirLabel);
        panel.add(progressBar);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panelTop, BorderLayout.NORTH);
        contentPane.add(panel, BorderLayout.SOUTH);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        //Create a timer.
        timer =
            new Timer(ONE_SECOND,
                      new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        progressBar.setValue(cur);
                        if (isDone)
                        {
                            //Toolkit.getDefaultToolkit().beep();
                            timer.stop();
                            progressBar.setValue(progressBar.getMinimum());
                            hideD();
                        }
                    }
                });
    }

    public void hideD()
    {
        this.setVisible(false);
    }

    public void setCurrent(int newCur)
    {
        cur = newCur;
        //System.out.println("progress is  now "+cur);
    }

    public void setDone()
    {
        isDone = true;
    }

    public void init()
    {
        pack();
        Rectangle frameSize = getBounds();
        int newX = 0;
        int newY = 0;
        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenD.width;
        int screenHeight = screenD.height;
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

    public void startProgress(String taskString)
    {
        dirLabel.setText(taskString);
        pack();
        progressBar.setValue(progressBar.getMinimum());
        isDone = false;
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
        this.setVisible(true);
        timer.start();
    }
}
