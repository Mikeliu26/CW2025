package Model;

/**
 * Represents different game modes with unique rules and challenges.
 */
public enum GameMode {

    ZEN("Zen Mode",
            "Endless relaxation - board clears when full",
            500, 5, false, false),

    SPRINT("Sprint Mode",
            "Clear 20 lines as fast as possible",
            250, 3, true, false),

    BLITZ("Blitz Mode",
            "Maximum score in 2 minutes",
            150, 2, false, true),

    CHAOS("Chaos Mode",
            "Only see last 6 pieces - test your memory!",
            100, 1, false, false);

    private final String displayName;
    private final String description;
    private final int baseSpeed;  // Fall speed in milliseconds
    private final int previewCount;  // Number of next pieces to show
    private final boolean hasLineGoal;  // Sprint mode has line goal
    private final boolean hasTimeLimit;  // Blitz mode has time limit

    GameMode(String displayName, String description, int baseSpeed,
             int previewCount, boolean hasLineGoal, boolean hasTimeLimit) {
        this.displayName = displayName;
        this.description = description;
        this.baseSpeed = baseSpeed;
        this.previewCount = previewCount;
        this.hasLineGoal = hasLineGoal;
        this.hasTimeLimit = hasTimeLimit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public int getPreviewCount() {
        return previewCount;
    }

    public boolean hasLineGoal() {
        return hasLineGoal;
    }

    public boolean hasTimeLimit() {
        return hasTimeLimit;
    }

    /**
     * Checks if this mode has invisible piece mechanic (Chaos).
     */
    public boolean hasInvisiblePieces() {
        return this == CHAOS;
    }

    /**
     * Checks if this mode has no game over (Zen).
     */
    public boolean isEndless() {
        return this == ZEN;
    }

    /**
     * Gets the goal for Sprint mode (20 lines).
     */
    public int getLineGoal() {
        return hasLineGoal ? 20 : -1;
    }

    /**
     * Gets time limit for Blitz mode (120 seconds).
     */
    public int getTimeLimit() {
        return hasTimeLimit ? 120 : -1;
    }
}