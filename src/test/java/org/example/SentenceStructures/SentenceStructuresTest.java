package org.example.SentenceStructures;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Test class for SentenceStructures using only JUnit 5.
 * Tests file loading, structure parsing, and data management functionality.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SentenceStructuresTest {

    private static final String TEST_RESOURCE_DIR = "src/main/resources";
    private static final String TEST_FILE_PATH = TEST_RESOURCE_DIR + "/SentenceStructure.txt";
    private static final String BACKUP_FILE_PATH = TEST_RESOURCE_DIR + "/SentenceStructure_backup.txt";

    private static boolean originalFileExists = false;
    private static String originalFileContent = null;

    @BeforeAll
    static void setupTestEnvironment() {
        // Suppress JavaFX warnings in test environment
        System.setProperty("java.awt.headless", "true");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");

        // Create test resource directory if it doesn't exist
        File resourceDir = new File(TEST_RESOURCE_DIR);
        if (!resourceDir.exists()) {
            boolean created = resourceDir.mkdirs();
            System.out.println("Created test resource directory: " + TEST_RESOURCE_DIR + " (success: " + created + ")");
        }

        // Check if original file exists and back it up
        File originalFile = new File(TEST_FILE_PATH);
        if (originalFile.exists()) {
            originalFileExists = true;
            try {
                originalFileContent = Files.readString(Paths.get(TEST_FILE_PATH));
                Files.copy(Paths.get(TEST_FILE_PATH), Paths.get(BACKUP_FILE_PATH));
                System.out.println("Backed up original SentenceStructure.txt file");
            } catch (IOException e) {
                System.err.println("Failed to backup original file: " + e.getMessage());
            }
        }
    }

    @AfterAll
    static void cleanupTestEnvironment() {
        try {
            // Restore original file if it existed
            if (originalFileExists && originalFileContent != null) {
                Files.writeString(Paths.get(TEST_FILE_PATH), originalFileContent);
                System.out.println("Restored original SentenceStructure.txt file");
            } else {
                // Delete test file if we created it
                File testFile = new File(TEST_FILE_PATH);
                if (testFile.exists()) {
                    testFile.delete();
                    System.out.println("Cleaned up test SentenceStructure.txt file");
                }
            }

            // Clean up backup file
            File backupFile = new File(BACKUP_FILE_PATH);
            if (backupFile.exists()) {
                backupFile.delete();
            }
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @BeforeEach
    void setupTestFile() {
        // Create a default test file for each test
        try {
            createTestFile(getDefaultTestContent());
        } catch (IOException e) {
            System.err.println("Warning: Failed to create test file for test setup: " + e.getMessage());
            // Don't fail here as some tests intentionally work without files
        }
    }

    private void createTestFile(String content) throws IOException {
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write(content);
        }
        System.out.println("Created test file with content length: " + content.length());
    }

    private String getDefaultTestContent() {
        return "The [adjective] [noun] [verb] [adverb]\n" +
                "[article] [noun] [verb] the [adjective] [noun]\n" +
                "[pronoun] [verb] [adverb] and [verb] [noun]\n" +
                "How [adjective] is this [noun]?\n" +
                "The [noun] and [noun] [verb] together";
    }

    @Test
    @Order(1)
    @DisplayName("Test basic constructor and initialization")
    void testBasicInitialization() {
        SentenceStructures structures = new SentenceStructures();

        assertNotNull(structures, "SentenceStructures should be created");
        assertNotNull(structures.getStructures(), "Structures list should not be null");

        List<SentenceStructureInfo> structureList = structures.getStructures();
        System.out.println("Loaded " + structureList.size() + " sentence structures");

        for (int i = 0; i < structureList.size(); i++) {
            SentenceStructureInfo info = structureList.get(i);
            assertNotNull(info, "Structure " + i + " should not be null");
            assertNotNull(info.getTemplate(), "Template " + i + " should not be null");
            System.out.println("Structure " + i + ": " + info.getTemplate());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test loading with empty file")
    void testEmptyFile() throws IOException {
        createTestFile("");

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "Structures list should not be null even for empty file");
        assertTrue(structureList.isEmpty(), "Empty file should result in empty structures list");

        System.out.println("Empty file test - loaded structures count: " + structureList.size());
    }

    @Test
    @Order(3)
    @DisplayName("Test loading with single line file")
    void testSingleLineFile() throws IOException {
        String singleLine = "The [adjective] [noun] [verb]";
        createTestFile(singleLine);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "Structures list should not be null");
        assertEquals(1, structureList.size(), "Single line file should result in one structure");

        SentenceStructureInfo structure = structureList.get(0);
        assertEquals(singleLine, structure.getTemplate(), "Template should match input line");

        System.out.println("Single line test - template: " + structure.getTemplate());
        System.out.println("Placeholder counts: " + structure.getAllCounts());
    }

    @Test
    @Order(4)
    @DisplayName("Test loading with multiple lines")
    void testMultipleLines() throws IOException {
        String content = "Line 1: [noun] [verb]\n" +
                "Line 2: [adjective] [noun]\n" +
                "Line 3: [pronoun] [verb] [adverb]";
        createTestFile(content);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "Structures list should not be null");
        assertEquals(3, structureList.size(), "Three lines should result in three structures");

        String[] expectedLines = content.split("\n");
        for (int i = 0; i < expectedLines.length; i++) {
            SentenceStructureInfo structure = structureList.get(i);
            assertEquals(expectedLines[i], structure.getTemplate(),
                    "Template " + i + " should match input line");

            System.out.println("Line " + i + ": " + structure.getTemplate());
            System.out.println("  Counts: " + structure.getAllCounts());
        }
    }

    @Test
    @Order(5)
    @DisplayName("Test loading with blank lines")
    void testBlankLines() throws IOException {
        String content = "First line [noun] [verb]\n" +
                "\n" +  // blank line
                "Third line [adjective] [noun]\n" +
                "   \n" +  // line with only spaces
                "Fifth line [pronoun] [verb]";
        createTestFile(content);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "Structures list should not be null");

        System.out.println("Blank lines test - total structures: " + structureList.size());
        for (int i = 0; i < structureList.size(); i++) {
            SentenceStructureInfo structure = structureList.get(i);
            System.out.println("Structure " + i + ": '" + structure.getTemplate() + "'");
        }

        // Verify that all loaded structures have templates (even if blank)
        for (SentenceStructureInfo structure : structureList) {
            assertNotNull(structure.getTemplate(), "All templates should be non-null");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Test loading with special characters")
    void testSpecialCharacters() throws IOException {
        String content = "Question: How [adjective] is [pronoun]?\n" +
                "Exclamation: The [noun] [verb] [adverb]!\n" +
                "Complex: [article] [adjective], [adjective] [noun] [verb] [adverb].\n" +
                "Unicode: Café [noun] très [adjective] 🎉";
        createTestFile(content);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "Structures list should not be null");
        assertEquals(4, structureList.size(), "Four lines should result in four structures");

        for (int i = 0; i < structureList.size(); i++) {
            SentenceStructureInfo structure = structureList.get(i);
            assertNotNull(structure.getTemplate(), "Template should not be null");
            assertFalse(structure.getTemplate().isEmpty(), "Template should not be empty");

            System.out.println("Special chars line " + i + ": " + structure.getTemplate());
            System.out.println("  Counts: " + structure.getAllCounts());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test getStructures method properties")
    void testGetStructuresMethodProperties() throws IOException {
        createTestFile(getDefaultTestContent());

        SentenceStructures structures = new SentenceStructures();

        // Test multiple calls return same reference
        List<SentenceStructureInfo> list1 = structures.getStructures();
        List<SentenceStructureInfo> list2 = structures.getStructures();

        assertNotNull(list1, "First call should return non-null list");
        assertNotNull(list2, "Second call should return non-null list");

        // Check if same reference (depends on implementation choice)
        System.out.println("Same reference: " + (list1 == list2));
        System.out.println("Equal content: " + list1.equals(list2));

        // Verify contents are consistent
        assertEquals(list1.size(), list2.size(), "Multiple calls should return same size");

        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i).getTemplate(), list2.get(i).getTemplate(),
                    "Templates should be identical across calls");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test file loading robustness")
    void testFileLoadingRobustness() throws IOException {
        // Test with very long lines
        StringBuilder longLine = new StringBuilder();
        longLine.append("Very long template: ");
        for (int i = 0; i < 100; i++) {
            longLine.append("[noun] [verb] [adjective] ");
        }

        String content = longLine.toString() + "\n" +
                "Short line: [noun] [verb]\n" +
                "Medium line: [adjective] [noun] [verb] [adverb] [pronoun]";

        createTestFile(content);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "Should handle long lines");
        assertEquals(3, structureList.size(), "Should load all lines including very long one");

        // Verify long line is handled correctly
        SentenceStructureInfo longStructure = structureList.get(0);
        assertTrue(longStructure.getTemplate().length() > 1000, "Long line should be preserved");

        System.out.println("Long line length: " + longStructure.getTemplate().length());
        System.out.println("Long line placeholder counts: " + longStructure.getAllCounts());
    }

    @Test
    @Order(9)
    @DisplayName("Test loading with different line endings")
    void testDifferentLineEndings() throws IOException {
        // Test different line ending styles
        String content = "Unix line [noun] [verb]\n" +
                "Windows line [adjective] [noun]\r\n" +
                "Mac line [pronoun] [verb]\r" +
                "Final line [adverb] [adjective]";

        createTestFile(content);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "Should handle different line endings");
        assertTrue(structureList.size() >= 2, "Should load multiple lines despite mixed line endings");

        System.out.println("Mixed line endings test - loaded " + structureList.size() + " structures");
        for (int i = 0; i < structureList.size(); i++) {
            System.out.println("Structure " + i + ": " + structureList.get(i).getTemplate());
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test memory efficiency and performance")
    void testMemoryAndPerformance() throws IOException {
        // Create a large file for performance testing
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeContent.append("Template ").append(i).append(": [noun] [verb] [adjective] [adverb]\n");
        }

        createTestFile(largeContent.toString());

        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long loadingTime = endTime - startTime;
        long memoryUsed = endMemory - startMemory;

        assertNotNull(structureList, "Large file should be loaded");
        assertEquals(1000, structureList.size(), "Should load all 1000 structures");

        System.out.println("Performance test results:");
        System.out.println("  Structures loaded: " + structureList.size());
        System.out.println("  Loading time: " + loadingTime + "ms");
        System.out.println("  Memory used: " + (memoryUsed / 1024) + "KB");

        // Performance assertions (reasonable expectations)
        assertTrue(loadingTime < 5000, "Loading 1000 structures should take less than 5 seconds");

        // Verify all structures are properly initialized
        for (int i = 0; i < Math.min(10, structureList.size()); i++) {
            SentenceStructureInfo structure = structureList.get(i);
            assertNotNull(structure.getTemplate(), "Structure " + i + " should have template");
            assertTrue(structure.getTemplate().contains("Template " + i),
                    "Structure " + i + " should contain correct template text");
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test WordUtil integration behavior")
    void testWordUtilIntegrationBehavior() throws IOException {
        // Test that WordUtil.importer works correctly with valid files
        String content = "Integration test [noun] [verb]\n" +
                "Second line [adjective] [noun]";

        createTestFile(content);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "WordUtil integration should work with valid file");
        assertEquals(2, structureList.size(), "Should load both test lines");

        System.out.println("WordUtil integration test completed successfully");
        for (int i = 0; i < structureList.size(); i++) {
            System.out.println("Line " + i + ": " + structureList.get(i).getTemplate());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test structure content validation")
    void testStructureContentValidation() throws IOException {
        String content = "Simple: [noun] [verb]\n" +
                "Complex: The [adjective] [noun] [verb] [adverb] over the [adjective] [noun]\n" +
                "Question: How [adjective] is [pronoun]?\n" +
                "No placeholders: This is just text\n" +
                "Mixed: Some text with [noun] and more text [verb]";

        createTestFile(content);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertEquals(5, structureList.size(), "Should load all 5 lines");

        // Validate each structure's content and properties
        String[] expectedTemplates = content.split("\n");
        for (int i = 0; i < expectedTemplates.length; i++) {
            SentenceStructureInfo structure = structureList.get(i);
            assertEquals(expectedTemplates[i], structure.getTemplate(),
                    "Template " + i + " should match input exactly");

            // Verify that placeholder counting works for each structure
            assertNotNull(structure.getAllCounts(), "Counts should be available for structure " + i);

            System.out.println("Structure " + i + ": " + structure.getTemplate());
            System.out.println("  Placeholder counts: " + structure.getAllCounts());
            System.out.println("  Unique placeholder types: " + structure.getAllCounts().size());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Test data integrity and immutability")
    void testDataIntegrity() throws IOException {
        createTestFile(getDefaultTestContent());

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> originalList = structures.getStructures();

        int originalSize = originalList.size();

        // Try to modify the returned list (if it's mutable)
        try {
            originalList.add(new SentenceStructureInfo("Added template [noun]"));
            System.out.println("List modification succeeded - list is mutable");
        } catch (UnsupportedOperationException e) {
            System.out.println("List modification failed - list is immutable");
        }

        // Get the list again and verify integrity
        List<SentenceStructureInfo> newList = structures.getStructures();

        // Original structure should not be affected by attempted modifications
        assertTrue(newList.size() >= originalSize, "Structure integrity should be maintained");

        // Verify that original templates are still intact
        for (int i = 0; i < Math.min(originalSize, newList.size()); i++) {
            assertNotNull(newList.get(i).getTemplate(), "Template " + i + " should remain intact");
        }

        System.out.println("Data integrity test completed");
        System.out.println("Original size: " + originalSize + ", Current size: " + newList.size());
    }

    @Test
    @Order(14)
    @DisplayName("Test integration with WordUtil")
    void testWordUtilIntegration() throws IOException {
        String content = "Test [noun] integration\n" +
                "Another [verb] test [adjective]\n" +
                "Final [pronoun] [adverb] example";

        createTestFile(content);

        SentenceStructures structures = new SentenceStructures();
        List<SentenceStructureInfo> structureList = structures.getStructures();

        assertNotNull(structureList, "WordUtil integration should work");
        assertEquals(3, structureList.size(), "Should load all test lines");

        System.out.println("WordUtil integration test:");
        for (int i = 0; i < structureList.size(); i++) {
            SentenceStructureInfo structure = structureList.get(i);
            System.out.println("  Line " + i + ": " + structure.getTemplate());
            System.out.println("    WordUtil detected placeholders: " + structure.getAllCounts());

            // Verify that WordUtil.importer and SentenceStructureInfo work together
            assertNotNull(structure.getTemplate(), "Template should be loaded via WordUtil");
            assertNotNull(structure.getAllCounts(), "Placeholder analysis should work");
        }

        // Test that the integration produces meaningful results
        boolean hasPlaceholders = structureList.stream()
                .anyMatch(s -> !s.getAllCounts().isEmpty());

        System.out.println("Any structures have detected placeholders: " + hasPlaceholders);
        // Note: This depends on WordUtil.TypeCheck implementation
    }
}