package com.foxandhounds.foxandhounds_v1.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the game board for Fox and Hounds.
 *
 * This class uses a HashMap to store the state of each cell, which provides
 * O(1) lookups for cell states. It also uses a HashSet to quickly check
 * if a cell is occupied. This is more memory efficient than storing a full
 * 8x8 matrix as we only need to track occupied cells.
 */
public class Board {
    // Constants for board dimensions
    public static final int BOARD_SIZE = 8;

    // Enum for possible cell states
    public enum CellState {
        EMPTY, FOX, HOUND
    }

    // HashMap to store the state of each cell (only occupied cells are stored)
    // The key is a string in the format "row,col"
    private Map<String, CellState> boardMap;

    // HashSet to track which cells are occupied (for faster checks)
    private Set<String> occupiedCells;

    /**
     * Constructor initializes an empty board.
     */
    public Board() {
        boardMap = new HashMap<>();
        occupiedCells = new HashSet<>();
        initializeBoard();
    }

    /**
     * Initializes the board with starting positions.
     * Places four hounds on the top row (black squares) and
     * places the fox on the bottom left corner.
     */
    public void initializeBoard() {
        // Clear any existing pieces
        boardMap.clear();
        occupiedCells.clear();

        // Place hounds at the top row's dark squares (positions 0,1 0,3 0,5 0,7)
        for (int col = 1; col < BOARD_SIZE; col += 2) {
            setCellState(0, col, CellState.HOUND);
        }

        // Place fox at the bottom left corner (7,0)
        setCellState(BOARD_SIZE - 1, 0, CellState.FOX);

        // Debug output to verify initialization
        System.out.println("Board initialized with fox at: " + (BOARD_SIZE - 1) + ",0");
        System.out.println("Hounds at: 0,1 0,3 0,5 0,7");
    }

    /**
     * Helper method to convert row, col to string key.
     * This is used as the key in the HashMap and HashSet.
     *
     * @param row Row number (0-7)
     * @param col Column number (0-7)
     * @return String key in format "row,col"
     */
    private String cellKey(int row, int col) {
        return row + "," + col;
    }

    /**
     * Sets the state of a cell.
     *
     * @param row Row number (0-7)
     * @param col Column number (0-7)
     * @param state New state for the cell (EMPTY, FOX, HOUND)
     */
    public void setCellState(int row, int col, CellState state) {
        String key = cellKey(row, col);

        // If clearing a cell, remove it from occupied cells
        if (state == CellState.EMPTY) {
            boardMap.remove(key);
            occupiedCells.remove(key);
        } else {
            boardMap.put(key, state);
            occupiedCells.add(key);
        }
    }

    /**
     * Gets the state of a cell.
     *
     * @param row Row number (0-7)
     * @param col Column number (0-7)
     * @return State of the cell (EMPTY if not explicitly set)
     */
    public CellState getCellState(int row, int col) {
        String key = cellKey(row, col);
        return boardMap.getOrDefault(key, CellState.EMPTY);
    }

    /**
     * Checks if a cell is occupied.
     *
     * @param row Row number (0-7)
     * @param col Column number (0-7)
     * @return true if cell is occupied, false otherwise
     */
    public boolean isCellOccupied(int row, int col) {
        return occupiedCells.contains(cellKey(row, col));
    }

    /**
     * Checks if coordinates are within the board.
     *
     * @param row Row number
     * @param col Column number
     * @return true if coordinates are valid, false otherwise
     */
    public boolean isValidCell(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /**
     * Checks if a move is valid for a given player (fox or hound).
     *
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow Destination row
     * @param toCol Destination column
     * @param isFox true if the piece is a fox, false if it's a hound
     * @return true if move is valid, false otherwise
     */
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, boolean isFox) {
        // Debug output
        System.out.println("Checking move: " + fromRow + "," + fromCol + " to " + toRow + "," + toCol + " (isFox: " + isFox + ")");

        // Check if source and destination are valid cells
        if (!isValidCell(fromRow, fromCol) || !isValidCell(toRow, toCol)) {
            System.out.println("Invalid cell coordinates");
            return false;
        }

        // Check if the source has the correct piece
        CellState sourceState = getCellState(fromRow, fromCol);
        if ((isFox && sourceState != CellState.FOX) || (!isFox && sourceState != CellState.HOUND)) {
            System.out.println("Source doesn't have correct piece. Source state: " + sourceState);
            return false;
        }

        // Check if the destination is empty
        if (getCellState(toRow, toCol) != CellState.EMPTY) {
            System.out.println("Destination is not empty");
            return false;
        }

        // Check if the move is diagonal (one square)
        int rowDiff = toRow - fromRow;
        int colDiff = Math.abs(toCol - fromCol);

        if (colDiff != 1 || Math.abs(rowDiff) != 1) {
            System.out.println("Move is not diagonal single step");
            return false;
        }

        // For hounds: Can only move forward (down the board)
        if (!isFox && rowDiff <= 0) {
            System.out.println("Hound cannot move backward");
            return false;
        }

        // Fox can move diagonally in any direction
        System.out.println("Move is valid");
        return true;
    }

    /**
     * Updates the board after a move.
     *
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow Destination row
     * @param toCol Destination column
     */
    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        CellState piece = getCellState(fromRow, fromCol);
        setCellState(fromRow, fromCol, CellState.EMPTY);
        setCellState(toRow, toCol, piece);
        System.out.println("Moved piece from " + fromRow + "," + fromCol + " to " + toRow + "," + toCol);
    }

    /**
     * Checks if the fox is blocked (no valid moves).
     *
     * @param foxRow Fox's row
     * @param foxCol Fox's column
     * @return true if fox is blocked, false otherwise
     */
    public boolean isFoxBlocked(int foxRow, int foxCol) {
        // Check all four diagonal directions
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] dir : directions) {
            int newRow = foxRow + dir[0];
            int newCol = foxCol + dir[1];

            if (isValidCell(newRow, newCol) &&
                    isValidMove(foxRow, foxCol, newRow, newCol, true)) {
                return false; // Fox has at least one valid move
            }
        }

        return true; // Fox is blocked
    }

    /**
     * Checks if the fox has escaped (reached the top row).
     *
     * @param foxRow Fox's row
     * @return true if fox has escaped, false otherwise
     */
    public boolean hasFoxEscaped(int foxRow) {
        return foxRow == 0;
    }

    /**
     * Print the current board state to the console (for debugging)
     */
    public void printBoard() {
        System.out.println("Current Board State:");
        for (int row = 0; row < BOARD_SIZE; row++) {
            String line = "";
            for (int col = 0; col < BOARD_SIZE; col++) {
                CellState state = getCellState(row, col);
                if (state == CellState.EMPTY) {
                    line += ". ";
                } else if (state == CellState.FOX) {
                    line += "F ";
                } else if (state == CellState.HOUND) {
                    line += "H ";
                }
            }
            System.out.println(line);
        }
    }
}