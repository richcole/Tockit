package docsearcher;

public class MessageRunner
    implements Runnable
{
    String title = "";
    String details = "";
    DocSearch docS;

    public MessageRunner(String title,
                         String details,
                         DocSearch dcS)
    {
        this.title = title;
        this.details = details;
        this.docS = dcS;
    }

    /**
     * DOCUMENT ME!
     */
    public void run()
    {
        docS.showMessageDialog(title, details);
    }
}
