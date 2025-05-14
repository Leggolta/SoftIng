package org.example;
import java.util.ArrayList;
public class SentenceStructures
{
    private ArrayList<String> SentenceStructureList;
    public SentenceStructures()
    {
        SentenceStructureList = WordUtil.importer("include/libraries/SentenceStructures.txt");
    }
    public String Random()
    {
        String RandElem = SentenceStructureList.get(WordUtil.Randomizer(SentenceStructureList.size()));
        return RandElem;
    }
}