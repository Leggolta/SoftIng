package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.net.URL;

/**
 * Main entry point for the JavaFX application.
 * Loads the FXML layout and applies external CSS styling.
 */
public class UI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Load the UI layout from the FXML resource
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/main.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        URL cssUrl = getClass().getResource("/org/example/css/style.css");

        // Apply the stylesheet if found
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("WARNING: style.css not found at /org/example/css/style.css");
        }
        assert cssUrl != null;
        scene.getStylesheets().add(cssUrl.toExternalForm());

        // Set the window title shown in the title bar
        primaryStage.setTitle("Nonsense generator");

        // Set and resize a scene from the FXML and attach it to the stage
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();

        // Show the window on screen
        primaryStage.show();
    }
}
