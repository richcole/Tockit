package docsearcher;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class DocSplashViewer
    extends JPanel
{
    Image image = null;
    String labelText = "Loading Please wait...";
    JWindow j;
    int Iheight = 0;
    int Iwidth = 0;
    int xOffset = 0;
    int yOffset = 0;
    String brokenImageString = "brokenImage.jpg";
    String pathSep = System.getProperty("file.separator");
    Image offScreenBuffer;
    Image brokenImage;
    MediaTracker tracker;
    DocSearch monitor;
    Dimension preferredSize = new Dimension(460, 390);
    int sizeW = 900;
    int sizeH = 940;
    boolean isCached = false;
    boolean isBroken = false;
    String saveDir = "";
    String lastStatus = "";
    boolean refresh = false;
    boolean hasMon = false;

    DocSplashViewer(String fileNameStr)
    {
        image = loadImage(fileNameStr);
        repaint();
    }

    DocSplashViewer(String fileNameStr,
                    String labelT)
    {
        image = loadImage(fileNameStr);
        labelText = labelT;
        repaint();
    }

    public void init()
    {
        image = Toolkit.getDefaultToolkit().getImage(brokenImageString);
        brokenImage = Toolkit.getDefaultToolkit().getImage(brokenImageString);
        tracker = new MediaTracker(this);
        preferredSize = new Dimension(sizeW, sizeH);
    }

    public void close()
    {
        if (j != null)
        {
            j.dispose();
        }
    }

    public void setMonitor(DocSearch mon)
    {
        hasMon = true;
        monitor = mon;
        repaint();
        statusThread st = new statusThread();
        st.start();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.blue);
        setBackground(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        String drawText = "";
        if (isCached)
        {
            drawText += "USING CACHED IMAGE";
        }

        g.setColor(Color.white);
        g.fillRect(0, 0, sizeW, sizeH);
        g.drawRect(0, 0, Iwidth + 2, Iheight + 2);
        if (!isBroken)
        {
            if ((Iwidth != 0) && (Iheight != 0))
            {
                g.drawImage(image, 1, 1, Iwidth, Iheight, this);
            }

            g.fillRect(0, 0, Iwidth, 17);
            g.setColor(Color.black);
            g.drawString(labelText, 10, 13);
        }
        else
        {
            if ((Iwidth != 0) && (Iheight != 0))
            {
                g.drawImage(image, 0, 0, Iwidth, Iheight, this);
            }
            else
            {
                g.drawImage(image, 0, 0, this);
            }

            g.fillRect(0, 0, labelText.length() * 17, 20);
            g.drawString(labelText, 31, 41);
        }
    }

    public Image loadImage(String fileNameStr)
    {
        Image newImage = null;
        if (fileNameStr.toLowerCase().startsWith("http://"))
        {
            try
            {
                String fileOnly =
                    fileNameStr.substring((fileNameStr.lastIndexOf("/") + 1),
                                          fileNameStr.length());
                String domOnly =
                    fileNameStr.substring(0, (fileNameStr.lastIndexOf("/") + 1));
                System.out.println("\tfile:" + fileOnly + "\n\tURL prefix:"
                                   + domOnly);
                convertURLToFile(fileNameStr, fileOnly);
                if (!saveDir.equals(""))
                {
                    fileOnly = Utils.addFolder(saveDir, fileOnly);
                }

                newImage = Toolkit.getDefaultToolkit().getImage(fileOnly);
                tracker = new MediaTracker(this);
                tracker.addImage(newImage, 0);
                tracker.waitForAll();
                Iheight = newImage.getHeight(this);
                Iwidth = newImage.getWidth(this);
                if ((Iheight <= 0) || (Iwidth <= 0))
                {
                    newImage =
                        Toolkit.getDefaultToolkit().getImage(brokenImageString);
                    tracker = new MediaTracker(this);
                    tracker.addImage(newImage, 0);
                    tracker.waitForAll();
                    Iheight = newImage.getHeight(this);
                    Iwidth = newImage.getWidth(this);
                    System.out.println("BROKEN IMAGE - failed to load:\n\t"
                                       + fileOnly);
                    isBroken = true;
                }
                else
                {
                    isBroken = false;
                }

                if (!isBroken)
                {
                    System.out.println("...finished loading image from URL.");
                }
            }
            catch (Exception eI)
            {
                System.out.println("FAILED TO LOAD IMAGE - \n\tImage problem:"
                                   + eI.toString());
                isBroken = true;
                eI.printStackTrace();
            }
        }
        else
        {
            try
            {
                newImage = Toolkit.getDefaultToolkit().getImage(fileNameStr);
                tracker = new MediaTracker(this);
                tracker.addImage(newImage, 0);
                tracker.waitForAll();
                Iheight = newImage.getHeight(this);
                Iwidth = newImage.getWidth(this);
                if ((Iheight <= 0) || (Iwidth <= 0))
                {
                    newImage =
                        Toolkit.getDefaultToolkit().getImage(brokenImageString);
                    System.out.println("BROKEN IMAGE - failed to load:\n\t"
                                       + fileNameStr);
                    isBroken = true;
                    Iheight = 30;
                    Iwidth = 200;
                }
                else
                {
                    isBroken = false;
                }
            }
            catch (Exception eI)
            {
                System.out.println("Image problem:" + eI.toString());
                eI.printStackTrace();
            }
        }

        return newImage;
    }

    public void changeImage(String fileNameStr)
    {
        image = loadImage(fileNameStr);
        repaint();
    }

    public int getHeight()
    {
        return Iheight;
    }

    public int getWidth()
    {
        return Iwidth;
    }

    public void display()
    {
        j = new JWindow();
        j.getContentPane().setLayout(new GridLayout(1, 1));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        j.getContentPane().setLayout(gridbaglayout);
        JScrollPane imageScroll = new JScrollPane(this);
        imageScroll.setPreferredSize(new Dimension(365, 365));
        gridbagconstraints.fill = GridBagConstraints.BOTH;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbaglayout.setConstraints(imageScroll, gridbagconstraints);
        j.getContentPane().add(imageScroll);

        Dimension screenD;
        screenD = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenD.width;
        int screenHeight = screenD.height;

        j.setLocation((screenWidth / 2) - (350 / 2),
                      (screenHeight / 2) - (250 / 2));
        j.pack();
        j.setVisible(true);
        repaint();
    }

    public void convertURLToFile(String urlString,
                                 String fileToSaveAs)
    {
        byte curBint;
        int numBytes = 0;
        int curI = 0;
        FileOutputStream dos;
        InputStream urlStream;
        try
        {
            URL url = new URL(urlString);
            File saveFile;
            if (!saveDir.equals(""))
            {
                saveFile = new File(saveDir, fileToSaveAs);
            }
            else
            {
                saveFile = new File(fileToSaveAs);
            }

            if ((!saveFile.exists()) || (refresh))
            {
                if (refresh)
                {
                    System.out.println("Refreshing image...");
                    refresh = false;
                }

                dos = new FileOutputStream(saveFile);
                URLConnection conn = (URLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.connect();
                urlStream = conn.getInputStream();

                //urlStream = url.openConnection().getInputStream();
                while (curI != -1)
                {
                    curI = urlStream.read();
                    curBint = (byte)curI;
                    dos.write(curBint);
                    numBytes++;
                }

                urlStream.close();
                dos.close();
            }
            else
            {
                System.out.println("Using cached image....");
                isCached = true;
            }

            if (!isCached)
            {
                System.out.println("File " + fileToSaveAs + " has " + numBytes
                                   + " bytes.");
            }
        }
        catch (Exception eF)
        {
            System.out.println("Error retrieving file:" + eF.toString());
            eF.printStackTrace();
        }
    }

    public Dimension getPreferredSize()
    {
        int newW = 360;
        int newH = 360;
        if (image != null)
        {
            newW = image.getWidth(this);
            newH = image.getHeight(this);
        }

        preferredSize = new Dimension(newW, newH);

        return preferredSize;
    }

    public class statusThread
        implements Runnable
    {
        Thread statusValidator;

        public void start()
        {
            if (statusValidator == null)
            {
                statusValidator = new Thread(this, "statusValidator");
                statusValidator.start();
            }
        }

        public void stop()
        {
            statusValidator.interrupt();
            statusValidator = null;
        }

        public void run()
        {
            for (Thread thread = Thread.currentThread();
                     statusValidator == thread;)
            {
                try
                {
                    // we run validation in a thread so as not to
                    // interfere with repaints of GUI
                    if (hasMon)
                    {
                        labelText = monitor.curStatusString;
                        if (!lastStatus.equals(labelText))
                        {
                            lastStatus = labelText;
                            repaint();
                        }
                    }
                }
                catch (Exception eF)
                {
                    System.out.println("Loading thread was stopped!\n"
                                       + eF.toString());
                }
                finally
                {
                    stop();
                    if (statusValidator != null)
                    {
                        statusValidator.destroy();
                    }
                }
            }
        }
    }
}
