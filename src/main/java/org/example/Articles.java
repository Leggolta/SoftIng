package org.example;

import java.util.ArrayList;

public class Articles {
    private ArrayList<String> ArticleList;

    public Articles() {
        ArticleList = WordUtil.importer("src/main/resources/Articles.txt");
    }

    public String Random() {
        return ArticleList.get(WordUtil.Randomizer(ArticleList.size()));
    }
}
