package org.example.words;

import java.util.ArrayList;

public abstract class WordList {
    protected ArrayList<String> words;

    /**
     * Loads all words from the given file path into the internal list.
     *
     * @param filePath the path to the word list file to import
     */
    public WordList(String filePath) {
        words = WordUtil.importer(filePath);
    }

    /**
     * Returns a random word from the list.
     * Uses WordUtil.Randomizer to pick an index; returns an empty string if the list is empty.
     *
     * @return a randomly selected word, or "" if no words are available
     */
    public String Random() {
        if (words.isEmpty()) return "";
        return words.get(WordUtil.Randomizer(words.size()));
    }
}