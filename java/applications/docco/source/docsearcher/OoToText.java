package docsearcher;

import java.io.*;
import java.util.zip.*;

public class OoToText
{
    final int BUFFER = 2048;
    String content = "content.xml";
    String meta = "meta.xml";
    String fileName;
    String tempFile;
    String metaFile = "";

    OoToText(String fileName,
             String tempFile,
             String metaFile)
    {
        this.fileName = fileName;
        this.tempFile = tempFile;
        this.metaFile = metaFile;
    }

    public void parse()
        throws IOException
    {
        // 1st convert the file to uncompressed xml
        System.out.println("Parsing " + fileName);
        FileOutputStream fos;
        BufferedOutputStream dest;
        FileInputStream fis = new FileInputStream(fileName);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        int count;
        String entryName = "";
        byte[] data = new byte[BUFFER];
        try
        {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                entryName = entry.getName();
                if (entryName.endsWith(content))
                {
                    System.out.println("Extracting:" + entryName);
                    fos = new FileOutputStream(tempFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1)
                    {
                        dest.write(data, 0, count);
                    }

                    dest.flush();
                    dest.close();
                }
                else if (entryName.endsWith(meta))
                {
                    System.out.println("Extracting:" + entryName);
                    fos = new FileOutputStream(metaFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1)
                    {
                        dest.write(data, 0, count);
                    }

                    dest.flush();
                    dest.close();
                }
                else
                {
                    System.out.println("zip entry:" + entryName);
                }
            }

            zis.close();
        }
        catch (Exception eZ)
        {
            eZ.printStackTrace();
        }
        finally
        {
            // debug output?
        }
    }
}
