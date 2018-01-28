package com.waynegames.motiondarts;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

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
        loadLanguage(0);   // Later load in the previously selected language from file
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
        assetManager.load("models/dart_01.g3db", Model.class);
        assetManager.load("models/dartboard_01.g3db", Model.class);
        assetManager.load("models/environment_01.g3db", Model.class);
        assetManager.load("models/menu_spacesurround.g3db", Model.class);

        // Textures
        assetManager.load("textures/defaultButton.png", Texture.class);
        assetManager.load("textures/settingsButton.png", Texture.class);
        assetManager.load("textures/languageButton.png", Texture.class);
        assetManager.load("textures/backButton.png", Texture.class);
        assetManager.load("textures/exitButton.png", Texture.class);
        assetManager.load("textures/title.png", Texture.class);
        assetManager.load("textures/submenu_background.png", Texture.class);
        assetManager.load("textures/tickButton.png", Texture.class);
        assetManager.load("textures/connectButton.png", Texture.class);
        assetManager.load("textures/helpButton.png", Texture.class);
        assetManager.load("textures/selectedLanguage.png", Texture.class);
        assetManager.load("textures/selectedButton.png", Texture.class);
        assetManager.load("textures/selectedSmallButton.png", Texture.class);
        assetManager.load("textures/langImage.png", Texture.class);
        assetManager.load("textures/sliderBar.png", Texture.class);
        assetManager.load("textures/sliderBit.png", Texture.class);
        assetManager.load("textures/sliderBitSelected.png", Texture.class);
        assetManager.load("textures/leftButton.png", Texture.class);
        assetManager.load("textures/rightButton.png", Texture.class);
        assetManager.load("textures/menuPopup.png", Texture.class);
        assetManager.load("textures/upButton.png", Texture.class);
        assetManager.load("textures/downButton.png", Texture.class);

        assetManager.load("textures/flag1.png", Texture.class);
        assetManager.load("textures/flag2.png", Texture.class);
        assetManager.load("textures/flag3.png", Texture.class);
        assetManager.load("textures/flag4.png", Texture.class);
        assetManager.load("textures/flag5.png", Texture.class);
        assetManager.load("textures/flag6.png", Texture.class);
        assetManager.load("textures/flag7.png", Texture.class);
        assetManager.load("textures/flag8.png", Texture.class);
        assetManager.load("textures/flag9.png", Texture.class);
        assetManager.load("textures/flag10.png", Texture.class);

        assetManager.finishLoading();

    }

    /**
     * Loads text and position values from language files
     */
    static void loadLanguage(int language) {
        FileHandle[] langFiles = {Gdx.files.internal("languages/english.txt"), Gdx.files.internal("languages/bulgarian.txt"), Gdx.files.internal("languages/russian.txt")};
        FileHandle[] indentFiles = {Gdx.files.internal("text_indent_values/englishTextIndents.txt"), Gdx.files.internal("text_indent_values/bulgarianTextIndents.txt"), Gdx.files.internal("text_indent_values/russianTextIndents.txt")};

        MenuScreen.text = new String[200];
        MenuScreen.textIndent = new int[200];

        String languageInput = langFiles[language].readString();
        MenuScreen.text = languageInput.split("\n");

        String indentInput = indentFiles[language].readString();
        String[] indentStrArr = indentInput.split("\n");
        for(int i = 0; i < indentStrArr.length; i++) {
            MenuScreen.textIndent[i] = Integer.parseInt(indentStrArr[i].trim());
        }

    }

}
