package com.waynegames.motiondarts;

import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.Random;

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

    int totalDartsThrown;

    int winner = 0;

    int[][][] dartScore = new int[100][2][3]; // [turn][player][throw] Integer value of each dart's score
    int[][][] dartNature = new int[100][2][3]; // [turn][player][throw] Nature of throw (miss, normal, double, triple, bull, etc.)

    int[][] overallScore = new int[100][2]; // [turn][player] End of turn overall score
    // vv this needs to be made local to score_501
    float[][] gameStatistics = new float[2][5]; // Avg dart, 1st dart avg, 2nd dart avg, 3rd dart avg, highest score
    float[] personalStatistics = new float[2]; //  player dart avg, player lifetime darts thrown

    private BoundingBox dartboardBox;

    ScoreSystem() {
        // Get dartboard measurements
        dartboardBox = new BoundingBox();
        GameScreen.dartboardModelInst1.calculateBoundingBox(dartboardBox);
    }

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

        // Convert values into coordinates
        int xCoord = (int) (-landX * 10000 * (1000 / dartboardBox.getDepth())) + 500;
        int yCoord = (int) (landY * 10000 * (1000 / dartboardBox.getHeight())) + 500;

        int zone;

        // If dart lands on the dartboard's bounding box then it will be within the array bounds, otherwise it's definitely missed
        try {
            zone = MotionDarts.hitZones[xCoord][yCoord];
        } catch (ArrayIndexOutOfBoundsException e) {
            zone = 0;
        }

        zone = (zone == 83) ? wireHit(xCoord, yCoord, 1) : zone;

        return zone;

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
            dartNature[turn][currentPlayer][dartsThrown] = 7;
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
            dartNature[turn][currentPlayer][dartsThrown] = (dartLandZone - 80) + 3;
        }


    }

    int[] getScore() {
        return new int[2];
    }

    /**
     * Returns the innings array from the Score_Cricket claas. To be overwritten by the Score_Cricket
     * class, and only used where the game mode is cricket. If the game mode is not cricket, then an
     * empty 2D INT-Array will be returned.
     *
     * @return Empty array, until overwritten with innings array
     */
    int[][] getInnings() {
        return new int[7][2];
    }

    int[][] getInningHits() { return new int[7][2];}

    int getPlayerBatting() {
        return 0;
    }

    int getWickets() {
        return 0;
    }

    int[] getTurnsBowling() { return new int[2]; }

    /**
     * If the dart hits a wire, this recursive method will find the nearest scoring area
     *
     * @param xPix X coordinate of dart landing
     * @param yPix Y coordinate of dart landing
     * @param searchRadius How far to search away from centre, increases with each recursion
     * @return Randomly selected nearby scoring zone
     */
    private int wireHit(int xPix, int yPix, int searchRadius) {

        int[] hitArray = new int[(searchRadius * 2 + 1) * (searchRadius * 2 + 1)];
        int hits = 0;

        for(int i = xPix - searchRadius; i < xPix + searchRadius; i++) {
            for(int j = yPix - searchRadius; j < yPix + searchRadius; j++) {
                if(MotionDarts.hitZones[i][j] != 83) {
                    hitArray[hits] = MotionDarts.hitZones[i][j];
                    hits++;
                }
            }
        }

        if(hits > 0) {
            return hitArray[new Random().nextInt(hits)];
        } else{
            return wireHit(xPix, yPix, searchRadius + 1);
        }
    }

    /**
     * Called every throw, calculates averages and other statistics
     */
    void calculateStatistics() {
        totalDartsThrown = turn * 3 + dartsThrown + 1;
    }

}
