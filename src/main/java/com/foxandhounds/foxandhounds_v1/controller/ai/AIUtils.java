package com.foxandhounds.foxandhounds_v1.controller.ai;

import com.foxandhounds.foxandhounds_v1.model.*;
import java.util.*;

/**
 * Utility methods shared across AI strategies
 */
public class AIUtils {

    /**
     * Find any valid move (fallback option for all strategies)
     */
    public static Move findAnyValidMove(List<Hound> hounds, Board board) {
        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            if (!possibleMoves.isEmpty()) {
                int[] move = possibleMoves.get(0);
                return new Move(i, hound.getRow(), hound.getCol(), move[0], move[1]);
            }
        }
        return null;
    }

    /**
     * Get all possible moves for all hounds
     */
    public static List<Move> getAllPossibleMoves(List<Hound> hounds, Board board) {
        List<Move> allMoves = new ArrayList<>();

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                allMoves.add(new Move(i, hound.getRow(), hound.getCol(), move[0], move[1]));
            }
        }

        return allMoves;
    }

    /**
     * Find a move that completely traps the fox
     * This is a winning move for any AI level if available
     */
    public static Move findFoxTrappingMove(List<Hound> hounds, Fox fox, Board board) {
        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                // Temporarily make the move
                int originalRow = hound.getRow();
                int originalCol = hound.getCol();

                // Simulate the move
                board.movePiece(originalRow, originalCol, move[0], move[1]);
                hound.move(move[0], move[1]);

                // Check if fox is now blocked
                boolean foxBlocked = board.isFoxBlocked(fox.getRow(), fox.getCol());

                // Undo the move
                hound.move(originalRow, originalCol);
                board.movePiece(move[0], move[1], originalRow, originalCol);

                if (foxBlocked) {
                    return new Move(i, originalRow, originalCol, move[0], move[1]);
                }
            }
        }
        return null;
    }

    /**
     * Find paths to top row using BFS
     * Returns a map where key is the column in top row and value is list of paths to that position
     */
    public static Map<Integer, List<int[]>> findPathsToTopRow(Fox fox, Board board) {
        Map<Integer, List<int[]>> pathsToTopByColumn = new HashMap<>();

        // Track visited cells
        Set<String> visited = new HashSet<>();
        Map<String, String> parents = new HashMap<>();
        Queue<int[]> queue = new LinkedList<>();

        // Start BFS from fox position
        queue.add(new int[]{fox.getRow(), fox.getCol()});
        visited.add(fox.getRow() + "," + fox.getCol());

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];
            String currentKey = row + "," + col;

            // If we've reached the top row, reconstruct the path
            if (row == 0) {
                List<int[]> path = new ArrayList<>();
                String cellKey = currentKey;

                // Reconstruct path from parents map
                while (cellKey != null) {
                    String[] coords = cellKey.split(",");
                    int pathRow = Integer.parseInt(coords[0]);
                    int pathCol = Integer.parseInt(coords[1]);
                    path.add(0, new int[]{pathRow, pathCol});

                    cellKey = parents.get(cellKey);
                }

                // Store path indexed by column it reaches in top row
                pathsToTopByColumn.computeIfAbsent(col, k -> new ArrayList<>()).add(path.get(0));
                continue;
            }

            // Try all four diagonal directions
            int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow < 0 || newRow >= Board.BOARD_SIZE || newCol < 0 || newCol >= Board.BOARD_SIZE) {
                    continue;  // Out of bounds
                }

                String newKey = newRow + "," + newCol;

                // If not visited and cell is empty
                if (!visited.contains(newKey) && !board.isCellOccupied(newRow, newCol)) {
                    visited.add(newKey);
                    parents.put(newKey, currentKey);
                    queue.add(new int[]{newRow, newCol});
                }
            }
        }

        return pathsToTopByColumn;
    }

    /**
     * Count total number of paths from a path map
     */
    public static int countTotalPaths(Map<Integer, List<int[]>> pathsMap) {
        int totalPaths = 0;
        for (List<int[]> paths : pathsMap.values()) {
            totalPaths += paths.size();
        }
        return totalPaths;
    }

    /**
     * Find a move that directly blocks a fox that's trying to reach the top row
     */
    public static Move findDirectBlockingMove(List<Hound> hounds, Fox fox, Board board) {
        // Get fox's possible moves
        List<int[]> foxMoves = fox.getPossibleMoves(board);

        // Check if any move gets the fox to the top row
        for (int[] foxMove : foxMoves) {
            if (foxMove[0] == 0) {
                // Fox can reach top row! Try to block
                for (int i = 0; i < hounds.size(); i++) {
                    Hound hound = hounds.get(i);
                    List<int[]> possibleMoves = hound.getPossibleMoves(board);

                    // See if any hound can move to this position
                    for (int[] move : possibleMoves) {
                        if (move[0] == foxMove[0] && move[1] == foxMove[1]) {
                            return new Move(i, hound.getRow(), hound.getCol(), move[0], move[1]);
                        }
                    }
                }
            }
        }

        return null;
    }
}