package Model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

/**
 * Manages game timer for time-based modes.
 * Can count up (Sprint) or count down (Blitz).
 */
public class GameTimer {

    private Timeline timeline;
    private IntegerProperty secondsElapsed;
    private int startTime;  // For countdown modes
    private boolean isCountdown;
    private Runnable onTimeUp;  // Callback when timer reaches 0

    public GameTimer(boolean countdown, int startSeconds) {
        this.isCountdown = countdown;
        this.startTime = countdown ? startSeconds : 0;
        this.secondsElapsed = new SimpleIntegerProperty(startTime);
    }

    /**
     * Starts the timer.
     */
    public void start() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (isCountdown) {
                // Count down
                int newValue = secondsElapsed.get() - 1;
                secondsElapsed.set(newValue);

                if (newValue <= 0 && onTimeUp != null) {
                    stop();
                    onTimeUp.run();
                }
            } else {
                // Count up
                secondsElapsed.set(secondsElapsed.get() + 1);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    /**
     * Pauses the timer.
     */
    public void pause() {
        if (timeline != null) {
            timeline.pause();
        }
    }

    /**
     * Resumes the timer.
     */
    public void resume() {
        if (timeline != null) {
            timeline.play();
        }
    }

    /**
     * Resets the timer to start value.
     */
    public void reset() {
        secondsElapsed.set(startTime);
    }

    /**
     * Gets seconds elapsed/remaining.
     */
    public int getSeconds() {
        return secondsElapsed.get();
    }

    /**
     * Gets the seconds property for binding.
     */
    public IntegerProperty secondsProperty() {
        return secondsElapsed;
    }

    /**
     * Formats seconds as MM:SS.
     */
    public static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Sets callback for when countdown reaches 0.
     */
    public void setOnTimeUp(Runnable callback) {
        this.onTimeUp = callback;
    }
}