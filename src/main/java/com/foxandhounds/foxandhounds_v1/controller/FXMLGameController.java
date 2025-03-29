package com.foxandhounds.foxandhounds_v1.controller;

import com.foxandhounds.foxandhounds_v1.model.GameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller for the main game UI
 *
 * This controller handles user interactions with the main game interface,
 * including the difficulty selector and new game button.
 */
public class FXMLGameController implements Initializable {

    @FXML
    private ComboBox<String> difficultyComboBox;

    @FXML
    private Button newGameButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label turnLabel;

    @FXML
    private FXMLBoardController boardPaneController;

    // The game manager
    private GameManager gameManager;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the game manager
        gameManager = new GameManager();

        // Set up the difficulty combo box
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyComboBox.setValue("Easy");
        difficultyComboBox.setOnAction(e -> {
            switch (difficultyComboBox.getValue()) {
                case "Easy":
                    gameManager.setAIDifficulty(AIController.Difficulty.EASY);
                    break;
                case "Medium":
                    gameManager.setAIDifficulty(AIController.Difficulty.MEDIUM);
                    break;
                case "Hard":
                    gameManager.setAIDifficulty(AIController.Difficulty.HARD);
                    break;
            }
        });

        // Set up a listener to update the UI when the game state changes
        gameManager.addGameStateListener(() -> updateUI());

        // Wait until everything is initialized
        Platform.runLater(() -> {
            // Pass the game manager to the board controller
            if (boardPaneController != null) {
                boardPaneController.setGameManager(gameManager);
                boardPaneController.setMainController(this);

                // Initialize the UI
                updateUI();
            }
        });
    }

    /**
     * Updates the UI based on the current game state
     */
    public void updateUI() {
        GameState gameState = gameManager.getGameState();
        statusLabel.setText(gameState.getStatusMessage());
        turnLabel.setText(gameState.isFoxTurn() ? "Fox's Turn" : "Hounds' Turn");

        // Update the board UI
        if (boardPaneController != null) {
            boardPaneController.updateBoard();
        }
    }

    /**
     * Handles the new game button action
     */
    @FXML
    private void handleNewGame() {
        gameManager.initializeGame();
        if (boardPaneController != null) {
            boardPaneController.clearSelection();
        }
        updateUI();
    }

    /**
     * Called when AI makes a move
     */
    public void makeAIMove() {
        if (gameManager.makeAIMove()) {
            // Update UI
            Platform.runLater(() -> updateUI());
        } else {
            // If AI couldn't make a move, make sure we update the UI
            Platform.runLater(() -> {
                updateUI();
                System.out.println("AI couldn't make a move!");
            });
        }
    }
}