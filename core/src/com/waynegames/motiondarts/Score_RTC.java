package com.waynegames.motiondarts;

public class Score_RTC extends ScoreSystem {

    private int[] target = {1, 1};

    @Override
    void handleScore(int dartLandZone) {
        super.handleScore(dartLandZone);

        if(dartNature[turn][currentPlayer][dartsThrown] <= 3 && dartScore[turn][currentPlayer][dartsThrown] / dartNature[turn][currentPlayer][dartsThrown] == target[currentPlayer]) {

            if(target[currentPlayer] == 20) {
                /* Game Win */
                // Set winner
                winner = currentPlayer + 1;

                overallScore[turn][0] = target[0];
                overallScore[turn][1] = target[1];

                // End Game
                GameScreen.endGame = true;

            } else{
                target[currentPlayer]++;
            }

        }
    }

    @Override
    void calculateStatistics() {
        super.calculateStatistics();

        // Accuracy percentage
        gameStatistics[currentPlayer][0] = (target[currentPlayer] - 1) / (float) totalDartsThrown * 100.0f;
        // Average darts taken to advance to the next number
        gameStatistics[currentPlayer][1] = (target[currentPlayer] - 1 > 0) ? (float) totalDartsThrown / (target[currentPlayer] - 1) : totalDartsThrown;

    }

    @Override
    int[] getScore() {
        return target;
    }
}
