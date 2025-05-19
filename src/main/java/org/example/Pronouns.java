package org.example;

import java.util.ArrayList;

public class Pronouns {
    private ArrayList<String> PronounList;

    public Pronouns() {
        PronounList = WordUtil.importer("src/main/resources/Pronouns.txt");
    }

    public String Random() {
        return PronounList.get(WordUtil.Randomizer(PronounList.size()));
    }
}
