package docsearcher;

import java.util.*;

public class DocSearcherIndex
{
    boolean shouldBeSearched = false;
    String desc = "";
    String path = "";
    String indexerPath = "";
    int depth = 0;
    String lastIndexed = "";
    boolean isWeb = false;
    String match = "";
    String replace = "";
    String archiveDir = "";
    int indexPolicy = 0;
    boolean isAnImport = false;

    DocSearcherIndex(String path,
                     String desc,
                     boolean shouldBeSearched,
                     int depth,
                     String indexerPath,
                     boolean isWeb,
                     String match,
                     String replace,
                     String lastIndexed,
                     int indexPolicy,
                     String archiveDir)
    {
        this.archiveDir = archiveDir;
        this.indexPolicy = indexPolicy;
        this.isWeb = isWeb;
        this.match = match;
        this.replace = replace;
        this.shouldBeSearched = shouldBeSearched;
        this.path = path;
        this.depth = depth;
        this.desc = desc;
        this.indexerPath = indexerPath;
        this.lastIndexed = lastIndexed;
    }

    DocSearcherIndex(String path,
                     String desc,
                     boolean shouldBeSearched,
                     int depth,
                     String indexerPath,
                     boolean isWeb,
                     String match,
                     String replace,
                     int indexPolicy,
                     String archiveDir)
    {
        this.archiveDir = archiveDir;
        this.indexPolicy = indexPolicy;
        this.isWeb = isWeb;
        if (!isWeb)
        {
            this.match = "na";
            this.replace = "na";
        }
        else
        {
            this.match = match;
            this.replace = replace;
        }

        this.shouldBeSearched = shouldBeSearched;
        this.path = path;
        if (path.toLowerCase().endsWith(".zip"))
        {
            isAnImport = true;
        }
         // end for an imported zip archive
        else
        {
            isAnImport = false;
        }

        this.depth = depth;
        this.desc = desc;
        this.indexerPath = indexerPath;

        // calendar info
        Calendar nowD = Calendar.getInstance();
        String mon = "" + (nowD.get(Calendar.MONTH) + 1);
        String year = "" + (nowD.get(Calendar.YEAR));
        String day = "" + nowD.get(Calendar.DAY_OF_MONTH);
        this.lastIndexed = mon + "/" + day + "/" + year;
    }

}
