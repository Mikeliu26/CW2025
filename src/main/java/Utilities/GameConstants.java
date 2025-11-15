package Utilities;

public final class GameConstants {

    // Prevent instantiation
    private GameConstants() {
        throw new AssertionError("Cannot instantiate GameConstants");
    }

    // Display constants
    public static final int BRICK_SIZE = 20;
    public static final int VERTICAL_OFFSET = -42;

    // Timing constants (in milliseconds)
    public static final int FALL_SPEED_MS = 400;
    public static final int NOTIFICATION_DURATION_MS = 2000;

    // Board dimensions
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 25;

    // Initial brick position
    public static final int INITIAL_BRICK_X = 4;
    public static final int INITIAL_BRICK_Y = 0;  // Start at top!

    // Scoring
    public static final int SCORE_PER_LINE = 50;
    public static final int SCORE_MULTIPLIER_DOUBLE = 3;
    public static final int SCORE_MULTIPLIER_TRIPLE = 5;
    public static final int SCORE_MULTIPLIER_TETRIS = 8;
}