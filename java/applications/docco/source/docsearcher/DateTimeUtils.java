package docsearcher;

import java.text.DateFormat;
import java.util.Calendar;

import org.apache.lucene.document.DateField;

public class DateTimeUtils
{

    public static String getFileDate(String modDate)
    {
        StringBuffer retB = new StringBuffer();
        Calendar nowD = Calendar.getInstance();
        java.util.Date dn = new java.util.Date(modDate);
        nowD.setTime(dn);
        int mon = nowD.get(Calendar.MONTH) + 1;
        int year = nowD.get(Calendar.YEAR);
        int day = nowD.get(Calendar.DAY_OF_MONTH);
        retB.append(year + "");
        if (mon < 10)
        {
            retB.append("0");
        }

        retB.append(mon + "");
        if (day < 10)
        {
            retB.append("0");
        }

        retB.append(day + "");

        return retB.toString();
    }

    public static java.util.Date getJDateFromString(String dateStr)
    {
        java.util.Date rd = new java.util.Date(dateStr);

        //System.out.println("date:"+rd.toString());
        return rd;
    }

    public static int getDaysOld(String date)
    {
        int returnInt = 0;
        long tempLong;
        if (!date.equals(""))
        {
            try
            {
                long indexTime;
                long nowTime;
                java.util.Date today = new java.util.Date(getToday());
                java.util.Date then = new java.util.Date(date);
                nowTime = today.getTime();
                indexTime = then.getTime();

                // perform math to compute the actual number of days
                if (nowTime > indexTime)
                {
                    indexTime = indexTime / (1000 * 60 * 60 * 24);
                    nowTime = nowTime / (1000 * 60 * 60 * 24);
                    returnInt = (int)(nowTime - indexTime);
                }
            }
            catch (Exception eD)
            {
                System.out.println("Error obtaining days old for  " + date
                                   + "\n" + eD.toString());
            }
        }

        return returnInt;
    }

    public static int getDaysOld(DocSearcherIndex dsi)
    {
        int returnInt = 0;
        long tempLong;
        if (!dsi.lastIndexed.equals(""))
        {
            try
            {
                long indexTime;
                long nowTime;
                java.util.Date today = new java.util.Date(getToday());
                java.util.Date then = new java.util.Date(dsi.lastIndexed);
                nowTime = today.getTime();
                indexTime = then.getTime();

                // perform math to compute the actual number of days
                if (nowTime > indexTime)
                {
                    indexTime = indexTime / (1000 * 60 * 60 * 24);
                    nowTime = nowTime / (1000 * 60 * 60 * 24);
                    returnInt = (int)(nowTime - indexTime);
                }
            }
            catch (Exception eD)
            {
                System.out.println("Error obtaining days old for index "
                                   + dsi.desc + "\n" + eD.toString());
            }
        }

        return returnInt;
    }

    public static String getToday()
    {
        String thisYear = "";
        Calendar nowD = Calendar.getInstance();
        String mon = "" + (nowD.get(Calendar.MONTH) + 1);
        String year = "" + (nowD.get(Calendar.YEAR));
        String day = "" + nowD.get(Calendar.DAY_OF_MONTH);
        thisYear = mon + "/" + day + "/" + year;

        return thisYear;
    }

    public static String getLastYear()
    {
        String lastYear = "";
        Calendar nowD = Calendar.getInstance();
        String mon = "" + (nowD.get(Calendar.MONTH) + 1);
        String year = "" + (nowD.get(Calendar.YEAR));
        String day = "" + nowD.get(Calendar.DAY_OF_MONTH);
        String reportDate = mon + "/" + day + "/" + year;
        int ly = 2002;
        try
        {
            ly = Integer.parseInt(year) - 1;
        }
        catch (Exception eR)
        {
            ly = 2002;
        }

        return lastYear = mon + "/" + day + "/" + ly;
    }

    public static String getFileDate(long modDate)
    {
        StringBuffer retB = new StringBuffer();
        Calendar nowD = Calendar.getInstance();
        java.util.Date dn = new java.util.Date(modDate);
        nowD.setTime(dn);
        int mon = nowD.get(Calendar.MONTH) + 1;
        int year = nowD.get(Calendar.YEAR);
        int day = nowD.get(Calendar.DAY_OF_MONTH);
        retB.append(year + "");
        if (mon < 10)
        {
            retB.append("0");
        }

        retB.append(mon + "");
        if (day < 10)
        {
            retB.append("0");
        }

        retB.append(day + "");

        return retB.toString();
    }

    public static String getNormalFileDate(long modDate)
    {
        StringBuffer retB = new StringBuffer();
        Calendar nowD = Calendar.getInstance();
        java.util.Date dn = new java.util.Date(modDate);
        nowD.setTime(dn);
        int mon = nowD.get(Calendar.MONTH) + 1;
        int year = nowD.get(Calendar.YEAR);
        int day = nowD.get(Calendar.DAY_OF_MONTH);
        retB.append(mon + "/");
        retB.append(day + "/");
        retB.append(year + "");

        return retB.toString();
    }

    public static String getDateFormatNormal(String toConv)
    {
        return getDateParsed(toConv);
    }

    public static String getDateFromField(String s)
    {
        String returnString = "";
        try
        {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
            returnString = DateField.dateToString(df.parse(s));
            System.out.println("Converted date:" + s + " TO " + returnString);
            return returnString;
        }
        catch (Exception eR)
        {
            System.out.println("Unable to convert date from field info:" + s);
            return "Unknown";
        }
    }

    public static String getTimeStringForIndex(long ltime)
    {
        String returnString = "";
        try
        {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
            returnString = DateField.timeToString(ltime);
            return returnString;
        }
        catch (Exception eR)
        {
            System.out.println("Unable to convert date from field info:"
                               + eR.toString());

            return "Unknown";
        }
    }

    public static String getDateParsed(String s)
    {
        try
        {
            java.util.Date bd = DateField.stringToDate(s);
            Calendar nowD = Calendar.getInstance();
            nowD.setTime(bd);
            int mon = nowD.get(Calendar.MONTH) + 1;
            int year = nowD.get(Calendar.YEAR);
            int day = nowD.get(Calendar.DAY_OF_MONTH);

            return mon + "/" + day + "/" + year;
        }
        catch (Exception eR)
        {
            System.out.println("Unable to convert date:" + s);

            return "Unknown";
        }
    }

    public static String getDateIndex(long timeOfFile)
    {
        String returnString = getNormalFileDate(timeOfFile);
        try
        {
            returnString = getDateParsed(returnString);
        }
        catch (Exception eR)
        {
        }

        return returnString;
    }
}
