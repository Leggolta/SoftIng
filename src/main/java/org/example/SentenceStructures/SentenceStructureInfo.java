package org.example.SentenceStructures;
import org.example.words.WordUtil;

import java.util.HashMap;
import java.util.Map;

public class SentenceStructureInfo {
    private String template;
    private Map<String, Integer> placeholderCount;

    /**
     * Constructor
     * Initializes the template string and sets up the map to track placeholder counts.
     * Then triggers the placeholder counting process.
     *
     * @param template the sentence template containing word placeholders
     */
    public SentenceStructureInfo(String template) {
        this.template = template;
        this.placeholderCount = new HashMap<>();
        countPlaceholders();
    }

    /**
     * Scans the template for tokens, determines their placeholder type via WordUtil.TypeCheck,
     * and increments the count for each detected placeholder type.
     */
    private void countPlaceholders() {
        String[] tokens = template.split(" ");
        for (String token : tokens) {
            String type = WordUtil.TypeCheck(token);
            if (type != null) {
                placeholderCount.put(type, placeholderCount.getOrDefault(type, 0) + 1);
            }
        }
    }

    /**
     * Retrieves the original template string.
     *
     * @return the sentence template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Returns how many placeholders of the given type were found.
     *
     * @param type the placeholder category (e.g. "NOUN", "VERB")
     * @return the count of placeholders of that type, or 0 if none were found
     */
    public int getCount(String type) {
        return placeholderCount.getOrDefault(type, 0);
    }

    /**
     * Provides the full map of all placeholder types and their counts.
     *
     * @return a map from placeholder types to their detected counts
     */
    public Map<String, Integer> getAllCounts() {
        return placeholderCount;
    }
}
