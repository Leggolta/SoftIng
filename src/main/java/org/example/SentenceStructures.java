package org.example;

import java.util.ArrayList;
import java.util.List;

public class SentenceStructures {
    private List<SentenceStructureInfo> structures;

    public SentenceStructures() {
        List<String> lines = WordUtil.importer("src/main/resources/SentenceStructure.txt");
        structures = new ArrayList<>();
        for (String line : lines) {
            structures.add(new SentenceStructureInfo(line));
        }
    }

    public List<SentenceStructureInfo> getStructures() {
        return structures;
    }
}
