package Model;

import Data.ClearRow;
import Data.ViewData;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;


public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    /**
     * Gets the current falling brick.
     *
     * @return the current Brick
     */
    Brick getCurrentBrick();

    /**
     * Sets a new current brick (for hold piece feature).
     *
     * @param brick the brick to set as current
     */
    void setCurrentBrick(Brick brick);

    /**
     * Gets the brick generator.
     *
     * @return the BrickGenerator instance
     */
    BrickGenerator getBrickGenerator();
}
