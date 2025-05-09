package Library;
import java.util.ArrayList;
public class Adjectives
{
    private ArrayList<String> AdjectiveList;
    public Adjectives()
    {
        AdjectiveList = WordUtil.importer("include/libraries/Adjectives.txt");
    }
    public String Random()
    {
        String RandElem = AdjectiveList.get(WordUtil.Randomizer(AdjectiveList.size()));
        return RandElem;
    }
}