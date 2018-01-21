package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Handles all of the touch input and graphics output for the menu interface.
 * Unused for now, will be used when development on menus begins
 *
 * @author Michael Wayne
 * @version v0.1.0
 */
public class MenuScreen extends ScreenAdapter {

    private ModelBatch modelBatch;
    private SpriteBatch spriteBatch;

    private PerspectiveCamera perspectiveCamera;
    private Environment environment;

    private ModelInstance spaceSurroundModelInst;

    private BitmapFont menuButtonFont;
    private BitmapFont menuButtonFontBold;
    private BitmapFont menuTitleFont;
    private BitmapFont menuDescriptionFont;
    private BitmapFont menuInputFont;
    private BitmapFont menuStatusFontRed;
    private BitmapFont menuStatusFontGreen;

    private FreeTypeFontGenerator freeTypeFontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter;
    private ShapeRenderer shapeRenderer;

    private Sprite defaultButton;
    private Sprite settingsButton;
    private Sprite languageButton;
    private Sprite exitButton;
    private Sprite title;
    private Sprite submenuBackground;
    private Sprite backButton;
    private Sprite tickButton;
    private Sprite connectButton;
    private Sprite helpButton;
    private Sprite flag1;
    private Sprite flag2;
    private Sprite flag3;
    private Sprite flag4;
    private Sprite flag5;
    private Sprite flag6;
    private Sprite flag7;
    private Sprite flag8;
    private Sprite flag9;
    private Sprite flag10;
    private Sprite selectedButton;
    private Sprite selectedButtonSmall;
    private Sprite selectedLanguage;
    private Sprite langImage;
    private Sprite sliderBar;
    private Sprite sliderBit;
    private Sprite sliderBitSelected;
    private Sprite leftButton;
    private Sprite rightButton;

    private static Array<ModelInstance> instances = new Array<ModelInstance>();

    private int screenWidth;
    private int screenHeight;

    private int language = 0;

    static String[] menuText;
    static int[] menuTextIndent;

    // 1 = Main Menu, 2 = Game Setup Screen, 3 = Multiplayer Setup Screen, 4 = Customisation Menu, 5 = Settings Menu, 6 = Language Menu, 7 = Tutorial Screen, 8 = Summary Screen, 9 = Server Connection Screen, 10 = Game Mode Info Screen
    private int menuScreen = 1;
    private int prevScreen = 0;
    private int gameInfoScreen = 1;

    private int scaleConstant;

    private int selectedOpposition = 2;
    private int selectedGameMode = 1;
    private boolean selectedPracticeMode = false;
    private boolean selectedSpecificPlayer = false;

    private String tempUsername = "";
    private String opponentUsername = "";

    private boolean usernameAvailable = false;
    private boolean opponentAvailable = false;

    private boolean usernameChecked = false;
    private boolean opponentChecked = false;

    private boolean showConnecting = false;
    private boolean showHelpMultiplayer = false;

    private boolean[] buttonDown = new boolean[10];

    /* Animation Variables */
    private int timeCounter = 0;
    private int dart = 0;

    private float[] dartY = new float[3];
    private float[] dartZ = new float[3];
    private float[] rot = new float[3];

    private int throwTime = 0;

    MenuScreen(final MotionDarts game) {

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        scaleConstant = screenWidth / 720;

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        setupFonts();

        /* Viewpoint Setup */
        // PerspectiveCamera setup: Field of Vision, viewpoint width, viewpoint height
        perspectiveCamera = new PerspectiveCamera(70, screenWidth, screenHeight);
        perspectiveCamera.near = 1.0f;
        perspectiveCamera.far = 100000.0f;
        perspectiveCamera.position.set(-1800.0f, 300.0f, -400.0f);
        perspectiveCamera.lookAt(2000.0f, 1800.0f, 200.0f);


        /* Models Setup */
        // Moving everything into the right place for setup
        // 100.0f = 1 cm
        // Get the loaded models from the assetManager
        Model dartModel1 = MotionDarts.assetManager.get("models/dart_01.g3db", Model.class);
        Model dartboardModel1 = MotionDarts.assetManager.get("models/dartboard_01.g3db", Model.class);
        Model environmentModel1 = MotionDarts.assetManager.get("models/environment_01.g3db", Model.class);
        Model spaceSurroundModel = MotionDarts.assetManager.get("models/menu_spacesurround.g3db", Model.class);

        // Assign Models to ModelInstances
        GameScreen.dartModelInstances[0] = new ModelInstance(dartModel1);
        GameScreen.dartModelInstances[1] = new ModelInstance(dartModel1);
        GameScreen.dartModelInstances[2] = new ModelInstance(dartModel1);
        GameScreen.dartboardModelInst1 = new ModelInstance(dartboardModel1);
        GameScreen.environmentModelInst1 = new ModelInstance(environmentModel1);
        spaceSurroundModelInst = new ModelInstance(spaceSurroundModel);

        instances.add(GameScreen.dartModelInstances[0]);
        instances.add(GameScreen.dartModelInstances[1]);
        instances.add(GameScreen.dartModelInstances[2]);
        instances.add(GameScreen.dartboardModelInst1);
        instances.add(spaceSurroundModelInst);

        for(int i = 0; i < 3; i++) {
            GameScreen.dartModelInstances[i].transform.setToTranslation(4100.0f, -10000.0f, -19000.0f);
            GameScreen.dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 225);
            GameScreen.dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
        }

        GameScreen.dartboardModelInst1.transform.setToTranslation(0.0f, 0.0f, 0.0f);
        GameScreen.dartboardModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 90);
        GameScreen.dartboardModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 180);

        spaceSurroundModelInst.transform.setToTranslation(0.0f, 0.0f, -2000.0f);
        spaceSurroundModelInst.transform.rotate(0.0f, 1.0f, 0.0f, 90);

        /* 2D Graphics Setup */
        Texture defaultButtonTexture = MotionDarts.assetManager.get("textures/defaultButton.png", Texture.class);
        Texture settingsButtonTexture = MotionDarts.assetManager.get("textures/settingsButton.png", Texture.class);
        Texture languageButtonTexture = MotionDarts.assetManager.get("textures/languageButton.png", Texture.class);
        Texture exitButtonTexture = MotionDarts.assetManager.get("textures/exitButton.png", Texture.class);
        Texture titleTexture = MotionDarts.assetManager.get("textures/title.png", Texture.class);
        Texture submenuBackgroundTexture = MotionDarts.assetManager.get("textures/submenu_background.png", Texture.class);
        Texture backButtonTexture = MotionDarts.assetManager.get("textures/backButton.png", Texture.class);
        Texture tickButtonTexture = MotionDarts.assetManager.get("textures/tickButton.png", Texture.class);
        Texture connectButtonTexture = MotionDarts.assetManager.get("textures/connectButton.png", Texture.class);
        Texture helpButtonTexture = MotionDarts.assetManager.get("textures/helpButton.png", Texture.class);
        Texture flag1Texture = MotionDarts.assetManager.get("textures/flag1.png", Texture.class);
        Texture flag2Texture = MotionDarts.assetManager.get("textures/flag2.png", Texture.class);
        Texture flag3Texture = MotionDarts.assetManager.get("textures/flag3.png", Texture.class);
        Texture flag4Texture = MotionDarts.assetManager.get("textures/flag4.png", Texture.class);
        Texture flag5Texture = MotionDarts.assetManager.get("textures/flag5.png", Texture.class);
        Texture flag6Texture = MotionDarts.assetManager.get("textures/flag6.png", Texture.class);
        Texture flag7Texture = MotionDarts.assetManager.get("textures/flag7.png", Texture.class);
        Texture flag8Texture = MotionDarts.assetManager.get("textures/flag8.png", Texture.class);
        Texture flag9Texture = MotionDarts.assetManager.get("textures/flag9.png", Texture.class);
        Texture flag10Texture = MotionDarts.assetManager.get("textures/flag10.png", Texture.class);
        Texture selectedButtonTexture = MotionDarts.assetManager.get("textures/selectedButton.png", Texture.class);
        Texture selectedButtonSmallTexture = MotionDarts.assetManager.get("textures/selectedSmallButton.png", Texture.class);
        Texture selectedLanguageTexture = MotionDarts.assetManager.get("textures/selectedLanguage.png", Texture.class);
        Texture langImageTexture = MotionDarts.assetManager.get("textures/langImage.png", Texture.class);
        Texture sliderBarTexture = MotionDarts.assetManager.get("textures/sliderBar.png", Texture.class);
        Texture sliderBitTexture = MotionDarts.assetManager.get("textures/sliderBit.png", Texture.class);
        Texture sliderBitSelectedTexture = MotionDarts.assetManager.get("textures/sliderBitSelected.png", Texture.class);
        Texture leftButtonTexture = MotionDarts.assetManager.get("textures/leftButton.png", Texture.class);
        Texture rightButtonTexture = MotionDarts.assetManager.get("textures/rightButton.png", Texture.class);

        defaultButton = new Sprite(defaultButtonTexture);
        settingsButton = new Sprite(settingsButtonTexture);
        languageButton = new Sprite(languageButtonTexture);
        exitButton = new Sprite(exitButtonTexture);
        title = new Sprite(titleTexture);
        submenuBackground = new Sprite(submenuBackgroundTexture);
        backButton = new Sprite(backButtonTexture);
        tickButton = new Sprite(tickButtonTexture);
        connectButton = new Sprite(connectButtonTexture);
        helpButton = new Sprite(helpButtonTexture);
        flag1 = new Sprite(flag1Texture);
        flag2 = new Sprite(flag2Texture);
        flag3 = new Sprite(flag3Texture);
        flag4 = new Sprite(flag4Texture);
        flag5 = new Sprite(flag5Texture);
        flag6 = new Sprite(flag6Texture);
        flag7 = new Sprite(flag7Texture);
        flag8 = new Sprite(flag8Texture);
        flag9 = new Sprite(flag9Texture);
        flag10 = new Sprite(flag10Texture);
        selectedButton = new Sprite(selectedButtonTexture);
        selectedButtonSmall = new Sprite(selectedButtonSmallTexture);
        selectedLanguage = new Sprite(selectedLanguageTexture);
        langImage = new Sprite(langImageTexture);
        sliderBar = new Sprite(sliderBarTexture);
        sliderBit = new Sprite(sliderBitTexture);
        sliderBitSelected = new Sprite(sliderBitSelectedTexture);
        leftButton = new Sprite(leftButtonTexture);
        rightButton = new Sprite(rightButtonTexture);

        /* Game Environment */
        environment = new Environment();

        // Sets ambient lighting
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.0f));
        // Creates a new point light; first three parameters: color, next three: position, then intensity
        environment.add(new PointLight().set(0.6f, 0.6f, 0.8f, 0.0f, 4000.0f, -10000.0f, 150000000.0f));

        /* Touch Input */
        InputAdapter inputAdapter = new InputAdapter() {
            @Override
            public boolean touchDown(int touchX, int touchY, int pointer, int button) {

                switch (menuScreen) {
                    case 1:
                        break;

                    case 2:
                        if(touchY < (screenHeight - 975 * scaleConstant) && touchY > (screenHeight - 1055 * scaleConstant)) {
                            if(touchX > 30 * scaleConstant && touchX < 248 * scaleConstant) {
                                selectedOpposition = 1;
                            } else if(touchX >= 248 * scaleConstant && touchX < 468 * scaleConstant) {
                                selectedOpposition = 2;
                            } else if(touchX >= 468 * scaleConstant && touchX < 690 * scaleConstant) {
                                selectedOpposition = 3;
                            }
                        }

                        if(touchX > 30 * scaleConstant && touchX < 360 * scaleConstant) {
                            if(touchY < (screenHeight - 410 * scaleConstant) && touchY >= (screenHeight - 612 * scaleConstant)) {
                                selectedGameMode = 3;
                            } else if(touchY < (screenHeight - 612 * scaleConstant) && touchY > (screenHeight - 814) * scaleConstant) {
                                selectedGameMode = 1;
                            }
                        } else if(touchX >= 360 * scaleConstant && touchX < 690 * scaleConstant) {
                            if(touchY < (screenHeight - 410 * scaleConstant) && touchY >= (screenHeight - 612 * scaleConstant)) {
                                selectedGameMode = 4;
                            } else if(touchY < (screenHeight - 612 * scaleConstant) && touchY > (screenHeight - 814) * scaleConstant) {
                                selectedGameMode = 2;
                            }
                        }

                        break;

                    case 3:
                        if(touchX > 30 * scaleConstant && touchX < 690 * scaleConstant) {
                            if(touchY > (screenHeight - 1055 * scaleConstant) && touchY <= (screenHeight - 955 * scaleConstant)) {
                                selectedOpposition = 4;
                                selectedSpecificPlayer = false;
                            } else if(touchY > (screenHeight - 955 * scaleConstant) && touchY <= (screenHeight - 855 * scaleConstant)) {
                                selectedOpposition = 5;
                                selectedSpecificPlayer = false;
                            } else if(touchY > (screenHeight - 855 * scaleConstant) && touchY < (screenHeight - 755 * scaleConstant)) {
                                selectedOpposition = 5;
                                selectedSpecificPlayer = true;
                            }
                        }

                        if(touchX > 30 * scaleConstant && touchX < 360 * scaleConstant) {
                            if(touchY < (screenHeight - 230 * scaleConstant) && touchY >= (screenHeight - 432 * scaleConstant)) {
                                selectedGameMode = 3;
                            } else if(touchY < (screenHeight - 432 * scaleConstant) && touchY > (screenHeight - 634) * scaleConstant) {
                                selectedGameMode = 1;
                            }
                        } else if(touchX >= 360 * scaleConstant && touchX < 690 * scaleConstant) {
                            if(touchY < (screenHeight - 230 * scaleConstant) && touchY >= (screenHeight - 432 * scaleConstant)) {
                                selectedGameMode = 4;
                            } else if(touchY < (screenHeight - 432 * scaleConstant) && touchY > (screenHeight - 634) * scaleConstant) {
                                selectedGameMode = 2;
                            }
                        }

                        break;

                    case 6:
                        if(touchX > 120 * scaleConstant && touchX < 320 * scaleConstant) {
                            if(touchY < (screenHeight - 950 * scaleConstant) && touchY > (screenHeight - 1075 * scaleConstant)) {
                                language = 0;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            } else if(touchY < (screenHeight - 750 * scaleConstant) && touchY > (screenHeight - 875 * scaleConstant)) {
                                language = 2;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            } else if(touchY < (screenHeight - 550 * scaleConstant) && touchY > (screenHeight - 675 * scaleConstant)) {
                                language = 4;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            } else if(touchY < (screenHeight - 350 * scaleConstant) && touchY > (screenHeight - 475 * scaleConstant)) {
                                language = 6;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            } else if(touchY < (screenHeight - 150 * scaleConstant) && touchY > (screenHeight - 275 * scaleConstant)) {
                                language = 8;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            }
                        } else if(touchX > 400 * scaleConstant && touchX < 600 * scaleConstant) {
                            if(touchY < (screenHeight - 950 * scaleConstant) && touchY > (screenHeight - 1075 * scaleConstant)) {
                                language = 1;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            } else if(touchY < (screenHeight - 750 * scaleConstant) && touchY > (screenHeight - 875 * scaleConstant)) {
                                language = 3;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            } else if(touchY < (screenHeight - 550 * scaleConstant) && touchY > (screenHeight - 675 * scaleConstant)) {
                                language = 5;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            } else if(touchY < (screenHeight - 350 * scaleConstant) && touchY > (screenHeight - 475 * scaleConstant)) {
                                language = 7;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            } else if(touchY < (screenHeight - 150 * scaleConstant) && touchY > (screenHeight - 275 * scaleConstant)) {
                                language = 9;
                                setupFonts();
                                MotionDarts.loadLanguage(language);
                            }
                        }
                        break;

                }

                return true;
            }

            public boolean touchUp(int touchX, int touchY, int pointer, int button) {

                /* Reset all buttons */
                for(int i = 0; i < buttonDown.length; i++) {
                    buttonDown[i] = false;
                }

                switch(menuScreen) {

                    case 1:     // Main Menu
                        if (touchX >= screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 && touchX <= screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + 300 * scaleConstant) {
                            if (touchY >= screenHeight / 16 * 7 && touchY <= screenHeight / 16 * 7 + 150 * scaleConstant) {
                                menuScreen = 2;
                                selectedOpposition = 2;
                            } else if (touchY >= screenHeight / 16 * 7 + 200 * scaleConstant && touchY <= screenHeight / 16 * 7 + 350 * scaleConstant) {
                                menuScreen = 3;
                                selectedOpposition = 4;
                            } else if (touchY >= screenHeight / 16 * 7 + 400 * scaleConstant && touchY <= screenHeight / 16 * 7 + 550 * scaleConstant) {
                                menuScreen = 4;
                            }
                        }

                        if (touchY >= screenHeight - 90 * scaleConstant && touchY <= screenHeight - 10 * scaleConstant) {
                            if (touchX >= 10 * scaleConstant && touchX <= 90 * scaleConstant) {
                                menuScreen = 5;
                            } else if (touchX >= 100 * scaleConstant && touchX <= 180 * scaleConstant) {
                                menuScreen = 6;
                            } else if (touchX >= 190 * scaleConstant && touchX <= 270 * scaleConstant) {
                                menuScreen = 7;
                            } else if (touchX >= screenWidth - 90 * scaleConstant && touchX <= screenWidth - 10 * scaleConstant) {
                                Gdx.app.exit();
                            }
                        }
                        break;

                    case 2:     // Single player Game setup
                        if(touchX > screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 && touchX <  screenWidth / 2 + (defaultButton.getWidth() * scaleConstant) / 2 && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            game.setScreen(new GameScreen(game));
                            GameScreen.gameClass = new GameClass(selectedGameMode, selectedOpposition);
                        }

                        if(touchX > 20 * scaleConstant && touchX < 100 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            menuScreen = 1;
                        }

                        if(touchX > 620 * scaleConstant && touchX < 700 * scaleConstant && touchY < screenHeight - 830 * scaleConstant && touchY >= screenHeight - 910 * scaleConstant) {
                            menuScreen = 10;
                            prevScreen = 2;
                        }

                        // Practice mode selection button
                        if(touchX > 30 * scaleConstant && touchX < 110 * scaleConstant && touchY < (screenHeight - 300 * scaleConstant) && touchY > (screenHeight - 380 * scaleConstant)) {
                            selectedPracticeMode = !selectedPracticeMode;
                        }
                        break;

                    case 3:     // Multi Player Game setup

                        if(touchX > screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 && touchX <  screenWidth / 2 + (defaultButton.getWidth() * scaleConstant) / 2 && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {

                            if(selectedSpecificPlayer) {
                                // Connect to server
                                menuScreen = 9;
                                usernameChecked = false;
                                opponentChecked = false;
                            } else if(selectedOpposition == 5) {
                                if(!showConnecting) {
                                    showConnecting = true;
                                    // Connect to server
                                    // Matchmake
                                    // Begin Game
                                }
                            } else {
                                // Begin Game
                                game.setScreen(new GameScreen(game));
                                GameScreen.gameClass = new GameClass(selectedGameMode, selectedOpposition);
                            }

                        } else if(touchX > 20 * scaleConstant && touchX < 100 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            menuScreen = 1;
                        }

                        if(touchX > 620 * scaleConstant && touchX < 700 * scaleConstant && touchY < screenHeight - 650 * scaleConstant && touchY >= screenHeight - 730 * scaleConstant) {
                            menuScreen = 10;
                            prevScreen = 3;
                        }
                        break;

                    case 4:

                        if(touchX > 20 * scaleConstant && touchX < 100 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            menuScreen = 1;
                        }

                        if(touchY >= screenHeight - 1160 * scaleConstant && touchY <= screenHeight - 1080 * scaleConstant) {
                            if(touchX >= 530 * scaleConstant && touchX <= 610 * scaleConstant) {

                            } else if(touchX >= 620 * scaleConstant && touchX <= 700 * scaleConstant) {

                            }
                        } else if(touchY >= screenHeight - 740 * scaleConstant && touchY <= screenHeight - 660 * scaleConstant) {
                            if(touchX >= 530 * scaleConstant && touchX <= 610 * scaleConstant) {

                            } else if(touchX >= 620 * scaleConstant && touchX <= 700 * scaleConstant) {

                            }
                        }

                        break;

                    case 5:

                        if(touchX > 20 * scaleConstant && touchX < 100 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            menuScreen = 1;
                        }

                        break;

                    case 6:

                        if(touchX > 20 * scaleConstant && touchX < 100 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            menuScreen = 1;
                        }

                        break;

                    case 7:

                        if(touchX > 20 * scaleConstant && touchX < 100 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            menuScreen = 1;
                        }

                        break;

                    case 9:     // MultiPlayer Server Connect Screen

                        if(touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            if(touchX > 20 * scaleConstant && touchX < 100 * scaleConstant) {
                                menuScreen = 3;
                            } else if(touchX > 110 * scaleConstant && touchX < 190 * scaleConstant) {
                                showHelpMultiplayer = !showHelpMultiplayer;
                            }
                        }

                        if(touchX > screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 && touchX <  screenWidth / 2 + (defaultButton.getWidth() * scaleConstant) / 2 && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            // Check that everything's been entered correctly
                            if(usernameAvailable && opponentAvailable) {
                                if(!showConnecting) {
                                    showConnecting = true;
                                    // Wait for other user
                                    // Start Game
                                }
                            } else{
                                usernameChecked = true;
                                opponentChecked = true;
                            }
                        }

                        if(touchX > 30 * scaleConstant && touchX < 580 * scaleConstant) {
                            if(touchY < (screenHeight - 975 * scaleConstant) && touchY > (screenHeight - 1055 * scaleConstant)) {

                                Gdx.input.getTextInput(new Input.TextInputListener() {
                                    @Override
                                    public void input(String text) {

                                        // Text length limiter
                                        if(text.length() > 15) {
                                            text = text.substring(0, 15);
                                        }

                                        tempUsername = text;

                                        // Reassigns the font, to ensure all used characters are loaded in
                                        freeTypeFontParameter.characters += text;
                                        freeTypeFontParameter.color = new Color(0, 0, 0, 255);
                                        freeTypeFontParameter.size = 55 * scaleConstant;
                                        freeTypeFontParameter.borderWidth = 0;
                                        menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                                    }

                                    @Override
                                    public void canceled() {

                                    }
                                }, menuText[27], tempUsername, menuText[28]);

                            } else if(touchY < (screenHeight - 735 * scaleConstant) && touchY > (screenHeight - 815 * scaleConstant)) {

                                Gdx.input.getTextInput(new Input.TextInputListener() {
                                    @Override
                                    public void input(String text) {

                                        // Text length limiter
                                        if(text.length() > 15) {
                                            text = text.substring(0, 15);
                                        }

                                        opponentUsername = text;

                                        // Reassigns the font, to ensure all used characters are loaded in
                                        freeTypeFontParameter.characters += text;
                                        freeTypeFontParameter.color = new Color(0, 0, 0, 255);
                                        freeTypeFontParameter.size = 55 * scaleConstant;
                                        freeTypeFontParameter.borderWidth = 0;
                                        menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                                    }

                                    @Override
                                    public void canceled() {

                                    }
                                }, menuText[30], opponentUsername, menuText[31]);

                            }
                        }

                        if(touchX > 605 && touchX < 685) {
                            if(touchY < (screenHeight - 975 * scaleConstant) && touchY > (screenHeight - 1055 * scaleConstant)) {
                                // Run check in server to see if username is in use
                                usernameAvailable = false;  // Replace this with function call
                                usernameChecked = true;
                            } else if(touchY < (screenHeight - 735 * scaleConstant) && touchY > (screenHeight - 815 * scaleConstant)) {
                                // Run check in server to see if target exists
                                opponentAvailable = false;  // Replace this with function call
                                opponentChecked = true;
                            }
                        }
                        break;

                    case 10:

                        if(touchX > 20 * scaleConstant && touchX < 100 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            menuScreen = prevScreen;
                        } else if(touchX > 620 * scaleConstant && touchX < 700 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            // Right Button
                            if(gameInfoScreen < 4) {
                                gameInfoScreen++;
                            }
                        } else if(touchX > 520 * scaleConstant && touchX < 600 * scaleConstant && touchY > screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant) {
                            // Left Button
                            if(gameInfoScreen > 1) {
                                gameInfoScreen--;
                            }
                        }

                        break;
                }
                return true;
            }
        };

        Gdx.input.setInputProcessor(inputAdapter);
	}

	@Override
	public void render (float delta) {
        // Sets Viewport
        Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
        // Clears Screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        /* Background Animations */
        backgroundAnimation();

        /* Draw 3D graphics to screen */
        modelBatch.begin(perspectiveCamera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        /* Draw 2D graphics over the 3D graphics */
        switch(menuScreen) {

            case 1:
                drawMainMenuScreen();
                break;

            case 2:
                drawGameSetupScreen();
                break;

            case 3:
                drawMultiplayerSetupScreen();
                break;

            case 4:
                drawCustomisationScreen();
                break;

            case 5:
                drawSettingsScreen();
                break;

            case 6:
                drawLanguageScreen();
                break;

            case 7:
                drawTutorialScreen();
                break;

            case 9:
                drawMultiplayerTargetedConnectScreen();
                break;

            case 10:
                drawGamemodeInfoScreen();
                break;

        }

        /* Button Down Detection */
        if(Gdx.input.isTouched()) {

            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();

            switch(menuScreen) {
                case 1:
                    buttonDown[0] = (touchY >= screenHeight / 16 * 7 && touchY <= screenHeight / 16 * 7 + 150 * scaleConstant) && (touchX >= screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 && touchX <= screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + 300 * scaleConstant);
                    buttonDown[1] = (touchY >= screenHeight / 16 * 7 + 200 && touchY <= screenHeight / 16 * 7 + 350 * scaleConstant) && (touchX >= screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 && touchX <= screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + 300 * scaleConstant);
                    buttonDown[2] = (touchY >= screenHeight / 16 * 7 + 400 && touchY <= screenHeight / 16 * 7 + 550 * scaleConstant) && (touchX >= screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 && touchX <= screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + 300 * scaleConstant);

                    buttonDown[3] = (touchX >= 10 * scaleConstant && touchX <= 90 * scaleConstant) && (touchY >= screenHeight - 90 * scaleConstant && touchY <= screenHeight - 10 * scaleConstant);
                    buttonDown[4] = (touchX >= 100 * scaleConstant && touchX <= 180 * scaleConstant) && (touchY >= screenHeight - 90 * scaleConstant && touchY <= screenHeight - 10 * scaleConstant);
                    buttonDown[5] = (touchX >= 190 * scaleConstant && touchX <= 270 * scaleConstant) && (touchY >= screenHeight - 90 * scaleConstant && touchY <= screenHeight - 10 * scaleConstant);
                    buttonDown[6] = (touchX >= screenWidth - 90 && touchX <= screenWidth - 10) && (touchY >= screenHeight - 90 * scaleConstant && touchY <= screenHeight - 10 * scaleConstant);
                    break;

                case 2:
                    buttonDown[0] = (touchX >= 20 * scaleConstant && touchX <= 100 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    buttonDown[1] = (touchX >= 620 * scaleConstant && touchX <= 700 * scaleConstant) && (touchY >= screenHeight - 910 * scaleConstant && touchY <= screenHeight - 830 * scaleConstant);
                    buttonDown[2] = (touchX >= 210 * scaleConstant && touchX <= 510 * scaleConstant) && (touchY >= screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    break;

                case 3:
                    buttonDown[0] = (touchX >= 20 * scaleConstant && touchX <= 100 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    buttonDown[1] = (touchX >= 620 * scaleConstant && touchX <= 700 * scaleConstant) && (touchY >= screenHeight - 730 * scaleConstant && touchY <= screenHeight - 650 * scaleConstant);
                    buttonDown[2] = (touchX >= 210 * scaleConstant && touchX <= 510 * scaleConstant) && (touchY >= screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    break;

                case 4:
                    buttonDown[0] = (touchX >= 20 * scaleConstant && touchX <= 100 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    buttonDown[1] = (touchX >= 530 * scaleConstant && touchX <= 610 * scaleConstant) && (touchY >= screenHeight - 1160 * scaleConstant && touchY <= screenHeight - 1080 * scaleConstant);
                    buttonDown[2] = (touchX >= 620 * scaleConstant && touchX <= 700 * scaleConstant) && (touchY >= screenHeight - 1160 * scaleConstant && touchY <= screenHeight - 1080 * scaleConstant);
                    buttonDown[3] = (touchX >= 530 * scaleConstant && touchX <= 610 * scaleConstant) && (touchY >= screenHeight - 740 * scaleConstant && touchY <= screenHeight - 660 * scaleConstant);
                    buttonDown[4] = (touchX >= 620 * scaleConstant && touchX <= 700 * scaleConstant) && (touchY >= screenHeight - 740 * scaleConstant && touchY <= screenHeight - 660 * scaleConstant);
                    break;

                case 5:
                    buttonDown[0] = (touchX >= 20 * scaleConstant && touchX <= 100 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    buttonDown[1] = (touchY <= screenHeight - 928 * scaleConstant && touchY >= screenHeight - 1028 * scaleConstant) && (touchX >= 60 * scaleConstant && touchX <= 660 * scaleConstant);

                    if(buttonDown[1]) {
                        GameScreen.sensitivityZ = (touchX - 60) / 600 + 0.5f;
                    }
                    break;

                case 6:
                    buttonDown[0] = (touchX >= 20 * scaleConstant && touchX <= 100 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    break;

                case 7:
                    buttonDown[0] = (touchX >= 20 * scaleConstant && touchX <= 100 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    break;

                case 9:
                    buttonDown[0] = (touchX >= 20 * scaleConstant && touchX <= 100 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    buttonDown[1] = (touchX >= 110 * scaleConstant && touchX <= 190 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    buttonDown[2] = (touchX >= 210 * scaleConstant && touchX <= 510 * scaleConstant) && (touchY >= screenHeight - 170 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    break;

                case 10:
                    buttonDown[0] = (touchX >= 20 * scaleConstant && touchX <= 100 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    buttonDown[1] = (touchX >= 520 * scaleConstant && touchX <= 600 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    buttonDown[2] = (touchX >= 620 * scaleConstant && touchX <= 700 * scaleConstant) && (touchY >= screenHeight - 100 * scaleConstant && touchY <= screenHeight - 20 * scaleConstant);
                    break;
            }
        }

	}
	
	@Override
	public void dispose () {
        modelBatch.dispose();
        spriteBatch.dispose();
        GameScreen.instances.clear();
        MotionDarts.assetManager.dispose();
        freeTypeFontGenerator.dispose();
        menuButtonFont.dispose();
        menuButtonFontBold.dispose();
        menuTitleFont.dispose();
        shapeRenderer.dispose();
	}

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        perspectiveCamera.viewportWidth = width;
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.update();
    }

    private void setupFonts() {
        switch(language) {
            case 0:
                /* Fonts Setup*/
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfb.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.color = new Color(255, 255, 255, 255);
                freeTypeFontParameter.borderColor = new Color(0, 0, 132, 255);
                freeTypeFontParameter.borderWidth = 2;

                // Button and Subheading Font
                freeTypeFontParameter.size = 55 * scaleConstant;
                menuButtonFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Description Font
                freeTypeFontParameter.size = 30 * scaleConstant;
                menuDescriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (red)
                freeTypeFontParameter.color = new Color(255, 0, 0, 255);
                freeTypeFontParameter.size = 25 * scaleConstant;
                freeTypeFontParameter.borderWidth = 0;
                menuStatusFontRed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (green)
                freeTypeFontParameter.color = new Color(0, 255, 0, 255);
                menuStatusFontGreen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Input Text (for text input boxes)
                freeTypeFontParameter.color = new Color(0, 0, 0, 255);
                freeTypeFontParameter.size = 55 * scaleConstant;
                menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbbold.ttf"));

                // Button Font (Bold)
                freeTypeFontParameter.color = new Color(255, 255, 255, 255);
                freeTypeFontParameter.size = 55 * scaleConstant;
                freeTypeFontParameter.borderWidth = 2;
                menuButtonFontBold = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Title Font
                freeTypeFontParameter.size = 100 * scaleConstant;
                menuTitleFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                break;
            case 1:
            case 2:
                /* Fonts Setup*/
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbcyrillic.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.characters = "ЁЪЯШЕДСАЗХЦВФРТГБНЧЫУЙМКИОЛПЬЮЖЩЭёъяшедсазхцвфртгбнчыуймкиолпьюжщэ501234.()";
                freeTypeFontParameter.color = new Color(255, 255, 255, 255);
                freeTypeFontParameter.borderColor = new Color(0, 0, 132, 255);
                freeTypeFontParameter.borderWidth = 2;

                // Button and Subheading Font
                freeTypeFontParameter.size = 55 * scaleConstant;
                menuButtonFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Description Font
                freeTypeFontParameter.size = 30 * scaleConstant;
                menuDescriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (red)
                freeTypeFontParameter.color = new Color(255, 0, 0, 255);
                freeTypeFontParameter.size = 25 * scaleConstant;
                freeTypeFontParameter.borderWidth = 0;
                menuStatusFontRed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (green)
                freeTypeFontParameter.color = new Color(0, 255, 0, 255);
                menuStatusFontGreen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Input Text (for text input boxes)
                freeTypeFontParameter.color = new Color(0, 0, 0, 255);
                freeTypeFontParameter.size = 55 * scaleConstant;
                menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Button Font (Bold)
                freeTypeFontParameter.color = new Color(255, 255, 255, 255);
                freeTypeFontParameter.size = 55 * scaleConstant;
                freeTypeFontParameter.borderWidth = 2;
                menuButtonFontBold = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Title Font
                freeTypeFontParameter.size = 100 * scaleConstant;
                menuTitleFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                break;
        }
    }

    private void backgroundAnimation() {
        final int CURRENT_FPS = (Gdx.graphics.getFramesPerSecond() > 10) ? Gdx.graphics.getFramesPerSecond() : 30;

        // Constant dartboard rotation
        GameScreen.dartboardModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, -0.30f / (CURRENT_FPS / 30.0f));
        spaceSurroundModelInst.transform.rotate(1.0f, 0.0f, 0.0f, -0.10f / (CURRENT_FPS / 30.0f));

        // Makes darts rotate with the dartboard, by moving them at different rates, depending on distance from dartboard centre
        for(int i = 0; i < dart; i++) {
            rot[i] += 0.30f / (CURRENT_FPS / 30.0f);
            GameScreen.dartModelInstances[i].transform.setToTranslation(0.0f - (float) Math.cos(Math.toRadians(-rot[i] + 90))
                    * 2.0f * dartY[i], dartY[i] * (float) Math.sin(Math.toRadians(-rot[i] + 90)) * 2.0f, dartZ[i]);
            GameScreen.dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 90);
            GameScreen.dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
        }

        if(dart < 3) {

            // Throws a dart onto the rotating board
            if (timeCounter >= throwTime + 2 * CURRENT_FPS) {

                // Generates a random y position for the dart
                rot[dart] = 0;
                dartY[dart] = new Random().nextInt(600) + 200.0f;
                dartZ[dart] = -425.0f;

                dart++;

                throwTime = new Random().nextInt(2 * CURRENT_FPS);

                timeCounter = 0;
            } else {
                timeCounter++;
            }

        } else{
            // Resets the darts once they have rotated out of view, so that they can be thrown in again
            for(int i = 0; i < 3; i++) {

                if(rot[i] > 130) {

                    dartZ[i] = -19000.0f;
                    rot[i] = 0;

                    if(i == 2) {
                        dart = 0;
                    }
                }
            }
        }
    }

    private void drawMainMenuScreen() {
        spriteBatch.begin();

        // Title
        spriteBatch.draw(title, -screenWidth / 8, screenHeight - screenWidth / 16 * 11, screenWidth / 8 * 10, screenWidth / 8 * 5);

        // Main Buttons
        spriteBatch.draw(defaultButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, screenHeight / 16 * 7, 300 * scaleConstant, 150 * scaleConstant);
        spriteBatch.draw(defaultButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, screenHeight / 16 * 7 - 200 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant);
        spriteBatch.draw(defaultButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, screenHeight / 16 * 7 - 400 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant);
        // Small Buttons
        spriteBatch.draw(settingsButton, 10 * scaleConstant, 10 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        spriteBatch.draw(languageButton, 100 * scaleConstant, 10 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        spriteBatch.draw(helpButton, 190 * scaleConstant, 10 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        spriteBatch.draw(exitButton, screenWidth - 90 * scaleConstant, 10 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Main Button Text
        menuButtonFont.draw(spriteBatch, menuText[0], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[0] * scaleConstant, screenHeight / 16 * 7 + 96 * scaleConstant);
        menuButtonFont.draw(spriteBatch, menuText[1], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[1] * scaleConstant, screenHeight / 16 * 7 - 200 * scaleConstant + 96 * scaleConstant);
        menuButtonFont.draw(spriteBatch, menuText[2], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[2] * scaleConstant, screenHeight / 16 * 7 - 400 * scaleConstant + 96 * scaleConstant);

        // Button Down Images
        if(buttonDown[0]) { spriteBatch.draw(selectedButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, screenHeight / 16 * 7, 300 * scaleConstant, 150 * scaleConstant); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, screenHeight / 16 * 7 - 200 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, screenHeight / 16 * 7 - 400 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant); }

        if(buttonDown[3]) { spriteBatch.draw(selectedButtonSmall, 10 * scaleConstant, 10 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[4]) { spriteBatch.draw(selectedButtonSmall, 100 * scaleConstant, 10 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[5]) { spriteBatch.draw(selectedButtonSmall, 190 * scaleConstant, 10 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }

        if(buttonDown[6]) { spriteBatch.draw(selectedButtonSmall, screenWidth - 90 * scaleConstant, 10 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }

        spriteBatch.end();
    }

    private void drawGameSetupScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Begin game button
        spriteBatch.draw(defaultButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, 20 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant);
        // Back button
        spriteBatch.draw(backButton, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        // Help button
        spriteBatch.draw(helpButton, 620 * scaleConstant, 830 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Title text
        menuTitleFont.draw(spriteBatch, menuText[3], menuTextIndent[3] * scaleConstant, screenHeight / 32 * 31);
        // Button text
        menuButtonFontBold.draw(spriteBatch, menuText[6], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[6] * scaleConstant, 20 * scaleConstant + 96 * scaleConstant);

        // Sub heading text
        menuButtonFont.draw(spriteBatch, menuText[4], 30 * scaleConstant, screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, menuText[5], 30 * scaleConstant, screenHeight / 32 * 22);
        menuButtonFont.draw(spriteBatch, menuText[16], 125 * scaleConstant, screenHeight / 32 * 9);

        // Difficulty Selection text
        menuButtonFontBold.draw(spriteBatch, menuText[7], (30 + menuTextIndent[7]) * scaleConstant, 1038 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[8], (250 + menuTextIndent[8]) * scaleConstant, 1038 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[9], (470 + menuTextIndent[9]) * scaleConstant, 1038 * scaleConstant);

        // Game Mode Selection text
        menuButtonFontBold.draw(spriteBatch, menuText[10], 40 * scaleConstant, 664 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[11], 365 * scaleConstant, 714 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[12], 365 * scaleConstant, 664 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[13], 40 * scaleConstant, 459 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[14], 365 * scaleConstant, 509 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[15], 365 * scaleConstant, 459 * scaleConstant);

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, 620 * scaleConstant, 830 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButton, 210 * scaleConstant, 20 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant); }

        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Difficulty Selector Box outlines
        shapeRenderer.setColor(new Color(0, 0, 0.5f, 1.0f));
        shapeRenderer.rect(30 * scaleConstant, 1055 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 975 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 975 * scaleConstant, 4 * scaleConstant, 80 * scaleConstant);
        shapeRenderer.rect(248 * scaleConstant, 975 * scaleConstant, 4 * scaleConstant, 80 * scaleConstant);
        shapeRenderer.rect(468 * scaleConstant, 975 * scaleConstant, 4 * scaleConstant, 80 * scaleConstant);
        shapeRenderer.rect(688 * scaleConstant, 975 * scaleConstant, 4 * scaleConstant, 80 * scaleConstant);

        // Game Mode Selector
        shapeRenderer.rect(30 * scaleConstant, 814 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 612 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 410 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 410 * scaleConstant, 4 * scaleConstant, 408 * scaleConstant);
        shapeRenderer.rect(688 * scaleConstant, 410 * scaleConstant, 4 * scaleConstant, 408 * scaleConstant);
        shapeRenderer.rect(358 * scaleConstant, 410 * scaleConstant, 4 * scaleConstant, 408 * scaleConstant);

        // Practice Mode RadioButton
        shapeRenderer.rect(30 * scaleConstant, 300 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        shapeRenderer.rect(36 * scaleConstant, 306 * scaleConstant, 68 * scaleConstant, 68 * scaleConstant);
        shapeRenderer.setColor(new Color(0, 0, 0.0f, 1.0f));
        if(selectedPracticeMode) { shapeRenderer.rect(45 * scaleConstant, 315 * scaleConstant, 50 * scaleConstant, 50 * scaleConstant); }

        // Selection Dimmer (dims out what isn't selected)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(new Color(0, 0, 0.0f, 0.5f));
        if(selectedOpposition != 1 || selectedPracticeMode) { shapeRenderer.rect(34 * scaleConstant, 979 * scaleConstant, 214 * scaleConstant, 76 * scaleConstant); }
        if(selectedOpposition != 2 || selectedPracticeMode) { shapeRenderer.rect(252 * scaleConstant, 979 * scaleConstant, 216 * scaleConstant, 76 * scaleConstant); }
        if(selectedOpposition != 3 || selectedPracticeMode) { shapeRenderer.rect(472 * scaleConstant, 979 * scaleConstant, 216 * scaleConstant, 76 * scaleConstant); }

        if(selectedGameMode != 1 || selectedPracticeMode) { shapeRenderer.rect(34 * scaleConstant, 614 * scaleConstant, 326 * scaleConstant, 200 * scaleConstant); }
        if(selectedGameMode != 3 || selectedPracticeMode) { shapeRenderer.rect(34 * scaleConstant, 412 * scaleConstant, 326 * scaleConstant, 200 * scaleConstant); }
        if(selectedGameMode != 2 || selectedPracticeMode) { shapeRenderer.rect(362 * scaleConstant, 614 * scaleConstant, 326 * scaleConstant, 200 * scaleConstant); }
        if(selectedGameMode != 4 || selectedPracticeMode) { shapeRenderer.rect(362 * scaleConstant, 412 * scaleConstant, 326 * scaleConstant, 200 * scaleConstant); }

        shapeRenderer.end();
    }

    private void drawMultiplayerSetupScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Begin game button
        spriteBatch.draw(defaultButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, 20 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant);
        // Back button
        spriteBatch.draw(backButton, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        // Help button
        spriteBatch.draw(helpButton, 620 * scaleConstant, 650 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Title text
        menuTitleFont.draw(spriteBatch, menuText[17], menuTextIndent[17] * scaleConstant, screenHeight / 32 * 31);
        // Button text
        if(selectedOpposition == 5) {
            if(showConnecting) {
                menuButtonFont.draw(spriteBatch, menuText[44], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[44] * scaleConstant, 20 * scaleConstant + 96 * scaleConstant);
            } else{
                menuButtonFontBold.draw(spriteBatch, menuText[23], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[23] * scaleConstant, 20 * scaleConstant + 96 * scaleConstant);
            }
        } else{
            menuButtonFontBold.draw(spriteBatch, menuText[6], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[6] * scaleConstant, 20 * scaleConstant + 96 * scaleConstant);
        }
        // Sub heading text
        menuButtonFont.draw(spriteBatch, menuText[18], 30 * scaleConstant, screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, menuText[5], 30 * scaleConstant, screenHeight / 32 * 18 - 20 * scaleConstant);

        // Multiplayer Type Selector text
        menuButtonFontBold.draw(spriteBatch, menuText[19], 40 * scaleConstant, 1045 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[20], 40 * scaleConstant, 945 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[24], 40 * scaleConstant, 845 * scaleConstant);
        // Multiplayer Type Selector description text
        menuDescriptionFont.draw(spriteBatch, menuText[21], 40 * scaleConstant, 990 * scaleConstant);
        menuDescriptionFont.draw(spriteBatch, menuText[22], 40 * scaleConstant, 890 * scaleConstant);
        menuDescriptionFont.draw(spriteBatch, menuText[25], 40 * scaleConstant, 790 * scaleConstant);

        // Game Mode Selection text
        menuButtonFontBold.draw(spriteBatch, menuText[10], 40 * scaleConstant, 484 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[11], 365 * scaleConstant, 534 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[12], 365 * scaleConstant, 484 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[13], 40 * scaleConstant, 279 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[14], 365 * scaleConstant, 329 * scaleConstant);
        menuButtonFontBold.draw(spriteBatch, menuText[15], 365 * scaleConstant, 279 * scaleConstant);

        // Button Down Highlighting
        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, 620 * scaleConstant, 650 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButton, 210 * scaleConstant, 20 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant); }

        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Multiplayer mode selector
        shapeRenderer.setColor(new Color(0, 0, 0.5f, 1.0f));
        shapeRenderer.rect(30 * scaleConstant, 1055 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 955 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 855 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 755 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 755 * scaleConstant, 4 * scaleConstant, 300 * scaleConstant);
        shapeRenderer.rect(690 * scaleConstant, 755 * scaleConstant, 4 * scaleConstant, 300 * scaleConstant);

        // Game Mode Selector
        shapeRenderer.rect(30 * scaleConstant, 634 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 432 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 230 * scaleConstant, 660 * scaleConstant, 4 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 230 * scaleConstant, 4 * scaleConstant, 408 * scaleConstant);
        shapeRenderer.rect(688 * scaleConstant, 230 * scaleConstant, 4 * scaleConstant, 408 * scaleConstant);
        shapeRenderer.rect(358 * scaleConstant, 230 * scaleConstant, 4 * scaleConstant, 408 * scaleConstant);

        // Selection Dimmer (dims out what isn't selected)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(new Color(0, 0, 0.0f, 0.5f));
        if(selectedOpposition != 4) { shapeRenderer.rect(34 * scaleConstant, 959 * scaleConstant, 656 * scaleConstant, 96 * scaleConstant); }
        if(selectedOpposition != 5 || selectedSpecificPlayer) { shapeRenderer.rect(34 * scaleConstant, 859 * scaleConstant, 656 * scaleConstant, 96 * scaleConstant); }
        if(selectedOpposition != 5 || !selectedSpecificPlayer) { shapeRenderer.rect(34 * scaleConstant, 759 * scaleConstant, 656 * scaleConstant, 96 * scaleConstant); }

        if(selectedGameMode != 1) { shapeRenderer.rect(34 * scaleConstant, 434 * scaleConstant, 326 * scaleConstant, 200 * scaleConstant); }
        if(selectedGameMode != 3) { shapeRenderer.rect(34 * scaleConstant, 232 * scaleConstant, 326 * scaleConstant, 200 * scaleConstant); }
        if(selectedGameMode != 2) { shapeRenderer.rect(362 * scaleConstant, 434 * scaleConstant, 326 * scaleConstant, 200 * scaleConstant); }
        if(selectedGameMode != 4) { shapeRenderer.rect(362 * scaleConstant, 232 * scaleConstant, 326 * scaleConstant, 200 * scaleConstant); }

        shapeRenderer.end();
    }

    private void drawCustomisationScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Navigation buttons
        // Darts
        spriteBatch.draw(leftButton, 530 * scaleConstant, 1060 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        spriteBatch.draw(rightButton, 620 * scaleConstant, 1060 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        // Location
        spriteBatch.draw(leftButton, 530 * scaleConstant, 660 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        spriteBatch.draw(rightButton, 620 * scaleConstant, 660 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Title text
        menuTitleFont.draw(spriteBatch, menuText[45], menuTextIndent[45] * scaleConstant, screenHeight / 32 * 31);

        // Sub heading text
        menuButtonFont.draw(spriteBatch, menuText[46], 30 * scaleConstant, screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, menuText[47], 30 * scaleConstant, screenHeight / 32 * 18);

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, 530 * scaleConstant, 1060 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButtonSmall, 620 * scaleConstant, 1060 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[3]) { spriteBatch.draw(selectedButtonSmall, 530 * scaleConstant, 660 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[4]) { spriteBatch.draw(selectedButtonSmall, 620 * scaleConstant, 660 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }

        spriteBatch.end();
    }

    private void drawSettingsScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Title text
        menuTitleFont.draw(spriteBatch, menuText[48], menuTextIndent[48] * scaleConstant, screenHeight / 32 * 31);
        // Sub heading text
        menuButtonFont.draw(spriteBatch, menuText[52] + ": " + (int) ((GameScreen.sensitivityZ) * 100) + "%", 30 * scaleConstant, screenHeight / 32 * 28);

        // Slider
        spriteBatch.draw(sliderBar, 60 * scaleConstant, 960 * scaleConstant, 600 * scaleConstant, 75 * scaleConstant);
        spriteBatch.draw(sliderBit, (40 + 6 * (GameScreen.sensitivityZ - 0.5f) * 100) * scaleConstant, 948 * scaleConstant, 40 * scaleConstant, 100 * scaleConstant);

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[1]) { spriteBatch.draw(sliderBitSelected, (40 + 6 * (GameScreen.sensitivityZ - 0.5f) * 100) * scaleConstant, 948 * scaleConstant, 40 * scaleConstant, 100 * scaleConstant); }

        spriteBatch.end();
    }

    private void drawLanguageScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Language Buttons
        spriteBatch.draw(flag1, 120 * scaleConstant, 950 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag2, 400 * scaleConstant, 950 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag3, 120 * scaleConstant, 750 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag4, 400 * scaleConstant, 750 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag5, 120 * scaleConstant, 550 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag6, 400 * scaleConstant, 550 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag7, 120 * scaleConstant, 350 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag8, 400 * scaleConstant, 350 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag9, 120 * scaleConstant, 150 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);
        spriteBatch.draw(flag10, 400 * scaleConstant, 150 * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);

        // Title text
        menuTitleFont.draw(spriteBatch, menuText[49], menuTextIndent[49] * scaleConstant, screenHeight / 32 * 31);

        // Language text
        spriteBatch.draw(langImage, 0, 0, 720 * scaleConstant, 1280 * scaleConstant);

        // Selection Overlay
        spriteBatch.draw(selectedLanguage, (120 + 280 * ((language) % 2)) * scaleConstant, (950 - 200 * ((language) / 2)) * scaleConstant, 200 * scaleConstant, 125 * scaleConstant);

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }

        spriteBatch.end();
    }

    public void drawTutorialScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Title text
        menuTitleFont.draw(spriteBatch, menuText[50], menuTextIndent[50] * scaleConstant, screenHeight / 32 * 31);

        // Info text
        for(int i = 116; i < 136; i++) {
            menuDescriptionFont.draw(spriteBatch, menuText[i], 30 * scaleConstant, (1120 - 30 * (i - 117)) * scaleConstant);
        }

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }

        spriteBatch.end();
    }

    private void drawMultiplayerTargetedConnectScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Begin game button
        spriteBatch.draw(defaultButton, screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2, 20 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant);
        // Back button
        spriteBatch.draw(backButton, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        spriteBatch.draw(helpButton, 110 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        // Confirm buttons
        spriteBatch.draw(connectButton, 605 * scaleConstant, 975 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        spriteBatch.draw(connectButton, 605 * scaleConstant, 735 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);


        // Title text
        menuTitleFont.draw(spriteBatch, menuText[17], menuTextIndent[17] * scaleConstant, screenHeight / 32 * 31);
        // Button text
        if(showConnecting) {
            menuButtonFont.draw(spriteBatch, menuText[44], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[44] * scaleConstant, 20 * scaleConstant + 96 * scaleConstant);
        } else{
            menuButtonFontBold.draw(spriteBatch, menuText[23], screenWidth / 2 - (defaultButton.getWidth() * scaleConstant) / 2 + menuTextIndent[23] * scaleConstant, 20 * scaleConstant + 96 * scaleConstant);
        }
        // Sub heading text
        menuButtonFont.draw(spriteBatch, menuText[26], 30 * scaleConstant, screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, menuText[27], 30 * scaleConstant, screenHeight / 32 * 22);

        // Status text (informs user whether username is taken or whether opponent exists)
        if(usernameChecked) {
            if(usernameAvailable) {
                menuStatusFontGreen.draw(spriteBatch, menuText[32], 30 * scaleConstant, 970 * scaleConstant);
            } else{
                menuStatusFontRed.draw(spriteBatch, menuText[33], 30 * scaleConstant, 970 * scaleConstant);
            }
        }

        if(opponentChecked) {
            if(opponentAvailable) {
                menuStatusFontGreen.draw(spriteBatch, menuText[33], 30 * scaleConstant, 730 * scaleConstant);
            } else{
                menuStatusFontRed.draw(spriteBatch, menuText[34], 30 * scaleConstant, 730 * scaleConstant);
            }
        }

        if(showHelpMultiplayer) {
            // Information text
            menuDescriptionFont.draw(spriteBatch, menuText[36], 30 * scaleConstant, 650 * scaleConstant);
            menuDescriptionFont.draw(spriteBatch, menuText[37], 30 * scaleConstant, 610 * scaleConstant);
            menuDescriptionFont.draw(spriteBatch, menuText[38], 30 * scaleConstant, 570 * scaleConstant);
            menuDescriptionFont.draw(spriteBatch, menuText[39], 30 * scaleConstant, 530 * scaleConstant);
            menuDescriptionFont.draw(spriteBatch, menuText[40], 30 * scaleConstant, 490 * scaleConstant);
            menuDescriptionFont.draw(spriteBatch, menuText[41], 30 * scaleConstant, 450 * scaleConstant);
            menuDescriptionFont.draw(spriteBatch, menuText[42], 30 * scaleConstant, 410 * scaleConstant);
            menuDescriptionFont.draw(spriteBatch, menuText[43], 30 * scaleConstant, 370 * scaleConstant);
        }

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, 110 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButton, 210 * scaleConstant, 20 * scaleConstant, 300 * scaleConstant, 150 * scaleConstant); }

        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Temporary Username Box
        shapeRenderer.setColor(new Color(0, 0, 0.5f, 1.0f));
        shapeRenderer.rect(30 * scaleConstant, 975 * scaleConstant, 550 * scaleConstant, 80 * scaleConstant);
        shapeRenderer.rect(30 * scaleConstant, 735 * scaleConstant, 550 * scaleConstant, 80 * scaleConstant);
        shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        shapeRenderer.rect(34 * scaleConstant, 979 * scaleConstant, 542 * scaleConstant, 72 * scaleConstant);
        shapeRenderer.rect(34 * scaleConstant, 739 * scaleConstant, 542 * scaleConstant, 72 * scaleConstant);

        shapeRenderer.end();

        spriteBatch.begin();

        // Input text
        menuInputFont.draw(spriteBatch, tempUsername, 40 * scaleConstant, 1035 * scaleConstant);
        menuInputFont.draw(spriteBatch, opponentUsername, 40 * scaleConstant, 795 * scaleConstant);

        spriteBatch.end();
    }

    public void drawGamemodeInfoScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Navigation Buttons
        spriteBatch.draw(leftButton, 520 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);
        spriteBatch.draw(rightButton, 620 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant);

        // Title text
        menuTitleFont.draw(spriteBatch, menuText[51], menuTextIndent[51] * scaleConstant, screenHeight / 32 * 31);

        // Info text
        int firstLine = 0;
        int totalLines = 0;

        switch(gameInfoScreen) {
            case 1:
                menuButtonFont.draw(spriteBatch, menuText[10], 30 * scaleConstant, screenHeight / 32 * 28);
                firstLine = 53;
                totalLines = 22;
                break;

            case 2:
                menuButtonFont.draw(spriteBatch, menuText[11] + " " + menuText[12], 30 * scaleConstant, screenHeight / 32 * 28);
                firstLine = 75;
                totalLines = 9;
                break;

            case 3:
                menuButtonFont.draw(spriteBatch, menuText[13], 30 * scaleConstant, screenHeight / 32 * 28);
                firstLine = 84;
                totalLines = 17;
                break;

            case 4:
                menuButtonFont.draw(spriteBatch, menuText[14] + " " + menuText[15], 30 * scaleConstant, screenHeight / 32 * 28);
                firstLine = 101;
                totalLines = 15;
                break;
        }

        // Draw text to screen
        for(int i = firstLine; i < firstLine + totalLines; i++) {
            menuDescriptionFont.draw(spriteBatch, menuText[i], 30 * scaleConstant, (1060 - 30 * (i - firstLine)) * scaleConstant);
        }

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, 20 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, 520 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButtonSmall, 620 * scaleConstant, 20 * scaleConstant, 80 * scaleConstant, 80 * scaleConstant); }

        spriteBatch.end();
    }
}
