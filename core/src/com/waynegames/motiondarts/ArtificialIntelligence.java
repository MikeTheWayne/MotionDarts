package com.waynegames.motiondarts;

import java.util.Random;

public class ArtificialIntelligence {

    private int hitRadius;

    int targetX;
    int targetY;

    float landingPosX;
    float landingPosY;

    boolean throwing = false;

    private Random random = new Random();

    // Hard coded targets; 1 - 20: Singles, 21 - 40: Doubles, 41 - 60: Triples, 61: Outer Bull, 62: Bull
    final int[][] TARGETS =
            {
                    {590, 225}, {675, 730}, {500, 785}, {735, 330}, {410, 230}, {790, 500}, {330, 735}, {225, 590}, {265, 330}, {775, 585},
                    {210, 500}, {330, 270}, {775, 410}, {215, 410}, {740, 665}, {265, 675}, {590, 770}, {670, 265}, {410, 770}, {500, 210},
                    {610, 160}, {715, 785}, {500, 857}, {790, 290}, {387, 160}, {858, 500}, {290, 790}, {160, 610}, {160, 390}, {842, 605},
                    {142, 500}, {290, 210}, {840, 390}, {160, 390}, {705, 795}, {215, 715}, {615, 840}, {615, 840}, {710, 210}, {392, 842},
                    {565, 297}, {630, 670}, {500, 713}, {673, 375}, {435, 298}, {714, 500}, {375, 673}, {298, 565}, {328, 375}, {704, 564},
                    {287, 500}, {375, 328}, {703, 433}, {297, 435}, {675, 624}, {330, 628}, {568, 702}, {625, 327}, {435, 702}, {500, 287},
                    {500, 470}, {500, 500}
            };

    ArtificialIntelligence(int difficulty) {
        switch (difficulty) {
            case 1:     // Easy
                hitRadius = 150;
                break;
            case 2:     // Medium
                hitRadius = 65;
                break;
            case 3:     // Pro
                hitRadius = 35;
                break;
        }

    }

    void aiThrow() {

        int randX = random.nextInt(hitRadius * 2);
        int randY = random.nextInt(hitRadius * 2);

        landingPosX = -((targetX + randX - hitRadius) - 500) * 4.6f / 10000.0f;
        landingPosY = -((targetY + randY - hitRadius) - 500) * 4.6f / 10000.0f;

    }

    int targeting_501(int score) {

        if(score == 50) {
            // Aim for Bull
            return 61;
        } else if(score > 60){
            // Aim for T20 or T19
            return (random.nextInt(5) == 0) ? 58 : 59;
        } else if(score > 40) {
            return 19;
        } else {
            if(score % 2 == 0) {
                // Aim for Double
                return 19 + score / 2;
            } else{
                // Aim for 1, make even
                return 0;
            }
        }

    }

    int targeting_RTC(int target) {
        return target - 1;
    }

    int targeting_Cricket(int[] score, int[][] innings) {

        int action;

        // Determine what the ai should do
        if(score[1] > score[0] + 100) {
            // Favour opening more unopened openings
            action = 1;
        } else if(score[1] > score[0] + 50) {
            // Favour openings and scoring points equally, small favour towards closing opponent's
            action = (random.nextInt(5) < 2) ? 1 : (random.nextInt(3) < 2) ? 2 : 3;
        } else{
            // Favour closing opponent's and scoring points

            int[] highestInning = {0, 0};

            for(int i = 0; i < 2; i++) {
                for (int j = 0; j < innings[i].length; j++) {
                    if (innings[i][j] >= 3) {
                        highestInning[i] = j;
                        break;
                    }
                }
            }

            if(highestInning[1] < highestInning[0]) {
                action = (random.nextInt(7) < 5) ? 2 : 3;
            } else{
                action = (random.nextInt(7) < 4) ? 3 : 2;
            }

        }

        int target = 0;
        boolean targetLocated = false;

        switch (action) {
            case 1:
                target = findNewInning(target, innings);
                targetLocated = true;
                break;
            case 2:
                // Target the highest scoring opened inning
                for(int i = 0; i < 7; i++) {
                    if(innings[1][i] >= 3 && innings[0][i] < 3) {
                        target = i;
                        targetLocated = true;
                        break;
                    }
                }

                break;
            case 3:
                // Target the highest scoring opened opponent inning
                for(int i = 0; i < 7; i++) {
                    if(innings[0][i] >= 3 && innings[1][i] < 3) {
                        target = i;
                        targetLocated = true;
                        break;
                    }
                }

                break;
        }

        if(!targetLocated) {
            target = findNewInning(target, innings);
        }

        int[] tempTargets = {59, 58, 57, 56, 55, 54, 61};

        return tempTargets[target];
    }

    int targeting_UKCricket(boolean batting) {

        if(batting) {
            // Maximum Score Possible
            return (random.nextInt(5) == 0) ? 58 : 59;
        } else{
            // Bullseye
            return 61;
        }

    }

    private int findNewInning(int target, int[][] innings) {
        // Target the highest unopened inning, with the least hits by the other player
        for(int i = 0; i < 7; i++) {
            // Algorithm assigns a score based on how close each player is to getting the inning
            // boolean viableTarget = 2 - innings[0][i] + innings[1][i] > 2 - innings[0][i] + innings[1][target];

            if(innings[1][i] < 3 && innings[0][i] < 3) {
                target = i;
                break;
            }
        }

        return target;
    }
}
