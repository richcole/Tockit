package docsearcher;

import java.io.*;
import org.apache.poi.hssf.eventmodel.*;
import org.apache.poi.poifs.eventfilesystem.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelProps
{
    String fileName;
    String tempFile;
    String author;
    String title;
    String keyWords = "";
    MyPOIFSReaderListener mpfsrl;
    ExcelListener el;

    ExcelProps(String fileName,
               String tempFile)
    {
        this.fileName = fileName;
        this.tempFile = tempFile;
        el = new ExcelListener();
        el.tempFile = tempFile;
    }

    public void getProps()
        throws IOException
    {
        FileInputStream fin;
        try
        {
            POIFSReader r = new POIFSReader();
            mpfsrl = new MyPOIFSReaderListener();
            fin = new FileInputStream(fileName);
            r.registerListener(mpfsrl, "\005SummaryInformation");
            r.read(fin);
            fin.close();
            if (mpfsrl.title != null)
            {
                title = mpfsrl.title;
            }
            else
            {
                title = fileName;
            }

            if (mpfsrl.author != null)
            {
                author = mpfsrl.author;
            }
            else
            {
                author = "";
            }

            if (mpfsrl.keyWords != null)
            {
                keyWords = mpfsrl.keyWords;
            }
            else
            {
                keyWords = "";
            }

            System.out.println("EXCEL FILE:" + fileName + "\ntitle=" + title
                               + "\nauthor=" + author);
        }
        catch (Exception eR)
        {
            System.out.println("excel props failed " + eR.toString());
            title = fileName;
            author = "";
            keyWords = "";
        }

        //  
        try
        {
            // proceed to write to file
            // create a new file input stream with the input file specified
            // at the command line
            fin = new FileInputStream(fileName);
            POIFSFileSystem poifs = new POIFSFileSystem(fin);
            InputStream din = poifs.createDocumentInputStream("Workbook");
            HSSFRequest req = new HSSFRequest();
            req.addListenerForAllRecords(el);
            HSSFEventFactory factory = new HSSFEventFactory();
            factory.processEvents(req, din);
            fin.close();
            din.close();
            System.out.println("EXCEL INDEXING COMPLETE FOR:" + fileName);
        }
        catch (Exception eN)
        {
            System.out.println("Error reading excel file:" + fileName
                               + "\n[err=" + eN.toString() + "]");

            //System.out.println("Excel content:"+el.excelText.toString());
            el.excelText = new StringBuffer();
            eN.printStackTrace();
        }
        finally
        {
            // save the EL stringbuffer	
            saveFile(tempFile, el.excelText);

            //System.out.println("Excel content:"+el.excelText.toString());
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
            System.out.println("File Save Error - Error Reported was:\n"
                               + eF.toString() + "\n\nFor file " + fileName);
            error = true;
        }
         // end for catch

        if (!error)
        {
            System.out.println("Save Successful -File : " + fileName
                               + "\n- was saved .");
        }
    }
}
