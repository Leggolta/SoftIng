package org.example.words;

import java.util.ArrayList;

public abstract class WordList {
    protected ArrayList<String> words;

    public WordList(String filePath) {
        words = WordUtil.importer(filePath);
    }

    public String Random() {
        if (words.isEmpty()) return "";  // sicurezza se la lista è vuota
        return words.get(WordUtil.Randomizer(words.size()));
    }
}