package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

public class Controller {
    @FXML
    private TextField inputField;

    @FXML
    private TextArea outputArea;

    @FXML
    private void onGenerateClicked() {
        String text = inputField.getText();
        outputArea.setText("Hai scritto: " + text);
    }
}
