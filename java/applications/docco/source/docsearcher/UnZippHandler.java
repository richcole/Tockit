package docsearcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZippHandler
{
    final int BUFFER = 2048;
    String fileName = "";
    String targetDir = "";
    String pathSep = System.getProperty("file.separator");

    UnZippHandler(String fileName,
                  String targetDir)
    {
        this.fileName = fileName;
        this.targetDir = targetDir;
    }

    public void unZip()
        throws IOException
    {
        // does folder targetDir exist?
        File testTargetDir = new File(targetDir);
        if (!testTargetDir.exists())
        {
            throw new IOException("TARGET DIRECTORY (" + targetDir
                                  + ") DOESN'T EXIST!");
        }
        else
        { // target directory exists
            System.out.println("Unzipping archive: " + fileName
                               + " to directory: " + targetDir);
            int numFilesExtracted = 0;
            int numFolders = 0;
            File curZipFile;
            File curExtractFolderFile;
            String curExtractFolderString = "";
            String newFile = "";
            String curZipFolderString = "";
            String entryFileNameOnly = "";
            FileOutputStream fos;
            BufferedOutputStream dest;
            FileInputStream fis = new FileInputStream(fileName);
            ZipInputStream zis =
                new ZipInputStream(new BufferedInputStream(fis));
            int count;
            boolean madeFolder = false;
            String entryName = "";
            byte[] data = new byte[BUFFER];
            ZipEntry entry;
            try
            {
                while ((entry = zis.getNextEntry()) != null)
                {
                    entryName = entry.getName();
                    entryFileNameOnly = Utils.getNameOnly(entryName);
                    curZipFolderString = Utils.getFolderOnly(entryName);
                    if (!curZipFolderString.equals(""))
                    {
                        curExtractFolderString =
                            Utils.addFolder(targetDir, curZipFolderString);
                    }
                    else
                    {
                        curExtractFolderString = targetDir;
                    }

                    newFile =
                        Utils.addFolder(curExtractFolderString, entryFileNameOnly);
                    if (pathSep.equals("\\"))
                    {
                        newFile = Utils.replaceAll("/", newFile, "\\");
                    }
                    else
                    {
                        newFile = Utils.replaceAll("\\", newFile, "/");
                    }

                    System.out.println("Extracting from zip:" + entryName
                                       + " to " + newFile);
                    curExtractFolderFile = new File(curExtractFolderString);
                    if (!curExtractFolderFile.exists())
                    {
                        madeFolder = curExtractFolderFile.mkdir();
                    }
                    else
                    {
                        madeFolder = true;
                    }

                    if ((madeFolder) && (!entryFileNameOnly.equals("")))
                    {
                        fos = new FileOutputStream(newFile);
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = zis.read(data, 0, BUFFER)) != -1)
                        {
                            dest.write(data, 0, count);
                        }

                        dest.flush();
                        dest.close();
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

    public static void main(String[] args)
    {
        String zipArchive = args[0];
        String target = args[1];
        if ((zipArchive != null) && (target != null))
        {
            UnZippHandler uz = new UnZippHandler(args[0], args[1]);
            try
            {
                uz.unZip();
            }
            catch (Exception eR)
            {
                System.out.println(eR.toString());
            }
        }
        else
        {
            System.out.println("usage :\njava UnZippHanlder zip_File destination_Dir");
        }
    }

}
