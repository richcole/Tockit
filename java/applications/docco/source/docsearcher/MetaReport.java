package docsearcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

public class MetaReport
{
    DocSearch ds;

    public MetaReport()
    {
    }

    public void getMetaReport(DocSearch ds)
    {
        this.ds = ds;
        MetaDialog md =
            new MetaDialog(ds, Messages.getString("DocSearch.metaDataOptions"),
                           true);
        md.init();
        md.setVisible(true);
        if (md.confirmed)
        {
            try
            {
                String numMaxStr =
                    (String)md.maxDocs.getModel().getElementAt(md.maxDocs
                                                               .getSelectedIndex());
                int maxNum = 500;
                int maxDaysOld = 730;
                maxNum = Integer.parseInt(numMaxStr);
                maxDaysOld = Integer.parseInt(md.dateField.getText());

                //
                doMetaDataReport((DocSearcherIndex)ds.indexes.get(md.indexChoice
                                                                  .getSelectedIndex()),
                                 md.listAll.isSelected(),
                                 md.pathRequired.isSelected(),
                                 md.pathField.getText(),
                                 md.authRequired.isSelected(),
                                 md.authField.getText(),
                                 md.reportField.getText(), maxNum,
                                 md.dateRequired.isSelected(), maxDaysOld);
                ds.setStatus(Messages.getString("DocSearch.statusReportComplete"));
            }
            catch (Exception eR)
            {
                ds.setStatus(Messages.getString("DocSearch.statusReportError")
                             + eR.toString());
            }
        }
        else
        {
            ds.setStatus(Messages.getString("DocSearch.statusReportCancelled"));
        }
    }

    public void doMetaDataReport(DocSearcherIndex di,
                                 boolean listAll,
                                 boolean pathRequired,
                                 String pathText,
                                 boolean authRequired,
                                 String authText,
                                 String reportFile,
                                 int maxDocs,
                                 boolean useDaysOld,
                                 int maxDays)
    {
        try
        {
            // intialize our metrics
            int numBadDocs = 0;
            int totalDocs = 0;
            int numGoodDocs = 0;

            // initialize our temp variables
            String author = "";
            String description = "";
            String title = "";
            String date = "";
            String path = "";
            String size = "";
            String keywords = "";
            String type = "";
            boolean isGood = true;
            boolean curSkip = false;
            int curTypeInt = 0;

            // initialize our filewriter, printwriter, and stagingfile
            String stagingFileName =
                Utils.addFolder(DocSearch.workingDir, "meta_data_staging_file.htm");
            File stageingFile = new File(stagingFileName);
            FileWriter filewriter = new FileWriter(stageingFile);
            PrintWriter pw = new PrintWriter(filewriter);

            // initialize the reader
            IndexReader ir = IndexReader.open(di.indexerPath);
            int numDocs = ir.maxDoc();
            Document doc;
            ds.setStatus("There are " + numDocs + " in index " + di.desc);

            // write the start of the table
            pw.println("<table border = 1><tr>");
            pw.println("<th valign = top>Document</th>");
            pw.println("<th valign = top>Title</th>");
            pw.println("<th valign = top>Author</th>");
            pw.println("<th valign = top>Date</th>");
            pw.println("<th valign = top>Description</th>");
            pw.println("<th valign = top>Keywords</th>");
            pw.println("<th valign = top>Size</th>");
            pw.println("<th valign = top>Type</th>");
            pw.println("</tr>");
            for (int i = 0; i < numDocs; i++)
            {
                if (!ir.isDeleted(i))
                {
                    doc = ir.document(i);
                    if (doc != null)
                    {
                        curSkip = false;

                        // put in the docs values
                        if (di.isWeb)
                        {
                            path = doc.get("URL");
                        }
                        else
                        {
                            path = doc.get("path");
                        }

                        ds.setStatus("Examining document: " + path);
                        isGood = true;
                        type = doc.get("type");
                        curTypeInt = Utils.getTypeInt(type);
                        author = doc.get("author");
                        description = doc.get("summary");
                        title = doc.get("title");
                        size = doc.get("size");
                        keywords = doc.get("keywords");
                        date =
                            DateTimeUtils.getDateFormatNormal(doc.get("mod_date"));

                        // determine if we even need to examine it
                        if (pathRequired)
                        {
                            if (path.indexOf(pathText) == -1)
                            {
                                curSkip = true;
                            }
                        }

                        if (authRequired)
                        {
                            if (author.indexOf(authText) == -1)
                            {
                                curSkip = true;
                            }
                        }

                        // determine if its bad of good
                        if (!curSkip)
                        {
                            totalDocs++;
                            isGood =
                                goodMetaData(title, description, author, date,
                                             keywords, curTypeInt, useDaysOld,
                                             maxDays);

                            // write to our file  
                            if ((!isGood) || (listAll))
                            {
                                pw.println("<tr>");
                                pw.println("<td valign = top>"); // path
                                pw.println(path);
                                pw.println("</td>");
                                pw.println("<td valign = top><small>");
                                pw.println(title);
                                pw.println("</small></td>");
                                pw.println("<td valign = top>");
                                pw.println(author);
                                pw.println("</td>");
                                pw.println("<td valign = top>");
                                pw.println(date);
                                pw.println("</td>");
                                pw.println("<td valign = top><small>");
                                pw.println(description);
                                pw.println("</small></td>");
                                pw.println("<td valign = top><small>");
                                pw.println(keywords);
                                pw.println("</small></td>");
                                pw.println("<td valign = top>");
                                pw.println(size);
                                pw.println("</td>");
                                pw.println("<td valign = top>");
                                pw.println(type);
                                pw.println("</td>");
                                pw.println("</tr>");
                            }

                            if (isGood)
                            {
                                ds.setStatus(path + " has needed meta data.");
                                numGoodDocs++;
                            }
                            else
                            {
                                ds.setStatus(path + " is missing meta data.");
                                numBadDocs++;
                            }
                        }

                        // end for not skipped
                        else
                        {
                            ds.setStatus("Skipping document: " + path);
                        }
                    }

                    // end of doc is not null
                }

                if (i > maxDocs)
                {
                    break;
                }
            }

            // write the end of the document
            pw.println("</table></body></html>");

            // close our printwriter and filewriter
            filewriter.close();
            pw.close();
            int percentGood = 0;
            if (totalDocs > 0)
            {
                percentGood = ((numGoodDocs * 100) / totalDocs);
            }

            ds.setStatus("Percent of Documents with Good Meta Data: "
                         + percentGood + " (" + numGoodDocs + " of "
                         + totalDocs + ", " + numBadDocs + " bad).");

            // now write the summary
            String headerFileName =
                Utils.addFolder(DocSearch.workingDir, "meta_data_header.htm");
            File headerFile = new File(headerFileName);
            FileWriter hwriter = new FileWriter(headerFile);
            PrintWriter hw = new PrintWriter(hwriter);
            hw.println("<html><head><title>Meta Data Report for ");
            hw.println(di.desc);
            hw.println("</title><meta name = description content = \"lists documents with poorly searchable meta data\"><meta name = \"author\" content = \"DocSearcher\"></head><body><h1>Meta Data Report for ");
            hw.println(di.desc);
            hw.println("</h1><p align = left>Index had <b>");
            hw.println("" + numBadDocs);
            hw.println("</b> documents which had poor meta data<br>and <b>");
            hw.println("" + numGoodDocs);
            hw.println("</b> had all required meta data.</p><p align = left>Overall, this index had a good-to-bad ratio of <b>");
            hw.println("" + percentGood + "</b> percent . </p>");
            hw.println("<p align = left>The table below lists the details of each document's meta data.</p>");
            hwriter.close();
            hw.close();

            // compile our results
            combineFiles(headerFileName, stagingFileName);
            boolean renameSuccess = headerFile.renameTo(new File(reportFile));
            ds.curPage = Messages.getString("DocSearch.report");
            if (renameSuccess)
            {
                ds.doExternal(reportFile);
            }
            else
            {
                ds.doExternal(headerFileName);
            }
        }
        catch (Exception eI)
        {
            ds.setStatus(Messages.getString("DocSearch.statusMetaDataError")
                         + di.desc + ":" + eI.toString());
        }
    }

    private void combineFiles(String firstFile,
                              String secondFile)
    {
        synchronized (this)
        {
            ds.setStatus("Please wait: Combining data files (" + firstFile
                         + " and " + secondFile + ")");
            try
            {
                File firstFi = new File(firstFile);
                FileInputStream firFi = new FileInputStream(firstFi);

                //  temporary storage
                String tempFile =
                    Utils.addFolder(DocSearch.workingDir, "temp_copy.txt");
                File tempFi = new File(tempFile);
                FileOutputStream fo = new FileOutputStream(tempFi);

                //
                File secondFi = new File(secondFile);
                FileInputStream secFi = new FileInputStream(secondFi);

                // write the first file to the temp file
                int curI = 0;
                byte curBint;
                while (curI != -1)
                {
                    curI = firFi.read();
                    curBint = (byte)curI;
                    if (curI != -1)
                    {
                        fo.write(curBint);
                    }
                    else
                    {
                        break;
                    }
                }
                firFi.close();
                curI = 0;
                while (curI != -1)
                {
                    curI = secFi.read();
                    curBint = (byte)curI;
                    if (curI != -1)
                    {
                        fo.write(curBint);
                    }
                    else
                    {
                        break;
                    }
                }
                secFi.close();
                fo.close();
                copyFile(tempFile, firstFile);
            }
             // end for try
            catch (Exception eF)
            {
                // show the err
            }
        }
    }

    private boolean goodMetaData(String title,
                                 String description,
                                 String author,
                                 String date,
                                 String keywords,
                                 int curTypeInt,
                                 boolean useDaysOld,
                                 int maxDays)
    {
        boolean returnbool = true;
        if (useDaysOld)
        {
            if (DateTimeUtils.getDaysOld(date) > maxDays)
            {
                returnbool = false;
            }
        }

        title = title.toLowerCase();
        author = author.toLowerCase();
        description = description.toLowerCase();
        int titleLen = title.length();
        int descLen = description.length();
        int authLen = author.length();
        int keywdLen = keywords.length();
        switch (curTypeInt)
        {
        case 0: // html

            // should have all meta data !
            if ((descLen < 3) || (authLen == 0) || (titleLen < 3)
                    || (keywdLen < 3))
            {
                returnbool = false;
            }
            else if (title.startsWith("new_page"))
            {
                returnbool = false;
            }

            break;

        case 2: // ms word
            if ((authLen == 0) || (titleLen < 3))
            {
                returnbool = false;
            }

            break;

        case 3: // ms word
            if ((authLen == 0) || (titleLen < 3))
            {
                returnbool = false;
            }

            break;

        case 4: // pdf
            if ((authLen == 0) || (titleLen < 3))
            {
                returnbool = false;
            }

            break;

        case 5: // rtf

            // nothing to check here except date
            break;

        case 6: // open office writer
            if ((authLen == 0) || (titleLen < 3))
            {
                returnbool = false;
            }

            break;

        case 7: // open office impress
            if ((authLen == 0) || (titleLen < 3))
            {
                returnbool = false;
            }

            break;

        case 8: // open office calc
            if ((authLen == 0) || (titleLen < 3))
            {
                returnbool = false;
            }

            break;

        case 9: // open office draw
            if ((authLen == 0) || (titleLen < 3))
            {
                returnbool = false;
            }

            break;

        default: // text

            // nothing to check here
            break;
        }

        return returnbool;
    }

    private void copyFile(String originalFileName,
                          String newFileName)
    {
        boolean ioSuccess = true;
        try
        {
            File origFile = new File(originalFileName);
            FileInputStream fi = new FileInputStream(origFile);
            File newFile = new File(newFileName);
            FileOutputStream fo = new FileOutputStream(newFile);
            int curI = 0; // reset i
            byte rB;
            byte curBint;
            while (curI != -1)
            {
                curI = fi.read();
                if (curI != -1)
                {
                    curBint = (byte)curI;
                    fo.write(curBint);
                }
                else
                {
                    break;
                }
            }

            fo.close();
            fi.close();
        }
        catch (Exception eF)
        {
            ioSuccess = false;
        }
        finally
        {
            if (ioSuccess)
            {
                //System.out.println("File "+originalFileName+"\n was successfully copied to:\n"+newFileName);
            }
            else
            {
                System.out.println("ERROR creating file: " + newFileName);
            }
        }
    }
}
