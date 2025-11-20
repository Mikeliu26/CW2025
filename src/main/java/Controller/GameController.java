package Controller;

import Data.ClearRow;
import Data.DownData;
import Data.MoveEvent;
import Data.ViewData;
import Model.Board;
import Model.SimpleBoard;
import com.comp2042.*;
import Utilities.GameConstants;
import Model.HoldManager;
import com.comp2042.logic.bricks.Brick;
import java.util.List;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import Model.HighScoreManager;


/**
 * Main game controller
 * Handles game logic and user inputs
 * Act as middle person between GUI and game model
 */

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(GameConstants.BOARD_HEIGHT, GameConstants.BOARD_WIDTH);

    private final GuiController viewGuiController;

    private final HoldManager holdManager = new HoldManager();

    /**
     * Constructs a new GameController and initializes the game.
     * Sets up the board, GUI bindings, and initial game state.
     */

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        updateNextPiecesDisplay();
        int highScore = HighScoreManager.getInstance().getHighScore();
        viewGuiController.updateHighScoreDisplay(highScore);
    }

    /**
     * Handles downward movement of the current brick.
     * Processes line clearing and game over conditions.
     * @param event
     * @return
     */

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            holdManager.resetHoldLock();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                checkHighScore();
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            updateNextPiecesDisplay();

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles leftward motion of current brick
     * @param event
     * @return
     */

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles rightward motion of current block
     * @param event
     * @return
     */

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles rotation of current block
     * @param event
     * @return
     */

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Create a new game
     */

    @Override
    public void createNewGame() {
        board.newGame();
        holdManager.clear();
        viewGuiController.updateHoldDisplay(null);
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    /**
     * Gets the game board for ghost piece calculations.
     *
     * @return the current Board object
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Handles holding the current piece.
     * Swaps current piece with held piece, or stores it if first hold.
     */
    public void holdCurrentPiece() {
        if (!holdManager.canHold()) {
            return;  // Already held this piece
        }

        Brick currentBrick = board.getCurrentBrick();
        Brick swappedBrick = holdManager.holdBrick(currentBrick);

        if (swappedBrick == null) {
            // First hold - get next brick
            board.createNewBrick();
            updateNextPiecesDisplay();
        } else {
            // Swap with held brick
            board.setCurrentBrick(swappedBrick);
        }

        // Update hold display
        Brick heldBrick = holdManager.getHeldBrick();
        if (heldBrick != null) {
            viewGuiController.updateHoldDisplay(heldBrick.getShape());
        }

        // Refresh view
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.refreshBrick(board.getViewData());
    }
    /**
     * Performs a hard drop of the current piece.
     * Instantly drops the piece to the ghost position and locks it.
     */
    public void hardDropPiece() {
        int dropDistance = 0;

        // Count how far we drop
        while (board.moveBrickDown()) {
            dropDistance++;
        }

        // Award 2 points per row dropped
        if (dropDistance > 0) {
            board.getScore().add(dropDistance * 2);
        }

        // Piece has landed - process as normal down event
        board.mergeBrickToBackground();
        holdManager.resetHoldLock();  // Allow holding next piece

        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            checkHighScore();
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }

        updateNextPiecesDisplay();

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.refreshBrick(board.getViewData());
    }

    /**
     * Updates the next pieces preview display.
     */
    private void updateNextPiecesDisplay() {
        if (board.getBrickGenerator() instanceof RandomBrickGenerator) {
            RandomBrickGenerator generator = (RandomBrickGenerator) board.getBrickGenerator();
            List<Brick> nextBricks = generator.getNextBricks(5);
            viewGuiController.updateNextPanels(nextBricks);
        }
    }

    /**
     * Checks if current score beats high score.
     * Shows notification if new high score achieved.
     */
    private void checkHighScore() {
        int currentScore = board.getScore().scoreProperty().get();
        boolean isNewHighScore = HighScoreManager.getInstance().checkAndUpdateHighScore(currentScore);

        if (isNewHighScore) {
            viewGuiController.showNewHighScoreNotification();
            viewGuiController.updateHighScoreDisplay(currentScore);
        }
    }
}
