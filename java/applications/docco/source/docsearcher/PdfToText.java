package docsearcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.pdfbox.cos.COSDocument;
import org.pdfbox.cos.COSObject;
import org.pdfbox.encryption.DecryptDocument;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

public class PdfToText
{
    public static final String DEFAULT_ENCODING = "ISO-8859-1";

    //"US-ASCII";
    //"UTF-8";
    private static final String PASSWORD = "-password";
    String fileName;
    String tempFile = "";
    String author = "";
    COSObject cos;

    PdfToText(String fileName,
              String tempFile)
    {
        this.fileName = fileName;
        this.tempFile = tempFile;
    }

    public void parse()
        throws Exception
    {
    	System.out.println("PDF parser");
        int currentArgumentIndex = 0;
        String password = "";
        String encoding = DEFAULT_ENCODING;
        PDFTextStripper stripper = new PDFTextStripper();
        InputStream input = null;
        Writer output = null;
        COSDocument document = null;
        try
        {
            input = new FileInputStream(fileName);
            document = parseDocument(input);
            FileInputStream fis = new FileInputStream(fileName);
            PDFParser parser = new PDFParser(fis);
            parser.parse();
            PDDocument metaD = parser.getPDDocument();
            if (document.isEncrypted())
            {
                try
                {
                    DecryptDocument decryptor = new DecryptDocument(document);
                    decryptor.decryptDocument(password);
                }
                catch (InvalidPasswordException e)
                {
                    //they didn't suppply a password and the default of "" was wrong.
                    System.err.println("Error: The document is encrypted.");
                }
            }
             // end if encrypted

            // now get the author
            PDDocumentInformation info = metaD.getDocumentInformation();
            
            System.out.println("pdf file creation date: " + info.getCreationDate());

            // get the author
            author = info.getAuthor();
            fis.close();

            //
            output =
                new OutputStreamWriter(new FileOutputStream(tempFile), encoding);
            System.out.println("writing text into temp file " + tempFile);
            stripper.writeText(document, output);
			//stripper.writeText(document, new OutputStreamWriter(System.out));
        }
        catch (Exception eR)
        {
            eR.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }

            if (output != null)
            {
                output.close();
            }

            if (document != null)
            {
                document.close();
            }
        }
    }

    private static COSDocument parseDocument(InputStream input)
        throws IOException
    {
        PDFParser parser = new PDFParser(input);
        parser.parse();

        return parser.getDocument();
    }
}
