package docsearcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZippHandler
{
    final static FolderFilter ff = new FolderFilter();
    final int BUFFER = 2048;
    String fileName;
    String archiveFromDir;
    String pathSep = System.getProperty("file.separator");
    String archiveFolderNameOnly = "";

    ZippHandler(String fileName,
                String archiveFromDir)
    {
        this.fileName = fileName;
        this.archiveFromDir = archiveFromDir;
    }

    public void zip()
        throws IOException
    {
        archiveFolderNameOnly = Utils.getFolderOnly(archiveFromDir);
        System.out.println("Archiving Directory (Recursively) "
                           + archiveFromDir + " to archive " + fileName);
        FileOutputStream dest = new FileOutputStream(fileName);
        BufferedInputStream origin = null;
        ZipOutputStream out =
            new ZipOutputStream(new BufferedOutputStream(dest));
        byte[] data = new byte[BUFFER];
        int count = 0;

        // get a list of files from current directory
        ArrayList folderList = new ArrayList();
        folderList.add(archiveFromDir);
        String curFolderString = "";
        int curItemNo = 0;
        int lastFoldNum = 0;
        int numFolders = 0;
        int numFiles = 0;
        File curFolderFile;
        String newFoundFolder = "";
        String[] foldersString;
        String[] filesString;
        String curFi = "";
        String curZipName = "";
        File testDir;
        try
        {
            do
            {
                curFolderString = (String)folderList.get(curItemNo);
                curFolderFile = new File(curFolderString);

                // handle any subfolders --> add them to our folderlist
                foldersString = curFolderFile.list(ff);
                numFolders = foldersString.length;

                // add our folders into the array
                for (int i = 0; i < numFolders; i++)
                {
                    newFoundFolder =
                        Utils.addFolder(curFolderString, foldersString[i]);
                    folderList.add(newFoundFolder);
                    lastFoldNum++;
                }
                 // end for adding folders

                filesString = curFolderFile.list();
                numFiles = filesString.length;
                for (int i = 0; i < numFiles; i++)
                {
                    // add them to our folderlist
                    curFi = curFolderString + pathSep + filesString[i];
                    curFi = Utils.replaceAll(pathSep + pathSep, curFi, pathSep);

                    // ADD THE FILE TO OUR ARCHIVE
                    testDir = new File(curFi);
                    if (!testDir.isDirectory())
                    {
                        FileInputStream fi = new FileInputStream(curFi);
                        origin = new BufferedInputStream(fi, BUFFER);
                        curZipName = getZipName(curFi, curFolderString);
                        System.out.println("Adding " + curZipName);
                        ZipEntry entry = new ZipEntry(curZipName);
                        out.putNextEntry(entry);
                        while ((count = origin.read(data, 0, BUFFER)) != -1)
                        {
                            out.write(data, 0, count);
                        }

                        origin.close();
                    }
                }
                 
                curItemNo++;
            }
            while (curItemNo <= lastFoldNum);

            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getZipName(String file,
                             String folder)
    {
        String tempString = "";
        int charsInFolder = folder.length();
        int charsInStartFolder = archiveFromDir.length();
        int charsInFile = file.length();
        if (charsInFolder >= charsInStartFolder)
        {
            // return the differend
            tempString = file.substring(charsInStartFolder + 1, charsInFile);

            return tempString;
        }
        else
        {
            return file;
        }
    }

    public static void main(String[] args)
    {
        String zipArchive = args[0];
        String target = args[1];
        if ((zipArchive != null) && (target != null))
        {
            ZippHandler zh = new ZippHandler(args[0], args[1]);
            try
            {
                zh.zip();
            }
            catch (Exception eR)
            {
                System.out.println(eR.toString());
            }
        }
        else
        {
            System.out.println("usage :\njava ZippHanlder zip_File target_Dir");
        }
    }
}
