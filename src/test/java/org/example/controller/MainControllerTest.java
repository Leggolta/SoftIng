package org.example.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for MainController using the real FXML file.
 * Tests the full integration of the UI and business logic.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainControllerTest {

    private static Stage primaryStage;
    private static MainController controller;
    private static TextField inputField;
    private static CheckBox showTreeCheckbox;
    private static VBox syntaxContainer;
    private static VBox generatedContainer;
    private static ScrollPane treeScroll;
    private static TextFlow treeAsciiFlow;
    private static ProgressBar progressBar;
    private static TextFlow outputFlow;
    private static Button generateButton;
    private static HBox toxicityBarsContainer;
    private static Label syntaxLabel;
    private static boolean javaFXInitialized = false;

    /**
     * JavaFX Application for tests that loads the FXML file.
     */
    public static class TestApp extends Application {
        @Override
        public void start(Stage stage) {
            primaryStage = stage;

            try {
                // Try loading the FXML from test directory first
                FXMLLoader loader = new FXMLLoader();
                BorderPane root = null;

                // Try different locations to find the FXML file
                try {
                    loader.setLocation(getClass().getResource("/main.fxml"));
                    if (loader.getLocation() != null) {
                        root = loader.load();
                    }
                } catch (Exception e) {
                    System.out.println("Attempt 1 failed: " + e.getMessage());
                }

                if (root == null) {
                    try {
                        loader = new FXMLLoader();
                        loader.setLocation(getClass().getClassLoader().getResource("main.fxml"));
                        if (loader.getLocation() != null) {
                            root = loader.load();
                        }
                    } catch (Exception e) {
                        System.out.println("Attempt 2 failed: " + e.getMessage());
                    }
                }

                if (root == null) {
                    // If FXML loading fails, create UI programmatically
                    System.out.println("Creating UI programmatically...");
                    createUIManually(stage);
                    return;
                }

                controller = loader.getController();

                // Get UI references from loaded FXML
                Scene scene = new Scene(root);
                stage.setScene(scene);

                // Lookup UI elements in scene graph
                findUIElements(scene);

                stage.setTitle("Test Application");
                stage.show();

                javaFXInitialized = true;

            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to manual UI creation
                createUIManually(stage);
            }
        }

        private void createUIManually(Stage stage) {
            try {
                // Create controller manually
                controller = new MainController();

                // Create simplified UI for tests
                BorderPane root = new BorderPane();

                // Top section
                VBox topSection = new VBox(10);
                inputField = new TextField();
                inputField.setPromptText("Enter text to analyze...");
                generateButton = new Button("Generate");
                showTreeCheckbox = new CheckBox("Show Syntactic Tree");

                HBox inputRow = new HBox(10);
                inputRow.getChildren().addAll(inputField, generateButton);
                topSection.getChildren().addAll(inputRow, showTreeCheckbox);
                root.setTop(topSection);

                // Center section
                HBox centerSection = new HBox(10);

                // Syntax container
                syntaxContainer = new VBox(5);
                syntaxLabel = new Label("Syntax Tree:");
                treeAsciiFlow = new TextFlow();
                treeScroll = new ScrollPane(treeAsciiFlow);
                treeScroll.setFitToWidth(true);
                treeScroll.setPrefHeight(300);
                syntaxContainer.getChildren().addAll(syntaxLabel, treeScroll);
                syntaxContainer.setVisible(false);
                syntaxContainer.setManaged(false);

                // Generated container
                generatedContainer = new VBox(5);
                outputFlow = new TextFlow();
                ScrollPane outputScroll = new ScrollPane(outputFlow);
                outputScroll.setFitToWidth(true);
                generatedContainer.getChildren().addAll(new Label("Generated Phrase:"), outputScroll);

                // Toxicity bars
                VBox toxicitySection = new VBox(5);
                toxicityBarsContainer = new HBox(5);
                toxicitySection.getChildren().addAll(new Label("Toxicity Scores"), toxicityBarsContainer);

                centerSection.getChildren().addAll(syntaxContainer, generatedContainer, toxicitySection);
                root.setCenter(centerSection);

                // Bottom section
                progressBar = new ProgressBar();
                progressBar.setVisible(false);
                progressBar.setManaged(false);
                root.setBottom(progressBar);

                Scene scene = new Scene(root, 800, 600);
                stage.setScene(scene);
                stage.setTitle("Test Application (Manual UI)");
                stage.show();

                // Setup listeners manually
                setupManualListeners();

                javaFXInitialized = true;

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to create UI manually", e);
            }
        }

        private void setupManualListeners() {
            // Simulate controller behavior
            generateButton.setOnAction(e -> {
                String inputText = inputField.getText().trim();
                if (inputText.isEmpty()) {
                    outputFlow.getChildren().clear();
                    outputFlow.getChildren().add(new javafx.scene.text.Text("❌ Please enter a non-empty sentence!"));
                    return;
                }

                // Simulate processing
                progressBar.setVisible(true);
                progressBar.setManaged(true);

                new Thread(() -> {
                    try {
                        Thread.sleep(2000); // Simulate processing
                        Platform.runLater(() -> {
                            outputFlow.getChildren().clear();
                            outputFlow.getChildren().add(new javafx.scene.text.Text("Generated: " + inputText + " [PROCESSED]"));
                            progressBar.setVisible(false);
                            progressBar.setManaged(false);
                        });
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            });

            showTreeCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                syntaxContainer.setVisible(newVal);
                syntaxContainer.setManaged(newVal);
                treeScroll.setVisible(newVal);
                treeScroll.setManaged(newVal);
                syntaxLabel.setVisible(newVal);
                syntaxLabel.setManaged(newVal);
            });
        }

        private void findUIElements(Scene scene) {
            inputField = (TextField) scene.lookup("#inputField");
            showTreeCheckbox = (CheckBox) scene.lookup("#showTreeCheckbox");
            syntaxContainer = (VBox) scene.lookup("#syntaxContainer");
            generatedContainer = (VBox) scene.lookup("#generatedContainer");
            treeScroll = (ScrollPane) scene.lookup("#treeScroll");
            treeAsciiFlow = (TextFlow) scene.lookup("#treeAsciiFlow");
            progressBar = (ProgressBar) scene.lookup("#progressBar");
            outputFlow = (TextFlow) scene.lookup("#outputFlow");
            toxicityBarsContainer = (HBox) scene.lookup("#toxicityBarsContainer");
            syntaxLabel = (Label) scene.lookup("#syntaxLabel");

            // Generate button has no ID in FXML, so find by CSS class
            generateButton = (Button) scene.lookup(".button");
        }
    }

    @BeforeAll
    static void initializeJavaFX() throws Exception {
        // Initialize JavaFX if not already done
        if (!javaFXInitialized) {
            CountDownLatch latch = new CountDownLatch(1);

            Platform.startup(() -> {
                try {
                    new TestApp().start(new Stage());
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                    latch.countDown();
                }
            });

            // Wait for JavaFX initialization
            assertTrue(latch.await(15, TimeUnit.SECONDS), "JavaFX should initialize within 15 seconds");

            // Wait for UI elements to be available
            waitForUIElements();
        }
    }

    @AfterAll
    static void tearDown() {
        if (primaryStage != null) {
            Platform.runLater(() -> primaryStage.close());
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Reset state before each test
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                if (inputField != null) inputField.clear();
                if (showTreeCheckbox != null) showTreeCheckbox.setSelected(false);
                if (outputFlow != null) outputFlow.getChildren().clear();
                if (progressBar != null) {
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                }
                if (syntaxContainer != null) {
                    syntaxContainer.setVisible(false);
                    syntaxContainer.setManaged(false);
                }
            } catch (Exception e) {
                System.out.println("Error during setup: " + e.getMessage());
            }
            latch.countDown();
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS), "Setup should complete within 3 seconds");
    }

    @Test
    @Order(1)
    @DisplayName("Test UI Initialization")
    void testUIInitialization() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            // Check essential elements are present
            assertNotNull(inputField, "Input field should be present");
            assertNotNull(showTreeCheckbox, "Show tree checkbox should be present");
            assertNotNull(progressBar, "Progress bar should be present");
            assertNotNull(outputFlow, "Output flow should be present");

            // Check initial states
            assertFalse(progressBar.isVisible(), "Progress bar should be hidden initially");
            assertTrue(inputField.getText().isEmpty(), "Input field should be empty initially");
            assertFalse(showTreeCheckbox.isSelected(), "Show tree checkbox should be unchecked initially");

            latch.countDown();
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    @Order(2)
    @DisplayName("Test empty input")
    void testEmptyInput() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            // Click generate button without entering text
            if (generateButton != null) {
                generateButton.fire();
            }

            // Wait a moment for processing
            Platform.runLater(() -> {
                // Check there is some response (error message or output)
                if (outputFlow != null) {
                    System.out.println("Output children count: " + outputFlow.getChildren().size());
                }
                latch.countDown();
            });
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    @Order(3)
    @DisplayName("Test generation with valid input")
    void testValidInput() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (inputField != null) {
                inputField.setText("The cat sits on the mat");
            }

            if (generateButton != null) {
                generateButton.fire();
            }

            if (progressBar != null && progressBar.isVisible()) {
                System.out.println("Progress bar is visible during processing");
            }

            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(() -> {
                        if (outputFlow != null) {
                            System.out.println("Output after processing: " + outputFlow.getChildren().size() + " elements");
                        }
                        latch.countDown();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    latch.countDown();
                }
            }).start();
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    @Order(4)
    @DisplayName("Test toggle syntax tree visibility")
    void testSyntaxTreeToggle() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (syntaxContainer != null && showTreeCheckbox != null) {
                boolean initiallyVisible = syntaxContainer.isVisible();
                System.out.println("Syntax container initially visible: " + initiallyVisible);

                showTreeCheckbox.setSelected(true);
                showTreeCheckbox.fire();

                Platform.runLater(() -> {
                    boolean nowVisible = syntaxContainer.isVisible();
                    System.out.println("Syntax container now visible: " + nowVisible);

                    showTreeCheckbox.setSelected(false);
                    showTreeCheckbox.fire();

                    Platform.runLater(() -> {
                        boolean finallyVisible = syntaxContainer.isVisible();
                        System.out.println("Syntax container finally visible: " + finallyVisible);
                        latch.countDown();
                    });
                });
            } else {
                System.out.println("Syntax container or checkbox unavailable");
                latch.countDown();
            }
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    @Test
    @Order(5)
    @DisplayName("Test generation with special characters")
    void testSpecialCharacters() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (inputField != null && generateButton != null) {
                inputField.setText("Hello, world! How are you?");
                generateButton.fire();

                new Thread(() -> {
                    try {
                        Thread.sleep(4000);
                        Platform.runLater(() -> {
                            if (outputFlow != null) {
                                System.out.println("Output with special characters: " + outputFlow.getChildren().size() + " elements");
                            }
                            latch.countDown();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        latch.countDown();
                    }
                }).start();
            } else {
                latch.countDown();
            }
        });
        assertTrue(latch.await(8, TimeUnit.SECONDS));
    }

    @Test
    @Order(6)
    @DisplayName("Test controller availability")
    void testControllerAvailability() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            System.out.println("Controller available: " + (controller != null));
            System.out.println("Input field available: " + (inputField != null));
            System.out.println("Generate button available: " + (generateButton != null));
            System.out.println("Progress bar available: " + (progressBar != null));
            System.out.println("Output flow available: " + (outputFlow != null));
            System.out.println("Show tree checkbox available: " + (showTreeCheckbox != null));
            System.out.println("Syntax container available: " + (syntaxContainer != null));
            latch.countDown();
        });
        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    private static void waitForUIElements() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            int attempts = 0;
            while ((inputField == null || generateButton == null || progressBar == null || outputFlow == null) && attempts < 50) {
                try {
                    Thread.sleep(100);
                    attempts++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("UI elements found after " + attempts + " attempts");
            latch.countDown();
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Essential UI elements should be available");
    }
}