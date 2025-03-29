package com.foxandhounds.foxandhounds_v1.controller;

import com.foxandhounds.foxandhounds_v1.controller.ai.*;
import com.foxandhounds.foxandhounds_v1.model.*;
import java.util.*;

/**
 * AIController uses the Strategy pattern to select and apply different AI difficulty levels.
 * This class has been refactored to use separate strategy implementations for each difficulty.
 */
public class AIController {
    /**
     * Enum for different AI difficulty levels.
     */
    public enum Difficulty {
        EASY,   // Beatable with basic strategy
        MEDIUM, // Challenging but possible to beat
        HARD    // Nearly impossible to beat
    }

    // Current difficulty level
    private Difficulty currentDifficulty;

    // Strategy implementations for each difficulty level
    private AIStrategy easyStrategy;
    private AIStrategy mediumStrategy;
    private AIStrategy hardStrategy;

    // Currently active strategy
    private AIStrategy currentStrategy;

    // Static counter to track strategy switches
    private static int strategyChangeCount = 0;

    /**
     * Constructor initializes the AI controller with a difficulty level.
     *
     * @param difficulty Initial difficulty level
     */
    public AIController(Difficulty difficulty) {
        // Initialize strategies
        this.easyStrategy = new EasyAIStrategy();
        this.mediumStrategy = new MediumAIStrategy();
        this.hardStrategy = new HardAIStrategy();

        // Set initial difficulty
        setDifficulty(difficulty);

        System.out.println("==================================================");
        System.out.println("✅ AIController initialized with difficulty: " + difficulty);
        System.out.println("==================================================");
    }

    /**
     * Sets the AI difficulty level.
     *
     * @param difficulty New difficulty level
     */
    public void setDifficulty(Difficulty difficulty) {
        // Only log a change if it's different from current
        boolean isChanging = this.currentDifficulty != difficulty;
        String oldDifficulty = (this.currentDifficulty != null) ? this.currentDifficulty.toString() : "NONE";

        this.currentDifficulty = difficulty;

        // Select appropriate strategy
        switch (difficulty) {
            case EASY:
                currentStrategy = easyStrategy;
                break;
            case MEDIUM:
                currentStrategy = mediumStrategy;
                break;
            case HARD:
                currentStrategy = hardStrategy;
                break;
            default:
                currentStrategy = easyStrategy;
        }

        if (isChanging) {
            strategyChangeCount++;
            System.out.println("==================================================");
            System.out.println("🔄 DIFFICULTY CHANGED: " + oldDifficulty + " -> " + difficulty);
            System.out.println("🔄 Strategy Change #" + strategyChangeCount);
            System.out.println("🔄 New Strategy Class: " + currentStrategy.getClass().getSimpleName());
            System.out.println("==================================================");
        }
    }

    /**
     * Gets the best move for the AI based on the current difficulty level.
     *
     * @param hounds List of hounds
     * @param fox The fox
     * @param board The game board
     * @return The best move for the AI
     */
    public Move getBestMove(List<Hound> hounds, Fox fox, Board board) {
        System.out.println("==================================================");
        System.out.println("🎮 AI MAKING MOVE with difficulty: " + currentDifficulty);
        System.out.println("🎮 Using Strategy: " + currentStrategy.getClass().getSimpleName());
        System.out.println("==================================================");

        // Delegate to the appropriate strategy
        Move move = currentStrategy.getBestMove(hounds, fox, board);

        if (move != null) {
            Hound hound = hounds.get(move.getHoundIndex());
            System.out.println("==================================================");
            System.out.println("✅ AI chose move: Hound " + move.getHoundIndex() +
                    " from (" + hound.getRow() + "," + hound.getCol() + ") to (" +
                    move.getToRow() + "," + move.getToCol() + ")");
            System.out.println("==================================================");
        } else {
            System.out.println("❌ AI could not find a valid move!");
        }

        return move;
    }

    /**
     * Returns the current active difficulty level.
     *
     * @return Current difficulty
     */
    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    /**
     * Returns the name of the current active strategy.
     *
     * @return Name of the current strategy class
     */
    public String getCurrentStrategyName() {
        if (currentStrategy == null) {
            return "NULL";
        }
        return currentStrategy.getClass().getSimpleName();
    }
}