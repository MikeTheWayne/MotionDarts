package com.waynegames.motiondarts;

public class Score_Cricket extends ScoreSystem {

    private int[][] innings = new int[2][7];    // 20, 19, 18, 17, 16, 15, Bull
    private int[][] inningHits = new int[2][7];
    private int[] score = new int[2];

    private int[] accurateHits = new int[2];

    @Override
    void handleScore(int dartLandZone) {
        super.handleScore(dartLandZone);

        /* Work out score */
        int targetHit = 0;

        if(dartNature[turn][currentPlayer][dartsThrown] == 4 || dartNature[turn][currentPlayer][dartsThrown] == 5) {
            // Bull or Outer Bull
            targetHit = 7;
            innings[currentPlayer][6] += dartNature[turn][currentPlayer][dartsThrown] - 3;
            accurateHits[currentPlayer]++;

        } else if(dartNature[turn][currentPlayer][dartsThrown] <= 3 && dartScore[turn][currentPlayer][dartsThrown] / dartNature[turn][currentPlayer][dartsThrown] >= 15) {
            // Regular Numbers
            targetHit = 21 - dartScore[turn][currentPlayer][dartsThrown] / dartNature[turn][currentPlayer][dartsThrown];
            innings[currentPlayer][targetHit - 1] += dartNature[turn][currentPlayer][dartsThrown];
            accurateHits[currentPlayer]++;

        }

        if (targetHit > 0 && innings[currentPlayer][targetHit - 1] >= 3 && innings[1 - currentPlayer][targetHit - 1] < 3) {

            int[] tempScores = {20, 19, 18, 17, 16, 15, 25};
            score[currentPlayer] += tempScores[targetHit - 1] * (innings[currentPlayer][targetHit - 1] - 3);
            inningHits[currentPlayer][targetHit - 1] += innings[currentPlayer][targetHit - 1] - 3;
            innings[currentPlayer][targetHit - 1] = 3;

        }

        /* Check for victory */
        int victoryVar = 0;
        for(int i = 0; i < 7; i++) {
            if(innings[currentPlayer][i] >= 3) {
                victoryVar++;
            }
        }

        if(victoryVar == 7) {
            // Set winner
            if(score[1 - currentPlayer] > score[currentPlayer]) {
                winner = (1 - currentPlayer) + 1;
            } else {
                winner = currentPlayer + 1;
            }

            overallScore[turn][0] = score[0];
            overallScore[turn][1] = score[1];

            // End Game
            GameScreen.endGame = true;
        }

    }

    @Override
    void calculateStatistics() {
        super.calculateStatistics();

        gameStatistics[currentPlayer][0] = accurateHits[currentPlayer] / (float) totalDartsThrown * 100.0f;
        gameStatistics[currentPlayer][1] = (gameStatistics[currentPlayer][1] * (totalDartsThrown - 1) + dartScore[turn][currentPlayer][dartsThrown]) / totalDartsThrown;
    }

    @Override
    public int[] getScore() {
        return score;
    }

    @Override
    public int[][] getInnings() {
        return innings;
    }

    @Override
    public int[][] getInningHits() {
        return inningHits;
    }
}
