package com.foxandhounds.foxandhounds_v1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Fox and Hounds game.
 *
 * This class initializes the JavaFX application and loads the FXML UI.
 */
public class FoxAndHoundsGame extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        // Set up the scene
        Scene scene = new Scene(root);

        // Configure the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fox and Hounds");
        primaryStage.setResizable(false);

        // Show the stage
        primaryStage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}