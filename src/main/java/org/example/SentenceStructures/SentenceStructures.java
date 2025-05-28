package org.example.SentenceStructures;
import org.example.words.WordUtil;

import java.util.ArrayList;
import java.util.List;

public class SentenceStructures {
    private List<SentenceStructureInfo> structures;

    /**
     * Loads sentence templates from a resource file, creates a
     * SentenceStructureInfo for each line, and stores them in a list.
     */
    public SentenceStructures() {
        List<String> lines = WordUtil.importer("src/main/resources/SentenceStructure.txt");
        structures = new ArrayList<>();
        for (String line : lines) {
            structures.add(new SentenceStructureInfo(line));
        }
    }

    /**
     * Retrieves the list of all analyzed sentence structures.
     *
     * @return list of SentenceStructureInfo objects representing each template
     */
    public List<SentenceStructureInfo> getStructures() {
        return structures;
    }
}
