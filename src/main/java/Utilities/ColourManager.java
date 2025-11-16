package Utilities;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Manages brick colours for the game
 * Connects with JavaFX to paint and render
 * Elliminate code duplication
 */

public class ColourManager {

    private static final Color[] BRICK_COLORS = {
            Color.TRANSPARENT,    // 0
            Color.AQUA,          // 1
            Color.BLUEVIOLET,    // 2
            Color.DARKGREEN,     // 3
            Color.YELLOW,        // 4
            Color.RED,           // 5
            Color.BEIGE,         // 6
            Color.BURLYWOOD      // 7
    };
/**
 * Retrieves colour for a given color code.
 * Returns white for invalid codes as a safe default.
 */

 public static Paint getFillColor(int colorCode) {
        if (colorCode >= 0 && colorCode < BRICK_COLORS.length) {
            return BRICK_COLORS[colorCode];
        }
        return Color.WHITE; // Default
    }
}