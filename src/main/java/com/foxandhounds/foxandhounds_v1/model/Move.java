package com.foxandhounds.foxandhounds_v1.model;

/**
 * Represents a move in the Fox and Hounds game.
 *
 * This class is primarily used to represent moves for the AI controller.
 * It stores the origin and destination coordinates, and which hound is making the move.
 */
public class Move {
    // Which hound is making the move (not used for fox moves)
    private int houndIndex;

    // Starting position
    private int fromRow;
    private int fromCol;

    // Destination position
    private int toRow;
    private int toCol;

    /**
     * Constructor for a hound move.
     *
     * @param houndIndex Index of the hound in the hounds list
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow Destination row
     * @param toCol Destination column
     */
    public Move(int houndIndex, int fromRow, int fromCol, int toRow, int toCol) {
        this.houndIndex = houndIndex;
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    /**
     * Constructor for a fox move (no hound index needed).
     *
     * @param fromRow Starting row
     * @param fromCol Starting column
     * @param toRow Destination row
     * @param toCol Destination column
     */
    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this(-1, fromRow, fromCol, toRow, toCol); // -1 indicates this is a fox move
    }

    /**
     * Gets the hound index for this move.
     *
     * @return Hound index or -1 if this is a fox move
     */
    public int getHoundIndex() {
        return houndIndex;
    }

    /**
     * Gets the starting row of the move.
     *
     * @return Starting row
     */
    public int getFromRow() {
        return fromRow;
    }

    /**
     * Gets the starting column of the move.
     *
     * @return Starting column
     */
    public int getFromCol() {
        return fromCol;
    }

    /**
     * Gets the destination row of the move.
     *
     * @return Destination row
     */
    public int getToRow() {
        return toRow;
    }

    /**
     * Gets the destination column of the move.
     *
     * @return Destination column
     */
    public int getToCol() {
        return toCol;
    }

    /**
     * Returns a string representation of the move.
     *
     * @return String representation of the move
     */
    @Override
    public String toString() {
        String piece = (houndIndex >= 0) ? "Hound " + houndIndex : "Fox";
        return piece + " moves from (" + fromRow + "," + fromCol +
                ") to (" + toRow + "," + toCol + ")";
    }
}