package com.foxandhounds.foxandhounds_v1.controller.ai;

import com.foxandhounds.foxandhounds_v1.model.*;
import java.util.*;

/**
 * Easy AI Strategy - Beatable but still provides a good challenge for beginners
 */
public class EasyAIStrategy implements AIStrategy {

    private final Random random = new Random();
    private static boolean firstUse = true;

    @Override
    public Move getBestMove(List<Hound> hounds, Fox fox, Board board) {
        // Log first use for debugging
        if (firstUse) {
            System.out.println("🔴🔴🔴 EASY STRATEGY INITIALIZED AND BEING USED 🔴🔴🔴");
            firstUse = false;
        }

        System.out.println("✓ Using EASY strategy");

        // Always take a winning move if detected (50% of the time)
        if (random.nextDouble() < 0.5) {
            Move winningMove = AIUtils.findFoxTrappingMove(hounds, fox, board);
            if (winningMove != null) {
                System.out.println("EASY: Found move to trap fox");
                return winningMove;
            }
        }

        // Block the fox from reaching the top row (30% of the time)
        if (random.nextDouble() < 0.3) {
            Move emergencyBlock = findEmergencyBlock(hounds, fox, board);
            if (emergencyBlock != null) {
                System.out.println("EASY: Blocking fox from reaching top row");
                return emergencyBlock;
            }
        }

        // 50% of the time: Use a simple approach strategy
        if (random.nextDouble() < 0.5) {
            Move approachMove = findSimpleApproachMove(hounds, fox, board);
            if (approachMove != null) {
                System.out.println("EASY: Moving toward fox");
                return approachMove;
            }
        }

        // 50% of the time: Make a completely random move
        List<Move> allPossibleMoves = AIUtils.getAllPossibleMoves(hounds, board);
        if (!allPossibleMoves.isEmpty()) {
            System.out.println("EASY: Making random move");
            return allPossibleMoves.get(random.nextInt(allPossibleMoves.size()));
        }

        // Fallback
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
     * Find a simple move that approaches the fox
     */
    private Move findSimpleApproachMove(List<Hound> hounds, Fox fox, Board board) {
        // Find the hound closest to the fox
        int closestHoundIndex = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < hounds.size(); i++) {
            Hound hound = hounds.get(i);
            int distance = Math.abs(hound.getRow() - fox.getRow()) + Math.abs(hound.getCol() - fox.getCol());

            if (distance < minDistance) {
                minDistance = distance;
                closestHoundIndex = i;
            }
        }

        // Move the closest hound toward the fox
        if (closestHoundIndex != -1) {
            Hound hound = hounds.get(closestHoundIndex);
            List<int[]> possibleMoves = hound.getPossibleMoves(board);

            if (!possibleMoves.isEmpty()) {
                // Find move that gets closest to fox
                int[] bestMove = null;
                int bestDistance = Integer.MAX_VALUE;

                for (int[] move : possibleMoves) {
                    int newDistance = Math.abs(move[0] - fox.getRow()) + Math.abs(move[1] - fox.getCol());

                    if (newDistance < bestDistance) {
                        bestDistance = newDistance;
                        bestMove = move;
                    }
                }

                if (bestMove != null) {
                    return new Move(closestHoundIndex, hound.getRow(), hound.getCol(), bestMove[0], bestMove[1]);
                }
            }
        }

        // If we can't move the closest hound, pick any hound
        for (int i = 0; i < hounds.size(); i++) {
            if (i != closestHoundIndex) {
                Hound hound = hounds.get(i);
                List<int[]> possibleMoves = hound.getPossibleMoves(board);

                if (!possibleMoves.isEmpty()) {
                    // Just pick the first move
                    int[] move = possibleMoves.get(0);
                    return new Move(i, hound.getRow(), hound.getCol(), move[0], move[1]);
                }
            }
        }

        return null;
    }
}