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
            /* Game Win */
            // Set score to 0
            score[currentPlayer] = 0;
            // Set winner
            winner = currentPlayer + 1;

            // Final scores
            overallScore[turn][0] = score[0];
            overallScore[turn][1] = score[1];

            // End Game
            GameScreen.endGame = true;

        } else if(scoreSubbed <= 0) {
            // Bust
            dartNature[turn][currentPlayer][dartsThrown] = 6;
        } else{
            // Normal Scoring
            score[currentPlayer] = scoreSubbed;
        }

    }

    @Override
    void calculateStatistics() {
        super.calculateStatistics();

        // Dart Average
        gameStatistics[currentPlayer][0] = (gameStatistics[currentPlayer][0] * (totalDartsThrown - 1) + dartScore[turn][currentPlayer][dartsThrown]) / totalDartsThrown;
        // 1st, 2nd, & 3rd dart average
        gameStatistics[currentPlayer][dartsThrown + 1] = (gameStatistics[currentPlayer][dartsThrown + 1] * turn + dartScore[turn][currentPlayer][dartsThrown]) / (turn + 1);
        // Highest score
        if(turn > 0) {
            gameStatistics[currentPlayer][4] = Math.max(dartScore[turn][currentPlayer][0] + dartScore[turn][currentPlayer][1] + dartScore[turn][currentPlayer][2], gameStatistics[currentPlayer][4]);
        }

        // Player Dart Average
        if(currentPlayer == 0) {
            personalStatistics[0] = (personalStatistics[0] * personalStatistics[1] + dartScore[turn][0][dartsThrown]) / ++personalStatistics[1];
        }
    }

    @Override
    int[] getScore() {
        return score;
    }
}
