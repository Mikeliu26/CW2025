package com.comp2042.logic.bricks;

import java.util.List;

public interface Brick {

    List<int[][]> getShapeMatrix();

    /**
     * Gets the current shape matrix for display.
     * Returns the first rotation state of the brick.
     *
     * @return 2D array representing the brick shape
     */
    default int[][] getShape() {
        List<int[][]> shapeMatrix = getShapeMatrix();
        if (shapeMatrix == null || shapeMatrix.isEmpty()) {
            return new int[0][0];
        }
        return shapeMatrix.get(0);  // Return first rotation
    }
}