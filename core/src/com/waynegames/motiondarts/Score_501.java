package com.waynegames.motiondarts;

/**
 * Inherits the methods from the ScoreSystem, adapts them for the 501 game mode of darts.
 *
 * @author Michael Wayne
 * @version v0.5.0
 */
public class Score_501 extends ScoreSystem {

    private int[] score = {501, 501};

    @Override
    void handleScore(int dartLandZone) {
        super.handleScore(dartLandZone);

        int scoreSubbed = score[currentPlayer] - dartScore[turn][currentPlayer][dartsThrown];

        if(dartNature[turn][currentPlayer][dartsThrown] == 2 && scoreSubbed == 0) {
            // Victory
            score[currentPlayer] = 0;
            endGame();
        } else if(scoreSubbed <= 0) {
            // Bust
            dartNature[turn][currentPlayer][dartsThrown] = 6;
        } else{
            score[currentPlayer] = scoreSubbed;
        }
    }

    @Override
    int[] getScore() {
        return score;
    }
}
