package com.foxandhounds.foxandhounds_v1.model;

/**
 * Represents the current state of the game.
 *
 * This class tracks whose turn it is, whether the game is over,
 * and the winner if the game has ended.
 */
public class GameState {
    // Possible winners
    public enum Winner {
        NONE, FOX, HOUNDS
    }

    // Is it the fox's turn?
    private boolean isFoxTurn;

    // Is the game over?
    private boolean isGameOver;

    // Who won the game?
    private Winner winner;

    // Current state message to display to the user
    private String statusMessage;

    /**
     * Constructor initializes a new game state.
     * Fox goes first, game is not over, no winner yet.
     */
    public GameState() {
        isFoxTurn = true;
        isGameOver = false;
        winner = Winner.NONE;
        statusMessage = "Fox's turn. Click on the fox to move.";
    }

    /**
     * Checks if it's the fox's turn.
     *
     * @return true if it's the fox's turn, false otherwise
     */
    public boolean isFoxTurn() {
        return isFoxTurn;
    }

    /**
     * Sets whose turn it is.
     *
     * @param isFoxTurn true if it's the fox's turn, false for hounds' turn
     */
    public void setFoxTurn(boolean isFoxTurn) {
        this.isFoxTurn = isFoxTurn;

        // Update status message based on whose turn it is
        if (!isGameOver) {
            if (isFoxTurn) {
                statusMessage = "Fox's turn. Click on the fox to move.";
            } else {
                statusMessage = "Hounds' turn. AI is thinking...";
            }
        }
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Sets the game as over with a winner.
     *
     * @param winner Who won the game
     */
    public void setGameOver(Winner winner) {
        this.isGameOver = true;
        this.winner = winner;

        // Update status message based on the winner
        if (winner == Winner.FOX) {
            statusMessage = "Fox has escaped! Fox wins!";
        } else if (winner == Winner.HOUNDS) {
            statusMessage = "Fox is trapped! Hounds win!";
        }
    }

    /**
     * Gets the current winner.
     *
     * @return Current winner (NONE if game is still in progress)
     */
    public Winner getWinner() {
        return winner;
    }

    /**
     * Gets the current status message.
     *
     * @return Status message to display to the user
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets a custom status message.
     *
     * @param statusMessage New status message
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Resets the game state for a new game.
     */
    public void reset() {
        isFoxTurn = true;
        isGameOver = false;
        winner = Winner.NONE;
        statusMessage = "Fox's turn. Click on the fox to move.";
    }
}