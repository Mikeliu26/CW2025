package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();
    private static final int PREVIEW_COUNT = 5; // Number of pieces to show

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());

        // Fill queue with initial pieces
        for (int i = 0; i < PREVIEW_COUNT; i++) {
            nextBricks.add(getRandomBrick());
        }
    }

    private Brick getRandomBrick() {
        return brickList.get(ThreadLocalRandom.current().nextInt(brickList.size()));
    }

    @Override
    public Brick getBrick() {
        // Add new piece to queue when one is taken
        nextBricks.add(getRandomBrick());
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    /**
     * Gets all upcoming pieces for preview display.
     *
     * @param count number of pieces to preview (2-5)
     * @return list of upcoming bricks
     */
    public List<Brick> getNextBricks(int count) {
        List<Brick> preview = new ArrayList<>();
        int previewCount = Math.min(count, nextBricks.size());

        int i = 0;
        for (Brick brick : nextBricks) {
            if (i >= previewCount) break;
            preview.add(brick);
            i++;
        }

        return preview;
    }
}