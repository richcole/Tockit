package docsearcher;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.poifs.eventfilesystem.POIFSReader;

public class WordProps
{
    String fileName;
    String tempFile;
    String author;
    String title;
    String keyWords = "";
    MyPOIFSReaderListener mpfsrl;

    WordProps(String fileName,
              String tempFile)
    {
        this.fileName = fileName;
        this.tempFile = tempFile;
    }

    public void getProps()
        throws IOException
    {
        POIFSReader r = new POIFSReader();
        mpfsrl = new MyPOIFSReaderListener();
        r.registerListener(mpfsrl, "\005SummaryInformation");
        r.read(new FileInputStream(fileName));

        //  
        title = mpfsrl.title;
        author = mpfsrl.author;
        keyWords = mpfsrl.keyWords;
        WordToText td = new WordToText(fileName, tempFile);
        td.getText();
    }

    public static void main(String[] args)
        throws IOException
    {
        final String filename = args[0];
        POIFSReader r = new POIFSReader();
        r.registerListener(new MyPOIFSReaderListener(), "\005SummaryInformation");
        r.read(new FileInputStream(filename));

        // create temp file of content
        String curDir = System.getProperty("user.dir");
        String pathSep = System.getProperty("file.separator");
        String tempF = curDir + pathSep + "temp.txt";

        //   
        WordToText td = new WordToText(filename, tempF);
        td.getText();
    }
}
