package docsearcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

public class Index
{
    DocSearch ds;

    public Index(DocSearch ds)
    {
        this.ds = ds;
    }

    public int addDocToIndex(String currentFi,
                             IndexWriter writer,
                             DocSearcherIndex di)
    {
        int returnInt = 0; // 0  =  OK, 1 =  failed, 2 = meta robots = noindex....
        String metaRobot = "";
        boolean thisErr = false;
        synchronized (this)
        {
            try
            {
                boolean isWord = false;
                boolean isExcel = false;
                boolean isPdf = false;
                boolean isRtf = false;
                boolean isOo = false;

                //
                Document doc;
                String urlStr = "";
                String lowerType = "";
                String author = "";
                String keyWords = "";
                String curType = "";
                String curTitle = "";
                String dateStr;
                String curSummary = "";
                File curFile = new File(currentFi);
                long curFileSize = curFile.length();
                int typeInt = 0;

                // get an iterator to iterate over our files
                InputStream is; // for our file
                curType = getFileType(currentFi);
                lowerType = curType.toLowerCase();
                typeInt = Utils.getTypeInt(lowerType);
                dateStr =
                    DateTimeUtils.getTimeStringForIndex(curFile.lastModified());
                if (di.isWeb)
                {
                    urlStr = Utils.getURL(currentFi, di.replace, di.match);
                }

                isWord = Utils.isWord(currentFi);
                isPdf = Utils.isPDF(currentFi);
                isRtf = Utils.isRtf(currentFi);
                isOo = Utils.isOo(currentFi);
                isExcel = Utils.isExcel(currentFi);
                if (isWord)
                {
                    WordProps wp = new WordProps(currentFi, ds.wordTextFile);
                    wp.getProps();
                    curTitle = wp.title;
                    keyWords = wp.keyWords;
                    author = wp.author;
                    curSummary = getDocSummary(ds.wordTextFile, typeInt);
                }
                else if (isExcel)
                {
                    ExcelProps ep = new ExcelProps(currentFi, ds.excelTextFile);
                    ep.getProps();
                    curTitle = ep.title;
                    keyWords = ep.keyWords;
                    author = ep.author;
                    curSummary = getDocSummary(ds.excelTextFile, typeInt);
                }
                 // end for excell
                else if (isPdf)
                {
                    PdfToText pp = new PdfToText(currentFi, ds.pdfTextFile);
                    pp.parse();
                    curSummary = getDocSummary(ds.pdfTextFile, typeInt);
                    author = pp.author;
                }
                else if (Utils.isRtf(currentFi))
                {
                    RtfToText rp = new RtfToText(currentFi, ds.rtfTextFile);
                    rp.parse();
                    curSummary = getDocSummary(ds.rtfTextFile, typeInt);
                }
                else if (isOo)
                {
                    OoToText op =
                        new OoToText(currentFi, ds.ooTextFile, ds.ooMetaTextFile);
                    op.parse();
                    author = getTagText("dc:creator", ds.ooMetaTextFile);
                    removeAllTags(ds.ooTextFile, ds.ooTextOnlyFile);
                    curSummary = getDocSummary(ds.ooTextOnlyFile, typeInt);
                    curTitle = getDocTitle(ds.ooTextOnlyFile, typeInt);
                }

                //
                doc = new Document();
                if (isWord)
                {
                    is = new FileInputStream(ds.wordTextFile);
                }
                else if (isExcel)
                {
                    is = new FileInputStream(ds.excelTextFile);
                }
                else if (isPdf)
                {
                    is = new FileInputStream(ds.pdfTextFile);
                }
                else if (isRtf)
                {
                    is = new FileInputStream(ds.rtfTextFile);
                }
                else if (isOo)
                {
                    is = new FileInputStream(ds.ooTextOnlyFile);
                }
                else
                {
                    is = new FileInputStream(currentFi);
                    curTitle = getDocTitle(currentFi, typeInt);
                    curSummary = getDocSummary(currentFi, typeInt);
                    if (typeInt == 0)
                    {
                        author = getAuthor(currentFi);
                    }
                }

                if (curTitle != null)
                {
                }
                else
                {
                    curTitle = "";
                }

                if (author != null)
                {
                }
                else
                {
                    author = "";
                }

                if (keyWords != null)
                {
                }
                else
                {
                    keyWords = "";
                }

                if (curSummary != null)
                {
                }
                else
                {
                    curSummary = "";
                }

                if (curTitle.trim().equals(""))
                {
                    curTitle = Utils.getNameOnly(currentFi);
                }

                if ((typeInt == 1)
                        && (curSummary.toLowerCase().indexOf("noindex") != -1))
                {
                    // if its a text document with NOINDEX in the start of the text - don't index it
                    ds.setStatus("Document " + currentFi
                                 + " PREFERS no indexing.");
                    returnInt = 2;
                }
                else if (typeInt == 0)
                {
                    // web page - check for meta name = robots content = noindex...
                    metaRobot = getMetaTag(currentFi, "robots");
                    if (metaRobot.toLowerCase().indexOf("noindex") != -1)
                    {
                        thisErr = true;
                        ds.setStatus("Document " + currentFi
                                     + " PREFERS no indexing.");
                        returnInt = 2;
                    }
                }

                if (returnInt != 2)
                {
                    doc.add(Field.UnIndexed("path", currentFi));
                    doc.add(Field.Keyword("size", "" + curFileSize));
                    doc.add(Field.Text("type", lowerType));
                    doc.add(Field.Text("author", author));
                    doc.add(Field.Text("mod_date", dateStr));
                    doc.add(Field.Text("keywords", keyWords));
                    doc.add(Field.Text("title", curTitle));
                    doc.add(Field.Text("summary", curSummary));
                    doc.add(Field.Text("body", (Reader)new InputStreamReader(is)));
                    doc.add(Field.Text("URL", urlStr));
                    System.out.println("Adding file: " + currentFi
                                       + "\n\ttype: " + lowerType
                                       + "\n\tTitle: " + curTitle
                                       + "\n\tSize: " + curFileSize
                                       + "\n\tSummary: " + curSummary
                                       + "\n\tDate:" + dateStr + "\n\tAuthor:"
                                       + author);
                    writer.addDocument(doc);
                }
                else
                {
                    ds.setStatus("DOCUMENT " + currentFi
                                 + " WAS NOT ADDED TO INDEX.");
                }

                is.close();
            }
            catch (Exception aE)
            {
                ds.setStatus("Error indexing " + currentFi + ":"
                             + aE.toString());
                returnInt = 1;
            }
            finally
            {
                return returnInt;
            }
        }
    }

    public int indexNum(int lastFound,
                        String fileName,
                        IndexReader ir)
    {
        int returnInt = -1;
        synchronized (this)
        {
            if (lastFound == -1)
            {
                lastFound = 0;
            }

            try
            {
                Document doc;
                String compareName = "";
                int numDocs = ir.maxDoc();
                for (int i = lastFound; i < numDocs; i++)
                {
                    if (!ir.isDeleted(i))
                    {
                        doc = ir.document(i);
                        if (doc != null)
                        {
                            compareName = doc.get("path");

                            //System.out.println("Comparing "+compareName+" to "+fileName);
                            if (compareName.equals(fileName))
                            {
                                //System.out.println("MATCH FOUND AT "+i);
                                returnInt = i;

                                break;
                            }
                        }
                    }
                }

                if (returnInt == -1)
                {
                    for (int i = lastFound; i > 0; i--)
                    {
                        if (!ir.isDeleted(i))
                        {
                            doc = ir.document(i);
                            if (doc != null)
                            {
                                compareName = doc.get("path");

                                //System.out.println("Comparing "+compareName+" to "+fileName);
                                if (compareName.equals(fileName))
                                {
                                    //System.out.println("MATCH FOUND AT "+i);
                                    returnInt = i;

                                    break;
                                }
                            }
                        }
                    }
                }

                if (returnInt == -1)
                {
                    ds.setStatus("File " + fileName + " not found in index!");
                }
            }
            catch (Exception eI)
            {
                ds.setStatus("Error determining if doc is already in index!");
                eI.printStackTrace();
            }
            finally
            {
                return returnInt;
            }
        }
    }

    public void updateIndex(DocSearcherIndex di)
    {
        int errNum = 0;
        StringBuffer noRobotsBuf = new StringBuffer();
        int numNoIndex = 0;
        int numErrors = 0;
        StringBuffer failedBuf = new StringBuffer();
        int addedSuccessFully = 0;
        failedBuf.append("\n");
        synchronized (this)
        {
            if (!di.path.toLowerCase().endsWith(".zip"))
            { // not a zip archive
                int numUpdates = 0;
                int numRemovals = 0;
                int numNew = 0;
                try
                {
                    IndexWriter iw;
                    IndexReader ir = IndexReader.open(di.indexerPath);
                    int numDocs = ir.maxDoc();
                    System.out.println("there are " + numDocs
                                       + " docs in index " + di.desc + "("
                                       + di.path + ")");
                    ArrayList allDocsInIndex = new ArrayList(); // indexed files
                    ArrayList allDocsInFolder = new ArrayList(); // current files
                    ArrayList newDocsToAdd = new ArrayList(); // files to be added that are new
                    String curFiModDate;
                    String realFileModDate;
                    File testFi;
                    String curFiName = "";
                    Document doc;
                    for (int i = 0; i < numDocs; i++)
                    {
                        if (!ir.isDeleted(i))
                        {
                            doc = ir.document(i);
                            if (doc != null)
                            {
                                curFiName = doc.get("path");
                                curFiModDate = doc.get("mod_date");
                                testFi = new File(curFiName);
                                if (testFi.exists())
                                {
                                    allDocsInIndex.add(curFiName);
                                    realFileModDate =
                                        DateTimeUtils.getTimeStringForIndex(testFi
                                                                            .lastModified());
                                    if (realFileModDate.equals(curFiModDate))
                                    {
                                        //System.out.println(curFiName+" does NOT need to be updated.");
                                    }
                                     // end for modified dates differ!
                                    else
                                    {
                                        System.out.println("TEMPORARILY REMOVING: "
                                                           + curFiName);
                                        numUpdates++;
                                        ir.delete(i); // remove document
                                        System.out.println("-Document removed...");
                                        ir.close();

                                        // open writer to add document once again
                                        ds.setStatus("REINDEXING: " + curFiName);
                                        iw = new IndexWriter(di.indexerPath,
                                                             new StandardAnalyzer(),
                                                             false);
                                        addedSuccessFully =
                                            addDocToIndex(curFiName, iw, di);
                                        iw.close();

                                        // reopen
                                        ir = IndexReader.open(di.indexerPath);
                                        switch (addedSuccessFully)
                                        {
                                        case 1: // error
                                            errNum++;
                                            if (errNum < 8)
                                            {
                                                failedBuf.append("\n");
                                                failedBuf.append(curFiName);
                                            }

                                            ds.setStatus("Error Indexing "
                                                         + curFiName);

                                            break;

                                        case 2: // meta robots = noindex
                                            numNoIndex++;
                                            if (numNoIndex < 8)
                                            {
                                                noRobotsBuf.append("\n");
                                                noRobotsBuf.append(curFiName);
                                            }

                                            ds.setStatus("No Indexing Meta Requirement found in : "
                                                         + curFiName);

                                            break;

                                        default: // OK
                                            numUpdates++;
                                            ds.setStatus("Indexing "
                                                         + curFiName
                                                         + " complete.");

                                            break;
                                        } // end of switch
                                    }
                                }
                                 // end for file exists
                                else
                                {
                                    ds.setStatus("DELETING: " + curFiName);
                                    ir.delete(i);
                                    numRemovals++;
                                }
                                 // end for file not found
                            }
                             // end for doc is not null
                        }
                         // end for not deleted

                        //else System.out.println("Document was null or deleted:"+i);
                    }
                     // end for getting gocs

                    // now add the new files
                    ArrayList folderList = new ArrayList();
                    folderList.add(di.path);
                    int lastFound = 0;
                    int startSubNum = Utils.countSLash(di.path);
                    int maxSubNum = startSubNum + di.depth;
                    String curFolderString = "";
                    String curFold;
                    String[] foldersString;
                    int numFolders = 0;
                    File curFolderFile;
                    int curSubNum = 0;
                    int lastItemNo = 0;
                    int curItemNo = 0;
                    int curSize = 1;
                    String[] filesString;
                    int numFiles = 0;
                    String curFi = "";
                    do
                    {
                        // create our folder file
                        curFolderString = (String)folderList.get(curItemNo);
                        curFolderFile = new File(curFolderString);
                        curSubNum = Utils.countSLash(curFolderString);

                        // handle any subfolders --> add them to our folderlist
                        foldersString = curFolderFile.list(DocSearch.ff);
                        numFolders = foldersString.length;
                        for (int i = 0; i < numFolders; i++)
                        {
                            // add them to our folderlist
                            curFold =
                                curFolderString + DocSearch.pathSep
                                + foldersString[i] + DocSearch.pathSep;
                            curFold =
                                Utils.replaceAll(DocSearch.pathSep
                                                 + DocSearch.pathSep, curFold,
                                                 DocSearch.pathSep);
                            folderList.add(curFold);
                            lastItemNo++;

                            // debug output
                            //System.out.println("Found folder "+curFold);
                        }
                         // end for having more than 0 folder

                        // add our files
                        filesString = curFolderFile.list(DocSearch.wf);
                        numFiles = filesString.length;
                        for (int i = 0; i < numFiles; i++)
                        {
                            // add them to our folderlist
                            curFi =
                                curFolderString + DocSearch.pathSep
                                + filesString[i];
                            curFi =
                                Utils.replaceAll(DocSearch.pathSep
                                                 + DocSearch.pathSep, curFi,
                                                 DocSearch.pathSep);
                            lastFound = indexNum(lastFound, curFi, ir);
                            if (lastFound == -1)
                            {
                                ir.close();

                                // open writer to add document once again
                                iw = new IndexWriter(di.indexerPath,
                                                     new StandardAnalyzer(),
                                                     false);
                                System.out.println("Please wait: Indexing NEW Document("
                                                   + curFi + ") - file # "
                                                   + curSize);
                                addedSuccessFully =
                                    addDocToIndex(curFi, iw, di);
                                switch (addedSuccessFully)
                                {
                                case 1: // error
                                    errNum++;
                                    if (errNum < 8)
                                    {
                                        failedBuf.append("\n");
                                        failedBuf.append(curFi);
                                    }

                                    ds.setStatus("Error Indexing : " + curFi);

                                    break;

                                case 2: // meta robots = noindex
                                    numNoIndex++;
                                    if (numNoIndex < 8)
                                    {
                                        noRobotsBuf.append("\n");
                                        noRobotsBuf.append(curFi);
                                    }

                                    ds.setStatus("Document Exlusion (robots = NOINDEX) : "
                                                 + curFi);

                                    break;

                                default: // OK
                                    numNew++;
                                    ds.setStatus("New Document Added : "
                                                 + curFi);

                                    break;
                                } // end of switch

                                curSize++;
                                iw.close();

                                // reopen
                                ir = IndexReader.open(di.indexerPath);
                            }
                        }
                         // end for having more than 0 folder

                        // increment our curItem
                        folderList.set(curItemNo, null); // remove memory overhead as you go!
                        curItemNo++;
                        if (curSubNum >= maxSubNum)
                        {
                            break;
                        }
                    }
                    while (curItemNo <= lastItemNo);

                    //
                    ir.close(); // always close!
                    StringBuffer updateMSGBuf = new StringBuffer();
                    updateMSGBuf.append("\n");
                    updateMSGBuf.append(numRemovals);
                    updateMSGBuf.append(" files were removed from index.");
                    updateMSGBuf.append("\n");
                    updateMSGBuf.append(numUpdates);
                    updateMSGBuf.append(" files were reindexed.");
                    updateMSGBuf.append("\n");
                    updateMSGBuf.append(numNew);
                    updateMSGBuf.append(" new files were added to the index.");
                    updateMSGBuf.append("\n");
                    if (errNum == 0)
                    {
                        updateMSGBuf.append("No errors were encountered during this process.");
                        if (numNoIndex > 0)
                        {
                            updateMSGBuf.append("\n\n" + numNoIndex
                                                + " files were not indexed due to meta data constraints (robots = NOINDEX), including:\n");
                            updateMSGBuf.append(noRobotsBuf.toString());
                        }

                        ds.showMessage("Update of index " + di.desc
                                       + " Completed", updateMSGBuf.toString());
                    }
                    else
                    {
                        updateMSGBuf.append("Errors were encountered during this process.\nThe following files had problems being indexed or re-indexed:\n"
                                            + failedBuf.toString());
                        if (numNoIndex > 0)
                        {
                            updateMSGBuf.append("\n\n" + numNoIndex
                                                + " files were not indexed due to meta data constraints (robots = NOINDEX), including:\n");
                            updateMSGBuf.append(noRobotsBuf.toString());
                        }

                        ds.showMessage("Errors during Update of index "
                                       + di.desc, updateMSGBuf.toString());
                    }
                }
                 // end of try
                catch (Exception eR)
                {
                    ds.showMessage("Error updating index " + di.desc,
                                   eR.toString());
                    eR.printStackTrace();
                }
                finally
                {
                    di.lastIndexed = DateTimeUtils.getToday();
                    ds.setStatus("Update of index " + di.desc + " completed.");
                }
            }
             // end if index is now from a zip archive
            else
            {
                ds.doZipArchiveUpdate(di);
            }
        }
    }

    private String getFileType(String fileName)
    {
        String returnString = "unknown";
        int fileTypeEnding = fileName.lastIndexOf(".");
        if (fileTypeEnding != -1)
        {
            returnString =
                fileName.substring(fileTypeEnding + 1, fileName.length());
        }

        return returnString;
    }

    private String getDocSummary(String fileName,
                                 int type)
    {
        String returnString = "Untitled";
        switch (type)
        {
        case 0: // HTML
            returnString = getHtmlSummary(fileName);

            break;

        case 1: // text
            returnString = getTextSummary(fileName);

            break;

        default: // all other
            returnString = getTextSummary(fileName);

            break;
        } // end for type

        return returnString;
    }

    private String getTitle(String fileName)
    {
        int lastSlash = fileName.lastIndexOf(DocSearch.pathSep);
        boolean foundFileTitle = false;
        String newTitle = "Untitled";
        int fileLen = fileName.length();
        int fileTypeEnding = fileName.lastIndexOf(".");
        if (lastSlash != -1)
        {
            lastSlash++;
            if (fileTypeEnding > lastSlash)
            {
                newTitle = fileName.substring(lastSlash, fileTypeEnding);
            }
            else
            {
                newTitle = fileName.substring(lastSlash, fileLen);
            }
        }
        else
        {
            lastSlash = fileName.lastIndexOf("\\");
            if (lastSlash != -1)
            {
                lastSlash++;
                if (fileTypeEnding > lastSlash)
                {
                    newTitle = fileName.substring(lastSlash, fileTypeEnding);
                }
                else
                {
                    newTitle = fileName.substring(lastSlash, fileLen);
                }
            }
             // end for windows file or URL
        }

        if (newTitle.length() != 0)
        {
            newTitle = Utils.replaceAll("_", newTitle, " ").trim();
            foundFileTitle = true;
        }

        if (!foundFileTitle)
        {
            return fileName;
        }

        return newTitle;
    }

    public String getTextSummary(String fileName)
    {
        String returnString = "No Summary";
        File file = new File(fileName);
        try
        {
            FileInputStream fi = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fi);
            Reader in = new BufferedReader(isr);
            int ch;
            int curI = 0; // reset i
            char curChar = ' ';
            byte curBint;
            int maxTitleLen = 286;
            int curCharNum = 0;
            char lastChar = ' ';
            boolean skipChar = false;
            int numLines = 0;
            StringBuffer titleBuf = new StringBuffer();
            while ((ch = in.read()) > -1)
            {
                curChar = (char)ch;

                // append to our title
                skipChar = false;
                if ((curChar == '\n') || (curChar == '\r'))
                {
                    curChar = ' ';
                }

                if ((curChar == ' ') && (lastChar == ' '))
                {
                    skipChar = true;
                }

                if (!skipChar)
                {
                    lastChar = curChar;
                    curCharNum++;
                    titleBuf.append(curChar);
                }

                if ((numLines > 3) || (curCharNum > maxTitleLen))
                {
                    break;
                }
            }

            String newTitle = titleBuf.toString().trim();
            if (newTitle.length() >= 4)
            {
                returnString = newTitle + "...";
            }
            else
            {
                returnString = getTitle(fileName);
            }

            fi.close();
            in.close();
        }
         // end of trying to catch file
        catch (Exception eF)
        {
            ds.setStatus("Error obtaining file title: " + fileName);
        }
        finally
        {
            return returnString;
        }
    }

    private String getHtmlSummary(String fileName)
    {
        String returnString = getMetaTag(fileName, "description");
        if (returnString.equals(""))
        {
            returnString = getMetaTag(fileName, "summary");
        }

        return returnString;
    }

    private String getMetaTag(String fileName,
                              String metaTag)
    {
        String lowerMetaTag = metaTag.toLowerCase();
        String returnString = "";
        File file = new File(fileName);
        try
        {
            FileInputStream fi = new FileInputStream(file);
            int curI = 0; // reset i
            char curChar = ' ';
            byte curBint;
            int maxTitleLen = 36;
            int curCharNum = 0;
            char lastChar = ' ';
            boolean skipChar = false;
            int numLines = 0;
            StringBuffer tagBuf = new StringBuffer();
            boolean inTag = false;
            boolean inTitle = false;
            String tagString = "";
            String lowerTag = "";
            String attr = "";
            String lowerAttr = "";
            while (curI != -1)
            {
                curI = fi.read();
                if (curI != -1)
                {
                    curChar = (char)curI;

                    // append to our title
                    skipChar = false;
                    if ((curChar == '\n') || (curChar == '\r'))
                    {
                        curChar = ' ';
                    }

                    curCharNum++;
                    if (curChar == '<')
                    {
                        inTag = true;
                    }

                    if (curChar == '>')
                    {
                        tagBuf.append(curChar);
                        inTag = false;
                        tagString = tagBuf.toString();
                        lowerTag = tagString.toLowerCase();
                        if (lowerTag.startsWith("<meta"))
                        {
                            attr = getTagString("name = ", tagString);
                            lowerAttr = attr.toLowerCase().trim();
                            if (lowerAttr.equals(lowerMetaTag))
                            {
                                returnString =
                                    getTagString("content = ", tagString);
                                System.out.println(fileName + " has "
                                                   + returnString + "\n for a "
                                                   + lowerMetaTag);

                                break;
                            }
                        }

                        if (lowerTag.startsWith("<body"))
                        {
                            break;
                        }

                        tagBuf = new StringBuffer();
                    }

                    if ((curChar == ' ') && (lastChar == ' '))
                    {
                        skipChar = true;
                    }

                    if (!skipChar)
                    {
                        lastChar = curChar;
                    }

                    if (inTag)
                    {
                        tagBuf.append(curChar);
                    }
                }
                else
                {
                    break;
                }
            }

            fi.close();
        }
        catch (Exception eF)
        {
            ds.setStatus("Error obtaining file author: " + fileName);
        }
        finally
        {
            return returnString;
        }
    }

    private String getAuthor(String fileName)
    {
        String returnString = "";
        File file = new File(fileName);
        try
        {
            FileInputStream fi = new FileInputStream(file);
            int curI = 0; // reset i
            char curChar = ' ';
            byte curBint;
            int maxTitleLen = 36;
            int curCharNum = 0;
            char lastChar = ' ';
            boolean skipChar = false;
            int numLines = 0;
            StringBuffer tagBuf = new StringBuffer();
            boolean inTag = false;
            boolean inTitle = false;
            String tagString = "";
            String lowerTag = "";
            String attr = "";
            String lowerAttr = "";
            while (curI != -1)
            {
                curI = fi.read();
                if (curI != -1)
                {
                    curChar = (char)curI;

                    // append to our title
                    skipChar = false;
                    if ((curChar == '\n') || (curChar == '\r'))
                    {
                        curChar = ' ';
                    }

                    curCharNum++;
                    if (curChar == '<')
                    {
                        inTag = true;
                    }

                    if (curChar == '>')
                    {
                        tagBuf.append(curChar);
                        inTag = false;
                        tagString = tagBuf.toString();
                        lowerTag = tagString.toLowerCase();
                        if (lowerTag.startsWith("<meta"))
                        {
                            attr = getTagString("name = ", tagString);
                            lowerAttr = attr.toLowerCase();
                            if ((lowerAttr.indexOf("author") != -1)
                                    || (lowerAttr.indexOf("webmaster") != -1))
                            {
                                returnString =
                                    getTagString("content = ", tagString);

                                break;
                            }
                        }

                        if (lowerTag.startsWith("<body"))
                        {
                            break;
                        }

                        tagBuf = new StringBuffer();
                    }

                    if ((curChar == ' ') && (lastChar == ' '))
                    {
                        skipChar = true;
                    }

                    if (!skipChar)
                    {
                        lastChar = curChar;
                    }

                    if (inTag)
                    {
                        tagBuf.append(curChar);
                    }
                }
                else
                {
                    break;
                }
            }

            fi.close();
        }
        catch (Exception eF)
        {
            ds.setStatus("Error obtaining file author: " + fileName);
        }
        finally
        {
            return returnString;
        }
    }

    private String getDocTitle(String fileName,
                               int type)
    {
        String returnString = "Untitled";
        switch (type)
        {
        case 0: // HTML
            returnString = getHtmlTitle(fileName);

            break;

        case 1: // text
            returnString = getTextTitle(fileName);

            break;

        case 6: // oo
            returnString = getTextTitle(fileName);

            break;

        case 7: // oo
            returnString = getTextTitle(fileName);

            break;

        case 8: // oo
            returnString = getTextTitle(fileName);

            break;

        case 9: // oo
            returnString = getTextTitle(fileName);

            break;

        default: // all other
            returnString = getTitle(fileName);

            break;
        } // end for type

        return returnString;
    }

    private String getHtmlTitle(String fileName)
    {
        String returnString = "Untitled";
        File file = new File(fileName);
        try
        {
            FileInputStream fi = new FileInputStream(file);
            int curI = 0; // reset i
            char curChar = ' ';
            byte curBint;
            int maxTitleLen = 36;
            int curCharNum = 0;
            char lastChar = ' ';
            boolean skipChar = false;
            int numLines = 0;
            StringBuffer tagBuf = new StringBuffer();
            boolean inTag = false;
            boolean inTitle = false;
            String tagString = "";
            String lowerTag = "";
            StringBuffer titleBuf = new StringBuffer();
            while (curI != -1)
            {
                curI = fi.read();
                if (curI != -1)
                {
                    curChar = (char)curI;

                    // append to our title
                    skipChar = false;
                    if ((curChar == '\n') || (curChar == '\r'))
                    {
                        curChar = ' ';
                    }

                    curCharNum++;
                    if (curChar == '<')
                    {
                        inTag = true;
                        if (inTitle)
                        {
                            break;
                        }
                    }

                    if (curChar == '>')
                    {
                        tagBuf.append(curChar);
                        inTag = false;
                        tagString = tagBuf.toString();
                        lowerTag = tagString.toLowerCase();
                        if (lowerTag.startsWith("<titl"))
                        {
                            inTitle = true;
                        }
                        else if (lowerTag.startsWith("</head"))
                        {
                            break;
                        }
                        else if (lowerTag.startsWith("<body"))
                        {
                            break;
                        }

                        tagBuf = new StringBuffer();
                    }
                     // end for end of a tag

                    if ((curChar == ' ') && (lastChar == ' '))
                    {
                        skipChar = true;
                    }

                    if (!skipChar)
                    {
                        lastChar = curChar;
                    }

                    if (inTag)
                    {
                        tagBuf.append(curChar);
                    }
                    else if (inTitle)
                    {
                        if ((!skipChar) && (curChar != '>'))
                        {
                            titleBuf.append(curChar);
                        }
                    }
                }
                else
                {
                    break;
                }
            }

            fi.close();
            String newTitle = titleBuf.toString().trim();
            if (newTitle.length() >= 4)
            {
                returnString = newTitle;
            }
        }
         // end of trying to catch file
        catch (Exception eF)
        {
            ds.setStatus("Error obtaining file title: " + fileName);
        }
        finally
        {
            return returnString;
        }
    }

    private String getTextTitle(String fileName)
    {
        String returnString = "Untitled";
        File file = new File(fileName);
        try
        {
            FileInputStream fi = new FileInputStream(file);
            int curI = 0; // reset i
            char curChar = ' ';
            byte curBint;
            int maxTitleLen = 36;
            int curCharNum = 0;
            char lastChar = ' ';
            boolean skipChar = false;
            int numLines = 0;
            StringBuffer titleBuf = new StringBuffer();
            while (curI != -1)
            {
                curI = fi.read();
                if (curI != -1)
                {
                    curChar = (char)curI;

                    // append to our title
                    skipChar = false;
                    if ((curChar == '\n') || (curChar == '\r'))
                    {
                        curChar = ' ';
                    }

                    if ((curChar == ' ') && (lastChar == ' '))
                    {
                        skipChar = true;
                    }

                    if (!skipChar)
                    {
                        lastChar = curChar;
                        curCharNum++;
                        titleBuf.append(curChar);
                    }

                    if ((numLines > 3) || (curCharNum > maxTitleLen))
                    {
                        break;
                    }
                }
                else
                {
                    break;
                }
            }

            String newTitle = titleBuf.toString().trim();
            if (newTitle.length() >= 4)
            {
                returnString = newTitle + "...";
            }
            else
            {
                returnString = getTitle(fileName);
            }

            fi.close();
        }
        catch (Exception eF)
        {
            ds.setStatus("Error obtaining file title: " + fileName);
        }
        finally
        {
            return returnString;
        }
    }

    private void removeAllTags(String originalFile,
                               String newFileName)
        throws IOException
    {
        boolean inTag = false;
        File origFile = new File(originalFile);
        FileInputStream fi = new FileInputStream(origFile);
        InputStreamReader isr = new InputStreamReader(fi);
        FileWriter filewriter = new FileWriter(newFileName);
        PrintWriter pw = new PrintWriter(filewriter);
        StringBuffer tagBuf = new StringBuffer();
        StringBuffer nonTagTextf = new StringBuffer();
        String t = "";
        int curI = 0; // reset i
        byte rB;
        byte curBint;
        char curChar = ' ';
        Reader in = new BufferedReader(isr);
        int ch;
        while ((ch = in.read()) > -1)
        {
            curChar = (char)ch;
            if (curChar == '>')
            {
                inTag = false;
                nonTagTextf = new StringBuffer();
            }
            else if (curChar == '<')
            {
                inTag = true;
                t = nonTagTextf.toString().trim();
                if (t.length() > 0)
                {
                    pw.println(t);
                }
            }

            if ((!inTag) && (curChar != '>'))
            {
                nonTagTextf.append(curChar);
            }
        }

        fi.close();
        in.close();
        filewriter.close();
        pw.close();
    }

    private String getTagText(String tagPrefix,
                              String fileName)
        throws IOException
    {
        tagPrefix = tagPrefix.toLowerCase();
        String tagStart = "<" + tagPrefix;
        String tagEnd = "</" + tagPrefix;
        StringBuffer retBuf = new StringBuffer();
        File origFile = new File(fileName);
        FileInputStream fi = new FileInputStream(origFile);
        int curI = 0; // reset i
        byte rB;
        byte curBint;
        char curChar = ' ';
        StringBuffer tagBuf = new StringBuffer();
        StringBuffer nonTagTextf = new StringBuffer();
        boolean readContent = false;
        boolean inTag = false;
        String tagStr = "";
        while (curI != -1)
        {
            curI = fi.read();
            if (curI != -1)
            {
                curBint = (byte)curI;
                curChar = (char)curI;
                if (curChar == '>')
                {
                    tagStr = tagBuf.toString().toLowerCase();
                    if (tagStr.startsWith(tagStart))
                    {
                        readContent = true;
                    }

                    if (tagStr.startsWith(tagEnd))
                    {
                        retBuf.append(nonTagTextf.toString());
                        System.out.println("Value for " + tagPrefix + " is "
                                           + nonTagTextf.toString() + " in "
                                           + fileName);

                        break;
                    }

                    tagBuf = new StringBuffer();
                    inTag = false;
                }
                else if (curChar == '<')
                {
                    inTag = true;
                }

                if (inTag)
                {
                    tagBuf.append(curChar);
                }
                else if ((readContent) && (curChar != '>'))
                {
                    nonTagTextf.append(curChar);
                }
            }
            else
            {
                break;
            }
        }

        fi.close();

        return retBuf.toString();
    }

    private String getTagString(String toLookFor,
                                String toLookIn)
    {
        String toLookInLower = toLookIn.toLowerCase();
        if (toLookInLower.indexOf(toLookFor) == -1)
        {
            return "";
        }
        else
        {
            boolean firstQFnd = true;
            StringBuffer tempS = new StringBuffer();
            int endPos = toLookIn.length();
            char tC = ' ';
            int startPos =
                toLookInLower.indexOf(toLookFor) + toLookFor.length();
            for (int i = startPos; i < endPos; i++)
            {
                tC = toLookIn.charAt(i);
                if (toLookIn.charAt(i) == '"')
                {
                    if (!firstQFnd)
                    {
                        break;
                    }
                    else
                    {
                        firstQFnd = false;
                    }
                }
                else if (toLookIn.charAt(i) == '>')
                {
                    break;
                }
                else
                {
                    tempS.append(tC);
                }
            }

            if (tempS.toString().trim().equals(""))
            {
                return "";
            }
            else
            {
                return tempS.toString();
            }
        }
    }
}
