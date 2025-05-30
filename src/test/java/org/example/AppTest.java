package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for App.java using only JUnit 5.
 * Tests the core business logic of sentence generation and toxicity analysis.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {

    private App app;

    @BeforeEach
    void setUp() {
        app = new App();
    }

    @Test
    @Order(1)
    @DisplayName("Test App initialization")
    void testAppInitialization() {
        assertNotNull(app, "App instance should be initialized");
    }

    @Test
    @Order(2)
    @DisplayName("Test SentenceResult class")
    void testSentenceResult() {
        String testStructure = "[article] [adjective] [noun] [verb]";
        String testText = "The quick fox jumps";
        double testToxicity = 0.25;

        App.SentenceResult result = new App.SentenceResult(testStructure, testText, testToxicity);

        assertNotNull(result, "SentenceResult should be created");
        assertEquals(testStructure, result.getStructure(), "Structure should match");
        assertEquals(testText, result.getText(), "Text should match");
        assertEquals(testToxicity, result.getToxicity(), 0.001, "Toxicity should match");
    }

    @Test
    @Order(3)
    @DisplayName("Test null input validation")
    void testNullInput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            app.generate(null);
        });

        assertTrue(exception.getMessage().contains("Invalid input"),
                "Should throw IllegalArgumentException for null input");
    }

    @Test
    @Order(4)
    @DisplayName("Test empty input validation")
    void testEmptyInput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            app.generate("");
        });

        assertTrue(exception.getMessage().contains("Invalid input"),
                "Should throw IllegalArgumentException for empty input");
    }

    @Test
    @Order(5)
    @DisplayName("Test whitespace-only input validation")
    void testWhitespaceInput() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            app.generate("   ");
        });

        assertTrue(exception.getMessage().contains("Invalid input"),
                "Should throw IllegalArgumentException for whitespace-only input");
    }

    @Test
    @Order(6)
    @DisplayName("Test input without letters validation")
    void testInputWithoutLetters() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            app.generate("123 456 789");
        });

        assertTrue(exception.getMessage().contains("Invalid input"),
                "Should throw IllegalArgumentException for input without letters");
    }

    @Test
    @Order(7)
    @DisplayName("Test valid simple input")
    void testValidSimpleInput() {
        try {
            System.out.println("Testing simple input: 'The cat sits'");

            List<App.SentenceResult> results = app.generate("The cat sits");

            assertNotNull(results, "Results should not be null");
            assertFalse(results.isEmpty(), "Results should not be empty");

            System.out.println("Generated " + results.size() + " sentences");

            for (int i = 0; i < results.size(); i++) {
                App.SentenceResult result = results.get(i);
                assertNotNull(result.getStructure(), "Structure should not be null");
                assertNotNull(result.getText(), "Text should not be null");
                assertFalse(result.getText().trim().isEmpty(), "Text should not be empty");
                assertTrue(result.getToxicity() >= 0.0 && result.getToxicity() <= 1.0,
                        "Toxicity should be between 0.0 and 1.0");

                System.out.println("Result " + (i+1) + ":");
                System.out.println("  Structure: " + result.getStructure());
                System.out.println("  Text: " + result.getText());
                System.out.println("  Toxicity: " + String.format("%.2f%%", result.getToxicity() * 100));
            }

        } catch (Exception e) {
            System.err.println("Error during generation: " + e.getMessage());
            e.printStackTrace();
            fail("Should not throw exception for valid input: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test longer input sentence")
    void testLongerInput() {
        try {
            String input = "The quick brown fox jumps over the lazy dog";
            System.out.println("Testing longer input: '" + input + "'");

            List<App.SentenceResult> results = app.generate(input);

            assertNotNull(results, "Results should not be null");
            assertFalse(results.isEmpty(), "Results should not be empty");

            System.out.println("Generated " + results.size() + " sentences from longer input");

            // Longer input should potentially generate more sentences
            for (App.SentenceResult result : results) {
                assertNotNull(result.getStructure(), "Structure should not be null");
                assertNotNull(result.getText(), "Text should not be null");
                assertFalse(result.getText().trim().isEmpty(), "Text should not be empty");
                assertTrue(result.getToxicity() >= 0.0 && result.getToxicity() <= 1.0,
                        "Toxicity should be between 0.0 and 1.0");

                // Verify text contains actual words
                String[] words = result.getText().split("\\s+");
                assertTrue(words.length > 0, "Generated text should contain words");

                System.out.println("Structure: " + result.getStructure());
                System.out.println("Text: " + result.getText());
                System.out.println("Toxicity: " + String.format("%.2f%%", result.getToxicity() * 100));
                System.out.println("---");
            }

        } catch (Exception e) {
            System.err.println("Error during generation with longer input: " + e.getMessage());
            e.printStackTrace();
            fail("Should not throw exception for valid longer input: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test input with punctuation")
    void testInputWithPunctuation() {
        try {
            String input = "Hello, world! How are you?";
            System.out.println("Testing input with punctuation: '" + input + "'");

            List<App.SentenceResult> results = app.generate(input);

            assertNotNull(results, "Results should not be null");
            assertFalse(results.isEmpty(), "Results should not be empty");

            System.out.println("Generated " + results.size() + " sentences from punctuated input");

            for (App.SentenceResult result : results) {
                assertNotNull(result.getStructure(), "Structure should not be null");
                assertNotNull(result.getText(), "Text should not be null");
                assertTrue(result.getToxicity() >= 0.0 && result.getToxicity() <= 1.0,
                        "Toxicity should be between 0.0 and 1.0");

                System.out.println("Structure: " + result.getStructure());
                System.out.println("Text: " + result.getText());
                System.out.println("Toxicity: " + String.format("%.2f%%", result.getToxicity() * 100));
            }

        } catch (Exception e) {
            System.err.println("Error during generation with punctuation: " + e.getMessage());
            e.printStackTrace();
            fail("Should not throw exception for input with punctuation: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test various input types")
    void testVariousInputTypes() {
        String[] testInputs = {
                "Beautiful sunny day",
                "The dog runs quickly",
                "Amazing wonderful fantastic",
                "She sings beautifully every morning",
                "Technology advances rapidly nowadays"
        };

        for (String input : testInputs) {
            try {
                System.out.println("\nTesting input: '" + input + "'");

                List<App.SentenceResult> results = app.generate(input);

                assertNotNull(results, "Results should not be null for input: " + input);
                assertFalse(results.isEmpty(), "Results should not be empty for input: " + input);

                for (App.SentenceResult result : results) {
                    assertNotNull(result.getStructure(), "Structure should not be null");
                    assertNotNull(result.getText(), "Text should not be null");
                    assertTrue(result.getToxicity() >= 0.0 && result.getToxicity() <= 1.0,
                            "Toxicity should be valid for input: " + input);

                    System.out.println("  Generated: " + result.getText() +
                            " (Toxicity: " + String.format("%.2f%%", result.getToxicity() * 100) + ")");
                }

            } catch (Exception e) {
                System.err.println("Error with input '" + input + "': " + e.getMessage());
                // Don't fail here as some inputs might legitimately cause errors
                // depending on Google Cloud API availability or network issues
            }
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test toxicity range validation")
    void testToxicityRange() {
        try {
            List<App.SentenceResult> results = app.generate("Happy peaceful wonderful day");

            assertNotNull(results, "Results should not be null");

            for (App.SentenceResult result : results) {
                double toxicity = result.getToxicity();
                assertTrue(toxicity >= 0.0 && toxicity <= 1.0,
                        "Toxicity should be between 0.0 and 1.0, but was: " + toxicity);
                assertTrue(Double.isFinite(toxicity), "Toxicity should be a finite number");
                System.out.println("Validated toxicity: " + String.format("%.4f", toxicity));
            }

        } catch (Exception e) {
            System.err.println("Error during toxicity range test: " + e.getMessage());
            e.printStackTrace();
            fail("Should not throw exception during toxicity validation: " + e.getMessage());
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test result consistency")
    void testResultConsistency() {
        try {
            String testInput = "The friendly cat sleeps peacefully";

            // Generate results multiple times
            List<App.SentenceResult> results1 = app.generate(testInput);
            List<App.SentenceResult> results2 = app.generate(testInput);

            assertNotNull(results1, "First results should not be null");
            assertNotNull(results2, "Second results should not be null");

            // Results should have same structure (same number of sentences)
            assertEquals(results1.size(), results2.size(),
                    "Number of results should be consistent");

            // All results should have valid structure
            for (int i = 0; i < results1.size(); i++) {
                App.SentenceResult r1 = results1.get(i);
                App.SentenceResult r2 = results2.get(i);

                assertNotNull(r1.getStructure(), "First result structure should not be null");
                assertNotNull(r2.getStructure(), "Second result structure should not be null");
                assertNotNull(r1.getText(), "First result text should not be null");
                assertNotNull(r2.getText(), "Second result text should not be null");

                assertTrue(r1.getToxicity() >= 0.0 && r1.getToxicity() <= 1.0,
                        "First result toxicity should be valid");
                assertTrue(r2.getToxicity() >= 0.0 && r2.getToxicity() <= 1.0,
                        "Second result toxicity should be valid");

                System.out.println("Run 1 - Structure: " + r1.getStructure() + ", Text: " + r1.getText());
                System.out.println("Run 2 - Structure: " + r2.getStructure() + ", Text: " + r2.getText());
            }

        } catch (Exception e) {
            System.err.println("Error during consistency test: " + e.getMessage());
            e.printStackTrace();
            fail("Should not throw exception during consistency test: " + e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Test performance with reasonable timeout")
    void testPerformance() {
        try {
            String testInput = "Quick performance test sentence";

            long startTime = System.currentTimeMillis();
            List<App.SentenceResult> results = app.generate(testInput);
            long endTime = System.currentTimeMillis();

            long duration = endTime - startTime;

            assertNotNull(results, "Results should not be null");
            assertFalse(results.isEmpty(), "Results should not be empty");

            System.out.println("Processing completed in: " + duration + "ms");

            // Processing should complete in reasonable time (< 60 seconds)
            // This accounts for Google Cloud API calls which can be slow
            assertTrue(duration < 60000,
                    "Processing should complete in less than 60 seconds, but took: " + duration + "ms");

        } catch (Exception e) {
            System.err.println("Error during performance test: " + e.getMessage());
            e.printStackTrace();
            fail("Should not throw exception during performance test: " + e.getMessage());
        }
    }

    @Test
    @Order(14)
    @DisplayName("Test result structure completeness")
    void testResultStructureCompleteness() {
        try {
            List<App.SentenceResult> results = app.generate("Complete structure test");

            assertNotNull(results, "Results should not be null");
            assertFalse(results.isEmpty(), "Results should not be empty");

            for (App.SentenceResult result : results) {
                // Test structure field
                String structure = result.getStructure();
                assertNotNull(structure, "Structure should not be null");
                assertFalse(structure.trim().isEmpty(), "Structure should not be empty");

                // Test text field
                String text = result.getText();
                assertNotNull(text, "Text should not be null");
                assertFalse(text.trim().isEmpty(), "Text should not be empty");

                // Text should contain actual words (not just whitespace or punctuation)
                assertTrue(text.matches(".*[a-zA-Z]+.*"), "Text should contain letters");

                // Test toxicity field
                double toxicity = result.getToxicity();
                assertTrue(Double.isFinite(toxicity), "Toxicity should be a finite number");
                assertTrue(toxicity >= 0.0, "Toxicity should not be negative");
                assertTrue(toxicity <= 1.0, "Toxicity should not exceed 1.0");

                // Text should be properly capitalized
                assertTrue(Character.isUpperCase(text.charAt(0)),
                        "First character should be uppercase");

                System.out.println("Validated result:");
                System.out.println("  Structure: " + structure);
                System.out.println("  Text: " + text);
                System.out.println("  Toxicity: " + String.format("%.4f", toxicity));
                System.out.println("  Text length: " + text.length());
                System.out.println("  Word count: " + text.split("\\s+").length);
            }

        } catch (Exception e) {
            System.err.println("Error during structure completeness test: " + e.getMessage());
            e.printStackTrace();
            fail("Should not throw exception during structure test: " + e.getMessage());
        }
    }

    @Test
    @Order(15)
    @DisplayName("Test edge cases")
    void testEdgeCases() {
        // Test with minimal valid input
        try {
            System.out.println("Testing minimal valid input...");
            List<App.SentenceResult> results = app.generate("A");
            assertNotNull(results, "Should handle single letter input");

        } catch (Exception e) {
            System.out.println("Single letter input caused exception: " + e.getMessage());
            // This might be expected depending on Google Cloud API requirements
        }

        // Test with mixed case
        try {
            System.out.println("Testing mixed case input...");
            List<App.SentenceResult> results = app.generate("ThE qUiCk BrOwN fOx");
            assertNotNull(results, "Should handle mixed case input");

            for (App.SentenceResult result : results) {
                System.out.println("Mixed case result: " + result.getText());
            }

        } catch (Exception e) {
            System.err.println("Mixed case input error: " + e.getMessage());
            fail("Should handle mixed case input: " + e.getMessage());
        }

        // Test with numbers and letters
        try {
            System.out.println("Testing input with numbers and letters...");
            List<App.SentenceResult> results = app.generate("The cat has 4 legs");
            assertNotNull(results, "Should handle input with numbers");

            for (App.SentenceResult result : results) {
                System.out.println("Numbers result: " + result.getText());
            }

        } catch (Exception e) {
            System.err.println("Input with numbers error: " + e.getMessage());
            fail("Should handle input with numbers: " + e.getMessage());
        }
    }
}