package docsearcher;

public class SearchedWord
{
    int numTimeSearched = 1;
    String name = "";
    String lowerName = "";

    SearchedWord(String name)
    {
        this.name = name;
        this.lowerName = name.toLowerCase();
    }

    public void hit()
    {
        numTimeSearched++;
    }
}

