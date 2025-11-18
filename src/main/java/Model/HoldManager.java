package Model;

import com.comp2042.logic.bricks.Brick;

/**
 * Manages the hold piece functionality.
 * Allows players to store one piece for later use.
 */
public class HoldManager {

    private Brick heldBrick = null;
    private boolean canHold = true;

    /**
     * Attempts to hold or swap the current brick.
     * Only can hold or swap one block per new block.
     *
     * @param currentBrick the brick to hold
     * @return the brick to use next (null if first hold, held brick if swapping)
     */
    public Brick holdBrick(Brick currentBrick) {
        if (!canHold) {
            return null;  // Can't hold right now
        }

        Brick brickToReturn = heldBrick;
        heldBrick = currentBrick;
        canHold = false;  // Lock holding until piece lands

        return brickToReturn;  // null on first hold, previous held brick on swap
    }

    /**
     * Resets the hold lock when a piece lands.
     * Allows holding again for the next piece.
     */
    public void resetHoldLock() {
        canHold = true;
    }

    /**
     * Gets the currently held brick.
     *
     * @return the held brick, or null if none
     */
    public Brick getHeldBrick() {
        return heldBrick;
    }

    /**
     * Checks if holding is currently allowed.
     *
     * @return true if can hold, false if already held this piece
     */
    public boolean canHold() {
        return canHold;
    }

    /**
     * Clears the held piece (for new game).
     */
    public void clear() {
        heldBrick = null;
        canHold = true;
    }
}