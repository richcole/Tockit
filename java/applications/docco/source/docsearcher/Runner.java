package docsearcher;

public class Runner
    implements Runnable
{
    String command = "";
    DocSearch docSearch;

    public Runner(String command,
                  DocSearch docSearch)
    {
        this.command = command;
        this.docSearch = docSearch;
    }

    public void run()
    {
        docSearch.handleEventCommand(command);
    }
}
