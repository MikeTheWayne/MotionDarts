package com.waynegames.motiondarts;

public class Score_Practice extends ScoreSystem{

    @Override
    void handleScore(int dartLandZone) {

        if(dartsThrown == 0) {
            dartScore[0][0][0] = 0;
            dartScore[0][0][1] = 0;
            dartScore[0][0][2] = 0;
        }

        super.handleScore(dartLandZone);

    }
}
