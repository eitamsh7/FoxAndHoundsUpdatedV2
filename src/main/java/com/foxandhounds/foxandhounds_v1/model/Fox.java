package com.foxandhounds.foxandhounds_v1.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Fox in the Fox and Hounds game.
 *
 * The Fox can move diagonally in any direction (forward or backward).
 * It wins by reaching the top row of the board.
 */
public class Fox {
    // Current position of the fox
    private int row;
    private int col;

    /**
     * Constructor to initialize the fox position.
     *
     * @param row Initial row of the fox
     * @param col Initial column of the fox
     */
    public Fox(int row, int col) {
        this.row = row;
        this.col = col;
        System.out.println("Fox created at position: " + row + "," + col);
    }

    /**
     * Gets the current row of the fox.
     *
     * @return Current row
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the current column of the fox.
     *
     * @return Current column
     */
    public int getCol() {
        return col;
    }

    /**
     * Updates the fox's position after a move.
     *
     * @param toRow Destination row
     * @param toCol Destination column
     */
    public void move(int toRow, int toCol) {
        System.out.println("Moving fox from " + this.row + "," + this.col + " to " + toRow + "," + toCol);
        this.row = toRow;
        this.col = toCol;
    }

    /**
     * Gets all possible valid moves for the fox.
     * Fox can move diagonally in any direction.
     *
     * @param board Current game board
     * @return List of possible moves as [row, col] arrays
     */
    public List<int[]> getPossibleMoves(Board board) {
        List<int[]> possibleMoves = new ArrayList<>();

        // Check all four diagonal directions
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (board.isValidMove(row, col, newRow, newCol, true)) {
                possibleMoves.add(new int[]{newRow, newCol});
                System.out.println("Valid fox move found: " + newRow + "," + newCol);
            }
        }

        System.out.println("Fox has " + possibleMoves.size() + " possible moves");
        return possibleMoves;
    }

    /**
     * Checks if the fox has escaped (reached the top row).
     *
     * @return true if fox has escaped, false otherwise
     */
    public boolean hasEscaped() {
        return row == 0;
    }
}