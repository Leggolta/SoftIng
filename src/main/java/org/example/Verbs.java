package org.example;

import java.util.ArrayList;
public class Verbs
{
    private ArrayList<String> VerbList;
    public Verbs()
    {
        VerbList = WordUtil.importer("src/main/resources/Verbs.txt");
    }
    public String Random()
    {
        String RandElem = VerbList.get(WordUtil.Randomizer(VerbList.size()));
        return RandElem;
    }
}