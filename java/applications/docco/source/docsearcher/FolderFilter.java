package docsearcher;

import java.io.File;
import java.io.FilenameFilter;

public class FolderFilter
    implements FilenameFilter
{

    public boolean accept(File directory,
                          String fileName)
    {
        String newFolder =
            directory.toString() + System.getProperty("file.separator")
            + fileName;
        File testFile = new File(newFolder);
        if (testFile.isDirectory())
        {
            return nonFpDir(newFolder);
        }
        return false;
    }

    public boolean nonFpDir(String dirString)
    {
        dirString = dirString.toLowerCase();
        boolean nonFp = true;
        if (dirString.indexOf("_vti") != -1)
        {
            nonFp = false;
        }
        else if (dirString.indexOf("_derived") != -1)
        {
            nonFp = false;
        }
        else if (dirString.indexOf("_private") != -1)
        {
            nonFp = false;
        }
        else if (dirString.indexOf("_themes") != -1)
        {
            nonFp = false;
        }
        else if (dirString.indexOf("_fpclass") != -1)
        {
            nonFp = false;
        }
        else if (dirString.indexOf("_borders") != -1)
        {
            nonFp = false;
        }
        else if (dirString.indexOf("cgi-bin") != -1)
        {
            nonFp = false;
        }
        else if (dirString.indexOf("_overlay") != -1)
        {
            nonFp = false;
        }
        else if (dirString.startsWith("."))
        {
            nonFp = false;
        }
        return nonFp;
    }
}
