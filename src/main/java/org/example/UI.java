package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX UI. Launches the main window based on FXML layout.
 */

public class UI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the UI layout from the FXML resource
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/fxml/main.fxml")
        );

        // Set the window title shown in the title bar
        primaryStage.setTitle("Nonsense generator");

        // Create a scene from the FXML and attach it to the stage
        primaryStage.setScene(new Scene(loader.load()));

        // Show the window on screen
        primaryStage.show();
    }
}
