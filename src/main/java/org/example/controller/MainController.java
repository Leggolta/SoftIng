package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.App;
import java.util.List;

/**
 * Controller for the JavaFX UI: handles user input and displays results.
 */

public class MainController {

    // Injected input field from FXML (user enters sentence here)
    @FXML private TextField inputField;

    // Injected output area from FXML (displays generated sentences)
    @FXML private TextArea outputArea;

    // Core logic instance: calls App.generate(...) to process the text
    private final App processor = new App();


    //Event handler invoked when the "Genera" button is clicked.
    @FXML
    private void onGenerateClicked() {
        // Trim and validate input
        String inputText = inputField.getText().trim();
        if (inputText.isEmpty()) {
            outputArea.setText("❌ Please enter a non-empty sentence!");
            return;
        }

        try {
            // Call the generate method to obtain a list of results
            List<String> results = processor.generate(inputText);

            // Clear previous output and append each line
            outputArea.clear();
            results.forEach(line -> outputArea.appendText(line + "\n\n"));
        } catch (Exception e) {
            // Show error message on exception
            outputArea.setText("Error processing input:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
