package docsearcher;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class WebFilter
    extends FileFilter
{
    // Accept all directories and all gif, jpg, or tiff files.
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }

        String extension = f.toString().toLowerCase();
        int lastDotIndex = extension.lastIndexOf(".");
        if (lastDotIndex != -1)
        {
            extension = extension.substring(lastDotIndex, extension.length());
            if ((extension.equals(".htm")) || (extension.equals(".html"))
                    || (extension.equals(".shtm"))
                    || (extension.equals(".jsp")) || (
                        extension.equals(".asp")
                    ) || (extension.equals(".cfm"))
                    || (extension.equals(".cfml"))
                    || (extension.equals(".shtml")))
            {
                return true;
            }
            return false;
        }
        return false;
    }

    // The description of this filter
    public String getDescription()
    {
        return "Just  Web Pages";
    }
}
