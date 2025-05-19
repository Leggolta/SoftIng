package org.example;

import java.util.ArrayList;
public class Nouns
{
    private ArrayList<String> NounList;
    public Nouns()
    {
        NounList = WordUtil.importer("src/main/resources/Nouns.txt");
    }
    public String Random()
    {
        String RandElem = NounList.get(WordUtil.Randomizer(NounList.size()));
        return RandElem;
    }
}