package Utilities;

/**
 * Make all the constants appear in one file for easy managements
 * Reduce error and increase ability and ease of editing
 */

public final class GameConstants {


    private GameConstants() {
        throw new AssertionError("Cannot instantiate GameConstants");
    }

    /** Size of each bricks in pixels */
    public static final int BRICK_SIZE = 20;
    /** Verticle offset for bricks panel positioning */
    public static final int VERTICAL_OFFSET = -42;
    /** Fall speed in ms */
    public static final int FALL_SPEED_MS = 400;
    /** Duration notifications*/
    public static final int NOTIFICATION_DURATION_MS = 2000;

    /** Board dimension for height and width*/
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 25;

    /** Initial vertical and horizonal position for new bricks */
    public static final int INITIAL_BRICK_X = 4;
    public static final int INITIAL_BRICK_Y = 0;  // Start at top!

    /** Scording system */
    public static final int SCORE_PER_LINE = 50;
    /** How many multiplier if you get multiple lines*/
    public static final int SCORE_MULTIPLIER_DOUBLE = 3;
    public static final int SCORE_MULTIPLIER_TRIPLE = 5;
    public static final int SCORE_MULTIPLIER_TETRIS = 8;
}