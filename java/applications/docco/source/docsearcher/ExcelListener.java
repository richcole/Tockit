package docsearcher;

import org.apache.poi.hssf.eventmodel.HSSFListener;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;

public class ExcelListener
    implements HSSFListener
{
    String tempFile;
    StringBuffer excelText = new StringBuffer();
    private SSTRecord sstrec;

    /**
     * This method listens for incoming records and handles them as required.
     *
     * @param record The record that was found while reading.
     */
    public void processRecord(Record record)
    {
        try
        {
            switch (record.getSid())
            {
            case BOFRecord.sid:
                BOFRecord bof = (BOFRecord)record;
                if (bof.getType() == BOFRecord.TYPE_WORKBOOK)
                {
                    System.out.println("Encountered workbook");

                    // assigned to the class level member
                }
                else if (bof.getType() == BOFRecord.TYPE_WORKSHEET)
                {
                    break;
                }

            case BoundSheetRecord.sid:
                BoundSheetRecord bsr = (BoundSheetRecord)record;

                break;

            case RowRecord.sid:
                RowRecord rowrec = (RowRecord)record;

                break;

            case NumberRecord.sid:
                NumberRecord numrec = (NumberRecord)record;
                excelText.append(numrec.getValue());
                excelText.append("\n");

                break;

            // SSTRecords store a array of unique strings used in Excel.
            case SSTRecord.sid:
                sstrec = (SSTRecord)record;
                for (int k = 0; k < sstrec.getNumUniqueStrings(); k++)
                {
                    excelText.append(sstrec.getString(k));
                    excelText.append("\n");

                    //System.out.println("String table value " + k + " = " + sstrec.getString(k));
                }

                break;

            case LabelSSTRecord.sid:
                LabelSSTRecord lrec = (LabelSSTRecord)record;
                excelText.append(sstrec.getString(lrec.getSSTIndex()));
                excelText.append("\n");

                // System.out.println("String cell found with value "+ sstrec.getString(lrec.getSSTIndex()));
                break;
            } // end for switch
        }
         // end for try
        catch (Exception eN)
        {
            System.out.println("ERR while trying to read excel file"
                               + eN.toString());
            System.out.println("Error was :" + eN.toString());
        }
    }
}
