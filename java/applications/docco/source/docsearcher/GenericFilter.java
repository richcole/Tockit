package docsearcher;

import java.io.File;
import java.io.FilenameFilter;

public class GenericFilter
    implements FilenameFilter
{
    public boolean accept(File directory,
                          String fileName)
    {
        File tf = new File(directory, fileName);
        if (tf.isDirectory())
        {
            return false;
        }
        else
        {
            String extension = fileName.toLowerCase();
            int lastDotIndex = extension.lastIndexOf(".");
            if (lastDotIndex != -1)
            {
                extension =
                    extension.substring(lastDotIndex, extension.length());
                if ((extension.equals(".htm")) || (extension.equals(".html"))
                        || (extension.equals(".asp"))
                        || (extension.equals(".txt"))
                        || (extension.equals(".java"))
                        || (extension.equals(".jsp"))
                        || (extension.equals(".shtm"))
                        || (extension.equals(".shtml"))
                        || (extension.equals(".cfml"))
                        || (extension.equals(".doc"))
                        || (extension.equals(".xls"))
                        || (extension.equals(".rtf"))
                        || (extension.equals(".pdf"))
                        || (extension.equals(".sxw"))
                        || (extension.equals(".sxp"))
                        || (extension.equals(".sxi"))
                        || (extension.equals(".sxd"))
                        || (extension.equals(".sxc"))
                        || (extension.equals(".cfm")))
                {
                    return true;
                }
                return false;
            }
            return false;
        }
    }
}
