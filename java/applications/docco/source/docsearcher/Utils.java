package docsearcher;

public class Utils
{

    public static String replaceAll(String s,
                                    String s1,
                                    String s2)
    {
        while (s1.indexOf(s) != -1)
        {
            int i = s1.indexOf(s);
            if (i >= 1)
            {
                s1 = s1.substring(0, i) + s2
                     + s1.substring(i + s.length(), s1.length());
            }
            else if (i == 0)
            {
                s1 = s2 + s1.substring(i + s.length(), s1.length());
            }
        }

        return s1;
    }

    public static String addFolder(String startFolder,
                                   String addedFolder)
    {
        String returnFolder = "";
        if (startFolder.indexOf(DocSearch.pathSep) != -1)
        {
            returnFolder = startFolder + DocSearch.pathSep + addedFolder;
            returnFolder =
                Utils.replaceAll(DocSearch.pathSep + DocSearch.pathSep,
                                 returnFolder, DocSearch.pathSep);
        }
        else
        {
            returnFolder = startFolder + DocSearch.pathSep + addedFolder;
        }

        return returnFolder;
    }

    public static boolean isText(String link)
    {
        String lowerLink = link.toLowerCase();
        if ((lowerLink.endsWith(".java")) || (lowerLink.endsWith(".txt"))
                || (lowerLink.endsWith(".ini")))
        {
            return true;
        }

        return false;
    }

    public static boolean isRtf(String link)
    {
        String lowerLink = link.toLowerCase();
        if (lowerLink.endsWith(".rtf"))
        {
            return true;
        }

        return false;
    }

    public static boolean isWord(String link)
    {
        String lowerLink = link.toLowerCase();
        if (lowerLink.endsWith(".doc"))
        {
            return true;
        }

        return false;
    }

    public static boolean isExcel(String link)
    {
        String lowerLink = link.toLowerCase();
        if (lowerLink.endsWith(".xls"))
        {
            return true;
        }

        return false;
    }

    public static boolean isPDF(String link)
    {
        String lowerLink = link.toLowerCase();
        if (lowerLink.endsWith(".pdf"))
        {
            return true;
        }

        return false;
    }

    public static boolean isOo(String link)
    {
        String lowerLink = link.toLowerCase();
        if ((lowerLink.endsWith(".sxw")) || (lowerLink.endsWith(".sxc"))
                || (lowerLink.endsWith(".sxi")) || (
                    lowerLink.endsWith(".sxp")
                ) || (lowerLink.endsWith(".sxd")))
        {
            return true;
        }

        return false;
    }

    public static boolean isViewAble(String link)
    {
        String lowerLink = link.toLowerCase();
        if ((lowerLink.endsWith(".htm")) || (lowerLink.endsWith(".html"))
                || (lowerLink.endsWith(".shtm"))
                || (lowerLink.endsWith(".shtml"))
                || (lowerLink.endsWith(".php")) || (
                    lowerLink.endsWith(".asp")
                ) || (lowerLink.endsWith(".txt"))
                || (lowerLink.endsWith(".rtf"))
                || (lowerLink.endsWith(".java"))
                || (lowerLink.endsWith(".jsp")))
        {
            return true;
        }

        return false;
    }

    public static boolean isHtml(String link)
    {
        String lowerLink = link.toLowerCase();
        if ((lowerLink.endsWith(".htm")) || (lowerLink.endsWith(".html"))
                || (lowerLink.endsWith(".shtm"))
                || (lowerLink.endsWith(".shtml"))
                || (lowerLink.endsWith(".php")) || (
                    lowerLink.endsWith(".asp")
                ) || (lowerLink.endsWith(".jsp")))
        {
            return true;
        }

        return false;
    }

    public static int getTypeInt(String lowerType)
    {
        int returnInt = -1;
        if ((lowerType.equals("html")) || (lowerType.equals("htm"))
                || (lowerType.equals("shtml")) || (lowerType.equals("shtm"))
                || (lowerType.equals("asp")) || (lowerType.equals("jsp"))
                || (lowerType.equals("php")))
        {
            returnInt = 0; // HTML
        }
        else if (lowerType.equals("txt"))
        {
            returnInt = 1; // TEXT
        }
        else if (lowerType.equals("doc"))
        {
            returnInt = 2;
        }
        else if (lowerType.equals("xls"))
        {
            returnInt = 3;
        }
        else if (lowerType.equals("pdf"))
        {
            returnInt = 4;
        }
        else if (lowerType.equals("rtf"))
        {
            returnInt = 5;
        }
        else if (lowerType.equals("sxw"))
        {
            returnInt = 6; // ooWriter
        }
        else if ((lowerType.equals("sxi")) || (lowerType.equals("sxp")))
        {
            returnInt = 7; // ooInpress
        }
        else if (lowerType.equals("sxc"))
        {
            returnInt = 8; // ooCalc
        }
        else if (lowerType.equals("sxd"))
        {
            returnInt = 9; // ooDraw
        }

        return returnInt;
    }

    public static String getURL(String fileName,
                                String match,
                                String replace)
    {
        String returnString = "";
        returnString =
            replace + fileName.substring(match.length(), fileName.length());
        returnString = Utils.replaceAll("\\", returnString, "/");

        //System.out.println("Converted file("+fileName+") to "+returnString+"\nmatch:"+match+"\nreplace:"+replace);
        return returnString;
    }

    public static int countSLash(String URLtoCount)
    {
        int returnInt = 0;
        int totalLen = URLtoCount.length();
        int startSpot = URLtoCount.indexOf(DocSearch.pathSep);
        if (startSpot != -1)
        {
            returnInt++;
            do
            {
                startSpot++;
                if (startSpot > totalLen)
                {
                    break;
                }

                startSpot = URLtoCount.indexOf(DocSearch.pathSep, startSpot);
                if (startSpot == -1)
                {
                    break;
                }

                returnInt++;

                //System.out.println("Found slash");
            }
            while (startSpot != -1);
        }

        return returnInt;
    }

    public static String getNameOnly(String fileString)
    {
        if (fileString.indexOf(DocSearch.pathSep) != -1)
        {
            return fileString.substring(fileString.lastIndexOf(DocSearch.pathSep)
                                        + 1, fileString.length());
        }
        else if (fileString.indexOf("\\") != -1)
        {
            return fileString.substring(fileString.lastIndexOf("\\") + 1,
                                        fileString.length());
        }
        else if (fileString.indexOf("/") != -1)
        {
            return fileString.substring(fileString.lastIndexOf("/") + 1,
                                        fileString.length());
        }
        else
        {
            return fileString;
        }
    }

    public static String getFolderOnly(String fileString)
    {
        if (fileString.indexOf(DocSearch.pathSep) != -1)
        {
            return fileString.substring(0,
                                        fileString.lastIndexOf(DocSearch.pathSep));
        }
        else if (fileString.indexOf("\\") != -1)
        {
            return fileString.substring(0, fileString.lastIndexOf("\\"));
        }
        else if (fileString.indexOf("/") != -1)
        {
            return fileString.substring(0, fileString.lastIndexOf("/"));
        }
        else
        {
            System.out.println("No path sep is found in " + fileString);

            return "";
        }
    }
}
