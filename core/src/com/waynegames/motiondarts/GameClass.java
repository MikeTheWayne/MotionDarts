package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Sets up and coordinates a game, including score, AI and networking, features all core variables
 *
 * @author Michael Wayne
 * @version v0.5.0
 */
public class GameClass {

    private int gameMode;
    private int competitionType;         // 0 = Practice, 1 - 3 = AI difficulty (easy to hard), 4 = Pass and play, 5 = Networked Multiplayer

    String[] playerNames = {"PLAYER 1", "PLAYER 2"};

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

        // Load in player's average scores
        readSaveData();

    }

    /**
     * Handles a new dart throw, including scoring
     *
     * @param landX The X coordinate of the dart landing
     * @param landY The Y coordinate of the dart landing
     */
    void newThrow(float landX, float landY) {

        scoreSystem.handleScore(scoreSystem.landZone(landX, landY));
        scoreSystem.calculateStatistics();

        scoreSystem.dartsThrown++;

        if(scoreSystem.dartsThrown == 3) {

            // Reset darts thrown
            scoreSystem.dartsThrown = 0;
            GameScreen.dartsReset = false;

            if(competitionType > 0) {
                // Advance turn (primarily for scoring)
                if (scoreSystem.turn < 99 && scoreSystem.currentPlayer == 1) {
                    scoreSystem.overallScore[scoreSystem.turn][0] = scoreSystem.getScore()[0];
                    scoreSystem.overallScore[scoreSystem.turn][1] = scoreSystem.getScore()[1];
                    writeSaveData();
                    scoreSystem.turn++;
                } else if (scoreSystem.turn == 99 && scoreSystem.currentPlayer == 1) {
                    scoreSystem.overallScore[scoreSystem.turn][0] = scoreSystem.getScore()[0];
                    scoreSystem.overallScore[scoreSystem.turn][1] = scoreSystem.getScore()[1];
                    GameScreen.endGame = true;
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

    int getGameMode() {
        return gameMode;
    }


    /**
     * Loads in save data, if it exists
     */
    private void readSaveData() {

        FileHandle saveFile = Gdx.files.local("savefile.txt");
        String[] dataIn;

        if(saveFile.exists()) {
            dataIn = saveFile.readString().split("\n");
            for(int i = 0; i < dataIn.length; i++) {
                if(!dataIn[i].equals("")) {
                    scoreSystem.personalStatistics[i] = Float.valueOf(dataIn[i]);
                }
            }
        }
    }

    /**
     * Writes save data to file
     */
    void writeSaveData() {

        FileHandle saveFile = Gdx.files.local("savefile.txt");

        // Clear text file
        saveFile.writeString("", false);

        for(int i = 0; i < scoreSystem.personalStatistics.length; i++) {
            saveFile.writeString(String.valueOf(scoreSystem.personalStatistics[i]) + "\n", true);
        }
    }
}
