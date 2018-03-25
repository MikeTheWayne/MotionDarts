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

    boolean aiTurn = false;
    boolean oppTurn = false;

    int startPlayer = 0;

    boolean gameStarted = false;
    private float distToBull = 0;

    String[] playerNames = {"PLAYER 1", "PLAYER 2"};

    ScoreSystem scoreSystem;
    ArtificialIntelligence ai;

    GameClass(int gameModeParam, int competitionTypeParam) {

        gameMode = gameModeParam;
        competitionType = competitionTypeParam;

        // Instantiate the score system, depending on the game mode
        switch (gameModeParam) {
            case 0:
                scoreSystem = new Score_Practice();
                break;
            case 1:
                scoreSystem = new Score_501();
                break;
            case 2:
                scoreSystem = new Score_RTC();
                break;
            case 3:
                scoreSystem = new Score_Cricket();
                break;
            case 4:
                scoreSystem = new Score_UKCricket();
                break;
        }

        switch(competitionTypeParam) {
            case 1:
                playerNames[1] = "EASY AI";
                ai = new ArtificialIntelligence(competitionTypeParam);
                break;
            case 2:
                playerNames[1] = "MEDIUM AI";
                ai = new ArtificialIntelligence(competitionTypeParam);
                break;
            case 3:
                playerNames[1] = "PRO AI";
                ai = new ArtificialIntelligence(competitionTypeParam);
                break;
            case 5:
                // Set up Multi-player
                break;
        }

        // Load in player's average scores
        readSaveData();

        // Send dart average to server
        if(competitionType == 5) {
            ServerComms.sendToServer(scoreSystem.personalStatistics[0] + "\n");
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
        scoreSystem.calculateStatistics();

        scoreSystem.dartsThrown++;

        if(scoreSystem.dartsThrown == 3 || scoreSystem.bust) {

            // Reset darts thrown
            scoreSystem.dartsThrown = 0;
            GameScreen.dartsReset = false;

            if(competitionType > 0) {
                // Advance turn (primarily for scoring)
                if (scoreSystem.turn < 99 && scoreSystem.currentPlayer != scoreSystem.firstPlayer) {
                    scoreSystem.overallScore[scoreSystem.turn][0] = scoreSystem.getScore()[0];
                    scoreSystem.overallScore[scoreSystem.turn][1] = scoreSystem.getScore()[1];
                    writeSaveData();
                    scoreSystem.turn++;
                } else if (scoreSystem.turn == 99 && scoreSystem.currentPlayer != scoreSystem.firstPlayer) {
                    scoreSystem.overallScore[scoreSystem.turn][0] = scoreSystem.getScore()[0];
                    scoreSystem.overallScore[scoreSystem.turn][1] = scoreSystem.getScore()[1];
                    GameScreen.endGame = true;
                }

                // Switch Player
                scoreSystem.currentPlayer = 1 - scoreSystem.currentPlayer;

                if(competitionType <= 3) {  // AI
                    aiTurn = !aiTurn;
                }
            }

        }

        if(competitionType == 5) {
            if(!oppTurn) {
                ServerComms.sendToServer("" + ((startPlayer == 1) ? 1 - scoreSystem.currentPlayer : scoreSystem.currentPlayer));
                ServerComms.sendToServer(scoreSystem.personalStatistics[0] + "\n");
            }

            if(scoreSystem.dartsThrown == 0 || scoreSystem.bust) {
                oppTurn = !oppTurn;
            }
        }

        scoreSystem.bust = false;
    }

    void firstThrow(float landX, float landY) {

        if(scoreSystem.currentPlayer == startPlayer) {
            // Calculate linear distance from bullseye
            distToBull = (float) Math.sqrt(landX * landX + landY * landY);

            scoreSystem.currentPlayer = 1 - scoreSystem.currentPlayer;

            if(competitionType <= 3) {  // AI
                aiTurn = true;
            } else if(competitionType == 5) {
                if(!oppTurn) {
                    ServerComms.sendToServer("" + scoreSystem.currentPlayer);
                    ServerComms.sendToServer(scoreSystem.personalStatistics[0] + "\n");
                }
                oppTurn = !oppTurn;
            }
        } else{
            // Calculate linear distance from bullseye, and compare to first player's landing
            scoreSystem.currentPlayer = (distToBull < (float) Math.sqrt(landX * landX + landY * landY)) ? startPlayer : 1 - startPlayer;
            scoreSystem.firstPlayer = scoreSystem.currentPlayer;

            if(competitionType == 5 && !oppTurn) {
                ServerComms.sendToServer("" + ((startPlayer == 1) ? 1 - scoreSystem.currentPlayer : scoreSystem.currentPlayer));
                ServerComms.sendToServer(scoreSystem.personalStatistics[0] + "\n");
            }

            if(scoreSystem.currentPlayer == 0) {
                aiTurn = false;
                oppTurn = false;
            } else if(competitionType == 5) {
                oppTurn = true;
            }

            gameStarted = true;
        }
    }

    int getGameMode() {
        return gameMode;
    }

    int getCompetitionType() { return competitionType; }


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
