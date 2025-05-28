package org.example.controller;

import org.example.App;
import org.example.App.SentenceResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;

/**
 * Controller for the main JavaFX UI.
 * Handles user input, triggers generation of nonsense sentences,
 * and displays syntactic tree and toxicity bars.
 */

public class MainController {

    // FXML-injected UI elements
    @FXML private TextField inputField;
    @FXML private TextArea outputArea;
    @FXML private CheckBox showTreeCheckbox;
    @FXML private VBox syntaxContainer;
    @FXML private VBox generatedContainer;
    @FXML private Label syntaxLabel;
    @FXML private ScrollPane treeScroll;
    @FXML private TextFlow treeAsciiFlow;
    @FXML private HBox toxicityBarsContainer;
    @FXML private ProgressBar progressBar;
    @FXML private TextFlow outputFlow;

    // Core processing logic
    private final App processor = new App();

    // Mapping from POS tags to full labels for tree display
    private final Properties syntaxTagsMap = new Properties();

    // Stanford CoreNLP pipeline for parse trees
    private StanfordCoreNLP pipeline;

    // Property bound to bar heights for toxicity visualization
    private final DoubleProperty toxicityScore = new SimpleDoubleProperty(0);

    /**
     * Called by FXMLLoader after all @FXML fields are injected.
     * Loads tag mappings, initializes the CoreNLP pipeline,
     * and sets up UI defaults and listeners.
     */
    @FXML
    public void initialize() {
        // Load syntax tag full names from properties file
        try (InputStream in = getClass().getResourceAsStream("/syntax_tags.properties")) {
            if (in == null) {
                System.err.println("labels.properties not found!");
            } else {
                syntaxTagsMap.load(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up Stanford CoreNLP for parse tree generation
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,parse");
        pipeline = new StanfordCoreNLP(props);

        // Hide progress bar and syntax tree section initially
        progressBar.setVisible(false);
        progressBar.setManaged(false);
        syntaxContainer.setVisible(false);
        syntaxContainer.setManaged(false);

        // Toggle visibility of the syntax tree when checkbox changes
        showTreeCheckbox.selectedProperty().addListener((obs, o, selected) -> {
            syntaxContainer.setVisible(selected);
            syntaxContainer.setManaged(selected);
            treeScroll.setVisible(selected);
            treeScroll.setManaged(selected);
            syntaxLabel.setVisible(selected);
            syntaxLabel.setManaged(selected);

            if (selected) buildTree();
        });
    }

    /**
     * Handler for the "Generate" button.
     * Validates input, shows a progress indicator, runs generation off the UI thread,
     * and then updates the UI with results or errors.
     */
    @FXML
    private void onGenerateClicked() {
        String inputText = inputField.getText().trim();
        if (inputText.isEmpty()) {
            outputArea.setText("❌ Please enter a non-empty sentence!");
            //treeView.setVisible(false);
            toxicityScore.set(0);
            return;
        }

        // Show indeterminate progress bar during processing
        progressBar.setVisible(true);
        progressBar.setManaged(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        // Run processing on a background thread to keep UI responsive
        new Thread(() -> {
            List<SentenceResult> results = null;
            Exception error = null;
            try {
                results = processor.generate(inputText);
            } catch (Exception e) {
                error = e;
            }

            final List<SentenceResult> finalResults = results;
            final Exception finalError = error;

            // Update UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                outputFlow.getChildren().clear();

                if (finalError != null) {
                    outputFlow.getChildren().add(new Text("Error processing input:\n" + finalError.getMessage()));
                    toxicityScore.set(0);
                } else {
                    if (finalResults.size() > 1) {
                        outputFlow.getChildren().add(new Text(
                                """
                                        The input sentence contains many words.
                                        Multiple nonsense sentences have been generated to use them all
                                        
                                        """
                        ));
                    }

                    Font baseFont = Font.getDefault();

                    for (SentenceResult r : finalResults) {
                        // normal text
                        Text sentenceText = new Text("→ " + r.getText() + "\n→ Sentence toxicity: ");
                        sentenceText.setFont(baseFont);

                        // bold percentage
                        Text boldPct = new Text(String.format("%.2f%%\n\n", r.getToxicity() * 100));
                        boldPct.setFont(Font.font(
                                baseFont.getFamily(),
                                FontWeight.BOLD,
                                baseFont.getSize()
                        ));

                        outputFlow.getChildren().addAll(sentenceText, boldPct);
                    }

                    toxicityBarsContainer.getChildren().clear();
                    for (SentenceResult r : finalResults) {
                        double tox = r.getToxicity();
                        AnchorPane container = new AnchorPane();
                        container.setPrefWidth(20);
                        container.setMinWidth(20);
                        container.prefHeightProperty().bind(toxicityBarsContainer.heightProperty());
                        container.getStyleClass().add("main-container");

                        Rectangle bar = new Rectangle();
                        bar.setArcWidth(8);
                        bar.setArcHeight(8);
                        bar.widthProperty().bind(container.widthProperty());
                        bar.heightProperty().bind(container.heightProperty().multiply(tox));
                        bar.setFill(Color.hsb((1 - tox) * 120, 1.0, 1.0));
                        AnchorPane.setBottomAnchor(bar, 0.0);
                        container.getChildren().add(bar);

                        Label lbl = new Label(String.format("%.0f%%", tox * 100));
                        VBox box = new VBox(lbl, container);
                        box.setAlignment(Pos.TOP_CENTER);
                        box.setSpacing(5);
                        toxicityBarsContainer.getChildren().add(box);
                    }

                    if (showTreeCheckbox.isSelected()) buildTree();
                }

                progressBar.setVisible(false);
                progressBar.setManaged(false);
            });

        }, "Generation-Thread").start();
    }

    /**
     * Builds and displays the ASCII-formatted parse tree
     * for the first sentence of the input text.
     */
    private void buildTree() {
        String text = inputField.getText();
        CoreDocument doc = new CoreDocument(text);
        pipeline.annotate(doc);

        List<CoreSentence> sents = doc.sentences();
        if (sents.isEmpty()) {
            treeAsciiFlow.getChildren().clear();
            return;
        }

        Tree stanfordTree = sents.getFirst().constituencyParse();
        treeAsciiFlow.getChildren().clear();
        buildStyledAscii(stanfordTree, "", true);
    }

    /**
     * Recursively renders each node of the parse tree as styled Text
     * and appends branches and labels into the TextFlow.
     *
     * @param node   current tree node
     * @param prefix accumulated ASCII prefix for branches
     * @param isLast whether this node is the last child of its parent
     */
    private void buildStyledAscii(Tree node, String prefix, boolean isLast) {
        // pick a non‐null font
        Font appFont = Font.getDefault();  // or syntaxLabel.getFont()

        String branch = isLast ? "└─ " : "├─ ";
        String tag    = node.label().value();
        String full   = syntaxTagsMap.getProperty(tag, tag);
        boolean leaf  = node.isLeaf();

        Text tBranch = new Text(prefix + branch);
        tBranch.setFont(appFont);

        Text tLabel  = new Text(full);
        tLabel.setFont(Font.font(
                appFont.getFamily(),
                leaf ? FontWeight.BOLD : FontWeight.NORMAL,
                appFont.getSize()
        ));

        treeAsciiFlow.getChildren().addAll(tBranch, tLabel, new Text("\n"));

        String childPrefix = prefix + (isLast ? "   " : "│  ");
        Tree[] kids = node.children();
        for (int i = 0; i < kids.length; i++) {
            buildStyledAscii(kids[i], childPrefix, i == kids.length - 1);
        }
    }


    private TreeItem<String> toTreeItem(Tree tree) {
        TreeItem<String> item = new TreeItem<>(tree.label().value());
        for (Tree child : tree.children()) {
            item.getChildren().add(toTreeItem(child));
        }
        return item;
    }

    /**
     * Builds a list of ASCII lines for the parse tree.
     * @return a list of lines in the tree representation
     */
    private List<String> toAsciiLines(Tree tree) {
        List<String> lines = new ArrayList<>();
        buildAscii(tree, "", true, lines);
        return lines;
    }

    /**
     * Helper for toAsciiLines: populates a list with each line of the ASCII tree.
     */
    private void buildAscii(Tree node, String prefix, boolean isLast, List<String> lines) {
        String branch = isLast ? "└─ " : "├─ ";
        String tag = node.label().value();
        String full = syntaxTagsMap.getProperty(tag, tag);

        lines.add(prefix + branch + full);

        String childPrefix = prefix + (isLast ? "   " : "│  ");
        Tree[] kids = node.children();
        for (int i = 0; i < kids.length; i++) {
            buildAscii(kids[i], childPrefix, i == kids.length - 1, lines);
        }
    }
}