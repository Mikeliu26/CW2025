package Model;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages high score persistence with separate scores per game mode.
 * Saves and loads the highest score achieved for each mode.
 */
public class HighScoreManager {

    private static HighScoreManager instance;
    private Map<GameMode, Integer> highScores;
    private final String SAVE_FILE = "highscores.dat";

    private HighScoreManager() {
        highScores = new HashMap<>();
        loadHighScores();
    }

    /**
     * Gets the singleton instance.
     *
     * @return the HighScoreManager instance
     */
    public static HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }

    /**
     * Loads high scores from file.
     * Creates file with score 0 for all modes if it doesn't exist.
     */
    private void loadHighScores() {
        try {
            if (Files.exists(Paths.get(SAVE_FILE))) {
                String content = Files.readString(Paths.get(SAVE_FILE));
                String[] lines = content.split("\n");

                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;

                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        GameMode mode = GameMode.valueOf(parts[0]);
                        int score = Integer.parseInt(parts[1]);
                        highScores.put(mode, score);
                    }
                }
            }

            // Initialize any missing modes with 0
            for (GameMode mode : GameMode.values()) {
                highScores.putIfAbsent(mode, 0);
            }

            saveHighScores();

        } catch (Exception e) {
            System.err.println("Error loading high scores: " + e.getMessage());
            // Initialize all modes to 0 on error
            for (GameMode mode : GameMode.values()) {
                highScores.put(mode, 0);
            }
        }
    }

    /**
     * Saves all high scores to file.
     */
    private void saveHighScores() {
        try {
            StringBuilder content = new StringBuilder();
            for (Map.Entry<GameMode, Integer> entry : highScores.entrySet()) {
                content.append(entry.getKey().name())
                        .append(":")
                        .append(entry.getValue())
                        .append("\n");
            }
            Files.writeString(Paths.get(SAVE_FILE), content.toString());
        } catch (Exception e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }

    /**
     * Gets the high score for a specific mode.
     *
     * @param mode the game mode
     * @return the high score value
     */
    public int getHighScore(GameMode mode) {
        return highScores.getOrDefault(mode, 0);
    }

    /**
     * Checks if a score is a new high score for the mode and updates if so.
     *
     * @param mode the game mode
     * @param score the score to check
     * @return true if this is a new high score, false otherwise
     */
    public boolean checkAndUpdateHighScore(GameMode mode, int score) {
        int currentHigh = highScores.getOrDefault(mode, 0);

        if (score > currentHigh) {
            highScores.put(mode, score);
            saveHighScores();
            return true;
        }
        return false;
    }

    /**
     * Resets the high score for a specific mode to 0.
     *
     * @param mode the game mode to reset
     */
    public void resetHighScore(GameMode mode) {
        highScores.put(mode, 0);
        saveHighScores();
    }

    /**
     * Resets all high scores to 0.
     */
    public void resetAllHighScores() {
        for (GameMode mode : GameMode.values()) {
            highScores.put(mode, 0);
        }
        saveHighScores();
    }
}