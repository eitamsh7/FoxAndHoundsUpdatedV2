package com.foxandhounds.foxandhounds_v1.controller;

import com.foxandhounds.foxandhounds_v1.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GameManager is the main controller class for the Fox and Hounds game.
 *
 * It coordinates the game state, board, pieces, and AI moves.
 * It handles game logic like validating moves, updating the board,
 * and checking win conditions.
 */
public class GameManager {
    // Game model components
    private Board board;
    private Fox fox;
    private List<Hound> hounds;
    private GameState gameState;
    private AIController aiController;

    // Current difficulty level - added for tracking
    private AIController.Difficulty currentDifficulty = AIController.Difficulty.EASY;

    // Interface for game state change listeners
    public interface GameStateListener {
        void onGameStateChanged();
    }

    // List of game state listeners
    private List<GameStateListener> gameStateListeners = new ArrayList<>();

    /**
     * Constructor initializes the game manager.
     */
    public GameManager() {
        board = new Board();
        hounds = new ArrayList<>();
        gameState = new GameState();
        aiController = new AIController(AIController.Difficulty.EASY);
        initializeGame();
    }

    /**
     * Initializes or resets the game to starting position.
     */
    public void initializeGame() {
        // Initialize the board
        board.initializeBoard();
        // Print board for debugging
        board.printBoard();

        // Create hounds (clear the list first to avoid duplications)
        hounds.clear();
        for (int col = 1; col < Board.BOARD_SIZE; col += 2) {
            Hound hound = new Hound(0, col);
            hounds.add(hound);
            System.out.println("Added hound at: 0," + col);
        }

        // Create fox at the bottom left corner (7,0)
        fox = new Fox(Board.BOARD_SIZE - 1, 0);
        System.out.println("Set fox at: " + (Board.BOARD_SIZE - 1) + ",0");

        // Reset game state (fox goes first)
        gameState.reset();

        // Make sure we're using the current difficulty level
        aiController.setDifficulty(currentDifficulty);

        System.out.println("Game initialized with difficulty: " + currentDifficulty);

        // Notify listeners of game state change
        notifyGameStateListeners();
    }

    /**
     * Adds a listener for game state changes.
     *
     * @param listener The listener to add
     */
    public void addGameStateListener(GameStateListener listener) {
        gameStateListeners.add(listener);
    }

    /**
     * Notifies all listeners that the game state has changed.
     */
    private void notifyGameStateListeners() {
        for (GameStateListener listener : gameStateListeners) {
            listener.onGameStateChanged();
        }
    }

    /**
     * Gets the current game board.
     *
     * @return The game board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the fox.
     *
     * @return The fox
     */
    public Fox getFox() {
        return fox;
    }

    /**
     * Gets the list of hounds.
     *
     * @return List of hounds
     */
    public List<Hound> getHounds() {
        return hounds;
    }

    /**
     * Gets the current game state.
     *
     * @return Current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Sets the AI difficulty level.
     *
     * @param difficulty AI difficulty level
     */
    public void setAIDifficulty(AIController.Difficulty difficulty) {
        this.currentDifficulty = difficulty;
        aiController.setDifficulty(difficulty);
        System.out.println("Difficulty changed to: " + difficulty);
    }

    /**
     * Makes a move for the fox.
     *
     * @param toRow Destination row
     * @param toCol Destination column
     * @return true if the move was successful, false otherwise
     */
    public boolean moveFox(int toRow, int toCol) {
        // Check if it's the fox's turn and the game is not over
        if (gameState.isGameOver() || !gameState.isFoxTurn()) {
            System.out.println("Cannot move fox: Game over or not fox's turn");
            return false;
        }

        System.out.println("Attempting fox move to: " + toRow + "," + toCol);
        System.out.println("Current fox position: " + fox.getRow() + "," + fox.getCol());

        // Check if the move is valid
        if (board.isValidMove(fox.getRow(), fox.getCol(), toRow, toCol, true)) {
            // Update the board
            board.movePiece(fox.getRow(), fox.getCol(), toRow, toCol);

            // Update the fox position
            fox.move(toRow, toCol);
            System.out.println("Fox moved to: " + toRow + "," + toCol);

            // Check if fox has escaped
            if (board.hasFoxEscaped(fox.getRow())) {
                gameState.setGameOver(GameState.Winner.FOX);
                System.out.println("Fox has escaped! Game over.");
                notifyGameStateListeners();
                return true;
            }

            // Switch turns
            gameState.setFoxTurn(false);
            notifyGameStateListeners();
            return true;
        } else {
            System.out.println("Invalid fox move!");
            return false;
        }
    }

    /**
     * Makes an AI move for the hounds.
     *
     * @return true if the AI made a move, false otherwise
     */
    public boolean makeAIMove() {
        // Check if it's the hounds' turn and the game is not over
        if (gameState.isGameOver() || gameState.isFoxTurn()) {
            System.out.println("Cannot make AI move: Game over or fox's turn");
            return false;
        }

        System.out.println("AI making move with difficulty: " + currentDifficulty);

        // Let AI choose the best move
        Move move = aiController.getBestMove(hounds, fox, board);

        if (move != null) {
            int houndIndex = move.getHoundIndex();
            int toRow = move.getToRow();
            int toCol = move.getToCol();

            System.out.println("AI chose to move hound " + houndIndex +
                    " to position (" + toRow + "," + toCol + ")");

            Hound hound = hounds.get(houndIndex);

            // Update the board
            board.movePiece(hound.getRow(), hound.getCol(), toRow, toCol);

            // Update the hound position
            hound.move(toRow, toCol);

            // Check if fox is blocked
            if (board.isFoxBlocked(fox.getRow(), fox.getCol())) {
                gameState.setGameOver(GameState.Winner.HOUNDS);
                System.out.println("Fox is blocked! Hounds win.");
                notifyGameStateListeners();
                return true;
            }

            // Switch turns
            gameState.setFoxTurn(true);
            notifyGameStateListeners();
            return true;
        } else {
            System.out.println("AI couldn't find a valid move!");
        }

        // If no move is possible, hounds lose
        gameState.setGameOver(GameState.Winner.FOX);
        gameState.setStatusMessage("Hounds can't move! Fox wins!");
        notifyGameStateListeners();
        return false;
    }

    /**
     * Checks if any hound can make a valid move.
     *
     * @return true if at least one hound can move, false otherwise
     */
    public boolean canAnyHoundMove() {
        for (Hound hound : hounds) {
            if (!hound.getPossibleMoves(board).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}