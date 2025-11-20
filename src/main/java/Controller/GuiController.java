package Controller;

import Utilities.ColorManager;
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
import Utilities.GhostPieceCalculator;
import java.awt.Point;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import com.comp2042.logic.bricks.Brick;

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
    private GridPane ghostPanel;

    @FXML
    private GridPane holdPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Button pauseButton;

    @FXML
    private GridPane nextPanel;

    @FXML
    private GridPane nextPanel1;

    @FXML
    private GridPane nextPanel2;

    @FXML
    private GridPane nextPanel3;

    @FXML
    private GridPane nextPanel4;

    @FXML
    private GridPane nextPanel5;

    @FXML
    private Label highScoreLabel;



    /**
     * Matrix of each rectangle representing game board display
     */
    private Rectangle[][] displayMatrix;
    /**
     * Listener for input events
     */
    private InputEventListener eventListener;
    /**
     * Rectangle representing current failing block
     */
    private Rectangle[][] rectangles;
    /**
     * To add ghost rectangle fields
     */
    private Rectangle[][] ghostRectangles;
    /**
     * To hold the ghost rectangle
     */
    private Rectangle[][] holdRectangles;
    /**
     * To show the preview of the next few blocks
     */
    private Rectangle[][][] nextPanelRectangles;
    /**
     * Timeline to control automatic brick failing
     */
    private Timeline timeLine;
    /**
     * Tracking pause state
     */
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    /**
     * Tracking game over state
     */
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    /**
     * Initializes the controller and sets up UI components.
     * Loads custom font, configures keyboard handlers, and prepares game view.
     * Called automatically by JavaFX when FXML is loaded.
     *
     * @param location  URL location of fxml
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
                    if (keyEvent.getCode() == KeyCode.H) {
                        holdPiece();
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        hardDrop();
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
        initHoldPanel();
        initNextPanels();

        final Reflection reflection = new Reflection();


        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    /**
     * Initializes the game view with board and brick displays.
     * Sets up the display matrix, brick preview, and game loop.
     *
     * @param boardMatrix the game board state matrix
     * @param brick       the initial brick view data
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
                rectangle.setFill(ColorManager.getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
        brickPanel.setLayoutY(GameConstants.VERTICAL_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);

        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setOpacity(0.3);  // Semi-transparent ghost
                ghostRectangles[i][j] = rectangle;
                ghostPanel.add(rectangle, j, i);
            }
        }

        updateGhostPosition(brick, boardMatrix);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(GameConstants.FALL_SPEED_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }


    public void refreshBrick(ViewData brick) {
        if (!isPause.getValue()) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * BRICK_SIZE);
            brickPanel.setLayoutY(GameConstants.VERTICAL_OFFSET + gamePanel.getLayoutY() + brick.getyPosition() * brickPanel.getHgap() + brick.getyPosition() * BRICK_SIZE);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            /** Update ghost block when moving */
            updateGhostPosition(brick, getCurrentBoard());
        }
    }

    /**
     * Updates the visual position and appearance of the current brick.
     * Only updates when game is not paused.
     *
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
     *
     * @param color     the color code for the rectangle
     * @param rectangle the rectangle to update
     */

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(ColorManager.getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    /**
     * Handles downward movement of the brick.
     * Processes movement, line clearing, and notifications.
     *
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
     *
     * @param eventListener the listener to handle input events
     */

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the score label to the game score property.
     * Automatically updates UI when score changes.
     *
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
     *
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
     *
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

    /**
     * Updates the ghost piece position and appearance.
     * Shows semi-transparent preview of where the brick will land.
     *
     * @param brick       current brick view data
     * @param boardMatrix current game board state
     */
    private void updateGhostPosition(ViewData brick, int[][] boardMatrix) {
        if (!isPause.getValue()) {
            // Calculate where brick will land
            Point ghostPos = GhostPieceCalculator.calculateGhostPosition(
                    boardMatrix,
                    brick.getBrickData(),
                    brick.getxPosition(),
                    brick.getyPosition()
            );

            // Position the ghost panel
            ghostPanel.setLayoutX(gamePanel.getLayoutX() + ghostPos.x * ghostPanel.getVgap() + ghostPos.x * BRICK_SIZE);
            ghostPanel.setLayoutY(GameConstants.VERTICAL_OFFSET + gamePanel.getLayoutY() + ghostPos.y * ghostPanel.getHgap() + ghostPos.y * BRICK_SIZE);

            // Update ghost rectangles to match current brick shape
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    if (brick.getBrickData()[i][j] != 0) {
                        ghostRectangles[i][j].setFill(ColorManager.getFillColor(brick.getBrickData()[i][j]));
                        ghostRectangles[i][j].setOpacity(0.3);  // 30% transparent
                        ghostRectangles[i][j].setArcHeight(9);
                        ghostRectangles[i][j].setArcWidth(9);
                    } else {
                        ghostRectangles[i][j].setFill(Color.TRANSPARENT);
                    }
                }
            }
        }
    }

    /**
     * Gets the current game board matrix.
     * Used for ghost piece calculation.
     *
     * @return the current board state
     */
    private int[][] getCurrentBoard() {
        if (eventListener != null && eventListener instanceof GameController) {
            return ((GameController) eventListener).getBoard().getBoardMatrix();
        }
        return new int[0][0];
    }

    /**
     * Initializes the hold piece display panel.
     * Creates a 4x4 grid for displaying the held brick.
     */
    public void initHoldPanel() {
        holdRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(GameConstants.BRICK_SIZE, GameConstants.BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setArcHeight(9);
                rectangle.setArcWidth(9);
                holdRectangles[i][j] = rectangle;
                holdPanel.add(rectangle, j, i);
            }
        }
    }

    /**
     * Initializes all next piece preview panels.
     * Creates a 4x4 grid for each of the 5 preview boxes.
     */
    public void initNextPanels() {
        GridPane[] panels = {nextPanel1, nextPanel2, nextPanel3, nextPanel4, nextPanel5};
        nextPanelRectangles = new Rectangle[5][4][4];

        int[] sizes = {16, 14, 14, 12, 12};  // Smaller size

        for (int panelIndex = 0; panelIndex < panels.length; panelIndex++) {
            int brickSize = sizes[panelIndex];

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(brickSize, brickSize);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.setArcHeight(6);
                    rectangle.setArcWidth(6);
                    nextPanelRectangles[panelIndex][i][j] = rectangle;
                    panels[panelIndex].add(rectangle, j, i);
                }
            }
        }
    }

    /**
     * Updates all next piece preview displays.
     *
     * @param nextBricks list of upcoming bricks to display
     */
    public void updateNextPanels(List<Brick> nextBricks) {
        for (int panelIndex = 0; panelIndex < Math.min(5, nextBricks.size()); panelIndex++) {
            Brick brick = nextBricks.get(panelIndex);
            int[][] brickData = brick.getShape();

            // Clear panel first
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    nextPanelRectangles[panelIndex][i][j].setFill(Color.TRANSPARENT);
                }
            }

            // Draw brick
            for (int i = 0; i < brickData.length; i++) {
                for (int j = 0; j < brickData[i].length; j++) {
                    if (brickData[i][j] != 0) {
                        nextPanelRectangles[panelIndex][i][j].setFill(
                                ColorManager.getFillColor(brickData[i][j])
                        );
                    }
                }
            }
        }
    }
    /**
     * Updates all next piece preview displays.
     *
     * @param nextBricks list of upcoming bricks to display
     */

    /**
     * Updates the hold panel display with the held brick.
     *
     * @param brickData the brick shape to display (null to clear)
     */
    public void updateHoldDisplay(int[][] brickData) {
        // Clear all rectangles first
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                holdRectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }

        // If there's a brick to display
        if (brickData != null) {
            for (int i = 0; i < brickData.length; i++) {
                for (int j = 0; j < brickData[i].length; j++) {
                    if (brickData[i][j] != 0) {
                        holdRectangles[i][j].setFill(ColorManager.getFillColor(brickData[i][j]));
                    }
                }
            }
        }
    }

    /**
     * Handles the hold piece action.
     * Requests the controller to swap current piece with held piece.
     */
    private void holdPiece() {
        if (eventListener instanceof GameController) {
            ((GameController) eventListener).holdCurrentPiece();
        }
    }

    /**
     * Performs a hard drop of the current piece.
     * Instantly drops the piece to the bottom (ghost position),
     * Via space bar.
     */
    private void hardDrop() {
        if (!isPause.getValue() && !isGameOver.getValue()) {
            if (eventListener instanceof GameController) {
                ((GameController) eventListener).hardDropPiece();
            }
        }
    }

    /**
     * Always update the new high score.
     *
     * @param highScore the high score value
     */
    public void updateHighScoreDisplay(int highScore) {
        highScoreLabel.setText(String.valueOf(highScore));
    }

    /**
     * Shows a "NEW HIGH SCORE!" notification.
     */
    public void showNewHighScoreNotification() {
        NotificationPanel notification = new NotificationPanel("NEW HIGH SCORE!");
        groupNotification.getChildren().add(notification);
        notification.showScore(groupNotification.getChildren());
    }
}