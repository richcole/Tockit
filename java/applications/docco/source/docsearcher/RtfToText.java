package docsearcher;

import java.io.*;

public class RtfToText
{
    String fileName;
    String tempFile = "";

    RtfToText(String fileName,
              String tempFile)
    {
        this.fileName = fileName;
        this.tempFile = tempFile;
    }

    public void parse()
        throws IOException
    {
        //javax.swing.text.rtf.RTFParser parser = new javax.swing.text.rtf.RTFParser();
        FileReader reader = null;
        File saveFile = new File(tempFile);
        FileWriter writer = new FileWriter(saveFile);
        try
        {
            reader = new FileReader(fileName);
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }

        javax.swing.text.DefaultStyledDocument doc =
            new javax.swing.text.DefaultStyledDocument();
        try
        {
            new javax.swing.text.rtf.RTFEditorKit().read(reader, doc, 0);
            writer.write(doc.getText(0, doc.getLength()));
        }
        catch (javax.swing.text.BadLocationException ble)
        {
            ble.printStackTrace();
        }
        finally
        {
            writer.close();
        }
    }
}
