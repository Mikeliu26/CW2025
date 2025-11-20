package Model;

/**
 * Manages game level progression and speed increases.
 * Level increases every 10 lines cleared.
 */
public class LevelManager {

    private int currentLevel;
    private int totalLinesCleared;
    private static final int LINES_PER_LEVEL = 10;
    private static final int BASE_SPEED = 400;  // Starting speed in ms
    private static final int SPEED_DECREASE_PER_LEVEL = 50;  // Gets faster each level
    private static final int MIN_SPEED = 100;  // Fastest possible speed

    public LevelManager() {
        currentLevel = 1;
        totalLinesCleared = 0;
    }

    /**
     * Adds cleared lines and updates level if threshold reached.
     *
     * @param linesCleared number of lines just cleared
     * @return true if level increased, false otherwise
     */
    public boolean addLines(int linesCleared) {
        int previousLevel = currentLevel;
        totalLinesCleared += linesCleared;
        currentLevel = (totalLinesCleared / LINES_PER_LEVEL) + 1;

        return currentLevel > previousLevel;  // Did we level up?
    }

    /**
     * Gets the current level.
     *
     * @return current level number
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Gets total lines cleared across all levels.
     *
     * @return total lines cleared
     */
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    /**
     * Gets lines needed until next level.
     *
     * @return lines remaining to next level
     */
    public int getLinesUntilNextLevel() {
        return LINES_PER_LEVEL - (totalLinesCleared % LINES_PER_LEVEL);
    }

    /**
     * Calculates fall speed for current level.
     * Speed increases (time decreases) with each level.
     *
     * @return fall speed in milliseconds
     */
    public int getFallSpeed() {
        int speed = BASE_SPEED - ((currentLevel - 1) * SPEED_DECREASE_PER_LEVEL);
        return Math.max(speed, MIN_SPEED);  // Don't go below minimum
    }

    /**
     * Resets level system for new game.
     */
    public void reset() {
        currentLevel = 1;
        totalLinesCleared = 0;
    }
}