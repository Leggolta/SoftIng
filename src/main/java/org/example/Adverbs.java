package org.example;

import java.util.ArrayList;

public class Adverbs {
    private ArrayList<String> AdverbList;

    public Adverbs() {
        AdverbList = WordUtil.importer("src/main/resources/Adverbs.txt");
    }

    public String Random() {
        return AdverbList.get(WordUtil.Randomizer(AdverbList.size()));
    }
}
