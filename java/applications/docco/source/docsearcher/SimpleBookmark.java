package docsearcher;

public class SimpleBookmark
{
    boolean shouldBeSearched = false;
    String desc = "";
    String urlString = "";

    SimpleBookmark(String urlString,
                   String desc)
    {
        this.urlString = urlString;
        this.desc = desc;
    }
}
