package com.foxandhounds.foxandhounds_v1.controller.ai;

import com.foxandhounds.foxandhounds_v1.model.*;
import java.util.List;

/**
 * Interface for different AI difficulty strategies
 */
public interface AIStrategy {
    /**
     * Calculate the best move for the AI based on the current board state
     *
     * @param hounds The list of hounds on the board
     * @param fox The fox on the board
     * @param board The current board state
     * @return The best move for the AI to make
     */
    Move getBestMove(List<Hound> hounds, Fox fox, Board board);
}