package docsearcher;

public class ResultItem
{
    float score;
    int type;
    String title;
    long size;
    String path;
    String summary;

    ResultItem(String path,
               String title,
               String summary,
               long size,
               float score,
               int type)
    {
        this.score = score;
        this.size = size;
        this.title = title;
        this.path = path;
        this.summary = summary;
    }

    public String toString()
    {
        return title;
    }
}
