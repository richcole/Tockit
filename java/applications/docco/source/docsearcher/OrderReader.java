package docsearcher;

import java.io.*;

public class OrderReader
{
    final static int maxItems = 10000;
    int lastItem;
    String[] items;
    int pos;
    boolean ioOK = true;
    String defaultOutName = "zzzOut.txt";
    String errorS = "";

    OrderReader()
    {
        items = new String[maxItems];
        lastItem = 0;
    }

    void add(String item)
    {
        if (lastItem < items.length)
        {
            items[lastItem++] = item;
        }
    }

    void show()
    {
        System.out.println("inside the array are :");
        for (int i = 0; i < lastItem; i++)
        {
            System.out.println(i + "  .   " + items[i]);
        }
    }

    String inItem(int pos)
    {
        String InItem = items[pos];

        return InItem;
    }

    void save(String fileName,
              String dirPath)
    {
        if (fileName.equals(""))
        {
            fileName = defaultOutName;
        }

        try
        {
            File outputFile;
            if (dirPath.equals(""))
            {
                outputFile = new File(fileName);
            }
            else
            {
                outputFile = new File(dirPath, fileName);
            }

            FileWriter out = new FileWriter(outputFile);
            PrintWriter fileOut = new PrintWriter(out);
            String output;
            for (int z = 0; z < lastItem; z++)
            {
                output = items[z];
                fileOut.println(output);

                //System.out.println(output);
            }

            fileOut.close();
            ioOK = true;
        }
        catch (IOException e)
        { // file problem
            ioOK = false;
            System.out.println("IO error: " + e.toString());
            errorS = e.toString();
        }
    }

    void saveOneName(String fileName)
    {
        if (fileName.equals(""))
        {
            fileName = defaultOutName;
        }

        try
        {
            File outputFile = new File(fileName);
            FileWriter out = new FileWriter(outputFile);
            PrintWriter fileOut = new PrintWriter(out);
            String output;
            for (int z = 0; z < lastItem; z++)
            {
                output = items[z];
                fileOut.println(output);

                //System.out.println(output);
            }

            fileOut.close();
            ioOK = true;
        }
        catch (IOException e)
        { // file problem
            ioOK = false;
            System.out.println("IO error: " + e.toString());
            errorS = e.toString();
        }
    }

    void addFile(String fileName)
    {
        if (fileName.equals(""))
        {
            fileName = defaultOutName;
        }

        try
        {
            File inputFile = new File(fileName);
            FileReader inBufRdr = new FileReader(inputFile);
            BufferedReader completeOrder = new BufferedReader(inBufRdr, 1);
            String inputString;
            try
            {
                while ((inputString = completeOrder.readLine()) != null)
                {
                    items[lastItem++] = inputString;
                }

                completeOrder.close();
                ioOK = true;
            }
            catch (IOException e)
            {
                System.out.println("IO error: " + e.toString());
                ioOK = false;
                errorS = e.toString();
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File Not Found Error:" + fileName);
        }
    }
}
