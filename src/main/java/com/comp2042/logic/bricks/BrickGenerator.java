package com.comp2042.logic.bricks;

import java.util.List;

public interface BrickGenerator {

    Brick getBrick();

    Brick getNextBrick();

    /**
     * Gets multiple upcoming pieces for preview.
     *
     * @param count number of pieces to preview
     * @return list of upcoming bricks
     */
    List<Brick> getNextBricks(int count);
}
