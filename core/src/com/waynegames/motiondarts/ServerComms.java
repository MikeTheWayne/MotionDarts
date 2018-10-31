package com.waynegames.motiondarts;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

class ServerComms {

    private static final String SERVER_IP = "18.191.102.50";

    private static boolean inGame = false;

    private static Socket socket;
    private static BufferedReader bufferedReader;

    static float[] serverIn = new float[4];
    static int readsLeft = 4;

    static float[][] serverInBuffer = new float[2][4];
    static int backLog = 0;

    static boolean newThrow = false;

    static float oppStats = 0;
    private static boolean incomingStat = false;

    static boolean disconnection = false;
    static boolean outOfTime = false;

    static int turnTimer = 0;

    static void connectToServer() {

        SocketHints socketHints = new SocketHints();
        socketHints.connectTimeout = 10000;

        try {
            MenuScreen.connectionFailed = false;
            inGame = false;

            // Set up socket with server
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, SERVER_IP, 40000, socketHints);

            // Read from server
            new Thread(new Runnable() {
                @Override
                public void run() {
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    String serverOutput;

                    do {
                        try {
                            serverOutput = bufferedReader.readLine();
                            serverOutput = serverOutput.replaceAll("\\s+", "");

                            if(!inGame) {
                                if (serverOutput.equals("0")) {
                                    MenuScreen.connectionFailed = true;
                                    MenuScreen.connectionFailReason = 2;
                                } else if (serverOutput.equals("1")) {
                                    MenuScreen.showWaiting = true;
                                } else if (serverOutput.equals("2")) {
                                    // Start Game
                                    MenuScreen.onlinePlayer = Integer.parseInt(bufferedReader.readLine());
                                    MenuScreen.selectedGameMode = Integer.parseInt(bufferedReader.readLine());
                                    MenuScreen.opponentName = bufferedReader.readLine();
                                    GameScreen.opponentSelectedDart = Integer.parseInt(bufferedReader.readLine());
                                    GameScreen.selectedLocation = Integer.parseInt(bufferedReader.readLine());

                                    MenuScreen.selectedOpposition = 5;
                                    MenuScreen.startGame = true;
                                    inGame = true;
                                } else if (serverOutput.equals("10")) {
                                    MenuScreen.usernameAvailable = false;
                                    MenuScreen.usernameChecked = true;
                                } else if (serverOutput.equals("11")) {
                                    MenuScreen.usernameAvailable = true;
                                    MenuScreen.usernameChecked = true;
                                } else if (serverOutput.equals("20")) {
                                    MenuScreen.opponentAvailable = false;
                                    MenuScreen.opponentChecked = true;
                                    MenuScreen.showWaiting = false;
                                } else if (serverOutput.equals("21")) {
                                    MenuScreen.opponentAvailable = true;
                                    MenuScreen.opponentChecked = true;
                                }
                            } else{
                                if(!serverOutput.equals("0")) {
                                    if(!serverOutput.equals("STAT") && !incomingStat) {

                                        if (!newThrow) {
                                            serverIn[4 - readsLeft] = Float.valueOf(serverOutput);
                                            readsLeft--;
                                            if (readsLeft == 0) {
                                                readsLeft = 4;
                                                newThrow = true;
                                            }
                                        } else {
                                            serverInBuffer[backLog][4 - readsLeft] = Float.valueOf(serverOutput);
                                            readsLeft--;
                                            if (readsLeft == 0) {
                                                readsLeft = 4;
                                                backLog++;
                                            }
                                        }

                                        turnTimer = 20 + GameScreen.gameClass.scoreSystem.currentPlayer * 5;

                                    } else if(incomingStat) {
                                        oppStats = Float.valueOf(serverOutput);
                                        incomingStat = false;
                                    } else{
                                        incomingStat = true;
                                    }
                                } else{

                                    if(!GameScreen.endGame && (GameScreen.gameClass.scoreSystem.getScore()[0] > 0 && GameScreen.gameClass.scoreSystem.getScore()[0] > 0)) {
                                        GameScreen.gameClass.scoreSystem.winner = (2 - GameScreen.gameClass.scoreSystem.currentPlayer);
                                        disconnection = true;
                                    }

                                    GameScreen.endGame = true;
                                    inGame = false;
                                    newThrow = false;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            socket.dispose();
                            break;
                        }
                    } while(!serverOutput.equals("0"));

                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            MenuScreen.connectionFailed = true;
            MenuScreen.connectionFailReason = 1;
        }

    }

    static void serverTimer() {
        final Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(turnTimer > 0 && !GameScreen.endGame) {
                    turnTimer--;
                } else{
                    if(!GameScreen.endGame) {
                        GameScreen.gameClass.scoreSystem.winner = (2 - GameScreen.gameClass.scoreSystem.currentPlayer);
                        outOfTime = true;
                    }

                    GameScreen.endGame = true;
                    inGame = false;
                    newThrow = false;
                    disconnectFromServer();
                    t.cancel();
                }

                if(!inGame) {
                    t.cancel();
                }
            }
        }, 0, 1000);
    }

    static void sendToServer(String message) {
        try {
            socket.getOutputStream().write((message + "\n").getBytes());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static void disconnectFromServer() {
        try {
            socket.dispose();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
