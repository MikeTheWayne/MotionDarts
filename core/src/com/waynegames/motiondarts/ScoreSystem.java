package com.waynegames.motiondarts;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Parent class for the score classes, features all default score keeping variables and keeps track
 * of the turn, darts thrown per turn, and the current player.
 *
 * @author Michael Wayne
 * @version v0.5.0
 */
public class ScoreSystem {

    int turn = 0;
    int dartsThrown = 0;
    int currentPlayer = 0;

    int[][][] dartScore = new int[100][2][3]; // [turn][player][throw] Integer value of each dart's score
    int[][][] dartNature = new int[100][2][3]; // [turn][player][throw] Nature of throw (miss, normal, double, triple, bull, etc.)

    /**
     * Converts the landing position into coordinates on the dartboard, inputs coordinates into hitZones array,
     * then outputs the contents of the memory location of that array. Bottom left of the dartboard is (0, 0),
     * top right is (1000, 1000), and bullseye is (500, 500)
     *
     * @param landX Where the dart landed in the X plane
     * @param landY Where the dart landed in the Y plane
     * @return The section of the dartboard which was hit, numbered from 0 to 82.
     */
    int landZone(float landX, float landY) {

        // Get dartboard measurements
        BoundingBox dartboardBox = new BoundingBox();
        GameScreen.dartboardModelInst1.calculateBoundingBox(dartboardBox);

        // Convert values into coordinates
        int xCoord = (int) (-landX * 10000 * (1000 / dartboardBox.getDepth())) + 500;
        int yCoord = (int) (landY * 10000 * (1000 / dartboardBox.getHeight())) + 500;

        // If dart lands on the dartboard's bounding box then it will be within the array bounds, otherwise it's definitely missed
        try {
            return MotionDarts.hitZones[xCoord][yCoord];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }

    }

    /**
     * Partly abstract method to be modified by subclasses, updates score arrays, then used by score
     * classes to update each individual score parameters
     *
     * @param dartLandZone The part of the dartboard where the dart landed
     */
    void handleScore(int dartLandZone) {

        // Calculate score of dart
        if(dartLandZone == 0) {
            // Miss
            dartScore[turn][currentPlayer][dartsThrown] = 0;
            dartNature[turn][currentPlayer][dartsThrown] = 0;
        } else if(dartLandZone > 0 && dartLandZone <= 40) {
            // Normal score, between 1 and 20
            dartScore[turn][currentPlayer][dartsThrown] = dartLandZone % 20;
            dartScore[turn][currentPlayer][dartsThrown] = (dartScore[turn][currentPlayer][dartsThrown] == 0) ? 20 : dartScore[turn][currentPlayer][dartsThrown];
            dartNature[turn][currentPlayer][dartsThrown] = 1;
        } else if(dartLandZone > 40 && dartLandZone <= 60) {
            // Triple
            dartScore[turn][currentPlayer][dartsThrown] = (dartLandZone - 40) * 3;
            dartNature[turn][currentPlayer][dartsThrown] = 3;
        } else if(dartLandZone > 60 && dartLandZone <= 80) {
            // Double
            dartScore[turn][currentPlayer][dartsThrown] = (dartLandZone - 60) * 2;
            dartNature[turn][currentPlayer][dartsThrown] = 2;
        } else{
            // Outer Bull, then Bull
            dartScore[turn][currentPlayer][dartsThrown] = (dartLandZone - 80) * 25;
            dartNature[turn][currentPlayer][dartsThrown] = (dartLandZone - 80);
        }


    }

    int[] getScore() {
        return new int[2];
    }

    /**
     * Ends the game, returns to the summary screen in the menus
     */
    void endGame() {
        // End of game
    }

}
