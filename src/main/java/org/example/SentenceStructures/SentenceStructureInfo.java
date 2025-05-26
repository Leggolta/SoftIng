package org.example.SentenceStructures;

import org.example.words.WordUtil;

import java.util.HashMap;
import java.util.Map;

public class SentenceStructureInfo {
    private String template;
    private Map<String, Integer> placeholderCount;

    public SentenceStructureInfo(String template) {
        this.template = template;
        this.placeholderCount = new HashMap<>();
        countPlaceholders();
    }

    private void countPlaceholders() {
        String[] tokens = template.split(" ");
        for (String token : tokens) {
            String type = WordUtil.TypeCheck(token);
            if (type != null) {
                placeholderCount.put(type, placeholderCount.getOrDefault(type, 0) + 1);
            }
        }
    }

    public String getTemplate() {
        return template;
    }

    public int getCount(String type) {
        return placeholderCount.getOrDefault(type, 0);
    }

    public Map<String, Integer> getAllCounts() {
        return placeholderCount;
    }
}
