package Utilities;

import java.awt.Point;

/**
 * Calculates the landing position for ghost piece preview.
 * Shows players where their current brick will land.
 */
public class GhostPieceCalculator {

    /**
     * Calculates the final landing position for a brick.
     * Simulates dropping the brick straight down until it hits something.
     *
     * @param boardMatrix the game board state
     * @param brickMatrix the brick shape matrix
     * @param currentX current horizontal position
     * @param currentY current vertical position
     * @return Point representing where the brick will land
     */

    public static Point calculateGhostPosition(int[][] boardMatrix, int[][] brickMatrix,
                                               int currentX, int currentY) {
        int ghostY = currentY;

        /** Keep moving down until we hit something */
        while (!MatrixOperations.intersect(boardMatrix, brickMatrix, currentX, ghostY + 1)) {
            ghostY++;
        }

        return new Point(currentX, ghostY);
    }
}