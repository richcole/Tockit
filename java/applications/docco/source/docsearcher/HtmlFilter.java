package docsearcher;

import java.io.File;
import java.io.FilenameFilter;

public class HtmlFilter
    implements FilenameFilter
{
    public boolean accept(File directory,
                          String fileName)
    {
        String extension = fileName.toLowerCase();
        int lastDotIndex = extension.lastIndexOf(".");
        if (lastDotIndex != -1)
        {
            extension = extension.substring(lastDotIndex, extension.length());
            if ((extension.equals(".htm")) || (extension.equals(".html"))
                    || (extension.equals(".asp")) || (
                        extension.equals(".txt")
                    ) || (extension.equals(".jsp"))
                    || (extension.equals(".shtm"))
                    || (extension.equals(".shtml"))
                    || (extension.equals(".cfml"))
                    || (extension.equals(".cfm")))
            {
                return true;
            }
            return false;
        }
        return false;
    }
}
