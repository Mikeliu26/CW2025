package Model;

import java.io.*;
import java.nio.file.*;

/**
 * Manages high score persistence.
 * Saves and loads the highest score achieved across game sessions.
 */
public class HighScoreManager {

    private static HighScoreManager instance;
    private int highScore;
    private final String SAVE_FILE = "highscore.dat";

    private HighScoreManager() {
        loadHighScore();
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
     * Loads the high score from file.
     * Creates file with score 0 if it doesn't exist.
     */
    private void loadHighScore() {
        try {
            if (Files.exists(Paths.get(SAVE_FILE))) {
                String content = Files.readString(Paths.get(SAVE_FILE));
                highScore = Integer.parseInt(content.trim());
            } else {
                highScore = 0;
                saveHighScore();
            }
        } catch (Exception e) {
            System.err.println("Error loading high score: " + e.getMessage());
            highScore = 0;
        }
    }

    /**
     * Saves the current high score to file.
     */
    private void saveHighScore() {
        try {
            Files.writeString(Paths.get(SAVE_FILE), String.valueOf(highScore));
        } catch (Exception e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    /**
     * Gets the current high score.
     *
     * @return the high score value
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Checks if a score is a new high score and updates if so.
     *
     * @param score the score to check
     * @return true if this is a new high score, false otherwise
     */
    public boolean checkAndUpdateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
            return true;
        }
        return false;
    }

    /**
     * Resets the high score to 0.
     * Used for testing or if user wants to reset.
     */
    public void resetHighScore() {
        highScore = 0;
        saveHighScore();
    }
}