package docsearcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.poi.hdf.extractor.WordDocument;

class WordToText
{
    String origFileName;
    String tempFile;
    WordDocument wd;

    WordToText(String origFileName,
               String tempFile)
    {
        this.tempFile = tempFile;
        this.origFileName = origFileName;
    }

    public void getText()
    {
        try
        {
            wd = new WordDocument(origFileName);
            Writer out = new BufferedWriter(new FileWriter(tempFile));
            wd.writeAllText(out);
            out.flush();
            out.close();
        }
        catch (Exception eN)
        {
            System.out.println("Error reading document:" + origFileName + "\n"
                               + eN.toString());
        }
    }
}
