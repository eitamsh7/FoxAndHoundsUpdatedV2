package com.foxandhounds.foxandhounds_v1.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Hound in the Fox and Hounds game.
 *
 * Hounds can only move diagonally forward (down the board).
 * They win by trapping the fox so it cannot move.
 */
public class Hound {
    // Current position of the hound
    private int row;
    private int col;

    /**
     * Constructor to initialize the hound position.
     *
     * @param row Initial row of the hound
     * @param col Initial column of the hound
     */
    public Hound(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the current row of the hound.
     *
     * @return Current row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the current column of the hound.
     *
     * @return Current column
     */
    public int getCol() {
        return col;
    }

    /**
     * Updates the hound's position after a move.
     *
     * @param toRow Destination row
     * @param toCol Destination column
     */
    public void move(int toRow, int toCol) {
        this.row = toRow;
        this.col = toCol;
    }

    /**
     * Gets all possible valid moves for this hound.
     * Hounds can only move diagonally forward (down the board).
     *
     * @param board Current game board
     * @return List of possible moves as [row, col] arrays
     */
    public List<int[]> getPossibleMoves(Board board) {
        List<int[]> possibleMoves = new ArrayList<>();

        // Hounds can only move diagonally forward (down the board)
        // These are the two diagonal forward directions
        int[][] directions = {{1, -1}, {1, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (board.isValidMove(row, col, newRow, newCol, false)) {
                possibleMoves.add(new int[]{newRow, newCol});
            }
        }

        return possibleMoves;
    }

    /**
     * Calculates the Manhattan distance from this hound to the fox.
     * This is used by the AI to determine which hound to move.
     *
     * @param fox The fox
     * @return Manhattan distance to the fox
     */
    public int distanceToFox(Fox fox) {
        return Math.abs(row - fox.getRow()) + Math.abs(col - fox.getCol());
    }
}