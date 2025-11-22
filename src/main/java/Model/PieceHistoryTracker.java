package Model;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Tracks recently placed pieces for Chaos mode.
 * Only the last N pieces remain visible.
 */
public class PieceHistoryTracker {

    private Queue<PiecePosition> recentPieces;
    private int maxVisible;

    public PieceHistoryTracker(int maxVisiblePieces) {
        this.maxVisible = maxVisiblePieces;
        this.recentPieces = new LinkedList<>();
    }

    /**
     * Records a new piece placement.
     *
     * @param positions array of [row, col] positions of the piece
     */
    public void addPiece(int[][] positions) {
        PiecePosition piece = new PiecePosition(positions);
        recentPieces.add(piece);

        // Remove oldest if we exceed max
        while (recentPieces.size() > maxVisible) {
            recentPieces.poll();
        }
    }

    /**
     * Checks if a position should be visible.
     *
     * @param row the row
     * @param col the column
     * @return true if this position is part of a recent piece
     */
    public boolean isVisible(int row, int col) {
        for (PiecePosition piece : recentPieces) {
            if (piece.contains(row, col)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all tracked pieces.
     */
    public void clear() {
        recentPieces.clear();
    }

    /**
     * Inner class to store piece positions.
     */
    private static class PiecePosition {
        private int[][] positions;

        public PiecePosition(int[][] positions) {
            this.positions = positions;
        }

        public boolean contains(int row, int col) {
            for (int[] pos : positions) {
                if (pos[0] == row && pos[1] == col) {
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * Gets the number of pieces currently tracked.
     */
    public int getRecentPiecesCount() {
        return recentPieces.size();
    }
}