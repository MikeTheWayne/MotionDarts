package com.waynegames.motiondarts;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * The base LibGdx class, begins as soon as the application is opened.
 *
 * @author Michael Wayne
 * @version v0.1.0
 */
public class MotionDarts extends Game {

    static int[][] hitZones = new int[1000][1000];

    static AssetManager assetManager;

    /**
     * LibGdx default class, called when application is opened.
     * Immediately sets the screen to MenuScreen.
     */
    @Override
    public void create () {
        // Loading
        hitZones = loadHitZones();
        loadAssets();

        // Start game
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void dispose() {assetManager.dispose();}


    /**
     * Loads in and analyses each pixel in the hitzones image, converting the color of the pixel
     * into an integer between 0 and 82, each representing a different zone of the dartboard.
     *
     * @return integer array
     */
    private int[][] loadHitZones() {

        int[][] hitZonesOut = new int[1000][1000];

        // Import hitzones image for analysis
        Pixmap hitzonesImage = new Pixmap(Gdx.files.internal("hitzones.png"));

        // Go through the image, analysing each pixel
        for(int i = 0; i < 1000; i++) {
            for(int j = 0; j < 1000; j++) {

                Color pixelColor = new Color(hitzonesImage.getPixel(i, j));

                // Colour of each pixel
                int r = (int) (pixelColor.r * 255);
                int g = (int) (pixelColor.g * 255);
                int b = (int) (pixelColor.b * 255);

                if(r == 255) {
                    // Miss
                    hitZonesOut[i][j] = 0;
                } else if(r == 128 && g == 128 && b == 128) {
                    // Wire hit
                    hitZonesOut[i][j] = 83;
                } else if(r == g && g == b) {
                    // Normal scoring area: Zones 1 - 40
                    hitZonesOut[i][j] = 40 - (r - 55) / 5;
                } else if(r == 0) {
                    // Triples: Zones 41 - 60
                    hitZonesOut[i][j] = 60 - (g - 155) / 5;
                } else if(g == 0) {
                    // Doubles: Zones 61 - 80
                    hitZonesOut[i][j] = 80 - (b - 155) / 5;
                } else if(b == 0) {
                    // Outer bull / bull: Zones 81 - 82
                    hitZonesOut[i][j] = 82 - (r - 200) / 50;
                }

            }
        }

        return hitZonesOut;
    }

    /**
     * Handles all of the loading for models and textures, any assets.
     */
    private void loadAssets() {

        // Load assets from assets folder
        assetManager = new AssetManager();

        // Models
        assetManager.load("dart_01.g3db", Model.class);
        assetManager.load("dartboard_01.g3db", Model.class);
        assetManager.load("environment_01.g3db", Model.class);
        assetManager.load("menu_spacesurround.g3db", Model.class);

        // Textures
        assetManager.load("defaultButton.png", Texture.class);
        assetManager.load("settingsButton.png", Texture.class);
        assetManager.load("languageButton.png", Texture.class);
        assetManager.load("backButton.png", Texture.class);
        assetManager.load("exitButton.png", Texture.class);
        assetManager.load("title.png", Texture.class);
        assetManager.load("submenu_background.png", Texture.class);
        assetManager.load("tickButton.png", Texture.class);
        assetManager.load("connectButton.png", Texture.class);
        assetManager.load("helpButton.png", Texture.class);
        assetManager.load("selectedLanguage.png", Texture.class);
        assetManager.load("selectedButton.png", Texture.class);
        assetManager.load("selectedSmallButton.png", Texture.class);

        assetManager.load("flag1.png", Texture.class);
        assetManager.load("flag2.png", Texture.class);
        assetManager.load("flag3.png", Texture.class);
        assetManager.load("flag4.png", Texture.class);
        assetManager.load("flag5.png", Texture.class);
        assetManager.load("flag6.png", Texture.class);
        assetManager.load("flag7.png", Texture.class);
        assetManager.load("flag8.png", Texture.class);
        assetManager.load("flag9.png", Texture.class);
        assetManager.load("flag10.png", Texture.class);

        assetManager.finishLoading();

    }

}
