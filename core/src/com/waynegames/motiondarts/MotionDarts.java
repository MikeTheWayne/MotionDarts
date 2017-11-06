package com.waynegames.motiondarts;

import com.badlogic.gdx.Game;

/**
 * The base LibGdx class, begins as soon as the application is opened.
 *
 * @author Michael Wayne
 * @version v0.1.0
 */
public class MotionDarts extends Game {

    /**
     * LibGdx default class, called when application is opened.
     * Immediately sets the screen to MenuScreen.
     */
    @Override
    public void create () {
        setScreen(new GameScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }

}
