package docsearcher;

public class LogSearch
{
    String time = "";
    String ip = "";
    int numResults = 0;
    double score = 0.0;
    String searchText = "";

    LogSearch(String time,
              String ip,
              int numResults,
              double score,
              String searchText)
    {
        this.time = time;
        this.ip = ip;
        this.numResults = numResults;
        this.score = score;
        this.searchText = searchText;
    }
}
