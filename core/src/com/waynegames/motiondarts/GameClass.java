package com.waynegames.motiondarts;

/**
 * Sets up and coordinates a game, including score, AI and networking, features all core variables
 *
 * @author Michael Wayne
 * @version v0.5.0
 */
public class GameClass {

    private int gameMode;
    private int competitionType;         // 0 = Practice, 1 - 3 = AI difficulty (easy to hard), 4 = Pass and play, 5 = Networked Multiplayer

    ScoreSystem scoreSystem;

    GameClass(int gameModeParam, int competitionTypeParam) {

        gameMode = gameModeParam;
        competitionType = competitionTypeParam;

        // Instantiate the score system, depending on the game mode
        switch (gameModeParam) {
            case 1:
                scoreSystem = new Score_501();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }

    }

    /**
     * Handles a new dart throw, including scoring
     *
     * @param landX The X coordinate of the dart landing
     * @param landY The Y coordinate of the dart landing
     */
    void newThrow(float landX, float landY) {

        scoreSystem.handleScore(scoreSystem.landZone(landX, landY));

        scoreSystem.dartsThrown++;

        if(scoreSystem.dartsThrown == 3) {

            // Reset darts thrown
            scoreSystem.dartsThrown = 0;

            if(competitionType > 0) {
                // Advance turn (primarily for scoring)
                if (scoreSystem.turn < 100 && scoreSystem.currentPlayer == 1) {
                    scoreSystem.turn++;
                } else if (scoreSystem.turn == 100) {
                    scoreSystem.endGame();
                }

                // Switch Player
                if (scoreSystem.currentPlayer == 0) {
                    scoreSystem.currentPlayer = 1;
                } else {
                    scoreSystem.currentPlayer = 0;
                }
            }

        }
    }

}
