package com.waynegames.motiondarts;

public class Score_UKCricket extends ScoreSystem {

    private int playerBatting = 0;

    private int scoreThisTurn = 0;

    private int wickets = 10;
    private int[] score = new int[2];
    private int[] turnsBowling = new int[2];

    private int wicketAccuracy = 0;
    private int[] prevScore = new int[2];

    @Override
    void handleScore(int dartLandZone) {
        super.handleScore(dartLandZone);

        // Scoring
        if(currentPlayer == playerBatting) {
            // Batting
            scoreThisTurn += dartScore[turn][currentPlayer][dartsThrown];
            if(dartsThrown == 2) {
                if(scoreThisTurn > 40) {
                    score[currentPlayer] += scoreThisTurn - 40;
                }
                scoreThisTurn = 0;
            }
        } else{
            // Bowling
            if(dartScore[turn][currentPlayer][dartsThrown] == 25 || dartScore[turn][currentPlayer][dartsThrown] == 50) {
                wickets -= dartScore[turn][currentPlayer][dartsThrown] / 25;
                wicketAccuracy++;
            }

            if(dartsThrown == 2 || wickets <= 0) {
                turnsBowling[currentPlayer]++;
            }
        }

        // Check for end game / player role switch
        if(wickets <= 0) {
            if(playerBatting == 0) {
                playerBatting = 1;
                turn++;
                dartsThrown = -1;
                wickets = 10;
            } else{
                // End game
                if(score[1 - currentPlayer] > score[currentPlayer]) {
                    winner = (1 - currentPlayer) + 1;
                } else {
                    winner = currentPlayer + 1;
                }

                // End Game
                GameScreen.endGame = true;
            }
        }

    }

    @Override
    void calculateStatistics() {
        super.calculateStatistics();

        if(currentPlayer == playerBatting) {
            if(dartsThrown == 2) {
                gameStatistics[currentPlayer][1] = (gameStatistics[currentPlayer][1] * (turn) + (score[currentPlayer] - prevScore[currentPlayer])) / ((float) turn + 1);
                prevScore[currentPlayer] = score[currentPlayer];
            }
        } else {
            gameStatistics[currentPlayer][0] = wicketAccuracy / (float) totalDartsThrown * 100;
        }
    }

    @Override
    public int[] getScore() {
        return score;
    }

    @Override
    public int getPlayerBatting() {
        return playerBatting;
    }

    @Override
    public int getWickets() {
        return wickets;
    }

    @Override
    public int[] getTurnsBowling() {
        return turnsBowling;
    }
}
