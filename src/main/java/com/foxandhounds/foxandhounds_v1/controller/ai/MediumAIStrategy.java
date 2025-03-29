package com.foxandhounds.foxandhounds_v1.controller.ai;

import com.foxandhounds.foxandhounds_v1.model.*;
import java.util.*;

/**
 * Medium AI Strategy - Challenging but beatable with skilled play
 */
public class MediumAIStrategy implements AIStrategy {

    // A small element of randomness to make it beatable (20% chance of suboptimal moves)
    private final Random random = new Random();
    private static boolean firstUse = true;

    @Override
    public Move getBestMove(List<Hound> hounds, Fox fox, Board board) {
        // Log first use for debugging
        if (firstUse) {
            System.out.println("🟡🟡🟡 MEDIUM STRATEGY INITIALIZED AND BEING USED 🟡🟡🟡");
            firstUse = false;
        }

        System.out.println("✓ Using MEDIUM strategy");

        // Always take a winning move if available (100% of the time)
        Move trappingMove = AIUtils.findFoxTrappingMove(hounds, fox, board);
        if (trappingMove != null) {
            System.out.println("MEDIUM: Found move to trap fox");
            return trappingMove;
        }

        // Always block the fox from reaching the top row if possible (90% of the time)
        if (random.nextDouble() < 0.9) {
            Move emergencyBlock = findEmergencyBlock(hounds, fox, board);
            if (emergencyBlock != null) {
                System.out.println("MEDIUM: Blocking fox from reaching top row");
                return emergencyBlock;
            }
        }

        // Block fox's paths to top row if possible (80% of the time)
        if (random.nextDouble() < 0.8) {
            Move pathBlockingMove = findPathBlockingMove(hounds, fox, board);
            if (pathBlockingMove != null) {
                System.out.println("MEDIUM: Blocking fox's paths");
                return pathBlockingMove;
            }
        }

        // Try to build a defensive wall (70% of the time)
        if (random.nextDouble() < 0.7) {
            Move wallMove = buildDefensiveLine(hounds, fox, board);
            if (wallMove != null) {
                System.out.println("MEDIUM: Building defensive line");
                return wallMove;
            }
        }

        // Use tactical approach moves (better than random but not perfect)
        Move tacticalMove = findTacticalApproachMove(hounds, fox, board);
        if (tacticalMove != null) {
            System.out.println("MEDIUM: Making tactical move");
            return tacticalMove;
        }

        // Fallback to any valid move
        System.out.println("MEDIUM: Fallback to any valid move");
        return AIUtils.findAnyValidMove(hounds, board);
    }

    /**
     * Check if fox can reach top row in next move and block it
     */
    private Move findEmergencyBlock(List<Hound> hounds, Fox fox, Board board) {
        List<int[]> foxMoves = fox.getPossibleMoves(board);

        // Find if fox can reach top row
        for (int[] foxMove : foxMoves) {
            if (foxMove[0] == 0) {
                // Fox can reach top row! Try to block
                for (int i = 0; i < hounds.size(); i++) {
                    Hound hound = hounds.get(i);
                    List<int[]> possibleMoves = hound.getPossibleMoves(board);

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

    /**
     * Find a move that blocks the fox's paths to the top row
     */
    private Move findPathBlockingMove(List<Hound> hounds, Fox fox, Board board) {
        // Get all paths to top row
        Map<Integer, List<int[]>> pathsToTop = AIUtils.findPathsToTopRow(fox, board);

        if (pathsToTop.isEmpty()) {
            return null;  // No paths to block
        }

        // Try to find moves that reduce paths
        int bestHoundIndex = -1;
        int[] bestMove = null;
        int mostPathsBlocked = 0;

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                // Simulate the move
                int originalRow = hound.getRow();
                int originalCol = hound.getCol();

                board.movePiece(originalRow, originalCol, move[0], move[1]);
                hound.move(move[0], move[1]);

                // Recalculate paths
                Map<Integer, List<int[]>> remainingPaths = AIUtils.findPathsToTopRow(fox, board);
                int currentPaths = AIUtils.countTotalPaths(pathsToTop);
                int remainingPathCount = AIUtils.countTotalPaths(remainingPaths);
                int pathsBlocked = currentPaths - remainingPathCount;

                // Undo the move
                hound.move(originalRow, originalCol);
                board.movePiece(move[0], move[1], originalRow, originalCol);

                // Check if this move blocks more paths
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
     * Try to build a defensive line to restrict fox movement
     */
    private Move buildDefensiveLine(List<Hound> hounds, Fox fox, Board board) {
        // Target row for the defensive line - typically 2-3 rows ahead of fox
        int foxRow = fox.getRow();
        int targetRow;

        if (foxRow <= 2) {
            targetRow = 1;  // Fox near top, build wall at row 1
        } else {
            targetRow = Math.max(1, foxRow - 2);  // 2 rows ahead of fox
        }

        // Check which hounds are already on this row
        List<Integer> houndsOnRow = new ArrayList<>();
        List<Integer> columnsOccupied = new ArrayList<>();

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            if (hound.getRow() == targetRow) {
                houndsOnRow.add(i);
                columnsOccupied.add(hound.getCol());
            }
        }

        // Try to move a hound to the target row
        int bestHoundIndex = -1;
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < hounds.size(); i++) {
            // Skip hounds already on target row
            if (houndsOnRow.contains(i)) {
                continue;
            }

            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                if (move[0] == targetRow) {
                    int score = 100;  // Base score for reaching target row

                    // Check for good spacing
                    boolean goodSpacing = true;
                    for (int col : columnsOccupied) {
                        if (Math.abs(col - move[1]) < 2) {
                            goodSpacing = false;
                            break;
                        }
                    }

                    if (goodSpacing) {
                        score += 50;
                    }

                    // Prefer central columns (better for blocking)
                    int distanceFromCenter = Math.abs(move[1] - Board.BOARD_SIZE / 2);
                    score -= distanceFromCenter * 5;

                    if (score > bestScore) {
                        bestScore = score;
                        bestHoundIndex = i;
                        bestMove = move;
                    }
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
     * Find a tactical move that balances approach with blocking
     */
    private Move findTacticalApproachMove(List<Hound> hounds, Fox fox, Board board) {
        int bestHoundIndex = -1;
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        // Get fox's possible moves to anticipate
        List<int[]> foxMoves = fox.getPossibleMoves(board);
        Set<String> foxMoveKeys = new HashSet<>();
        for (int[] move : foxMoves) {
            foxMoveKeys.add(move[0] + "," + move[1]);
        }

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            for (int[] move : possibleMoves) {
                int score = 0;

                // Base score: Distance to fox (closer is better)
                int distance = Math.abs(move[0] - fox.getRow()) + Math.abs(move[1] - fox.getCol());
                score += (10 - distance) * 5;

                // Bonus for moving toward fox's row
                if (move[0] > hound.getRow() && move[0] <= fox.getRow()) {
                    score += 20;
                }

                // Extra bonus for blocking a fox's potential move
                String moveKey = move[0] + "," + move[1];
                if (foxMoveKeys.contains(moveKey)) {
                    score += 40;
                }

                // Bonus for center control
                int center = Board.BOARD_SIZE / 2;
                int distanceToCenter = Math.abs(move[1] - center);
                score += (4 - distanceToCenter) * 5;

                // Bonus for horizontal alignment (forming a line)
                int sameRowCount = 0;
                for (Hound otherHound : hounds) {
                    if (otherHound != hound && otherHound.getRow() == move[0]) {
                        sameRowCount++;
                    }
                }
                score += sameRowCount * 15;

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
}