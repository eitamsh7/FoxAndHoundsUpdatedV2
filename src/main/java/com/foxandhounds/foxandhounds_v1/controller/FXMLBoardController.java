package com.foxandhounds.foxandhounds_v1.controller;

import com.foxandhounds.foxandhounds_v1.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller for the game board
 *
 * This controller handles rendering the board and processing clicks on cells.
 */
public class FXMLBoardController implements Initializable {

    // Constants for board visualization
    private static final int CELL_SIZE = 80;
    private static final Color LIGHT_CELL_COLOR = Color.WHEAT;
    private static final Color DARK_CELL_COLOR = Color.DARKGREEN;
    private static final Color FOX_COLOR = Color.ORANGE;
    private static final Color HOUND_COLOR = Color.BROWN;
    private static final Color HIGHLIGHT_COLOR = Color.LIGHTBLUE;
    private static final Color SELECTED_COLOR = Color.YELLOW;

    @FXML
    private GridPane boardGrid;

    // 2D arrays to store UI elements
    private StackPane[][] cellPanes;
    private Rectangle[][] cellRects;

    // Currently selected cell (for highlighting)
    private int selectedRow = -1;
    private int selectedCol = -1;

    // Reference to the main controller and game manager
    private FXMLGameController mainController;
    private GameManager gameManager;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize arrays for UI elements
        cellPanes = new StackPane[Board.BOARD_SIZE][Board.BOARD_SIZE];
        cellRects = new Rectangle[Board.BOARD_SIZE][Board.BOARD_SIZE];

        // Create the board cells
        createBoardCells();
    }

    /**
     * Sets the game manager for this controller
     *
     * @param gameManager The game manager
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Sets the main controller for this board controller
     *
     * @param mainController The main game controller
     */
    public void setMainController(FXMLGameController mainController) {
        this.mainController = mainController;
    }

    /**
     * Creates the visual board cells
     */
    private void createBoardCells() {
        boardGrid.getChildren().clear();

        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                // Create the cell rectangle
                Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
                rect.setFill(((row + col) % 2 == 0) ? LIGHT_CELL_COLOR : DARK_CELL_COLOR);
                rect.setStroke(Color.BLACK);
                rect.setStrokeWidth(1);

                // Add coordinate labels (optional - for debugging)
                Text coordText = new Text((col) + "," + (row));
                coordText.setFont(Font.font("Arial", 8));
                coordText.setFill(Color.GRAY);
                coordText.setTranslateX(-CELL_SIZE/3);
                coordText.setTranslateY(-CELL_SIZE/3);

                // Create a stack pane to hold the rectangle and pieces
                StackPane cellPane = new StackPane(rect, coordText);
                cellPanes[row][col] = cellPane;
                cellRects[row][col] = rect;

                // Add click handler for the cell
                final int r = row;
                final int c = col;
                cellPane.setOnMouseClicked(e -> handleCellClick(r, c));

                // Add the cell to the grid
                boardGrid.add(cellPane, col, row);
            }
        }
    }

    /**
     * Handles a click on a board cell
     *
     * @param row Clicked row
     * @param col Clicked column
     */
    private void handleCellClick(int row, int col) {
        if (gameManager == null) return;

        GameState gameState = gameManager.getGameState();
        Fox fox = gameManager.getFox();

        // If game is over or it's not the fox's turn, do nothing
        if (gameState.isGameOver() || !gameState.isFoxTurn()) {
            return;
        }

        // If no cell is selected yet
        if (selectedRow == -1) {
            // Check if the clicked cell contains the fox
            if (row == fox.getRow() && col == fox.getCol()) {
                // Highlight valid moves
                highlightValidMoves(fox);
                gameState.setStatusMessage("Select a destination for the fox");
            }
        } else {
            // A cell is already selected, try to move the fox
            if (gameManager.moveFox(row, col)) {
                // Move successful
                clearSelection();

                // If game is not over, let AI make its move
                if (!gameState.isGameOver()) {
                    // Update UI to show it's the AI's turn
                    gameState.setStatusMessage("Hounds' turn. AI is thinking...");
                    mainController.updateUI();

                    // Let AI make its move after a short delay
                    Platform.runLater(() -> {
                        try {
                            // Add a short delay for better user experience
                            Thread.sleep(500);
                            mainController.makeAIMove();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } else {
                // Invalid move, clear selection if clicked on non-valid cell
                List<int[]> possibleMoves = fox.getPossibleMoves(gameManager.getBoard());
                boolean validDestination = false;

                for (int[] move : possibleMoves) {
                    if (move[0] == row && move[1] == col) {
                        validDestination = true;
                        break;
                    }
                }

                if (!validDestination) {
                    // Clicked on non-valid destination, clear selection
                    clearSelection();
                    gameState.setStatusMessage("Click on the fox to move");
                }
            }
        }
    }

    /**
     * Highlights valid moves for the fox
     *
     * @param fox The fox
     */
    private void highlightValidMoves(Fox fox) {
        // Reset board colors
        resetBoardColors();

        // Highlight the fox
        selectedRow = fox.getRow();
        selectedCol = fox.getCol();
        cellRects[selectedRow][selectedCol].setStroke(SELECTED_COLOR);
        cellRects[selectedRow][selectedCol].setStrokeWidth(3);

        // Highlight possible destinations
        List<int[]> possibleMoves = fox.getPossibleMoves(gameManager.getBoard());

        for (int[] move : possibleMoves) {
            cellRects[move[0]][move[1]].setFill(HIGHLIGHT_COLOR);
            cellRects[move[0]][move[1]].setStroke(Color.BLUE);
            cellRects[move[0]][move[1]].setStrokeWidth(2);
        }
    }

    /**
     * Clears selection and highlighting
     */
    public void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        resetBoardColors();
    }

    /**
     * Resets all cell colors to their default
     */
    private void resetBoardColors() {
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                // Reset cell color
                cellRects[row][col].setFill(((row + col) % 2 == 0) ? LIGHT_CELL_COLOR : DARK_CELL_COLOR);
                cellRects[row][col].setStroke(Color.BLACK);
                cellRects[row][col].setStrokeWidth(1);
            }
        }
    }

    /**
     * Updates the board display
     */
    public void updateBoard() {
        if (gameManager == null) return;

        // Clear any existing pieces
        clearPieces();

        // Place fox
        Fox fox = gameManager.getFox();
        placeFox(fox.getRow(), fox.getCol());

        // Place hounds
        for (Hound hound : gameManager.getHounds()) {
            placeHound(hound.getRow(), hound.getCol());
        }
    }

    /**
     * Removes all pieces from the board
     */
    private void clearPieces() {
        for (int row = 0; row < Board.BOARD_SIZE; row++) {
            for (int col = 0; col < Board.BOARD_SIZE; col++) {
                // Remove any pieces (keeping only the rectangle and coordinate text)
                if (cellPanes[row][col].getChildren().size() > 2) {
                    cellPanes[row][col].getChildren().remove(2, cellPanes[row][col].getChildren().size());
                }
            }
        }
    }

    /**
     * Places a fox piece on the specified cell
     *
     * @param row Row position
     * @param col Column position
     */
    private void placeFox(int row, int col) {
        Circle foxCircle = new Circle(CELL_SIZE / 3);
        foxCircle.setFill(FOX_COLOR);
        foxCircle.setStroke(Color.DARKORANGE);
        foxCircle.setStrokeWidth(2);
        cellPanes[row][col].getChildren().add(foxCircle);
    }

    /**
     * Places a hound piece on the specified cell
     *
     * @param row Row position
     * @param col Column position
     */
    private void placeHound(int row, int col) {
        Circle houndCircle = new Circle(CELL_SIZE / 3);
        houndCircle.setFill(HOUND_COLOR);
        houndCircle.setStroke(Color.SADDLEBROWN);
        houndCircle.setStrokeWidth(2);
        cellPanes[row][col].getChildren().add(houndCircle);
    }
}