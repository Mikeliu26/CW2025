package Controller;

import Model.GameMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the mode selection screen.
 * Allows player to choose between different game modes.
 */
public class ModeSelectionController {

    /**
     * Starts game in Zen mode.
     */
    public void selectZenMode(ActionEvent event) {
        startGame(event, GameMode.ZEN);
    }

    /**
     * Starts game in Sprint mode.
     */
    public void selectSprintMode(ActionEvent event) {
        startGame(event, GameMode.SPRINT);
    }

    /**
     * Starts game in Blitz mode.
     */
    public void selectBlitzMode(ActionEvent event) {
        startGame(event, GameMode.BLITZ);
    }

    /**
     * Starts game in Chaos mode.
     */
    public void selectChaosMode(ActionEvent event) {
        startGame(event, GameMode.CHAOS);
    }

    /**
     * Loads the game with selected mode.
     */
    private void startGame(ActionEvent event, GameMode mode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/gameLayout.fxml"));
            Parent root = loader.load();

            // Get the GuiController
            GuiController guiController = loader.getController();

            // Set the game mode FIRST
            guiController.setGameMode(mode);

            // Create GameController which will initialize the game
            GameController gameController = new GameController(guiController);

            // Switch to game scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Tetris - " + mode.getDisplayName());
            stage.show();

            // Request focus for keyboard input
            root.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load game: " + e.getMessage());
        }
    }
}