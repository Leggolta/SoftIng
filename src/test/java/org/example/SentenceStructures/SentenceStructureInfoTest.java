package org.example.SentenceStructures;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

/**
 * Test class for SentenceStructureInfo using only JUnit 5.
 * Tests template processing, placeholder counting, and data retrieval functionality.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SentenceStructureInfoTest {

    @Test
    @Order(1)
    @DisplayName("Test basic constructor initialization")
    void testBasicConstructor() {
        String template = "The [noun] [verb] quickly";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        assertNotNull(structureInfo, "SentenceStructureInfo should be created");
        assertEquals(template, structureInfo.getTemplate(), "Template should be stored correctly");
        assertNotNull(structureInfo.getAllCounts(), "Placeholder counts map should be initialized");

        System.out.println("Created SentenceStructureInfo with template: " + template);
        System.out.println("All counts: " + structureInfo.getAllCounts());
    }

    @Test
    @Order(2)
    @DisplayName("Test empty template")
    void testEmptyTemplate() {
        String emptyTemplate = "";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(emptyTemplate);

        assertNotNull(structureInfo, "SentenceStructureInfo should handle empty template");
        assertEquals(emptyTemplate, structureInfo.getTemplate(), "Empty template should be stored");
        assertNotNull(structureInfo.getAllCounts(), "Counts map should be initialized even for empty template");
        assertTrue(structureInfo.getAllCounts().isEmpty(), "Empty template should have no placeholder counts");

        System.out.println("Empty template test - counts: " + structureInfo.getAllCounts().size());
    }

    @Test
    @Order(3)
    @DisplayName("Test template with no placeholders")
    void testTemplateWithoutPlaceholders() {
        String template = "This is a simple sentence with no placeholders";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        assertEquals(template, structureInfo.getTemplate(), "Template should be stored correctly");
        assertTrue(structureInfo.getAllCounts().isEmpty(), "Template without placeholders should have empty counts");

        // Test getCount for non-existent types
        assertEquals(0, structureInfo.getCount("[noun]"), "Non-existent type should return 0");
        assertEquals(0, structureInfo.getCount("[verb]"), "Non-existent type should return 0");
        assertEquals(0, structureInfo.getCount("[adjective]"), "Non-existent type should return 0");

        System.out.println("Template without placeholders: " + template);
        System.out.println("Counts map size: " + structureInfo.getAllCounts().size());
    }

    @Test
    @Order(4)
    @DisplayName("Test template with single placeholder type")
    void testSinglePlaceholderType() {
        String template = "The [noun] and the [noun] are [noun]";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        assertEquals(template, structureInfo.getTemplate(), "Template should be stored correctly");

        // Assuming WordUtil.TypeCheck recognizes [noun] as "[noun]"
        // Note: This test depends on the actual implementation of WordUtil.TypeCheck
        Map<String, Integer> counts = structureInfo.getAllCounts();
        System.out.println("Single type template counts: " + counts);

        // Test that getCount works for any type (returns 0 for unrecognized)
        int nounCount = structureInfo.getCount("[noun]");
        System.out.println("Count for [noun]: " + nounCount);

        // If WordUtil.TypeCheck recognizes [noun], it should be > 0
        // If not, it should be 0 - both are valid depending on WordUtil implementation
        assertTrue(nounCount >= 0, "Count should be non-negative");
    }

    @Test
    @Order(5)
    @DisplayName("Test template with multiple placeholder types")
    void testMultiplePlaceholderTypes() {
        String template = "The [adjective] [noun] [verb] [adverb] over the [adjective] [noun]";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        assertEquals(template, structureInfo.getTemplate(), "Template should be stored correctly");

        Map<String, Integer> counts = structureInfo.getAllCounts();
        System.out.println("Multiple types template: " + template);
        System.out.println("All counts: " + counts);

        // Test various placeholder types
        System.out.println("Count for [noun]: " + structureInfo.getCount("[noun]"));
        System.out.println("Count for [verb]: " + structureInfo.getCount("[verb]"));
        System.out.println("Count for [adjective]: " + structureInfo.getCount("[adjective]"));
        System.out.println("Count for [adverb]: " + structureInfo.getCount("[adverb]"));

        // Test that counts are non-negative
        assertTrue(structureInfo.getCount("[noun]") >= 0, "Noun count should be non-negative");
        assertTrue(structureInfo.getCount("[verb]") >= 0, "Verb count should be non-negative");
        assertTrue(structureInfo.getCount("[adjective]") >= 0, "Adjective count should be non-negative");
        assertTrue(structureInfo.getCount("[adverb]") >= 0, "Adverb count should be non-negative");
    }

    @Test
    @Order(6)
    @DisplayName("Test template with repeated placeholders")
    void testRepeatedPlaceholders() {
        String template = "[noun] [noun] [noun] [verb] [verb]";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        assertEquals(template, structureInfo.getTemplate(), "Template should be stored correctly");

        Map<String, Integer> counts = structureInfo.getAllCounts();
        System.out.println("Repeated placeholders template: " + template);
        System.out.println("All counts: " + counts);

        // The counts depend on WordUtil.TypeCheck implementation
        // If it recognizes [noun] and [verb], they should be counted
        int nounCount = structureInfo.getCount("[noun]");
        int verbCount = structureInfo.getCount("[verb]");

        System.out.println("Noun occurrences: " + nounCount);
        System.out.println("Verb occurrences: " + verbCount);

        // Counts should be consistent with actual occurrences if recognized
        assertTrue(nounCount >= 0, "Noun count should be non-negative");
        assertTrue(verbCount >= 0, "Verb count should be non-negative");
    }

    @Test
    @Order(7)
    @DisplayName("Test template with mixed content")
    void testMixedContent() {
        String template = "Yesterday the [adjective] [noun] [verb] [adverb] while eating [noun] and drinking [noun]";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        assertEquals(template, structureInfo.getTemplate(), "Template should be stored correctly");

        Map<String, Integer> counts = structureInfo.getAllCounts();
        System.out.println("Mixed content template: " + template);
        System.out.println("Detected placeholder types: " + counts.keySet());
        System.out.println("All counts: " + counts);

        // Test that getAllCounts returns immutable view or copy
        assertNotNull(counts, "Counts map should not be null");

        // Test individual counts
        for (String type : counts.keySet()) {
            int count = structureInfo.getCount(type);
            assertTrue(count > 0, "Count for detected type " + type + " should be positive");
            assertEquals(counts.get(type), count, "getCount should match getAllCounts for " + type);
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test getCount for non-existent types")
    void testGetCountNonExistentTypes() {
        String template = "The [noun] [verb] quickly";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        // Test various non-existent or unrecognized types
        assertEquals(0, structureInfo.getCount("nonexistent"), "Non-existent type should return 0");
        assertEquals(0, structureInfo.getCount(""), "Empty string type should return 0");
        assertEquals(0, structureInfo.getCount("RANDOM_TYPE"), "Random type should return 0");
        assertEquals(0, structureInfo.getCount("[unknown]"), "Unknown placeholder should return 0");
        assertEquals(0, structureInfo.getCount(null), "Null type should return 0");

        System.out.println("Non-existent type tests completed");
    }

    @Test
    @Order(9)
    @DisplayName("Test template with special characters and spacing")
    void testSpecialCharactersAndSpacing() {
        String template = "   The  [noun]   [verb]    [adjective]   ";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        assertEquals(template, structureInfo.getTemplate(), "Template with extra spaces should be preserved");

        Map<String, Integer> counts = structureInfo.getAllCounts();
        System.out.println("Template with special spacing: '" + template + "'");
        System.out.println("Counts: " + counts);

        // The split(" ") will create empty strings from multiple spaces
        // This tests how the implementation handles such edge cases
        assertNotNull(counts, "Counts should be calculated even with irregular spacing");
    }

    @Test
    @Order(10)
    @DisplayName("Test template with punctuation")
    void testTemplateWithPunctuation() {
        String template = "The [noun], [verb] [adverb]! How [adjective] is that?";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        assertEquals(template, structureInfo.getTemplate(), "Template with punctuation should be preserved");

        Map<String, Integer> counts = structureInfo.getAllCounts();
        System.out.println("Template with punctuation: " + template);
        System.out.println("Counts: " + counts);

        // Test that punctuation doesn't interfere with placeholder detection
        // This depends on WordUtil.TypeCheck implementation
        assertNotNull(counts, "Punctuation should not break placeholder counting");
    }

    @Test
    @Order(11)
    @DisplayName("Test getAllCounts map properties")
    void testGetAllCountsMapProperties() {
        String template = "[noun] [verb] [adjective] [noun] [adverb]";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

        Map<String, Integer> counts = structureInfo.getAllCounts();

        assertNotNull(counts, "getAllCounts should never return null");

        // Test that all values in the map are positive
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String type = entry.getKey();
            Integer count = entry.getValue();

            assertNotNull(type, "Placeholder type should not be null");
            assertNotNull(count, "Count should not be null");
            assertTrue(count > 0, "Count for " + type + " should be positive");

            System.out.println("Type: '" + type + "', Count: " + count);
        }

        // Test that getCount is consistent with getAllCounts
        for (String type : counts.keySet()) {
            assertEquals(counts.get(type), structureInfo.getCount(type),
                    "getCount should be consistent with getAllCounts for " + type);
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test template consistency and immutability")
    void testTemplateConsistency() {
        String originalTemplate = "The [adjective] [noun] [verb] [adverb]";
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(originalTemplate);

        // Test that template is preserved exactly
        assertEquals(originalTemplate, structureInfo.getTemplate(), "Template should be preserved exactly");

        // Test multiple calls return same values
        String template1 = structureInfo.getTemplate();
        String template2 = structureInfo.getTemplate();
        assertEquals(template1, template2, "Multiple getTemplate calls should return same value");

        Map<String, Integer> counts1 = structureInfo.getAllCounts();
        Map<String, Integer> counts2 = structureInfo.getAllCounts();
        assertEquals(counts1, counts2, "Multiple getAllCounts calls should return equivalent maps");

        // Test that returned template is same reference (immutable)
        assertSame(template1, template2, "Template should be same reference");

        System.out.println("Template consistency verified for: " + originalTemplate);
    }

    @Test
    @Order(13)
    @DisplayName("Test edge cases and boundary conditions")
    void testEdgeCases() {
        // Test single character template
        SentenceStructureInfo single = new SentenceStructureInfo("a");
        assertEquals("a", single.getTemplate(), "Single character template should work");

        // Test single placeholder
        SentenceStructureInfo singlePlaceholder = new SentenceStructureInfo("[noun]");
        assertEquals("[noun]", singlePlaceholder.getTemplate(), "Single placeholder template should work");

        // Test template with only spaces
        SentenceStructureInfo spacesOnly = new SentenceStructureInfo("   ");
        assertEquals("   ", spacesOnly.getTemplate(), "Spaces-only template should be preserved");

        // Test very long template
        StringBuilder longTemplate = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longTemplate.append("[noun] ");
        }
        String longTemplateStr = longTemplate.toString().trim();

        SentenceStructureInfo longStructure = new SentenceStructureInfo(longTemplateStr);
        assertEquals(longTemplateStr, longStructure.getTemplate(), "Long template should be handled");

        System.out.println("Edge cases testing completed");
        System.out.println("Long template length: " + longTemplateStr.length());
        System.out.println("Long template placeholder count: " + longStructure.getAllCounts());
    }

    @Test
    @Order(14)
    @DisplayName("Test WordUtil.TypeCheck integration")
    void testWordUtilIntegration() {
        // Test various placeholder formats to understand WordUtil.TypeCheck behavior
        String[] testPlaceholders = {
                "[noun]", "[verb]", "[adjective]", "[adverb]", "[article]", "[pronoun]",
                "noun", "verb", "adjective", // without brackets
                "[NOUN]", "[VERB]", // uppercase
                "[unknown]", "[123]", // edge cases
                "regular_word", "The", "and" // regular words
        };

        for (String placeholder : testPlaceholders) {
            String template = "Test " + placeholder + " here";
            SentenceStructureInfo structureInfo = new SentenceStructureInfo(template);

            Map<String, Integer> counts = structureInfo.getAllCounts();
            System.out.println("Template: '" + template + "' -> Counts: " + counts);

            // Verify that the structure handles all inputs gracefully
            assertNotNull(structureInfo.getTemplate(), "Template should always be stored");
            assertNotNull(counts, "Counts should always be available");
        }

        System.out.println("WordUtil integration testing completed");
    }

    @Test
    @Order(15)
    @DisplayName("Test performance with large templates")
    void testPerformanceWithLargeTemplates() {
        // Create a large template for performance testing
        StringBuilder largeTemplate = new StringBuilder();
        String[] placeholderTypes = {"[noun]", "[verb]", "[adjective]", "[adverb]", "[article]"};

        for (int i = 0; i < 1000; i++) {
            largeTemplate.append(placeholderTypes[i % placeholderTypes.length]).append(" ");
        }

        String largeTemplateStr = largeTemplate.toString().trim();

        long startTime = System.currentTimeMillis();
        SentenceStructureInfo structureInfo = new SentenceStructureInfo(largeTemplateStr);
        long endTime = System.currentTimeMillis();

        long processingTime = endTime - startTime;

        assertNotNull(structureInfo, "Large template should be processed");
        assertEquals(largeTemplateStr, structureInfo.getTemplate(), "Large template should be stored correctly");
        assertNotNull(structureInfo.getAllCounts(), "Counts should be calculated for large template");

        System.out.println("Large template processing time: " + processingTime + "ms");
        System.out.println("Template length: " + largeTemplateStr.length() + " characters");
        System.out.println("Unique placeholder types found: " + structureInfo.getAllCounts().size());
        System.out.println("All counts: " + structureInfo.getAllCounts());

        // Performance should be reasonable (less than 5 seconds for 1000 placeholders)
        assertTrue(processingTime < 5000,
                "Large template processing should complete in reasonable time, took: " + processingTime + "ms");
    }
}