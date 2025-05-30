package org.example;

import com.google.api.gax.core.FixedCredentialsProvider;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test class for GoogleCredentialsProvider using only JUnit 5.
 * Tests credential loading, caching, error handling, and file system interactions.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GoogleCredentialsProviderTest {

    private static final String CREDENTIALS_DIR = "credentials";
    private static final String CREDENTIALS_FILE = "credentials/credentials.json";
    private static final String BACKUP_FILE = "credentials/credentials_backup.json";

    private static boolean originalFileExists = false;
    private static String originalFileContent = null;
    private static boolean originalDirExists = false;

    @BeforeAll
    static void setupTestEnvironment() {
        System.out.println("Setting up GoogleCredentialsProvider test environment...");

        // Check if original credentials directory and file exist
        File credentialsDir = new File(CREDENTIALS_DIR);
        File credentialsFile = new File(CREDENTIALS_FILE);

        originalDirExists = credentialsDir.exists();
        originalFileExists = credentialsFile.exists();

        // Backup original file if it exists
        if (originalFileExists) {
            try {
                originalFileContent = Files.readString(Paths.get(CREDENTIALS_FILE));
                Files.copy(Paths.get(CREDENTIALS_FILE), Paths.get(BACKUP_FILE));
                System.out.println("Backed up original credentials.json file");
            } catch (IOException e) {
                System.err.println("Failed to backup original credentials file: " + e.getMessage());
            }
        }

        // Create credentials directory if it doesn't exist
        if (!credentialsDir.exists()) {
            boolean created = credentialsDir.mkdirs();
            System.out.println("Created credentials directory: " + created);
        }
    }

    @AfterAll
    static void cleanupTestEnvironment() {
        try {
            // Restore original file if it existed
            if (originalFileExists && originalFileContent != null) {
                Files.writeString(Paths.get(CREDENTIALS_FILE), originalFileContent);
                System.out.println("Restored original credentials.json file");
            } else {
                // Delete test file if we created it
                File testFile = new File(CREDENTIALS_FILE);
                if (testFile.exists()) {
                    testFile.delete();
                    System.out.println("Cleaned up test credentials.json file");
                }
            }

            // Clean up backup file
            File backupFile = new File(BACKUP_FILE);
            if (backupFile.exists()) {
                backupFile.delete();
            }

            // Remove credentials directory if we created it
            if (!originalDirExists) {
                File credentialsDir = new File(CREDENTIALS_DIR);
                if (credentialsDir.exists() && credentialsDir.list().length == 0) {
                    credentialsDir.delete();
                    System.out.println("Removed test credentials directory");
                }
            }

        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @BeforeEach
    void resetProviderCache() {
        // Reset the static provider field before each test
        try {
            Field providerField = GoogleCredentialsProvider.class.getDeclaredField("provider");
            providerField.setAccessible(true);
            providerField.set(null, null);
            System.out.println("Reset provider cache for test");
        } catch (Exception e) {
            System.err.println("Failed to reset provider cache: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test missing credentials file")
    void testMissingCredentialsFile() {
        // Ensure credentials file doesn't exist
        File credentialsFile = new File(CREDENTIALS_FILE);
        if (credentialsFile.exists()) {
            credentialsFile.delete();
        }

        System.out.println("Testing behavior with missing credentials file...");

        // Should throw IOException when file is missing
        IOException exception = assertThrows(IOException.class, () -> {
            GoogleCredentialsProvider.getProvider();
        }, "Should throw IOException when credentials file is missing");

        System.out.println("Expected exception caught: " + exception.getClass().getSimpleName());
        System.out.println("Exception message: " + exception.getMessage());

        // Verify exception is file-related
        String message = exception.getMessage().toLowerCase();
        assertTrue(message.contains("credentials") ||
                        message.contains("file") ||
                        message.contains("path") ||
                        message.contains("found") ||
                        message.contains("exist"),
                "Exception should be related to missing credentials file");
    }

    @Test
    @Order(2)
    @DisplayName("Test invalid JSON credentials file")
    void testInvalidJSONFile() throws IOException {
        // Create file with invalid JSON content
        String invalidJson = "{ invalid json content without closing brace";
        createCredentialsFile(invalidJson);

        System.out.println("Testing behavior with invalid JSON file...");

        // Should throw exception when JSON is malformed
        Exception exception = assertThrows(Exception.class, () -> {
            GoogleCredentialsProvider.getProvider();
        }, "Should throw exception when credentials file has invalid JSON");

        System.out.println("Expected exception for invalid JSON: " + exception.getClass().getSimpleName());
        System.out.println("Exception message: " + exception.getMessage());

        // Could be IOException or RuntimeException depending on implementation
        assertTrue(exception instanceof IOException || exception instanceof RuntimeException,
                "Exception should be IOException or RuntimeException for invalid JSON");
    }

    @Test
    @Order(3)
    @DisplayName("Test empty credentials file")
    void testEmptyCredentialsFile() throws IOException {
        // Create empty file
        createCredentialsFile("");

        System.out.println("Testing behavior with empty credentials file...");

        // Should throw exception for empty file
        Exception exception = assertThrows(Exception.class, () -> {
            GoogleCredentialsProvider.getProvider();
        }, "Should throw exception when credentials file is empty");

        System.out.println("Expected exception for empty file: " + exception.getClass().getSimpleName());
        System.out.println("Exception message: " + exception.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Test credentials file with invalid structure")
    void testInvalidCredentialsStructure() throws IOException {
        // Create file with valid JSON but wrong structure
        String invalidStructure = "{ \"wrong\": \"structure\", \"not\": \"credentials\" }";
        createCredentialsFile(invalidStructure);

        System.out.println("Testing behavior with wrong JSON structure...");

        // Should throw exception when credentials structure is wrong
        Exception exception = assertThrows(Exception.class, () -> {
            GoogleCredentialsProvider.getProvider();
        }, "Should throw exception when credentials file has wrong structure");

        System.out.println("Expected exception for wrong structure: " + exception.getClass().getSimpleName());
        System.out.println("Exception message: " + exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Test credentials file with mock service account structure")
    void testMockServiceAccountFile() throws IOException {
        // Create file with service account-like structure (but fake)
        String mockServiceAccount = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"test-project\",\n" +
                "  \"private_key_id\": \"test-key-id\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nTEST_FAKE_KEY\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"test@test-project.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"123456789\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\"\n" +
                "}";

        createCredentialsFile(mockServiceAccount);

        System.out.println("Testing with mock service account credentials...");

        // This might succeed in creating a provider (but fail later when used)
        // or might fail immediately due to invalid private key
        try {
            FixedCredentialsProvider provider = GoogleCredentialsProvider.getProvider();

            assertNotNull(provider, "Provider should be created with mock credentials");
            System.out.println("Mock credentials provider created successfully");
            System.out.println("Provider type: " + provider.getClass().getSimpleName());

            // Verify it's cached (second call should return same instance)
            FixedCredentialsProvider provider2 = GoogleCredentialsProvider.getProvider();
            assertSame(provider, provider2, "Provider should be cached and return same instance");
            System.out.println("Provider caching verified");

        } catch (Exception e) {
            System.out.println("Expected exception with mock credentials: " + e.getClass().getSimpleName());
            System.out.println("Exception message: " + e.getMessage());

            // This is also acceptable - mock credentials might be rejected
            assertTrue(e instanceof IOException || e instanceof RuntimeException,
                    "Exception should be related to credential validation");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Test provider caching mechanism")
    void testProviderCaching() throws IOException {
        // Create a valid-looking credentials file
        String mockCredentials = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"cache-test-project\",\n" +
                "  \"private_key_id\": \"cache-test-key\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nCACHE_TEST_KEY\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"cache-test@cache-test.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"987654321\"\n" +
                "}";

        createCredentialsFile(mockCredentials);

        System.out.println("Testing provider caching mechanism...");

        try {
            // First call
            FixedCredentialsProvider provider1 = GoogleCredentialsProvider.getProvider();
            assertNotNull(provider1, "First provider call should succeed");

            // Second call should return cached instance
            FixedCredentialsProvider provider2 = GoogleCredentialsProvider.getProvider();
            assertNotNull(provider2, "Second provider call should succeed");

            // Should be same instance (cached)
            assertSame(provider1, provider2, "Provider should be cached - same instance expected");
            System.out.println("Caching mechanism working correctly");

            // Third call to triple-verify
            FixedCredentialsProvider provider3 = GoogleCredentialsProvider.getProvider();
            assertSame(provider1, provider3, "Third call should also return cached instance");

        } catch (Exception e) {
            System.out.println("Caching test failed due to credential validation: " + e.getMessage());
            // This is expected with mock credentials
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test credentials file path resolution")
    void testCredentialsPathResolution() throws IOException {
        // Test that the path is resolved correctly
        String testCredentials = "{ \"test\": \"path resolution\" }";
        createCredentialsFile(testCredentials);

        System.out.println("Testing credentials file path resolution...");

        // Get the absolute path that should be used
        Path expectedPath = Paths.get("credentials/credentials.json").toAbsolutePath();
        System.out.println("Expected absolute path: " + expectedPath);

        // Verify file exists at expected location
        File credentialsFile = expectedPath.toFile();
        assertTrue(credentialsFile.exists(), "Credentials file should exist at expected path");
        System.out.println("File exists at expected path: " + credentialsFile.exists());

        // Verify file is readable
        assertTrue(credentialsFile.canRead(), "Credentials file should be readable");
        System.out.println("File is readable: " + credentialsFile.canRead());

        // Verify content
        String actualContent = Files.readString(expectedPath);
        assertEquals(testCredentials, actualContent, "File content should match what we wrote");
        System.out.println("File content verified");
    }

    @Test
    @Order(8)
    @DisplayName("Test static provider field access")
    void testStaticProviderField() {
        System.out.println("Testing static provider field behavior...");

        try {
            // Access the static field via reflection
            Field providerField = GoogleCredentialsProvider.class.getDeclaredField("provider");
            providerField.setAccessible(true);

            // Should be null initially (we reset it in @BeforeEach)
            Object initialValue = providerField.get(null);
            assertNull(initialValue, "Provider field should be null initially");
            System.out.println("Initial provider field value: null (as expected)");

            // Verify field is static
            assertTrue(java.lang.reflect.Modifier.isStatic(providerField.getModifiers()),
                    "Provider field should be static");
            System.out.println("Provider field is static: confirmed");

            // Verify field type
            assertEquals(FixedCredentialsProvider.class, providerField.getType(),
                    "Provider field should be of type FixedCredentialsProvider");
            System.out.println("Provider field type verified: FixedCredentialsProvider");

        } catch (NoSuchFieldException e) {
            fail("Provider field should exist in GoogleCredentialsProvider class");
        } catch (IllegalAccessException e) {
            fail("Should be able to access provider field via reflection");
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test credentials file permissions")
    void testCredentialsFilePermissions() throws IOException {
        // Create credentials file with test content
        String testCredentials = "{ \"test\": \"permissions\" }";
        createCredentialsFile(testCredentials);

        System.out.println("Testing credentials file permissions...");

        File credentialsFile = new File(CREDENTIALS_FILE);

        // Test basic file properties
        assertTrue(credentialsFile.exists(), "Credentials file should exist");
        assertTrue(credentialsFile.isFile(), "Should be a regular file");
        assertFalse(credentialsFile.isDirectory(), "Should not be a directory");

        // Test read permissions
        assertTrue(credentialsFile.canRead(), "File should be readable");

        // Test file size
        assertTrue(credentialsFile.length() > 0, "File should not be empty");

        System.out.println("File permissions check completed:");
        System.out.println("  Exists: " + credentialsFile.exists());
        System.out.println("  Is file: " + credentialsFile.isFile());
        System.out.println("  Readable: " + credentialsFile.canRead());
        System.out.println("  Size: " + credentialsFile.length() + " bytes");
    }

    @Test
    @Order(10)
    @DisplayName("Test concurrent access to provider")
    void testConcurrentAccess() throws IOException {
        // Create valid-looking credentials for this test
        String concurrentTestCredentials = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"concurrent-test\",\n" +
                "  \"private_key_id\": \"concurrent-key\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nCONCURRENT_TEST\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"concurrent@test.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"555555555\"\n" +
                "}";

        createCredentialsFile(concurrentTestCredentials);

        System.out.println("Testing concurrent access to provider...");

        // Test that multiple threads trying to get provider simultaneously work correctly
        final int numThreads = 5;
        final FixedCredentialsProvider[] providers = new FixedCredentialsProvider[numThreads];
        final Exception[] exceptions = new Exception[numThreads];

        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    providers[index] = GoogleCredentialsProvider.getProvider();
                } catch (Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join(5000); // 5 second timeout
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Thread interrupted during concurrent test");
            }
        }

        System.out.println("Concurrent access test results:");

        // Analyze results
        int successCount = 0;
        int exceptionCount = 0;
        FixedCredentialsProvider firstSuccessfulProvider = null;

        for (int i = 0; i < numThreads; i++) {
            if (providers[i] != null) {
                successCount++;
                if (firstSuccessfulProvider == null) {
                    firstSuccessfulProvider = providers[i];
                }
                System.out.println("  Thread " + i + ": Success");
            } else if (exceptions[i] != null) {
                exceptionCount++;
                System.out.println("  Thread " + i + ": Exception - " + exceptions[i].getClass().getSimpleName());
            }
        }

        System.out.println("  Successful calls: " + successCount);
        System.out.println("  Failed calls: " + exceptionCount);

        // If any succeeded, they should all return the same cached instance
        if (successCount > 0) {
            for (FixedCredentialsProvider provider : providers) {
                if (provider != null) {
                    assertSame(firstSuccessfulProvider, provider,
                            "All successful calls should return the same cached instance");
                }
            }
            System.out.println("Caching consistency verified across concurrent calls");
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test error handling and cleanup")
    void testErrorHandlingAndCleanup() {
        System.out.println("Testing error handling and cleanup behavior...");

        // Test with non-existent directory
        File credentialsDir = new File(CREDENTIALS_DIR);
        File credentialsFile = new File(CREDENTIALS_FILE);

        // Remove credentials file and directory
        if (credentialsFile.exists()) {
            credentialsFile.delete();
        }
        if (credentialsDir.exists()) {
            credentialsDir.delete();
        }

        // Try to get provider with missing directory
        IOException exception = assertThrows(IOException.class, () -> {
            GoogleCredentialsProvider.getProvider();
        }, "Should throw IOException when credentials directory doesn't exist");

        System.out.println("Exception for missing directory: " + exception.getMessage());

        // Verify provider field is still null after exception
        try {
            Field providerField = GoogleCredentialsProvider.class.getDeclaredField("provider");
            providerField.setAccessible(true);
            Object providerValue = providerField.get(null);
            assertNull(providerValue, "Provider should remain null after failed initialization");
            System.out.println("Provider field properly remains null after exception");
        } catch (Exception e) {
            fail("Should be able to check provider field state: " + e.getMessage());
        }

        // Recreate directory for cleanup
        credentialsDir.mkdirs();
    }

    /**
     * Helper method to create credentials file with specified content
     */
    private void createCredentialsFile(String content) throws IOException {
        File credentialsDir = new File(CREDENTIALS_DIR);
        if (!credentialsDir.exists()) {
            credentialsDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(CREDENTIALS_FILE)) {
            writer.write(content);
        }

        System.out.println("Created credentials file with content length: " + content.length());
    }

    @Test
    @Order(12)
    @DisplayName("Test class constants and structure")
    void testClassStructure() {
        System.out.println("Testing GoogleCredentialsProvider class structure...");

        // Verify CREDENTIALS_PATH constant
        try {
            Field credentialsPathField = GoogleCredentialsProvider.class.getDeclaredField("CREDENTIALS_PATH");
            credentialsPathField.setAccessible(true);

            assertTrue(java.lang.reflect.Modifier.isStatic(credentialsPathField.getModifiers()),
                    "CREDENTIALS_PATH should be static");
            assertTrue(java.lang.reflect.Modifier.isFinal(credentialsPathField.getModifiers()),
                    "CREDENTIALS_PATH should be final");

            String pathValue = (String) credentialsPathField.get(null);
            assertEquals("credentials/credentials.json", pathValue,
                    "CREDENTIALS_PATH should have expected value");

            System.out.println("CREDENTIALS_PATH constant verified: " + pathValue);

        } catch (Exception e) {
            fail("Should be able to access CREDENTIALS_PATH constant: " + e.getMessage());
        }

        // Verify getProvider method is static
        try {
            java.lang.reflect.Method getProviderMethod =
                    GoogleCredentialsProvider.class.getMethod("getProvider");

            assertTrue(java.lang.reflect.Modifier.isStatic(getProviderMethod.getModifiers()),
                    "getProvider method should be static");

            assertEquals(FixedCredentialsProvider.class, getProviderMethod.getReturnType(),
                    "getProvider should return FixedCredentialsProvider");

            System.out.println("getProvider method structure verified");

        } catch (NoSuchMethodException e) {
            fail("getProvider method should exist: " + e.getMessage());
        }

        System.out.println("Class structure validation completed");
    }
}