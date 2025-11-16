package Controller;

import Data.ClearRow;
import Data.DownData;
import Data.MoveEvent;
import Data.ViewData;
import Model.Board;
import Model.SimpleBoard;
import com.comp2042.*;
import Utilities.GameConstants;

/**
 * Main game controller
 * Handles game logic and user inputs
 * Act as middle person between GUI and game model
 */

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(GameConstants.BOARD_HEIGHT, GameConstants.BOARD_WIDTH);

    private final GuiController viewGuiController;
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
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

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
}
