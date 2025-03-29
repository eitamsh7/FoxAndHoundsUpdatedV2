package com.foxandhounds.foxandhounds_v1.controller.ai;

import com.foxandhounds.foxandhounds_v1.model.*;
import java.util.*;

/**
 * Enhanced Hard AI Strategy - Extremely difficult to beat
 */
public class HardAIStrategy implements AIStrategy {

    // Board center
    private final int BOARD_CENTER = Board.BOARD_SIZE / 2;
    private static boolean firstUse = true;

    @Override
    public Move getBestMove(List<Hound> hounds, Fox fox, Board board) {
        // Log first use for debugging
        if (firstUse) {
            System.out.println("🟢🟢🟢 HARD STRATEGY INITIALIZED AND BEING USED 🟢🟢🟢");
            firstUse = false;
        }

        System.out.println("✓ Using ENHANCED HARD strategy");

        // Print debug info
        System.out.println("Fox position: " + fox.getRow() + "," + fox.getCol());

        // HIGHEST PRIORITY: Emergency blocking - if fox can reach top row in next move
        Move emergencyBlock = findEmergencyBlock(hounds, fox, board);
        if (emergencyBlock != null) {
            System.out.println("HARD: EMERGENCY BLOCK - Fox was about to win!");
            return emergencyBlock;
        }

        // HIGH PRIORITY: Win if possible
        Move trappingMove = findFoxTrappingMove(hounds, fox, board);
        if (trappingMove != null) {
            System.out.println("HARD: Found move to trap fox");
            return trappingMove;
        }

        // Calculate how many moves the fox needs to reach the top row
        int foxDistanceToTop = fox.getRow();
        System.out.println("Fox is " + foxDistanceToTop + " moves from top row");

        // If fox is getting close to the top, focus on blocking its path
        if (foxDistanceToTop <= 3) {
            // PRIORITY: Block ALL paths to top row
            Move blockingMove = findComprehensiveBlockingMove(hounds, fox, board);
            if (blockingMove != null) {
                System.out.println("HARD: Comprehensive path blocking");
                return blockingMove;
            }
        }

        // Form a diagonal wall defense formation
        Move wallMove = formDiagonalWall(hounds, fox, board);
        if (wallMove != null) {
            System.out.println("HARD: Forming diagonal wall");
            return wallMove;
        }

        // Advanced strategic move based on positional advantage
        Move strategicMove = findAdvancedStrategicMove(hounds, fox, board);
        if (strategicMove != null) {
            System.out.println("HARD: Making advanced strategic move");
            return strategicMove;
        }

        // Fallback: Any tactical approach
        System.out.println("HARD: Falling back to tactical approach");
        return findTacticalApproachMove(hounds, fox, board);
    }

    /**
     * Check if fox can reach top row in one move and block it
     */
    private Move findEmergencyBlock(List<Hound> hounds, Fox fox, Board board) {
        List<int[]> foxMoves = fox.getPossibleMoves(board);

        for (int[] foxMove : foxMoves) {
            if (foxMove[0] == 0) { // Fox can reach top row!
                System.out.println("EMERGENCY: Fox can reach top row at position " + foxMove[0] + "," + foxMove[1]);

                // Try to block with any hound
                for (int i = 0; i < hounds.size(); i++) {
                    Hound hound = hounds.get(i);
                    List<int[]> possibleMoves = hound.getPossibleMoves(board);

                    for (int[] move : possibleMoves) {
                        if (move[0] == foxMove[0] && move[1] == foxMove[1]) {
                            System.out.println("EMERGENCY BLOCK: Using hound " + i + " at position " +
                                    hound.getRow() + "," + hound.getCol() + " to block");
                            return new Move(i, hound.getRow(), hound.getCol(), move[0], move[1]);
                        }
                    }
                }

                System.out.println("WARNING: Could not find a hound to block the fox's path to top row!");
            }
        }

        return null;
    }

    /**
     * Find a move that traps the fox completely
     */
    private Move findFoxTrappingMove(List<Hound> hounds, Fox fox, Board board) {
        // Check each hound's possible moves
        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                // Simulate the move
                int originalRow = hound.getRow();
                int originalCol = hound.getCol();

                board.movePiece(originalRow, originalCol, move[0], move[1]);
                hound.move(move[0], move[1]);

                // Check if fox is now blocked
                boolean foxBlocked = board.isFoxBlocked(fox.getRow(), fox.getCol());

                // Undo the move
                hound.move(originalRow, originalCol);
                board.movePiece(move[0], move[1], originalRow, originalCol);

                if (foxBlocked) {
                    System.out.println("TRAPPING MOVE FOUND: Can trap fox by moving hound " + i +
                            " to position " + move[0] + "," + move[1]);
                    return new Move(i, originalRow, originalCol, move[0], move[1]);
                }
            }
        }

        return null;
    }

    /**
     * Find a comprehensive blocking move that cuts off ALL possible paths to the top
     */
    private Move findComprehensiveBlockingMove(List<Hound> hounds, Fox fox, Board board) {
        // First, analyze all possible paths the fox could take to the top row
        Map<String, List<List<int[]>>> allPathsMap = findAllPathsToTop(fox, board);

        if (allPathsMap.isEmpty()) {
            return null; // No paths to block
        }

        // Find common critical points in these paths
        Set<String> criticalPoints = findCriticalPoints(allPathsMap);

        System.out.println("Found " + criticalPoints.size() + " critical points to block");

        // For each critical point, see if we can block it
        for (String point : criticalPoints) {
            String[] coords = point.split(",");
            int row = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);

            // Try to find a hound that can move to this point
            for (int i = 0; i < hounds.size(); i++) {
                Hound hound = hounds.get(i);
                List<int[]> possibleMoves = hound.getPossibleMoves(board);

                for (int[] move : possibleMoves) {
                    if (move[0] == row && move[1] == col) {
                        // Test if this move blocks ALL paths
                        int originalRow = hound.getRow();
                        int originalCol = hound.getCol();

                        board.movePiece(originalRow, originalCol, move[0], move[1]);
                        hound.move(move[0], move[1]);

                        Map<String, List<List<int[]>>> remainingPaths = findAllPathsToTop(fox, board);

                        // Undo the move
                        hound.move(originalRow, originalCol);
                        board.movePiece(move[0], move[1], originalRow, originalCol);

                        // If all paths are blocked, this is a perfect move
                        if (remainingPaths.isEmpty()) {
                            return new Move(i, originalRow, originalCol, move[0], move[1]);
                        }
                    }
                }
            }
        }

        // If we can't block all paths with one move, find the most effective blocking move
        int bestHoundIndex = -1;
        int[] bestMove = null;
        int mostPathsBlocked = 0;

        int totalInitialPaths = countTotalPaths(allPathsMap);

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                // Simulate this move
                int originalRow = hound.getRow();
                int originalCol = hound.getCol();

                board.movePiece(originalRow, originalCol, move[0], move[1]);
                hound.move(move[0], move[1]);

                // Calculate remaining paths
                Map<String, List<List<int[]>>> remainingPaths = findAllPathsToTop(fox, board);
                int remainingPathCount = countTotalPaths(remainingPaths);
                int pathsBlocked = totalInitialPaths - remainingPathCount;

                // Undo the move
                hound.move(originalRow, originalCol);
                board.movePiece(move[0], move[1], originalRow, originalCol);

                // Is this the best blocking move so far?
                if (pathsBlocked > mostPathsBlocked) {
                    mostPathsBlocked = pathsBlocked;
                    bestHoundIndex = i;
                    bestMove = move;
                }
            }
        }

        if (bestHoundIndex != -1 && bestMove != null && mostPathsBlocked > 0) {
            Hound hound = hounds.get(bestHoundIndex);
            return new Move(bestHoundIndex, hound.getRow(), hound.getCol(), bestMove[0], bestMove[1]);
        }

        return null;
    }

    /**
     * Find all possible paths from fox to top row
     */
    private Map<String, List<List<int[]>>> findAllPathsToTop(Fox fox, Board board) {
        Map<String, List<List<int[]>>> allPaths = new HashMap<>();

        // BFS search for all paths
        findPathsRecursive(fox.getRow(), fox.getCol(), new ArrayList<>(), allPaths, board, new HashSet<>());

        return allPaths;
    }

    /**
     * Recursive helper to find all paths
     */
    private void findPathsRecursive(int row, int col, List<int[]> currentPath,
                                    Map<String, List<List<int[]>>> allPaths,
                                    Board board, Set<String> visited) {
        // Add current position to path
        currentPath.add(new int[]{row, col});
        visited.add(row + "," + col);

        // If we reached the top row, record this path
        if (row == 0) {
            String endPoint = row + "," + col;
            if (!allPaths.containsKey(endPoint)) {
                allPaths.put(endPoint, new ArrayList<>());
            }
            allPaths.get(endPoint).add(new ArrayList<>(currentPath));
        } else {
            // Try all four diagonal directions
            int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String newKey = newRow + "," + newCol;

                // Check if this move is valid and not already visited
                if (board.isValidCell(newRow, newCol) &&
                        !board.isCellOccupied(newRow, newCol) &&
                        !visited.contains(newKey)) {

                    // Recursively explore this path
                    findPathsRecursive(newRow, newCol, new ArrayList<>(currentPath),
                            allPaths, board, new HashSet<>(visited));
                }
            }
        }
    }

    /**
     * Find critical points that appear in multiple paths
     */
    private Set<String> findCriticalPoints(Map<String, List<List<int[]>>> allPaths) {
        // Count occurrences of each position in all paths
        Map<String, Integer> pointFrequency = new HashMap<>();

        for (List<List<int[]>> pathsToEndpoint : allPaths.values()) {
            for (List<int[]> path : pathsToEndpoint) {
                // Skip the first point (fox position) and last point (top row)
                for (int i = 1; i < path.size() - 1; i++) {
                    int[] point = path.get(i);
                    String key = point[0] + "," + point[1];

                    pointFrequency.put(key, pointFrequency.getOrDefault(key, 0) + 1);
                }
            }
        }

        // Sort points by frequency (highest first)
        List<Map.Entry<String, Integer>> sortedPoints = new ArrayList<>(pointFrequency.entrySet());
        sortedPoints.sort((a, b) -> b.getValue() - a.getValue());

        // Take the top 30% most frequent points as critical
        Set<String> criticalPoints = new HashSet<>();
        int criticalCount = Math.max(1, (int)(sortedPoints.size() * 0.3));

        for (int i = 0; i < criticalCount && i < sortedPoints.size(); i++) {
            criticalPoints.add(sortedPoints.get(i).getKey());
        }

        return criticalPoints;
    }

    /**
     * Count total paths across all endpoints
     */
    private int countTotalPaths(Map<String, List<List<int[]>>> pathsMap) {
        int count = 0;
        for (List<List<int[]>> paths : pathsMap.values()) {
            count += paths.size();
        }
        return count;
    }

    /**
     * Form a defensive diagonal wall to block the fox
     */
    private Move formDiagonalWall(List<Hound> hounds, Fox fox, Board board) {
        // Determine ideal formation based on fox position
        boolean foxOnLeft = fox.getCol() < BOARD_CENTER;

        // Target positions for optimal diagonal wall
        List<int[]> targetPositions = new ArrayList<>();

        if (foxOnLeft) {
            // Fox on left, form wall slanting down-right
            targetPositions.add(new int[]{1, 2});
            targetPositions.add(new int[]{2, 3});
            targetPositions.add(new int[]{3, 4});
            targetPositions.add(new int[]{4, 5});
        } else {
            // Fox on right, form wall slanting down-left
            targetPositions.add(new int[]{1, 5});
            targetPositions.add(new int[]{2, 4});
            targetPositions.add(new int[]{3, 3});
            targetPositions.add(new int[]{4, 2});
        }

        // Find the hound furthest from its ideal position
        int worstHoundIndex = -1;
        int worstDistance = -1;

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            int[] target = targetPositions.get(i);

            int distance = Math.abs(hound.getRow() - target[0]) + Math.abs(hound.getCol() - target[1]);

            if (distance > worstDistance) {
                worstDistance = distance;
                worstHoundIndex = i;
            }
        }

        // If we found a hound that's far from ideal position, move it closer
        if (worstHoundIndex != -1 && worstDistance > 0) {
            Hound hound = hounds.get(worstHoundIndex);
            int[] target = targetPositions.get(worstHoundIndex);

            // Find the best move toward the target
            List<int[]> possibleMoves = hound.getPossibleMoves(board);
            int bestMoveIndex = -1;
            int bestImprovement = -1;

            for (int i = 0; i < possibleMoves.size(); i++) {
                int[] move = possibleMoves.get(i);
                int currentDistance = Math.abs(hound.getRow() - target[0]) + Math.abs(hound.getCol() - target[1]);
                int newDistance = Math.abs(move[0] - target[0]) + Math.abs(move[1] - target[1]);
                int improvement = currentDistance - newDistance;

                if (improvement > bestImprovement) {
                    bestImprovement = improvement;
                    bestMoveIndex = i;
                }
            }

            if (bestMoveIndex != -1 && bestImprovement > 0) {
                int[] move = possibleMoves.get(bestMoveIndex);
                return new Move(worstHoundIndex, hound.getRow(), hound.getCol(), move[0], move[1]);
            }
        }

        return null;
    }

    /**
     * Find an advanced strategic move
     */
    private Move findAdvancedStrategicMove(List<Hound> hounds, Fox fox, Board board) {
        int bestHoundIndex = -1;
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        // Get fox's possible moves
        List<int[]> foxMoves = fox.getPossibleMoves(board);

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                // Simulate this move
                int originalRow = hound.getRow();
                int originalCol = hound.getCol();

                board.movePiece(originalRow, originalCol, move[0], move[1]);
                hound.move(move[0], move[1]);

                // Evaluate position after move
                int score = evaluatePosition(hounds, fox, board);

                // Check how many moves fox will have after this move
                List<int[]> newFoxMoves = fox.getPossibleMoves(board);
                int moveReduction = foxMoves.size() - newFoxMoves.size();

                // Big bonus for restricting fox movement
                score += moveReduction * 50;

                // Bonus for moves that advance toward fox's row
                if (move[0] <= fox.getRow() + 1) {
                    score += 30;
                }

                // Bonus for moves that cut off diagonal paths
                if ((Math.abs(move[0] - fox.getRow()) == 1) &&
                        (Math.abs(move[1] - fox.getCol()) == 1)) {
                    score += 40;
                }

                // Undo the move
                hound.move(originalRow, originalCol);
                board.movePiece(move[0], move[1], originalRow, originalCol);

                if (score > bestScore) {
                    bestScore = score;
                    bestHoundIndex = i;
                    bestMove = move;
                }
            }
        }

        if (bestHoundIndex != -1 && bestMove != null) {
            Hound hound = hounds.get(bestHoundIndex);
            return new Move(bestHoundIndex, hound.getRow(), hound.getCol(), bestMove[0], bestMove[1]);
        }

        return null;
    }

    /**
     * Evaluate the current board position
     */
    private int evaluatePosition(List<Hound> hounds, Fox fox, Board board) {
        int score = 0;

        // Factor 1: Distance of hounds to fox
        for (Hound hound : hounds) {
            int distance = Math.abs(hound.getRow() - fox.getRow()) + Math.abs(hound.getCol() - fox.getCol());
            score -= distance * 5; // Closer is better
        }

        // Factor 2: Control of key diagonals
        score += evaluateDiagonalControl(hounds, fox);

        // Factor 3: Fox's distance from top row
        score += fox.getRow() * 15; // Further from top row is better

        // Factor 4: Hound formation quality
        score += evaluateHoundFormation(hounds);

        return score;
    }

    /**
     * Evaluate control of key diagonals
     */
    private int evaluateDiagonalControl(List<Hound> hounds, Fox fox) {
        int score = 0;

        // Count hounds on each diagonal type (positive and negative slope)
        Map<Integer, Integer> positiveDiagonals = new HashMap<>(); // r+c = constant
        Map<Integer, Integer> negativeDiagonals = new HashMap<>(); // r-c = constant

        for (Hound hound : hounds) {
            int posKey = hound.getRow() + hound.getCol();
            int negKey = hound.getRow() - hound.getCol();

            positiveDiagonals.put(posKey, positiveDiagonals.getOrDefault(posKey, 0) + 1);
            negativeDiagonals.put(negKey, negativeDiagonals.getOrDefault(negKey, 0) + 1);
        }

        // Check fox's diagonals
        int foxPosDiag = fox.getRow() + fox.getCol();
        int foxNegDiag = fox.getRow() - fox.getCol();

        // Huge bonus for controlling fox's diagonals
        if (positiveDiagonals.containsKey(foxPosDiag)) {
            score += positiveDiagonals.get(foxPosDiag) * 50;
        }

        if (negativeDiagonals.containsKey(foxNegDiag)) {
            score += negativeDiagonals.get(foxNegDiag) * 50;
        }

        return score;
    }

    /**
     * Evaluate quality of hound formation
     */
    private int evaluateHoundFormation(List<Hound> hounds) {
        int score = 0;

        // Check for hounds forming horizontal lines (strongest formation)
        Map<Integer, Integer> rowCounts = new HashMap<>();
        for (Hound hound : hounds) {
            int row = hound.getRow();
            rowCounts.put(row, rowCounts.getOrDefault(row, 0) + 1);
        }

        // Bonus for multiple hounds on same row
        for (Map.Entry<Integer, Integer> entry : rowCounts.entrySet()) {
            if (entry.getValue() > 1) {
                score += entry.getValue() * 20;
            }
        }

        // Check for good spacing between hounds
        int goodSpacings = 0;
        for (int i = 0; i < hounds.size(); i++) {
            for (int j = i + 1; j < hounds.size(); j++) {
                int rowDiff = Math.abs(hounds.get(i).getRow() - hounds.get(j).getRow());
                int colDiff = Math.abs(hounds.get(i).getCol() - hounds.get(j).getCol());

                // Ideal spacing is 2 units apart
                if ((rowDiff == 0 && colDiff == 2) ||
                        (rowDiff == 2 && colDiff == 0) ||
                        (rowDiff == 1 && colDiff == 1)) {
                    goodSpacings++;
                }
            }
        }

        score += goodSpacings * 15;

        return score;
    }

    /**
     * Find a tactical approach move (fallback)
     */
    private Move findTacticalApproachMove(List<Hound> hounds, Fox fox, Board board) {
        int bestHoundIndex = -1;
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                // Basic scoring - prefer moves closer to fox
                int distance = Math.abs(move[0] - fox.getRow()) + Math.abs(move[1] - fox.getCol());
                int score = 100 - (distance * 10);

                // Prefer forward movement
                if (move[0] > hound.getRow()) {
                    score += 20;
                }

                // Prefer center control
                int distanceFromCenter = Math.abs(move[1] - BOARD_CENTER);
                score += (3 - Math.min(3, distanceFromCenter)) * 5;

                if (score > bestScore) {
                    bestScore = score;
                    bestHoundIndex = i;
                    bestMove = move;
                }
            }
        }

        if (bestHoundIndex != -1 && bestMove != null) {
            Hound hound = hounds.get(bestHoundIndex);
            return new Move(bestHoundIndex, hound.getRow(), hound.getCol(), bestMove[0], bestMove[1]);
        }

        // Last resort
        return AIUtils.findAnyValidMove(hounds, board);
    }
}