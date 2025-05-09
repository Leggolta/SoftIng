package Library;
import java.util.ArrayList;
public class Nouns
{
    private ArrayList<String> NounList;
    public Nouns()
    {
        NounList = WordUtil.importer("include/libraries/Nouns.txt");
    }
    public String Random()
    {
        String RandElem = NounList.get(WordUtil.Randomizer(NounList.size()));
        return RandElem;
    }
}