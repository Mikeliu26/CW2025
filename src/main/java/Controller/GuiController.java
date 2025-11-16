package Controller;


import Utilities.ColourManager;
import Data.DownData;
import Data.MoveEvent;
import Data.ViewData;
import View.GameOverPanel;
import View.NotificationPanel;
import com.comp2042.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import Utilities.GameConstants;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX controller managing user interface for Tetris
 * Handles rendering, user input, animations, and visual updates.
 * Implimented for FXML
 */

public class GuiController implements Initializable {
    /**
     * Size of each brick in pixels
     */

    private static final int BRICK_SIZE = GameConstants.BRICK_SIZE;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button pauseButton;

    /** Matrix of each rectangle representing game board display */
    private Rectangle[][] displayMatrix;
    /** Listener for input events*/
    private InputEventListener eventListener;
    /** Rectangle representing current failing block */
    private Rectangle[][] rectangles;
    /** Timeline to control automatic brick failing */
    private Timeline timeLine;
    /** Tracking pause state */
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    /** Tracking game over state */
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    /**
     * Initializes the controller and sets up UI components.
     * Loads custom font, configures keyboard handlers, and prepares game view.
     * Called automatically by JavaFX when FXML is loaded.
     * @param location URL location of fxml
     * @param resources Resource bundle
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ADD VALIDATION HERE:
        URL fontUrl = getClass().getClassLoader().getResource("digital.ttf");
        if (fontUrl != null) {
            Font.loadFont(fontUrl.toExternalForm(), 38);
        } else {
            System.err.println("Warning: digital.ttf font not found! Using default font.");
        }
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!isPause.getValue() && !isGameOver.getValue()) {  // ✅ CHANGED THIS LINE
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                }

                // Pause key - works even when paused
                if (keyEvent.getCode() == KeyCode.P) {
                    pauseGame(null);
                    keyEvent.consume();
                }

                // New game key
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                    keyEvent.consume();
                }
            }
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    /**
     * Initializes the game view with board and brick displays.
     * Sets up the display matrix, brick preview, and game loop.
     * @param boardMatrix the game board state matrix
     * @param brick the initial brick view data
     */

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(ColourManager.getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(GameConstants.VERTICAL_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);


        timeLine = new Timeline(new KeyFrame(
                Duration.millis(GameConstants.FALL_SPEED_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }



    private void refreshBrick(ViewData brick) {
        if (!isPause.getValue()) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(GameConstants.VERTICAL_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }
    }

    /**
     * Updates the visual position and appearance of the current brick.
     * Only updates when game is not paused.
     * @param board current matrix board
     */

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Sets the visual properties of a rectangle based on color code.
     * @param color the color code for the rectangle
     * @param rectangle the rectangle to update
     */

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(ColourManager.getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    /**
     * Handles downward movement of the brick.
     * Processes movement, line clearing, and notifications.
     * @param event the move event containing source information
     */

    private void moveDown(MoveEvent event) {
        if (!isPause.getValue()) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    /**
     * Sets the input event listener for game interactions.
     * @param eventListener the listener to handle input events
     */

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the score label to the game score property.
     * Automatically updates UI when score changes.
     * @param integerProperty the score property to bind
     */

    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString("Score: %d"));
    }

    /**
     * Displays the game over screen and stops the game loop.
     * Called when the game board is full.
     */

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    /**
     * Start new game by resetting all the blocks.
     * Hide game over panel and restarting the loop.
     * @param actionEvent the button click event (can be null)
     */

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        pauseButton.setText("Pause");
    }

    /**
     * Pauses game.
     * DUring pausing the button displays resume.
     * @param actionEvent the button click event (can be null)
     */

    public void pauseGame(ActionEvent actionEvent) {
        if (!isPause.getValue()) {
            timeLine.pause();
            isPause.setValue(Boolean.TRUE);
            pauseButton.setText("Resume");
        } else {
            timeLine.play();
            isPause.setValue(Boolean.FALSE);
            pauseButton.setText("Pause");
        }
        gamePanel.requestFocus();
    }
}