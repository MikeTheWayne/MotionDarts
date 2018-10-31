package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    private MotionDarts game;

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

    private BitmapFont summaryFont;
    private BitmapFont summaryTextFont;
    private BitmapFont[] summaryFontColored;

    private BitmapFont customFont;

    private FreeTypeFontGenerator freeTypeFontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter;
    private ShapeRenderer shapeRenderer;

    private ModelInstance[] dartModelInstances = new ModelInstance[3];
    private ModelInstance dartboardModelInst1;
    private ModelInstance environmentModelInst1;

    private Sprite defaultButton, settingsButton, languageButton, exitButton, title, submenuBackground,
            backButton, tickButton, connectButton, helpButton, flag1, flag2, flag3, flag4, flag5, flag6,
            flag7, flag8, flag9, flag10, selectedButton, selectedButtonSmall, selectedLanguage, langImage,
            sliderBar, sliderBit, sliderBitSelected, leftButton, rightButton, upButton, downButton,
            customisationOption, customisationOptionSelected, dart1Icon, dart2Icon, dart3Icon, dart4Icon,
            dart5Icon, dart6Icon, env1Icon, env2Icon, env3Icon, soonIcon, anim1_1, anim1_2, anim1_3, anim1_4,
            anim1_5, anim1_6, anim2_1, anim2_2, anim2_3, anim2_4, anim2_5, support, locked;

    private static Array<ModelInstance> instances = new Array<ModelInstance>();

    private int screenWidth;
    private int screenHeight;

    static int language = 0;

    static String[] text;
    static int[] textIndent;

    // 1 = Main Menu, 2 = Game Setup Screen, 3 = Multiplayer Setup Screen, 4 = Customisation Menu, 5 = Settings Menu, 6 = Language Menu, 7 = Tutorial Screen, 8 = Summary Screen, 9 = Server Connection Screen, 10 = Game Mode Info Screen
    static int menuScreen = 1;
    private int prevScreen = 0;
    private int gameInfoScreen = 1;

    private double scaleConstant;

    static int selectedOpposition = 2;
    static int selectedGameMode = 1;
    static boolean selectedPracticeMode = false;
    private boolean selectedSpecificPlayer = false;

    private String tempUsername = "";
    private String opponentUsername = "";

    static boolean usernameAvailable = false;
    static boolean opponentAvailable = false;

    static boolean usernameChecked = false;
    static boolean opponentChecked = false;

    static boolean showWaiting = false;
    static boolean startGame = false;
    static boolean beginGame = false;
    static boolean beginLoop = false;
    static int onlinePlayer = 0;
    static String opponentName = "";

    private boolean showHelpMultiplayer = false;

    static boolean connectionFailed = false;
    static int connectionFailReason = 0;

    private boolean[] buttonDown = new boolean[10];

    private int summaryDisplayScore = 0;

    /* Animation Variables */
    private int timeCounter = 0;
    private int dart = 0;

    private float[] dartY = new float[3];
    private float[] dartZ = new float[3];
    private float[] rot = new float[3];

    private int throwTime = 0;


    private int anim1Stage = 0;
    private int anim2Stage = 0;

    private int animCounter = 0;

    /* Customisation menu variables */
    private int customisationDartsPage = 0;
    private int customisationLocationPage = 0;

    private int totalDartsPages = 2;
    private int totalLocationPages = 2;


    static boolean sound = true;
    static boolean music = false;

    private boolean ad = true;

    static boolean lachie = false;
    private int lachieCounter = 0;

    MenuScreen(final MotionDarts game) {
        this.game = game;

        GameScreen.selectedLocation = GameScreen.tempSelectedLocation;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        scaleConstant = screenWidth / 720.0;

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
        Model dartModel1 = MotionDarts.assetManager.get("models/" + GameScreen.dartFiles[GameScreen.selectedDart] + ".g3db", Model.class);
        Model dartboardModel1 = MotionDarts.assetManager.get("models/dartboard_01.g3db", Model.class);
        Model environmentModel1 = MotionDarts.assetManager.get("models/environment_01.g3db", Model.class);
        Model spaceSurroundModel = MotionDarts.assetManager.get("models/menu_spacesurround.g3db", Model.class);

        // Assign Models to ModelInstances
        dartModelInstances[0] = new ModelInstance(dartModel1);
        dartModelInstances[1] = new ModelInstance(dartModel1);
        dartModelInstances[2] = new ModelInstance(dartModel1);
        dartboardModelInst1 = new ModelInstance(dartboardModel1);
        environmentModelInst1 = new ModelInstance(environmentModel1);
        spaceSurroundModelInst = new ModelInstance(spaceSurroundModel);

        instances.add(dartboardModelInst1);
        instances.add(spaceSurroundModelInst);
        instances.add(dartModelInstances[0]);
        instances.add(dartModelInstances[1]);
        instances.add(dartModelInstances[2]);

        for(int i = 0; i < 3; i++) {
            dartModelInstances[i].transform.setToTranslation(4100.0f, -10000.0f, -19000.0f);
            dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 225);
            dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
        }

        dartboardModelInst1.transform.setToTranslation(0.0f, 0.0f, 0.0f);
        dartboardModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 90);
        dartboardModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 180);

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
        Texture upButtonTexture = MotionDarts.assetManager.get("textures/upButton.png", Texture.class);
        Texture downButtonTexture = MotionDarts.assetManager.get("textures/downButton.png", Texture.class);
        Texture customisationOptionTexture = MotionDarts.assetManager.get("textures/customisationOption.png", Texture.class);
        Texture customisationOptionSelectedTexture = MotionDarts.assetManager.get("textures/customisationOptionSelected.png", Texture.class);
        Texture dart1IconTexture = MotionDarts.assetManager.get("textures/dart1_icon.png", Texture.class);
        Texture dart2IconTexture = MotionDarts.assetManager.get("textures/dart2_icon.png", Texture.class);
        Texture dart3IconTexture = MotionDarts.assetManager.get("textures/dart3_icon.png", Texture.class);
        Texture dart4IconTexture = MotionDarts.assetManager.get("textures/dart4_icon.png", Texture.class);
        Texture dart5IconTexture = MotionDarts.assetManager.get("textures/dart5_icon.png", Texture.class);
        Texture dart6IconTexture = MotionDarts.assetManager.get("textures/dart6_icon.png", Texture.class);
        Texture env1IconTexture = MotionDarts.assetManager.get("textures/env1_icon.png", Texture.class);
        Texture env2IconTexture = MotionDarts.assetManager.get("textures/env2_icon.png", Texture.class);
        Texture env3IconTexture = MotionDarts.assetManager.get("textures/env3_icon.png", Texture.class);
        Texture soonIconTexture = MotionDarts.assetManager.get("textures/comingsoon_icon.png", Texture.class);
        Texture anim1_1Texture = MotionDarts.assetManager.get("textures/anim1_01.png", Texture.class);
        Texture anim1_2Texture = MotionDarts.assetManager.get("textures/anim1_02.png", Texture.class);
        Texture anim1_3Texture = MotionDarts.assetManager.get("textures/anim1_03.png", Texture.class);
        Texture anim1_4Texture = MotionDarts.assetManager.get("textures/anim1_04.png", Texture.class);
        Texture anim1_5Texture = MotionDarts.assetManager.get("textures/anim1_05.png", Texture.class);
        Texture anim1_6Texture = MotionDarts.assetManager.get("textures/anim1_06.png", Texture.class);
        Texture anim2_1Texture = MotionDarts.assetManager.get("textures/anim2_01.png", Texture.class);
        Texture anim2_2Texture = MotionDarts.assetManager.get("textures/anim2_02.png", Texture.class);
        Texture anim2_3Texture = MotionDarts.assetManager.get("textures/anim2_03.png", Texture.class);
        Texture anim2_4Texture = MotionDarts.assetManager.get("textures/anim2_04.png", Texture.class);
        Texture anim2_5Texture = MotionDarts.assetManager.get("textures/anim2_05.png", Texture.class);
        Texture supportTexture = MotionDarts.assetManager.get("textures/supportButton.png", Texture.class);
        Texture lockedTexture = MotionDarts.assetManager.get("textures/locked.png", Texture.class);

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
        upButton = new Sprite(upButtonTexture);
        downButton = new Sprite(downButtonTexture);
        customisationOption = new Sprite(customisationOptionTexture);
        customisationOptionSelected = new Sprite(customisationOptionSelectedTexture);
        dart1Icon = new Sprite(dart1IconTexture);
        dart2Icon = new Sprite(dart2IconTexture);
        dart3Icon = new Sprite(dart3IconTexture);
        dart4Icon = new Sprite(dart4IconTexture);
        dart5Icon = new Sprite(dart5IconTexture);
        dart6Icon = new Sprite(dart6IconTexture);
        env1Icon = new Sprite(env1IconTexture);
        env2Icon = new Sprite(env2IconTexture);
        env3Icon = new Sprite(env3IconTexture);
        soonIcon = new Sprite(soonIconTexture);
        anim1_1 = new Sprite(anim1_1Texture);
        anim1_2 = new Sprite(anim1_2Texture);
        anim1_3 = new Sprite(anim1_3Texture);
        anim1_4 = new Sprite(anim1_4Texture);
        anim1_5 = new Sprite(anim1_5Texture);
        anim1_6 = new Sprite(anim1_6Texture);
        anim2_1 = new Sprite(anim2_1Texture);
        anim2_2 = new Sprite(anim2_2Texture);
        anim2_3 = new Sprite(anim2_3Texture);
        anim2_4 = new Sprite(anim2_4Texture);
        anim2_5 = new Sprite(anim2_5Texture);
        support = new Sprite(supportTexture);
        locked = new Sprite(lockedTexture);

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
                        if(touchX > (int) (35 * scaleConstant) && touchX < (int) (185 * scaleConstant) && touchY > (screenHeight - (int) (300 * scaleConstant)) && touchY < (screenHeight - (int) (150 * scaleConstant))) {
                            if(MotionDarts.FREE_VERSION) {
                                Gdx.net.openURI("http://www.waynegames.com/");
                            }
                        }
                        break;

                    case 2:
                        if(touchY < (screenHeight - (int) (975 * scaleConstant)) && touchY > (screenHeight - (int) (1055 * scaleConstant))) {
                            if(touchX > (int) (30 * scaleConstant) && touchX < (int) (248 * scaleConstant)) {
                                selectedOpposition = 1;
                            } else if(touchX >= (int) (248 * scaleConstant) && touchX < (int) (468 * scaleConstant)) {
                                selectedOpposition = 2;
                            } else if(touchX >= (int) (468 * scaleConstant) && touchX < (int) (690 * scaleConstant)) {
                                selectedOpposition = 3;
                            }
                        }

                        if(touchX > (int) (30 * scaleConstant) && touchX < (int) (360 * scaleConstant)) {
                            if(touchY < (screenHeight - (int) (410 * scaleConstant)) && touchY >= (screenHeight - (int) (612 * scaleConstant))) {
                                selectedGameMode = 3;
                            } else if(touchY < (screenHeight - (int) (612 * scaleConstant)) && touchY > (screenHeight - (int) (814 * scaleConstant))) {
                                selectedGameMode = 1;
                            }
                        } else if(touchX >= (int) (360 * scaleConstant) && touchX < (int) (690 * scaleConstant)) {
                            if(touchY < (screenHeight - (int) (410 * scaleConstant)) && touchY >= (screenHeight - (int) (612 * scaleConstant))) {
                                selectedGameMode = 4;
                            } else if(touchY < (screenHeight - (int) (612 * scaleConstant)) && touchY > (screenHeight - (int) (814 * scaleConstant))) {
                                selectedGameMode = 2;
                            }
                        }

                        break;

                    case 3:
                        if(touchX > (int) (30 * scaleConstant) && touchX < (int) (690 * scaleConstant)) {
                            if(touchY > (screenHeight - (int) (1055 * scaleConstant)) && touchY <= (screenHeight - (int) (955 * scaleConstant))) {
                                selectedOpposition = 4;
                                selectedSpecificPlayer = false;
                                if(showWaiting) {
                                    ServerComms.disconnectFromServer();
                                    showWaiting = false;
                                }
                            } else if(touchY > (screenHeight - (int) (955 * scaleConstant)) && touchY <= (screenHeight - (int) (855 * scaleConstant))) {
                                selectedOpposition = 5;
                                selectedSpecificPlayer = false;
                            } else if(touchY > (screenHeight - (int) (855 * scaleConstant)) && touchY < (screenHeight - (int) (755 * scaleConstant))) {
                                selectedOpposition = 5;
                                selectedSpecificPlayer = true;
                                if(showWaiting) {
                                    ServerComms.disconnectFromServer();
                                    showWaiting = false;
                                }
                            }
                        }

                        if(touchX > (int) (30 * scaleConstant) && touchX < (int) (360 * scaleConstant)) {
                            if(touchY < (screenHeight - (int) (230 * scaleConstant)) && touchY >= (screenHeight - (int) (432 * scaleConstant))) {
                                if(showWaiting && selectedGameMode != 3) {
                                    ServerComms.disconnectFromServer();
                                    showWaiting = false;
                                }
                                selectedGameMode = 3;
                            } else if(touchY < (screenHeight - (int) (432 * scaleConstant)) && touchY > (screenHeight - (int) (634 * scaleConstant))) {
                                if(showWaiting && selectedGameMode != 1) {
                                    ServerComms.disconnectFromServer();
                                    showWaiting = false;
                                }
                                selectedGameMode = 1;
                            }
                        } else if(touchX >= (int) (360 * scaleConstant) && touchX < (int) (690 * scaleConstant)) {
                            if(touchY < (screenHeight - (int) (230 * scaleConstant)) && touchY >= (screenHeight - (int) (432 * scaleConstant))) {
                                if(showWaiting && selectedGameMode != 4) {
                                    ServerComms.disconnectFromServer();
                                    showWaiting = false;
                                }
                                selectedGameMode = 4;
                            } else if(touchY < (screenHeight - (int) (432 * scaleConstant)) && touchY > (screenHeight - (int) (634 * scaleConstant))) {
                                if(showWaiting && selectedGameMode != 2) {
                                    ServerComms.disconnectFromServer();
                                    showWaiting = false;
                                }
                                selectedGameMode = 2;
                            }
                        }

                        break;

                    case 4:

                        for(int i = 0; i < 3; i++) {
                            if(touchY <= screenHeight - (int) (740 * scaleConstant) && touchY >= screenHeight - (int) (1040 * scaleConstant)) {
                                if(touchX >= (20 + i * 230) * scaleConstant && touchX <= (240 + i * 230) * scaleConstant) {

                                    if(i + 1 + customisationDartsPage * 3 <= ((MotionDarts.FREE_VERSION) ? 3 : GameScreen.dartFiles.length)) {
                                        GameScreen.selectedDart = customisationDartsPage * 3 + i;

                                        Model dartModel1 = MotionDarts.assetManager.get("models/" + GameScreen.dartFiles[GameScreen.selectedDart] + ".g3db", Model.class);

                                        // Replace menu background darts with current selection
                                        instances.removeIndex(2);
                                        instances.removeIndex(2);
                                        instances.removeIndex(2);

                                        dartModelInstances[0] = new ModelInstance(dartModel1);
                                        dartModelInstances[1] = new ModelInstance(dartModel1);
                                        dartModelInstances[2] = new ModelInstance(dartModel1);

                                        instances.add(dartModelInstances[0]);
                                        instances.add(dartModelInstances[1]);
                                        instances.add(dartModelInstances[2]);
                                    }

                                }
                            } else if(touchY <= screenHeight - (int) (260 * scaleConstant) && touchY >= screenHeight - (int) (560 * scaleConstant)) {
                                if(touchX >= (20 + i * 230) * scaleConstant && touchX <= (240 + i * 230) * scaleConstant) {

                                    if(i + 1 + customisationLocationPage * 3 <= GameScreen.locationFiles.length) {
                                        GameScreen.selectedLocation = customisationLocationPage * 3 + i;
                                        GameScreen.tempSelectedLocation = GameScreen.selectedLocation;
                                    }

                                }
                            }
                        }

                        break;

                    case 6:
                        if(touchX > (int) (120 * scaleConstant) && touchX < (int) (320 * scaleConstant)) {
                            if(touchY < (screenHeight - (int) (950 * scaleConstant)) && touchY > (screenHeight - (int) (1075 * scaleConstant))) {
                                language = 0;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            } else if(touchY < (screenHeight - (int) (750 * scaleConstant)) && touchY > (screenHeight - (int) (875 * scaleConstant))) {
                                language = 2;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            } else if(touchY < (screenHeight - (int) (550 * scaleConstant)) && touchY > (screenHeight - (int) (675 * scaleConstant))) {
                                language = 4;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            } else if(touchY < (screenHeight - (int) (350 * scaleConstant)) && touchY > (screenHeight - (int) (475 * scaleConstant))) {
                                language = 6;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            } else if(touchY < (screenHeight - (int) (150 * scaleConstant)) && touchY > (screenHeight - (int) (275 * scaleConstant))) {
                                language = 8;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            }
                        } else if(touchX > (int) (400 * scaleConstant) && touchX < (int) (600 * scaleConstant)) {
                            if(touchY < (screenHeight - (int) (950 * scaleConstant)) && touchY > (screenHeight - (int) (1075 * scaleConstant))) {
                                language = 1;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            } else if(touchY < (screenHeight - (int) (750 * scaleConstant)) && touchY > (screenHeight - (int) (875 * scaleConstant))) {
                                language = 3;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            } else if(touchY < (screenHeight - (int) (550 * scaleConstant)) && touchY > (screenHeight - (int) (675 * scaleConstant))) {
                                language = 5;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            } else if(touchY < (screenHeight - (int) (350 * scaleConstant)) && touchY > (screenHeight - (int) (475 * scaleConstant))) {
                                language = 7;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
                            } else if(touchY < (screenHeight - (int) (150 * scaleConstant)) && touchY > (screenHeight - (int) (275 * scaleConstant))) {
                                language = 9;
                                MotionDarts.loadLanguage(language);
                                setupFonts();
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
                        if (touchX >= screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchX <= screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (300 * scaleConstant)) {
                            if (touchY >= screenHeight / 16 * 7 && touchY <= screenHeight / 16 * 7 + (int) (150 * scaleConstant)) {
                                menuScreen = 2;
                                selectedOpposition = 2;
                            } else if (touchY >= screenHeight / 16 * 7 + (int) (200 * scaleConstant) && touchY <= screenHeight / 16 * 7 + (int) (350 * scaleConstant)) {
                                menuScreen = 3;
                                selectedOpposition = 4;
                            } else if (touchY >= screenHeight / 16 * 7 + (int) (400 * scaleConstant) && touchY <= screenHeight / 16 * 7 + (int) (550 * scaleConstant)) {
                                menuScreen = 4;
                            }
                        }

                        if (touchY >= screenHeight - (int) (90 * scaleConstant) && touchY <= screenHeight - (int) (10 * scaleConstant)) {
                            if (touchX >= (int) (10 * scaleConstant) && touchX <= (int) (90 * scaleConstant)) {
                                menuScreen = 5;
                            } else if (touchX >= (int) (100 * scaleConstant) && touchX <= (int) (180 * scaleConstant)) {
                                menuScreen = 6;
                            } else if (touchX >= (int) (190 * scaleConstant) && touchX <= (int) (270 * scaleConstant)) {
                                menuScreen = 7;
                            } else if (touchX >= screenWidth - (int) (90 * scaleConstant) && touchX <= screenWidth - (int) (10 * scaleConstant)) {
                                dispose();
                                Gdx.app.exit();
                            }
                        }
                        break;

                    case 2:     // Single player Game setup
                        if(touchX > screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchX <  screenWidth / 2 + ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            beginGame = true;
                        }

                        if(touchX > (int) (20 * scaleConstant) && touchX < (int) (100 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            menuScreen = 1;
                        }

                        if(touchX > (int) (620 * scaleConstant) && touchX < (int) (700 * scaleConstant) && touchY < screenHeight - (int) (830 * scaleConstant) && touchY >= screenHeight - (int) (910 * scaleConstant)) {
                            menuScreen = 10;
                            prevScreen = 2;
                        }

                        // Practice mode selection button
                        if(touchX > (int) (30 * scaleConstant) && touchX < (int) (110 * scaleConstant) && touchY < (screenHeight - (int) (300 * scaleConstant)) && touchY > (screenHeight - (int) (380 * scaleConstant))) {
                            selectedPracticeMode = !selectedPracticeMode;
                        }
                        break;

                    case 3:     // Multi Player Game setup

                        if(touchX > screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchX <  screenWidth / 2 + ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {

                            if(selectedSpecificPlayer) {
                                // Connect to server
                                ServerComms.connectToServer();
                                ServerComms.sendToServer(String.valueOf(1));
                                ServerComms.sendToServer(String.valueOf(GameScreen.selectedDart));
                                ServerComms.sendToServer(String.valueOf(GameScreen.selectedLocation));
                                if(!connectionFailed) {
                                    menuScreen = 9;
                                    usernameChecked = false;
                                    opponentChecked = false;
                                }
                            } else if(selectedOpposition == 5) {
                                if(!showWaiting) {
                                    // Connect to server
                                    ServerComms.connectToServer();
                                    ServerComms.sendToServer(String.valueOf(0));
                                    ServerComms.sendToServer(String.valueOf(selectedGameMode));
                                    ServerComms.sendToServer(String.valueOf(GameScreen.selectedDart));
                                    ServerComms.sendToServer(String.valueOf(GameScreen.selectedLocation));
                                }
                            } else {
                                // Begin Game
                                beginGame = true;
                            }

                        } else if(touchX > (int) (20 * scaleConstant) && touchX < (int) (100 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            menuScreen = 1;
                            connectionFailed = false;
                            showWaiting = false;
                            ServerComms.disconnectFromServer();
                        }

                        if(touchX > (int) (620 * scaleConstant) && touchX < (int) (700 * scaleConstant) && touchY < screenHeight - (int) (650 * scaleConstant) && touchY >= screenHeight - (int) (730 * scaleConstant)) {
                            menuScreen = 10;
                            prevScreen = 3;
                        }
                        break;

                    case 4:

                        if(touchX > (int) (20 * scaleConstant) && touchX < (int) (100 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            menuScreen = 1;

                            // Write selection to text File
                            FileHandle custFile = Gdx.files.local("customisationSave.txt");
                            custFile.writeString("" + GameScreen.selectedDart + "\n", false);
                            custFile.writeString("" + GameScreen.selectedLocation, true);
                        }

                        if(touchY >= screenHeight - (int) (1160 * scaleConstant) && touchY <= screenHeight - (int) (1080 * scaleConstant)) {
                            if(touchX >= (int) (530 * scaleConstant) && touchX <= (int) (610 * scaleConstant) && customisationDartsPage > 0) {
                                customisationDartsPage--;
                            } else if(touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant) && customisationDartsPage < totalDartsPages - 1) {
                                customisationDartsPage++;
                            }
                        } else if(touchY >= screenHeight - (int) (660 * scaleConstant) && touchY <= screenHeight - (int) (580 * scaleConstant)) {
                            if(touchX >= (int) (530 * scaleConstant) && touchX <= (int) (610 * scaleConstant) && customisationLocationPage > 0) {
                                customisationLocationPage--;
                            } else if(touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant) && customisationLocationPage < totalLocationPages - 1) {
                                customisationLocationPage++;
                            }
                        }

                        break;

                    case 5:

                        if(touchX > (int) (20 * scaleConstant) && touchX < (int) (100 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            menuScreen = 1;

                            // Write settings to text File
                            FileHandle setFile = Gdx.files.local("settingsSave.txt");
                            setFile.writeString("" + GameScreen.sensitivityZ + "\n", false);
                            setFile.writeString("" + sound + "\n", true);
                            setFile.writeString("" + music, true);
                        }

                        // Sound selection button
                        if(touchX > (int) (40 * scaleConstant) && touchX < (int) (120 * scaleConstant) && touchY < (screenHeight - (int) (820 * scaleConstant)) && touchY > (screenHeight - (int) (900 * scaleConstant))) {
                            sound = !sound;
                            if(lachie) {
                                lachie = false;
                            }
                            lachieCounter++;
                            if(lachieCounter >= 10) {
                                lachie = true;
                                lachieCounter = 0;
                            } else{
                                lachie = false;
                            }
                        }

                        // Music selection button
                        if(touchX > (int) (40 * scaleConstant) && touchX < (int) (120 * scaleConstant) && touchY < (screenHeight - (int) (700 * scaleConstant)) && touchY > (screenHeight - (int) (780 * scaleConstant))) {
                            music = !music;
                        }

                        break;

                    case 6:

                        if(touchX > (int) (20 * scaleConstant) && touchX < (int) (100 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            menuScreen = 1;
                        }

                        break;

                    case 7:

                        if(touchX > (int) (20 * scaleConstant) && touchX < (int) (100 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            menuScreen = 1;
                        }

                        break;

                    case 8:

                        if(touchY > screenHeight - (int) (170 * scaleConstant) && touchY < screenHeight - (int) (20 * scaleConstant)) {
                            if(touchX > (int) (210 * scaleConstant) && touchX < (int) (510 * scaleConstant)) {
                                menuScreen = 1;
                                ad = true;
                            } /*else if(touchX > (int) (380 * scaleConstant) && touchX < (int) (680 * scaleConstant)) {
                                // Reload same game
                                selectedGameMode = GameScreen.gameClass.getGameMode();
                                selectedOpposition = GameScreen.gameClass.getCompetitionType();
                                loadGameScreen();
                            }*/
                        }

                        if(touchX > (int) (620 * scaleConstant) && touchX < (int) (700 * scaleConstant)) {
                            if(touchY > screenHeight - (int) (770 * scaleConstant) && touchY < screenHeight - (int) (690 * scaleConstant)) {
                                if(summaryDisplayScore > 0) {
                                    summaryDisplayScore -= 10;
                                }
                            } else if(touchY > screenHeight - (int) (680 * scaleConstant) && touchY < screenHeight - (int) (600 * scaleConstant)) {
                                if(summaryDisplayScore < 90) {
                                    summaryDisplayScore += 10;
                                }
                            }
                        }

                        break;

                    case 9:     // MultiPlayer Server Connect Screen

                        if(touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            if(touchX > (int) (20 * scaleConstant) && touchX < (int) (100 * scaleConstant)) {
                                menuScreen = 3;
                                connectionFailed = false;
                                showWaiting = false;
                                ServerComms.sendToServer("17");
                                ServerComms.disconnectFromServer();
                            } else if(touchX > (int) (110 * scaleConstant) && touchX < (int) (190 * scaleConstant)) {
                                showHelpMultiplayer = !showHelpMultiplayer;
                            }
                        }

                        if(touchX > screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchX <  screenWidth / 2 + ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            // Check that everything's been entered correctly
                            if(!showWaiting) {
                                // Wait for other user
                                // Start Game
                                ServerComms.sendToServer(String.valueOf(10));
                                ServerComms.sendToServer(tempUsername);
                                ServerComms.sendToServer(opponentUsername);
                                ServerComms.sendToServer(String.valueOf(selectedGameMode));
                            }
                        }

                        if(touchX > (int) (30 * scaleConstant) && touchX < (int) (580 * scaleConstant)) {
                            if(touchY < (screenHeight - (int) (975 * scaleConstant)) && touchY > (screenHeight - (int) (1055 * scaleConstant)) && !showWaiting) {
                                usernameChecked = false;

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
                                        freeTypeFontParameter.size = (int) (55 * scaleConstant);
                                        freeTypeFontParameter.borderWidth = 0;
                                        menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                                    }

                                    @Override
                                    public void canceled() {

                                    }
                                }, text[29], tempUsername, text[28]);

                            } else if(touchY < (screenHeight - (int) (735 * scaleConstant)) && touchY > (screenHeight - (int) (815 * scaleConstant)) && !showWaiting) {
                                opponentChecked = false;

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
                                        freeTypeFontParameter.size = (int) (55 * scaleConstant);
                                        freeTypeFontParameter.borderWidth = 0;
                                        menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                                    }

                                    @Override
                                    public void canceled() {

                                    }
                                }, text[30], opponentUsername, text[31]);

                            }
                        }

                        if(touchX > 605 * scaleConstant && touchX < 685 * scaleConstant) {
                            if(touchY < (screenHeight - (int) (975 * scaleConstant)) && touchY > (screenHeight - (int) (1055 * scaleConstant)) && !showWaiting) {
                                // Run check in server to see if username is in use
                                ServerComms.sendToServer(String.valueOf(11));
                                ServerComms.sendToServer(String.valueOf(tempUsername));
                            } else if(touchY < (screenHeight - (int) (735 * scaleConstant)) && touchY > (screenHeight - (int) (815 * scaleConstant)) && !showWaiting) {
                                // Run check in server to see if target exists
                                ServerComms.sendToServer(String.valueOf(12));
                                ServerComms.sendToServer(String.valueOf(opponentUsername));
                            }
                        }
                        break;

                    case 10:

                        if(touchX > (int) (20 * scaleConstant) && touchX < (int) (100 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            menuScreen = prevScreen;
                        } else if(touchX > (int) (620 * scaleConstant) && touchX < (int) (700 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            // Right Button
                            if(gameInfoScreen < 4) {
                                gameInfoScreen++;
                            }
                        } else if(touchX > (int) (520 * scaleConstant) && touchX < (int) (600 * scaleConstant) && touchY > screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant)) {
                            // Left Button
                            if(gameInfoScreen > 1) {
                                gameInfoScreen--;
                            }
                        }

                        break;
                }
                return true;
            }

            @Override
            public boolean keyDown(int keyCode) {

                if(keyCode == Input.Keys.BACK) {
                    dispose();
                }

                return false;
            }

        };

        Gdx.input.setInputProcessor(inputAdapter);
	}

	@Override
	public void render (float delta) {
        // Sets Viewport
        Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
        // Clears Screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | ((Gdx.graphics.getBufferFormat().coverageSampling) ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

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

            case 8:
                drawSummaryScreen();
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
                    buttonDown[0] = (touchY >= screenHeight / 16 * 7 && touchY <= screenHeight / 16 * 7 + (int) (150 * scaleConstant)) && (touchX >= screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchX <= screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (300 * scaleConstant));
                    buttonDown[1] = (touchY >= screenHeight / 16 * 7 + (int) (200 * scaleConstant) && touchY <= screenHeight / 16 * 7 + (int) (350 * scaleConstant)) && (touchX >= screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchX <= screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (300 * scaleConstant));
                    buttonDown[2] = (touchY >= screenHeight / 16 * 7 + (int) (400 * scaleConstant) && touchY <= screenHeight / 16 * 7 + (int) (550 * scaleConstant)) && (touchX >= screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 && touchX <= screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (300 * scaleConstant));

                    buttonDown[3] = (touchX >= (int) (10 * scaleConstant) && touchX <= (int) (90 * scaleConstant)) && (touchY >= screenHeight - (int) (90 * scaleConstant) && touchY <= screenHeight - (int) (10 * scaleConstant));
                    buttonDown[4] = (touchX >= (int) (100 * scaleConstant) && touchX <= (int) (180 * scaleConstant)) && (touchY >= screenHeight - (int) (90 * scaleConstant) && touchY <= screenHeight - (int) (10 * scaleConstant));
                    buttonDown[5] = (touchX >= (int) (190 * scaleConstant) && touchX <= (int) (270 * scaleConstant)) && (touchY >= screenHeight - (int) (90 * scaleConstant) && touchY <= screenHeight - (int) (10 * scaleConstant));
                    buttonDown[6] = (touchX >= screenWidth - 90 * scaleConstant && touchX <= screenWidth - 10 * scaleConstant) && (touchY >= screenHeight - (int) (90 * scaleConstant) && touchY <= screenHeight - (int) (10 * scaleConstant));
                    break;

                case 2:
                    buttonDown[0] = (touchX >= (int) (20 * scaleConstant) && touchX <= (int) (100 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    buttonDown[1] = (touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant)) && (touchY >= screenHeight - (int) (910 * scaleConstant) && touchY <= screenHeight - (int) (830 * scaleConstant));
                    buttonDown[2] = (touchX >= (int) (210 * scaleConstant) && touchX <= (int) (510 * scaleConstant)) && (touchY >= screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    break;

                case 3:
                    buttonDown[0] = (touchX >= (int) (20 * scaleConstant) && touchX <= (int) (100 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    buttonDown[1] = (touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant)) && (touchY >= screenHeight - (int) (730 * scaleConstant) && touchY <= screenHeight - (int) (650 * scaleConstant));
                    buttonDown[2] = (touchX >= (int) (210 * scaleConstant) && touchX <= (int) (510 * scaleConstant)) && (touchY >= screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    break;

                case 4:
                    buttonDown[0] = (touchX >= (int) (20 * scaleConstant) && touchX <= (int) (100 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    buttonDown[1] = (touchX >= (int) (530 * scaleConstant) && touchX <= (int) (610 * scaleConstant)) && (touchY >= screenHeight - (int) (1160 * scaleConstant) && touchY <= screenHeight - (int) (1080 * scaleConstant));
                    buttonDown[2] = (touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant)) && (touchY >= screenHeight - (int) (1160 * scaleConstant) && touchY <= screenHeight - (int) (1080 * scaleConstant));
                    buttonDown[3] = (touchX >= (int) (530 * scaleConstant) && touchX <= (int) (610 * scaleConstant)) && (touchY >= screenHeight - (int) (660 * scaleConstant) && touchY <= screenHeight - (int) (580 * scaleConstant));
                    buttonDown[4] = (touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant)) && (touchY >= screenHeight - (int) (660 * scaleConstant) && touchY <= screenHeight - (int) (580 * scaleConstant));
                    break;

                case 5:
                    buttonDown[0] = (touchX >= (int) (20 * scaleConstant) && touchX <= (int) (100 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    buttonDown[1] = (touchY <= screenHeight - (int) (928 * scaleConstant) && touchY >= screenHeight - (int) (1028 * scaleConstant)) && (touchX >= (int) (60 * scaleConstant) && touchX <= (int) (660 * scaleConstant));

                    if(buttonDown[1]) {
                        GameScreen.sensitivityZ = (touchX - 60) / (int) (600 * scaleConstant) + 0.5f;
                    }
                    break;

                case 6:
                    buttonDown[0] = (touchX >= (int) (20 * scaleConstant) && touchX <= (int) (100 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    break;

                case 7:
                    buttonDown[0] = (touchX >= (int) (20 * scaleConstant) && touchX <= (int) (100 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    break;

                case 8:
                    buttonDown[0] = (touchX >= (int) (210 * scaleConstant) && touchX <= (int) (510 * scaleConstant)) && (touchY >= screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    //buttonDown[1] = (touchX >= (int) (380 * scaleConstant) && touchX <= (int) (680 * scaleConstant)) && (touchY >= screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));

                    if(GameScreen.gameClass.getGameMode() <= 2) {
                        buttonDown[2] = (touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant)) && (touchY >= screenHeight - (int) (770 * scaleConstant) && touchY <= screenHeight - (int) (690 * scaleConstant));
                        buttonDown[3] = (touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant)) && (touchY >= screenHeight - (int) (680 * scaleConstant) && touchY <= screenHeight - (int) (600 * scaleConstant));
                    }
                    break;

                case 9:
                    buttonDown[0] = (touchX >= (int) (20 * scaleConstant) && touchX <= (int) (100 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    buttonDown[1] = (touchX >= (int) (110 * scaleConstant) && touchX <= (int) (190 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    buttonDown[2] = (touchX >= (int) (210 * scaleConstant) && touchX <= (int) (510 * scaleConstant)) && (touchY >= screenHeight - (int) (170 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    break;

                case 10:
                    buttonDown[0] = (touchX >= (int) (20 * scaleConstant) && touchX <= (int) (100 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    buttonDown[1] = (touchX >= (int) (520 * scaleConstant) && touchX <= (int) (600 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    buttonDown[2] = (touchX >= (int) (620 * scaleConstant) && touchX <= (int) (700 * scaleConstant)) && (touchY >= screenHeight - (int) (100 * scaleConstant) && touchY <= screenHeight - (int) (20 * scaleConstant));
                    break;
            }
        }

        // Tutorial Animations
        if(menuScreen == 7) {
            final int CURRENT_FPS = (Gdx.graphics.getFramesPerSecond() > 10) ? Gdx.graphics.getFramesPerSecond() : 30;

            animCounter++;

            if(animCounter % ((CURRENT_FPS * 1.5) / 8) < 1) {
                if(anim1Stage < 8) {
                    anim1Stage++;
                } else{
                    anim1Stage = 0;
                }
            }

            if(animCounter % ((CURRENT_FPS * 1.5) / 7) < 1) {
                if(anim2Stage < 7) {
                    anim2Stage++;
                } else{
                    anim2Stage = 0;
                }
            }

            if(animCounter > 2000001) {
                animCounter = 0;
            }

        }

        if(startGame) {
            ad = true;
            loadGameScreen();
            GameScreen.gameClass.scoreSystem.currentPlayer = onlinePlayer;
            GameScreen.gameClass.playerNames[1] = (opponentName.equals("")) ? "PLAYER 2" : opponentName;
            GameScreen.gameClass.playerNames[0] = (tempUsername.equals("")) ? "PLAYER 1" : tempUsername.toUpperCase();
            GameScreen.gameClass.oppTurn = onlinePlayer == 1;
            GameScreen.gameClass.startPlayer = onlinePlayer;
            ServerComms.turnTimer = 15 + onlinePlayer * 5;
            ServerComms.serverTimer();
            showWaiting = false;
            startGame = false;
        }

        if(beginGame) {
            spriteBatch.begin();
            menuButtonFont.draw(spriteBatch, text[169], (int) (260 * scaleConstant), (int) (230 * scaleConstant));
            spriteBatch.end();
            if(beginLoop) {
                beginGame = false;
                beginLoop = false;
                loadGameScreen();
            } else{
                beginLoop = true;
            }
        }
	}

	@Override
	public void dispose () {
        modelBatch.dispose();
        spriteBatch.dispose();
        instances.clear();
        freeTypeFontGenerator.dispose();
        menuButtonFont.dispose();
        menuButtonFontBold.dispose();
        menuTitleFont.dispose();
        menuDescriptionFont.dispose();
        summaryFont.dispose();
        menuInputFont.dispose();
        menuStatusFontGreen.dispose();
        menuStatusFontRed.dispose();
        summaryTextFont.dispose();
        customFont.dispose();
        shapeRenderer.dispose();
	}

	@Override
	public void pause() {

        if(menuScreen == 9) {
            ServerComms.sendToServer("17");
            menuScreen = 3;
        }

        if(showWaiting) {
            ServerComms.disconnectFromServer();
            showWaiting = false;
        }

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
        summaryFontColored = new BitmapFont[8];

        switch(language) {
            case 0:
            case 3:
            case 4:
            case 5:
            case 9:
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfb.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                /* Fonts Setup*/
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 1.0f);
                freeTypeFontParameter.borderWidth = 2;

                // Button and Subheading Font
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                menuButtonFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Description Font
                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                menuDescriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Customisation Screen Font
                freeTypeFontParameter.size = (int) (40 * scaleConstant);
                customFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (red)
                freeTypeFontParameter.color = new Color(1.0f, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (25 * scaleConstant);
                freeTypeFontParameter.borderWidth = 0;
                menuStatusFontRed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (green)
                freeTypeFontParameter.color = new Color(0, 1.0f, 0, 1.0f);
                menuStatusFontGreen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Input Text (for text input boxes)
                freeTypeFontParameter.color = new Color(0, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbbold.ttf"));

                // Button Font (Bold)
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                freeTypeFontParameter.borderWidth = 2;
                menuButtonFontBold = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Title Font
                freeTypeFontParameter.size = (int) (100 * scaleConstant);
                menuTitleFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Summary Font
                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                summaryTextFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                break;
            case 1:     // Bulgarian
            case 2:     // Russian
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbcyrillic.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.characters = "ЕДИНГРАЧМУЛТПЙЪСЩОВК501ЯФЖ2З(WI-F)ШБремнотбилскапшвдяугз,ъщ.3жйх4'чЦцdartboulseyю\"9876фPHONEЮ?ХыёЫЁьЬ%";

                /* Fonts Setup*/
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 1.0f);
                freeTypeFontParameter.borderWidth = 2;

                // Button and Subheading Font
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                menuButtonFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Description Font
                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                menuDescriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Customisation Screen Font
                freeTypeFontParameter.size = (int) (40 * scaleConstant);
                customFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (red)
                freeTypeFontParameter.color = new Color(1.0f, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (25 * scaleConstant);
                freeTypeFontParameter.borderWidth = 0;
                menuStatusFontRed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (green)
                freeTypeFontParameter.color = new Color(0, 1.0f, 0, 1.0f);
                menuStatusFontGreen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Input Text (for text input boxes)
                freeTypeFontParameter.color = new Color(0, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Button Font (Bold)
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                freeTypeFontParameter.borderWidth = 2;
                menuButtonFontBold = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Title Font
                freeTypeFontParameter.size = (int) (100 * scaleConstant);
                menuTitleFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                summaryTextFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;
            case 6:
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/chinese.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.characters = "%一个球员很多定制新游戏对手困难类型玩简单中501周围时钟蟋蟀英国实践家相同的设备全服务器通过2名之间电话反陌生人（WI-F）连接到你认识临称输入字可用其他正在使这，选择另存不.创建并将顶级框下面文本朋友还必须4们都按“”才能开始等候项飞镖位置语言怎么传感灵敏度标准比赛。根据落地得分如果它稀薄外环登陆是倍靠近心三靶绕公牛为每轮流投掷3支最后总成绩就被拿走了远离从该目让于击a来完双想例您剩那需要点取棋盘上数依次旦着号码以瞄第胜利板'打某些达高任何进步命赢积像往常样直然关闭再赚谁所有首先获或者两起涉及,9876和而且超会停止线值眼交换角色钱出请指放屏幕齐与快速移动机际但没释前抬作大效习惯尝试练去白方块播似乎自行问题重启返回主菜？摘统计结束抽奖功继续骰子平均二水售票窗口确性池糊失败无法找已满断载入中...红绿色蓝色黑曜石豪华保费酒吧体育酒吧阶段更多很快";/* Fonts Setup*/
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 1.0f);
                freeTypeFontParameter.borderWidth = 2;

                // Button and Subheading Font
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                menuButtonFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Description Font
                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                menuDescriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Customisation Screen Font
                freeTypeFontParameter.size = (int) (40 * scaleConstant);
                customFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (red)
                freeTypeFontParameter.color = new Color(1.0f, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (25 * scaleConstant);
                freeTypeFontParameter.borderWidth = 0;
                menuStatusFontRed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (green)
                freeTypeFontParameter.color = new Color(0, 1.0f, 0, 1.0f);
                menuStatusFontGreen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Input Text (for text input boxes)
                freeTypeFontParameter.color = new Color(0, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Button Font (Bold)
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.size = (int) (55 * scaleConstant);
                freeTypeFontParameter.borderWidth = 2;
                menuButtonFontBold = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Title Font
                freeTypeFontParameter.size = (int) (100 * scaleConstant);
                menuTitleFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                summaryTextFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;

            case 7:
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/japanese.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.characters = "%1人のプレイヤー多くカスタマズ新しいゲム反対難さ種類遊びます簡単中50わりザ・クロッリケトギ練習同じデバグルサ2間で電話をかけなンジャ（WI-F）と戦接続るにあたが知って連絡一時的名前入力だ相手敵者は利用可能他誰こ使存在.作成、それボ下テキ友3彼ら4次[]押待オショダツ設定言語方セ感度標準。ポ着地点基づ得ば薄外輪土倍増心近ドブア取巻側雄牛各交互投げ終合計全体コ離目等思う歳残ヒ必要勝つめ二重上べ数字打順並陸ぐ番号完最初選特「開」高達せんウ以獲通常どお閉もや両ち関係,9876よ始ラご挙例え止価値換再稼指画面緒携帯急速実際解放停持大効果動きメニ慣 \"試み行生不自然飛見場問題性起戻ュ？NO概統功継平均DARTィ正確象失敗フ切断赤緑青黒曜石贅沢なプレミアムパブスポーツバーステージもっと来る読み込んでいます...";

                /* Fonts Setup*/
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 1.0f);
                freeTypeFontParameter.borderWidth = 2;

                // Button and Subheading Font
                freeTypeFontParameter.size = (int) (35 * scaleConstant);
                menuButtonFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Description Font
                freeTypeFontParameter.size = (int) (20 * scaleConstant);
                menuDescriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Customisation Screen Font
                freeTypeFontParameter.size = (int) (40 * scaleConstant);
                customFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (red)
                freeTypeFontParameter.color = new Color(1.0f, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (15 * scaleConstant);
                freeTypeFontParameter.borderWidth = 0;
                menuStatusFontRed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (green)
                freeTypeFontParameter.color = new Color(0, 1.0f, 0, 1.0f);
                menuStatusFontGreen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Input Text (for text input boxes)
                freeTypeFontParameter.color = new Color(0, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (35 * scaleConstant);
                menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Button Font (Bold)
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.size = (int) (35 * scaleConstant);
                freeTypeFontParameter.borderWidth = 2;
                menuButtonFontBold = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Title Font
                freeTypeFontParameter.size = (int) (75 * scaleConstant);
                menuTitleFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                summaryTextFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;
            case 8:
                freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/hindi.ttf"));
                freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

                freeTypeFontParameter.characters = "एक खिलाड़ीईअनुूतरेंयप्दवमशआसध501चोओघटगजभहइब2फै(-)थउ,षौ.औॉ3ठ4'।णछ9876ँ\"?%";

                /* Fonts Setup*/
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 1.0f);
                freeTypeFontParameter.borderWidth = 2;

                // Button and Subheading Font
                freeTypeFontParameter.size = (int) (45 * scaleConstant);
                menuButtonFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Description Font
                freeTypeFontParameter.size = (int) (25 * scaleConstant);
                menuDescriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Customisation Screen Font
                freeTypeFontParameter.size = (int) (40 * scaleConstant);
                customFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (red)
                freeTypeFontParameter.color = new Color(1.0f, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (25 * scaleConstant);
                freeTypeFontParameter.borderWidth = 0;
                menuStatusFontRed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Status Font (green)
                freeTypeFontParameter.color = new Color(0, 1.0f, 0, 1.0f);
                menuStatusFontGreen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Input Text (for text input boxes)
                freeTypeFontParameter.color = new Color(0, 0, 0, 1.0f);
                freeTypeFontParameter.size = (int) (45 * scaleConstant);
                menuInputFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Button Font (Bold)
                freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
                freeTypeFontParameter.size = (int) (45 * scaleConstant);
                freeTypeFontParameter.borderWidth = 2;
                menuButtonFontBold = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                // Title Font
                freeTypeFontParameter.size = (int) (90 * scaleConstant);
                menuTitleFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

                freeTypeFontParameter.size = (int) (30 * scaleConstant);
                summaryTextFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
                break;
        }

        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfb.ttf"));
        freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 1.0f);
        freeTypeFontParameter.borderWidth = 2;

        // Summary screen font
        freeTypeFontParameter.size = (int) (30 * scaleConstant);
        freeTypeFontParameter.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        summaryFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.borderWidth = 1;
        summaryFontColored[1] = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.color = new Color(1.0f, 0, 0, 1.0f);
        summaryFontColored[3] = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.color = new Color(0.0f, 1.0f, 0, 1.0f);
        summaryFontColored[2] = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.color = new Color(1.0f, 0.5f, 0, 1.0f);
        summaryFontColored[4] = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.color = new Color(1.0f, 1.0f, 0, 1.0f);
        summaryFontColored[5] = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.color = new Color(0.7f, 0.7f, 0.7f, 1.0f);
        summaryFontColored[7] = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
        freeTypeFontParameter.color = new Color(0.4f, 0.4f, 0.4f, 1.0f);
        summaryFontColored[6] = freeTypeFontGenerator.generateFont(freeTypeFontParameter);
    }

    private void backgroundAnimation() {

        final int CURRENT_FPS = (Gdx.graphics.getFramesPerSecond() > 20) ? Gdx.graphics.getFramesPerSecond() : 60;

        // Constant dartboard rotation
        dartboardModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, -0.30f / (CURRENT_FPS / 30.0f));
        spaceSurroundModelInst.transform.rotate(1.0f, 0.0f, 0.0f, -0.10f / (CURRENT_FPS / 30.0f));

        // Makes darts rotate with the dartboard, by moving them at different rates, depending on distance from dartboard centre
        for(int i = 0; i < dart; i++) {
            rot[i] += 0.30f / (CURRENT_FPS / 30.0f);
            dartModelInstances[i].transform.setToTranslation(0.0f - (float) Math.cos(Math.toRadians(-rot[i] + 90))
                    * 2.0f * dartY[i], dartY[i] * (float) Math.sin(Math.toRadians(-rot[i] + 90)) * 2.0f, dartZ[i]);
            dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 90);
            dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
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

    /**
     * Loads in the game screen, cleans up after the menus
     */
    private void loadGameScreen() {
        game.setScreen(new GameScreen(game));
        if(selectedPracticeMode) {
            GameScreen.gameClass = new GameClass(0, 0);
        } else {
            GameScreen.gameClass = new GameClass(selectedGameMode, selectedOpposition);
        }
        dispose();
    }

    private void drawMainMenuScreen() {
        spriteBatch.begin();

        // Title
        spriteBatch.draw(title, -screenWidth / 8, screenHeight - screenWidth / 16 * 11, screenWidth / 8 * 10, screenWidth / 8 * 5);

        // Main Buttons
        spriteBatch.draw(defaultButton, screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2, screenHeight / 16 * 7, (int) (300 * scaleConstant), (int) (150 * scaleConstant));
        spriteBatch.draw(defaultButton, screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2, screenHeight / 16 * 7 - (int) (200 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant));
        spriteBatch.draw(defaultButton, screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2, screenHeight / 16 * 7 - (int) (400 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant));
        // Small Buttons
        spriteBatch.draw(settingsButton, (int) (10 * scaleConstant), (int) (10 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        spriteBatch.draw(languageButton, (int) (100 * scaleConstant), (int) (10 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        spriteBatch.draw(helpButton, (int) (190 * scaleConstant), (int) (10 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        spriteBatch.draw(exitButton, screenWidth - (int) (90 * scaleConstant), (int) (10 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Main Button Text
        menuButtonFont.draw(spriteBatch, text[0], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[0] * scaleConstant), screenHeight / 16 * 7 + (int) (96 * scaleConstant));
        menuButtonFont.draw(spriteBatch, text[1], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[1] * scaleConstant), screenHeight / 16 * 7 - (int) (200 * scaleConstant) + (int) (96 * scaleConstant));
        menuButtonFont.draw(spriteBatch, text[2], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[2] * scaleConstant), screenHeight / 16 * 7 - (int) (400 * scaleConstant) + (int) (96 * scaleConstant));

        // Button Down Images
        if(buttonDown[0]) { spriteBatch.draw(selectedButton, screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2, screenHeight / 16 * 7, (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButton, screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2, screenHeight / 16 * 7 - (int) (200 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButton, screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2, screenHeight / 16 * 7 - (int) (400 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }

        if(buttonDown[3]) { spriteBatch.draw(selectedButtonSmall, (int) (10 * scaleConstant), (int) (10 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[4]) { spriteBatch.draw(selectedButtonSmall, (int) (100 * scaleConstant), (int) (10 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[5]) { spriteBatch.draw(selectedButtonSmall, (int) (190 * scaleConstant), (int) (10 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }

        if(buttonDown[6]) { spriteBatch.draw(selectedButtonSmall, screenWidth - (int) (90 * scaleConstant), (int) (10 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }

        // Introductory help text
        if(!Gdx.files.local("savefile.txt").exists()) {
            menuButtonFont.draw(spriteBatch, "<-- Start here", (int) (280 * scaleConstant), (int) (70 * scaleConstant));
        }

        if(MotionDarts.FREE_VERSION) {
            spriteBatch.draw(support, (int) (35 * scaleConstant), (int) (150 * scaleConstant), (int) (150 * scaleConstant), (int) (150 * scaleConstant));
            menuDescriptionFont.draw(spriteBatch, "REMOVE ADS", (int) (43 * scaleConstant), (int) (140 * scaleConstant));
        }

        spriteBatch.end();
    }

    private void drawGameSetupScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Begin game button
        spriteBatch.draw(defaultButton, screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2, (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant));
        // Back button
        spriteBatch.draw(backButton, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        // Help button
        spriteBatch.draw(helpButton, (int) (620 * scaleConstant), (int) (830 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Title text
        menuTitleFont.draw(spriteBatch, text[3], (int) (textIndent[3] * scaleConstant), screenHeight / 32 * 31);
        // Button text
        menuButtonFontBold.draw(spriteBatch, text[6], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[6] * scaleConstant), (int) (20 * scaleConstant) + (int) (96 * scaleConstant));

        // Sub heading text
        menuButtonFont.draw(spriteBatch, text[4], (int) (30 * scaleConstant), screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, text[5], (int) (30 * scaleConstant), screenHeight / 32 * 22);
        menuButtonFont.draw(spriteBatch, text[16], (int) (125 * scaleConstant), screenHeight / 32 * 9);

        // Difficulty Selection text
        menuButtonFontBold.draw(spriteBatch, text[7], (int) ((30 + textIndent[7]) * scaleConstant), ((int) (1038 * scaleConstant)));
        menuButtonFontBold.draw(spriteBatch, text[8], (int) ((250 + textIndent[8]) * scaleConstant), ((int) (1038 * scaleConstant)));
        menuButtonFontBold.draw(spriteBatch, text[9], (int) ((470 + textIndent[9]) * scaleConstant), ((int) (1038 * scaleConstant)));

        // Game Mode Selection text
        menuButtonFontBold.draw(spriteBatch, text[10], (int) (40 * scaleConstant), (int) (664 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[11], (int) (365 * scaleConstant), (int) (714 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[12], (int) (365 * scaleConstant), (int) (664 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[13], (int) (40 * scaleConstant), (int) (459 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[14], (int) (365 * scaleConstant), (int) (509 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[15], (int) (365 * scaleConstant), (int) (459 * scaleConstant));

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (830 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButton, (int) (210 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }

        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Difficulty Selector Box outlines
        shapeRenderer.setColor(new Color(0, 0, 0.5f, 1.0f));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (1055 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (975 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (975 * scaleConstant), (int) (4 * scaleConstant), (int) (80 * scaleConstant));
        shapeRenderer.rect((int) (248 * scaleConstant), (int) (975 * scaleConstant), (int) (4 * scaleConstant), (int) (80 * scaleConstant));
        shapeRenderer.rect((int) (468 * scaleConstant), (int) (975 * scaleConstant), (int) (4 * scaleConstant), (int) (80 * scaleConstant));
        shapeRenderer.rect((int) (688 * scaleConstant), (int) (975 * scaleConstant), (int) (4 * scaleConstant), (int) (80 * scaleConstant));

        // Game Mode Selector
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (814 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (612 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (410 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (410 * scaleConstant), (int) (4 * scaleConstant), (int) (408 * scaleConstant));
        shapeRenderer.rect((int) (688 * scaleConstant), (int) (410 * scaleConstant), (int) (4 * scaleConstant), (int) (408 * scaleConstant));
        shapeRenderer.rect((int) (358 * scaleConstant), (int) (410 * scaleConstant), (int) (4 * scaleConstant), (int) (408 * scaleConstant));

        // Practice Mode RadioButton
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (300 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        shapeRenderer.rect((int) (36 * scaleConstant), (int) (306 * scaleConstant), (int) (68 * scaleConstant), (int) (68 * scaleConstant));
        shapeRenderer.setColor(new Color(0, 0, 0.0f, 1.0f));
        if(selectedPracticeMode) { shapeRenderer.rect((int) (45 * scaleConstant), (int) (315 * scaleConstant), (int) (50 * scaleConstant), (int) (50 * scaleConstant)); }

        // Selection Dimmer (dims out what isn't selected)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(new Color(0, 0, 0.0f, 0.5f));
        if(selectedOpposition != 1 || selectedPracticeMode) { shapeRenderer.rect((int) (34 * scaleConstant), (int) (979 * scaleConstant), (int) (214 * scaleConstant), (int) (76 * scaleConstant)); }
        if(selectedOpposition != 2 || selectedPracticeMode) { shapeRenderer.rect((int) (252 * scaleConstant), (int) (979 * scaleConstant), (int) (216 * scaleConstant), (int) (76 * scaleConstant)); }
        if(selectedOpposition != 3 || selectedPracticeMode) { shapeRenderer.rect((int) (472 * scaleConstant), (int) (979 * scaleConstant), (int) (216 * scaleConstant), (int) (76 * scaleConstant)); }

        if(selectedGameMode != 1 || selectedPracticeMode) { shapeRenderer.rect((int) (34 * scaleConstant), (int) (614 * scaleConstant), (int) (326 * scaleConstant), (int) (200 * scaleConstant)); }
        if(selectedGameMode != 3 || selectedPracticeMode) { shapeRenderer.rect((int) (34 * scaleConstant), (int) (412 * scaleConstant), (int) (326 * scaleConstant), (int) (200 * scaleConstant)); }
        if(selectedGameMode != 2 || selectedPracticeMode) { shapeRenderer.rect((int) (362 * scaleConstant), (int) (614 * scaleConstant), (int) (326 * scaleConstant), (int) (200 * scaleConstant)); }
        if(selectedGameMode != 4 || selectedPracticeMode) { shapeRenderer.rect((int) (362 * scaleConstant), (int) (412 * scaleConstant), (int) (326 * scaleConstant), (int) (200 * scaleConstant)); }

        shapeRenderer.end();
    }

    private void drawMultiplayerSetupScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Begin game button
        spriteBatch.draw(defaultButton, screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2, (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant));
        // Back button
        spriteBatch.draw(backButton, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        // Help button
        spriteBatch.draw(helpButton, (int) (620 * scaleConstant), (int) (650 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Title text
        menuTitleFont.draw(spriteBatch, text[17], (int) (textIndent[17] * scaleConstant), screenHeight / 32 * 31);
        // Button text
        if(selectedOpposition == 5) {
            if(showWaiting) {
                menuButtonFont.draw(spriteBatch, text[44], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[44] * scaleConstant), (int) (20 * scaleConstant) + (int) (96 * scaleConstant));
            } else{
                menuButtonFontBold.draw(spriteBatch, text[23], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[23] * scaleConstant), (int) (20 * scaleConstant) + (int) (96 * scaleConstant));
            }
        } else{
            menuButtonFontBold.draw(spriteBatch, text[6], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[6] * scaleConstant), (int) (20 * scaleConstant) + (int) (96 * scaleConstant));
        }
        // Sub heading text
        menuButtonFont.draw(spriteBatch, text[18], (int) (30 * scaleConstant), screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, text[5], (int) (30 * scaleConstant), screenHeight / 32 * 18 - (int) (20 * scaleConstant));

        // Multiplayer Type Selector text
        menuButtonFontBold.draw(spriteBatch, text[19], (int) (40 * scaleConstant), (int) (1045 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[20], (int) (40 * scaleConstant), (int) (945 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[24], (int) (40 * scaleConstant), (int) (845 * scaleConstant));
        // Multiplayer Type Selector description text
        menuDescriptionFont.draw(spriteBatch, text[21], (int) (40 * scaleConstant), (int) (990 * scaleConstant));
        menuDescriptionFont.draw(spriteBatch, text[22], (int) (40 * scaleConstant), (int) (890 * scaleConstant));
        menuDescriptionFont.draw(spriteBatch, text[25], (int) (40 * scaleConstant), (int) (790 * scaleConstant));

        // Game Mode Selection text
        menuButtonFontBold.draw(spriteBatch, text[10], (int) (40 * scaleConstant), (int) (484 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[11], (int) (365 * scaleConstant), (int) (534 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[12], (int) (365 * scaleConstant), (int) (484 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[13], (int) (40 * scaleConstant), (int) (279 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[14], (int) (365 * scaleConstant), (int) (329 * scaleConstant));
        menuButtonFontBold.draw(spriteBatch, text[15], (int) (365 * scaleConstant), (int) (279 * scaleConstant));

        // Successful Connection Text
        if(showWaiting) {
            menuStatusFontGreen.draw(spriteBatch, text[167], (int) (textIndent[167] * scaleConstant), (int) (200 * scaleConstant));
        }

        // Connection Error Text
        if(connectionFailed) {
            menuStatusFontRed.draw(spriteBatch, text[164], (int) (230 * scaleConstant), (int) (200 * scaleConstant));
            switch (connectionFailReason) {
                case 1:
                    menuStatusFontRed.draw(spriteBatch, "(" + text[165] + ")", (int) (400 * scaleConstant), (int) (200 * scaleConstant));
                    break;
                case 2:
                    menuStatusFontRed.draw(spriteBatch, "(" + text[166] + ")", (int) (400 * scaleConstant), (int) (200 * scaleConstant));
                    break;
            }
        }

        // Button Down Highlighting
        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (650 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButton, (int) (210 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }

        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Multiplayer mode selector
        shapeRenderer.setColor(new Color(0, 0, 0.5f, 1.0f));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (1055 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (955 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (855 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (755 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (755 * scaleConstant), (int) (4 * scaleConstant), (int) (300 * scaleConstant));
        shapeRenderer.rect((int) (690 * scaleConstant), (int) (755 * scaleConstant), (int) (4 * scaleConstant), (int) (300 * scaleConstant));

        // Game Mode Selector
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (634 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (432 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (230 * scaleConstant), (int) (660 * scaleConstant), (int) (4 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (230 * scaleConstant), (int) (4 * scaleConstant), (int) (408 * scaleConstant));
        shapeRenderer.rect((int) (688 * scaleConstant), (int) (230 * scaleConstant), (int) (4 * scaleConstant), (int) (408 * scaleConstant));
        shapeRenderer.rect((int) (358 * scaleConstant), (int) (230 * scaleConstant), (int) (4 * scaleConstant), (int) (408 * scaleConstant));

        // Selection Dimmer (dims out what isn't selected)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(new Color(0, 0, 0.0f, 0.5f));
        if(selectedOpposition != 4) { shapeRenderer.rect((int) (34 * scaleConstant), (int) (959 * scaleConstant), (int) (656 * scaleConstant), (int) (96 * scaleConstant)); }
        if(selectedOpposition != 5 || selectedSpecificPlayer) { shapeRenderer.rect((int) (34 * scaleConstant), (int) (859 * scaleConstant), (int) (656 * scaleConstant), (int) (96 * scaleConstant)); }
        if(selectedOpposition != 5 || !selectedSpecificPlayer) { shapeRenderer.rect((int) (34 * scaleConstant), (int) (759 * scaleConstant), (int) (656 * scaleConstant), (int) (96 * scaleConstant)); }

        if(selectedGameMode != 1) { shapeRenderer.rect((int) (34 * scaleConstant), (int) (434 * scaleConstant), (int) (326 * scaleConstant), (int) (200 * scaleConstant)); }
        if(selectedGameMode != 3) { shapeRenderer.rect((int) (34 * scaleConstant), (int) (232 * scaleConstant), (int) (326 * scaleConstant), (int) (200 * scaleConstant)); }
        if(selectedGameMode != 2) { shapeRenderer.rect((int) (362 * scaleConstant), (int) (434 * scaleConstant), (int) (326 * scaleConstant), (int) (200 * scaleConstant)); }
        if(selectedGameMode != 4) { shapeRenderer.rect((int) (362 * scaleConstant), (int) (232 * scaleConstant), (int) (326 * scaleConstant), (int) (200 * scaleConstant)); }

        shapeRenderer.end();
    }

    private void drawCustomisationScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Navigation buttons
        // Darts
        spriteBatch.draw(leftButton, (int) (530 * scaleConstant), (int) (1060 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        spriteBatch.draw(rightButton, (int) (620 * scaleConstant), (int) (1060 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        // Location
        spriteBatch.draw(leftButton, (int) (530 * scaleConstant), (int) (580 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        spriteBatch.draw(rightButton, (int) (620 * scaleConstant), (int) (580 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Title text
        menuTitleFont.draw(spriteBatch, text[45], (int) (textIndent[45] * scaleConstant), screenHeight / 32 * 31);

        // Sub heading text
        menuButtonFont.draw(spriteBatch, text[46], (int) (30 * scaleConstant), screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, text[47], (int) (30 * scaleConstant), screenHeight / 32 * 16);

        // Darts customisation options
        switch (customisationDartsPage) {
            case 0:
                spriteBatch.draw(customisationOption, (int) (20 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(customisationOption, (int) (250 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(customisationOption, (int) (480 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(dart1Icon, (int) (20 * scaleConstant), (int) (740 * scaleConstant), (int) (212 * scaleConstant), (int) (290 * scaleConstant));
                spriteBatch.draw(dart5Icon, (int) (250 * scaleConstant), (int) (740 * scaleConstant), (int) (212 * scaleConstant), (int) (290 * scaleConstant));
                spriteBatch.draw(dart6Icon, (int) (480 * scaleConstant), (int) (740 * scaleConstant), (int) (212 * scaleConstant), (int) (290 * scaleConstant));

                GlyphLayout text1 = new GlyphLayout(customFont, text[170]);
                GlyphLayout text2 = new GlyphLayout(customFont, text[171]);
                GlyphLayout text3 = new GlyphLayout(customFont, text[172]);

                customFont.draw(spriteBatch, text1, ((int) (130 * scaleConstant) - text1.width / 2), (int) (785 * scaleConstant));
                customFont.draw(spriteBatch, text2, ((int) (360 * scaleConstant) - text2.width / 2), (int) (785 * scaleConstant));
                customFont.draw(spriteBatch, text3, ((int) (590 * scaleConstant) - text3.width / 2), (int) (785 * scaleConstant));
                break;

            case 1:
                spriteBatch.draw(customisationOption, (int) (20 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(customisationOption, (int) (250 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(customisationOption, (int) (480 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(dart4Icon, (int) (20 * scaleConstant), (int) (740 * scaleConstant), (int) (212 * scaleConstant), (int) (290 * scaleConstant));
                spriteBatch.draw(dart2Icon, (int) (250 * scaleConstant), (int) (740 * scaleConstant), (int) (212 * scaleConstant), (int) (290 * scaleConstant));
                spriteBatch.draw(dart3Icon, (int) (480 * scaleConstant), (int) (740 * scaleConstant), (int) (212 * scaleConstant), (int) (290 * scaleConstant));

                GlyphLayout text4 = new GlyphLayout(customFont, text[173]);
                GlyphLayout text5 = new GlyphLayout(customFont, text[174]);
                GlyphLayout text6 = new GlyphLayout(customFont, text[175]);

                customFont.draw(spriteBatch, text4, ((int) (130 * scaleConstant) - text4.width / 2), (int) (785 * scaleConstant));
                customFont.draw(spriteBatch, text5, ((int) (360 * scaleConstant) - text5.width / 2), (int) (785 * scaleConstant));
                customFont.draw(spriteBatch, text6, ((int) (590 * scaleConstant) - text6.width / 2), (int) (785 * scaleConstant));

                if(MotionDarts.FREE_VERSION) {
                    spriteBatch.draw(locked, (int) (20 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                    spriteBatch.draw(locked, (int) (250 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                    spriteBatch.draw(locked, (int) (480 * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                }
                break;
        }

        // Location customsiation options
        switch (customisationLocationPage) {
            case 0:
                spriteBatch.draw(customisationOption, (int) (20 * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(customisationOption, (int) (250 * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(customisationOption, (int) (480 * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(env1Icon, (int) (20 * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(env2Icon, (int) (250 * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(env3Icon, (int) (480 * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));

                GlyphLayout text1 = new GlyphLayout(customFont, text[176]);
                GlyphLayout text2 = new GlyphLayout(customFont, text[177]);
                GlyphLayout text3 = new GlyphLayout(customFont, text[178]);

                customFont.draw(spriteBatch, text1, ((int) (130 * scaleConstant) - text1.width / 2), (int) (305 * scaleConstant));
                customFont.draw(spriteBatch, text2, ((int) (360 * scaleConstant) - text2.width / 2), (int) (305 * scaleConstant));
                customFont.draw(spriteBatch, text3, ((int) (590 * scaleConstant) - text3.width / 2), (int) (305 * scaleConstant));
                break;

            case 1:
                spriteBatch.draw(customisationOption, (int) (20 * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
                spriteBatch.draw(soonIcon, (int) (20 * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));

                GlyphLayout text4 = new GlyphLayout(customFont, text[179]);

                customFont.draw(spriteBatch, text4, ((int) (130 * scaleConstant) - text4.width / 2), (int) (305 * scaleConstant));
                break;
        }

        // Selected
        if(GameScreen.selectedDart / 3 == customisationDartsPage) {
            spriteBatch.draw(customisationOptionSelected, (int) ((20 + 230 * (GameScreen.selectedDart % 3)) * scaleConstant), (int) (740 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
        }

        if(GameScreen.selectedLocation / 3 == customisationLocationPage) {
            spriteBatch.draw(customisationOptionSelected, (int) ((20 + 230 * (GameScreen.selectedLocation % 3)) * scaleConstant), (int) (260 * scaleConstant), (int) (220 * scaleConstant), (int) (300 * scaleConstant));
        }


        // Button Highlighting
        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, (int) (530 * scaleConstant), (int) (1060 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (1060 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[3]) { spriteBatch.draw(selectedButtonSmall, (int) (530 * scaleConstant), (int) (580 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[4]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (580 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }

        spriteBatch.end();
    }

    private void drawSettingsScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Title text
        menuTitleFont.draw(spriteBatch, text[48], (int) (textIndent[48] * scaleConstant), screenHeight / 32 * 31);
        // Sub heading text
        menuButtonFont.draw(spriteBatch, text[52] + ": " + (int) ((GameScreen.sensitivityZ) * 100) + "%", (int) (30 * scaleConstant), screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, text[180], (int) (150 * scaleConstant), screenHeight / 32 * 22);
        menuButtonFont.draw(spriteBatch, text[181], (int) (150 * scaleConstant), screenHeight / 32 * 19);

        menuDescriptionFont.draw(spriteBatch, "Developed, with care, by Michael Wayne", (int) (170 * scaleConstant), (int) (45 * scaleConstant));

        // Slider
        spriteBatch.draw(sliderBar, (int) (60 * scaleConstant), (int) (960 * scaleConstant), (int) (600 * scaleConstant), (int) (75 * scaleConstant));
        spriteBatch.draw(sliderBit, (int) ((40 + 6 * (GameScreen.sensitivityZ - 0.5f) * 100) * scaleConstant), (int) (948 * scaleConstant), (int) (40 * scaleConstant), (int) (100 * scaleConstant));

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[1]) { spriteBatch.draw(sliderBitSelected, (int) ((40 + 6 * (GameScreen.sensitivityZ - 0.5f) * 100) * scaleConstant), (int) (948 * scaleConstant), (int) (40 * scaleConstant), (int) (100 * scaleConstant)); }


        spriteBatch.end();

        // Radio Buttons
        // Sound RadioButton
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0.5f, 1.0f));
        shapeRenderer.rect((int) (40 * scaleConstant), (int) (820 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        shapeRenderer.rect((int) (46 * scaleConstant), (int) (826 * scaleConstant), (int) (68 * scaleConstant), (int) (68 * scaleConstant));
        shapeRenderer.setColor(new Color(0, 0, 0.0f, 1.0f));
        if(sound) { shapeRenderer.rect((int) (55 * scaleConstant), (int) (835 * scaleConstant), (int) (50 * scaleConstant), (int) (50 * scaleConstant)); }
        shapeRenderer.end();

        // Music RadioButton
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0.5f, 1.0f));
        shapeRenderer.rect((int) (40 * scaleConstant), (int) (700 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        shapeRenderer.rect((int) (46 * scaleConstant), (int) (706 * scaleConstant), (int) (68 * scaleConstant), (int) (68 * scaleConstant));
        shapeRenderer.setColor(new Color(0, 0, 0.0f, 1.0f));
        if(music) { shapeRenderer.rect((int) (55 * scaleConstant), (int) (715 * scaleConstant), (int) (50 * scaleConstant), (int) (50 * scaleConstant)); }
        shapeRenderer.end();
    }

    private void drawLanguageScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Language Buttons
        spriteBatch.draw(flag1, (int) (120 * scaleConstant), (int) (950 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag2, (int) (400 * scaleConstant), (int) (950 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag3, (int) (120 * scaleConstant), (int) (750 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag4, (int) (400 * scaleConstant), (int) (750 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag5, (int) (120 * scaleConstant), (int) (550 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag6, (int) (400 * scaleConstant), (int) (550 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag7, (int) (120 * scaleConstant), (int) (350 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag8, (int) (400 * scaleConstant), (int) (350 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag9, (int) (120 * scaleConstant), (int) (150 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));
        spriteBatch.draw(flag10, (int) (400 * scaleConstant), (int) (150 * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));

        // Title text
        menuTitleFont.draw(spriteBatch, text[49], (int) (textIndent[49] * scaleConstant), screenHeight / 32 * 31);

        // Language text
        spriteBatch.draw(langImage, 0, 0, (int) (720 * scaleConstant), (int) (1280 * scaleConstant));

        // Selection Overlay
        spriteBatch.draw(selectedLanguage, (int) ((120 + 280 * ((language) % 2)) * scaleConstant), (int) ((950 - 200 * ((language) / 2)) * scaleConstant), (int) (200 * scaleConstant), (int) (125 * scaleConstant));

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }

        spriteBatch.end();
    }

    private void drawSummaryScreen() {

        if(ad && MotionDarts.FREE_VERSION) {
            MotionDarts.adInterface.showInterstitial();
            ad = false;
        }

        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Buttons for main menu/rematch
        spriteBatch.draw(defaultButton, (int) (210 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant));
        //spriteBatch.draw(defaultButton, (int) (380 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant));

        // Title text
        menuTitleFont.draw(spriteBatch, text[140], (int) (textIndent[140] * scaleConstant), screenHeight / 32 * 31);

        // Sub heading text
        menuButtonFont.draw(spriteBatch, text[141], (int) (30 * scaleConstant), screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, text[142], (int) (30 * scaleConstant), screenHeight / 32 * 13);

        // Button text
        menuButtonFont.draw(spriteBatch, text[143], (int) ((210 + textIndent[143]) * scaleConstant), (int) (116 * scaleConstant));
        //menuButtonFont.draw(spriteBatch, text[144], (380 + textIndent[144]) * scaleConstant, (int) (116 * scaleConstant));

        // Player names
        summaryFont.draw(spriteBatch, GameScreen.gameClass.playerNames[0], (int) (30 * scaleConstant), (int) (1040 * scaleConstant));
        summaryFont.draw(spriteBatch, GameScreen.gameClass.playerNames[1], (int) (360 * scaleConstant), (int) (1040 * scaleConstant));

        spriteBatch.end();

        switch (GameScreen.gameClass.getGameMode()) {
            case 1:
                spriteBatch.begin();

                // Score Table text
                for(int i = 0; i < 10; i++) {
                    // Turn
                    summaryFont.draw(spriteBatch, String.valueOf(i + 1 + summaryDisplayScore), (int) ((305 - 5 * String.valueOf(i + 1 + summaryDisplayScore).length()) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));

                    // Dart Scores
                    for(int player = 0; player < 2; player++) {
                        int[] dart = GameScreen.gameClass.scoreSystem.dartScore[i + summaryDisplayScore][player];
                        int[] dartNature = GameScreen.gameClass.scoreSystem.dartNature[i + summaryDisplayScore][player];

                        for (int j = 0; j < 3; j++) {
                            if (dartNature[j] != 0) {
                                summaryFontColored[dartNature[j]].draw(spriteBatch, String.valueOf(dart[j]), (int) ((30 + 50 * j + 420 * player + 9 * (2 - String.valueOf(dart[j]).length())) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                            }
                        }
                    }

                    // Overall Scores
                    if(i + summaryDisplayScore <= GameScreen.gameClass.scoreSystem.turn) {
                        summaryFont.draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.overallScore[i + summaryDisplayScore][0]), (int) ((230 - 14 * (String.valueOf(GameScreen.gameClass.scoreSystem.overallScore[i + summaryDisplayScore][0]).length() - 1)) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                        summaryFont.draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.overallScore[i + summaryDisplayScore][1]), (int) ((365) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                    }
                }

                // Draw score navigation buttons
                spriteBatch.draw(upButton, (int) (620 * scaleConstant), (int) (690 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
                spriteBatch.draw(downButton, (int) (620 * scaleConstant), (int) (600 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

                // Statistics
                float[][] stats = GameScreen.gameClass.scoreSystem.gameStatistics;

                for(int i = 0; i < 6; i++) {
                    GlyphLayout summaryLayout = new GlyphLayout(summaryTextFont, text[i + 148]);
                    summaryTextFont.draw(spriteBatch, summaryLayout, (int) (360 * scaleConstant) - summaryLayout.width / 2, (int) ((440 - i * 40) * scaleConstant));
                }

                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[0][0]) / 100.0), (int) (40 * scaleConstant), (int) ((440) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[1][0]) / 100.0), (int) (620 * scaleConstant), (int) ((440) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[0][1]) / 100.0), (int) (40 * scaleConstant), (int) ((400) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[1][1]) / 100.0), (int) (620 * scaleConstant), (int) ((400) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[0][2]) / 100.0), (int) (40 * scaleConstant), (int) ((360) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[1][2]) / 100.0), (int) (620 * scaleConstant), (int) ((360) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[0][3]) / 100.0), (int) (40 * scaleConstant), (int) ((320) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[1][3]) / 100.0), (int) (620 * scaleConstant), (int) ((320) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf((int) stats[0][4]), (int) (40 * scaleConstant), (int) ((280) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf((int) stats[1][4]), (int) (620 * scaleConstant), (int) ((280) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * GameScreen.gameClass.scoreSystem.personalStatistics[0]) / 100.0), (int) (40 * scaleConstant), (int) ((240) * scaleConstant));
                if(GameScreen.gameClass.getCompetitionType() == 5) {
                    summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * ServerComms.oppStats) / 100.0), (int) (620 * scaleConstant), (int) ((240) * scaleConstant));     // Get multiplayer statistic
                }

                // Button animations
                if(buttonDown[0]) { spriteBatch.draw(selectedButton, (int) (210 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
                //if(buttonDown[1]) { spriteBatch.draw(selectedButton, (int) (380 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
                if(buttonDown[2]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (690 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
                if(buttonDown[3]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (600 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }


                spriteBatch.end();

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                // Score table
                shapeRenderer.setColor(new Color(0.0f, 0.0f, 0.8f, 1.0f));
                shapeRenderer.rect((int) (28 * scaleConstant), (int) (998 * scaleConstant), (int) (554 * scaleConstant), (int) (6 * scaleConstant));
                shapeRenderer.rect((int) (253 * scaleConstant), (int) (598 * scaleConstant), (int) (6 * scaleConstant), (int) (402 * scaleConstant));
                shapeRenderer.rect((int) (353 * scaleConstant), (int) (598 * scaleConstant), (int) (6 * scaleConstant), (int) (402 * scaleConstant));
                shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                shapeRenderer.rect((int) (30 * scaleConstant), (int) (1000 * scaleConstant), (int) (550 * scaleConstant), (int) (2 * scaleConstant));
                shapeRenderer.rect((int) (255 * scaleConstant), (int) (600 * scaleConstant), (int) (2 * scaleConstant), (int) (400 * scaleConstant));
                shapeRenderer.rect((int) (355 * scaleConstant), (int) (600 * scaleConstant), (int) (2 * scaleConstant), (int) (400 * scaleConstant));

                // Statistics table
                shapeRenderer.setColor(new Color(0.0f, 0.0f, 0.8f, 1.0f));
                shapeRenderer.rect((int) (148 * scaleConstant), (int) (198 * scaleConstant), (int) (6 * scaleConstant), (int) (254 * scaleConstant));
                shapeRenderer.rect((int) (568 * scaleConstant), (int) (198 * scaleConstant), (int) (6 * scaleConstant), (int) (254 * scaleConstant));
                shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                shapeRenderer.rect((int) (150 * scaleConstant), (int) (200 * scaleConstant), (int) (2 * scaleConstant), (int) (250 * scaleConstant));
                shapeRenderer.rect((int) (570 * scaleConstant), (int) (200 * scaleConstant), (int) (2 * scaleConstant), (int) (250 * scaleConstant));

                shapeRenderer.end();
                break;

            case 2:
                spriteBatch.begin();

                // Score Table text
                for(int i = 0; i < 10; i++) {
                    // Turn
                    summaryFont.draw(spriteBatch, String.valueOf(i + 1 + summaryDisplayScore), (int) ((305 - 5 * String.valueOf(i + 1 + summaryDisplayScore).length()) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));

                    // Dart Scores
                    for(int player = 0; player < 2; player++) {
                        int[] dart = GameScreen.gameClass.scoreSystem.dartScore[i + summaryDisplayScore][player];
                        int[] dartNature = GameScreen.gameClass.scoreSystem.dartNature[i + summaryDisplayScore][player];

                        for (int j = 0; j < 3; j++) {
                            if (dartNature[j] != 0) {
                                summaryFontColored[dartNature[j]].draw(spriteBatch, String.valueOf((dartNature[j] <= 3) ? dart[j] / dartNature[j] : dart[j]), (int) ((30 + 50 * j + 420 * player + 9 * (2 - String.valueOf((dartNature[j] <= 3) ? dart[j] / dartNature[j] : dart[j]).length())) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                            }
                        }
                    }

                    // Overall Scores
                    if(i + summaryDisplayScore <= GameScreen.gameClass.scoreSystem.turn) {
                        summaryFont.draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.overallScore[i + summaryDisplayScore][0]), (int) ((230 - 14 * (String.valueOf(GameScreen.gameClass.scoreSystem.overallScore[i + summaryDisplayScore][0]).length() - 1)) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                        summaryFont.draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.overallScore[i + summaryDisplayScore][1]), (int) ((365) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                    }
                }

                // Draw score navigation buttons
                spriteBatch.draw(upButton, (int) (620 * scaleConstant), (int) (690 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
                spriteBatch.draw(downButton, (int) (620 * scaleConstant), (int) (600 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

                // Statistics
                stats = GameScreen.gameClass.scoreSystem.gameStatistics;

                GlyphLayout summaryLayout1 = new GlyphLayout(summaryTextFont, text[156]);
                GlyphLayout summaryLayout2 = new GlyphLayout(summaryTextFont, text[157]);
                GlyphLayout summaryLayout3 = new GlyphLayout(summaryTextFont, text[153]);

                summaryTextFont.draw(spriteBatch, summaryLayout1, (int) ((360) * scaleConstant) - summaryLayout1.width / 2, (int) ((440) * scaleConstant));
                summaryTextFont.draw(spriteBatch, summaryLayout2, (int) ((360) * scaleConstant) - summaryLayout2.width / 2, (int) ((400) * scaleConstant));
                summaryTextFont.draw(spriteBatch, summaryLayout3, (int) ((360) * scaleConstant) - summaryLayout3.width / 2, (int) ((240) * scaleConstant));

                summaryFont.draw(spriteBatch, String.valueOf(Math.round(10 * stats[0][0]) / 10.0) + "%", (int) (40 * scaleConstant), (int) ((440) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(10 * stats[1][0]) / 10.0) + "%", (int) (620 * scaleConstant), (int) ((440) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[0][1]) / 100.0), (int) (40 * scaleConstant), (int) ((400) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[1][1]) / 100.0), (int) (620 * scaleConstant), (int) ((400) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * GameScreen.gameClass.scoreSystem.personalStatistics[0]) / 100.0), (int) (40 * scaleConstant), (int) ((240) * scaleConstant));
                if(GameScreen.gameClass.getCompetitionType() == 5) {
                    summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * ServerComms.oppStats) / 100.0), (int) (620 * scaleConstant), (int) ((240) * scaleConstant));     // Get multiplayer statistic
                }

                // Button animations
                if(buttonDown[0]) { spriteBatch.draw(selectedButton, (int) (210 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
                //if(buttonDown[1]) { spriteBatch.draw(selectedButton, (int) (380 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
                if(buttonDown[2]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (690 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
                if(buttonDown[3]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (600 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }


                spriteBatch.end();

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                // Score table
                shapeRenderer.setColor(new Color(0.0f, 0.0f, 0.8f, 1.0f));
                shapeRenderer.rect((int) (28 * scaleConstant), (int) (998 * scaleConstant), (int) (554 * scaleConstant), (int) (6 * scaleConstant));
                shapeRenderer.rect((int) (253 * scaleConstant), (int) (598 * scaleConstant), (int) (6 * scaleConstant), (int) (402 * scaleConstant));
                shapeRenderer.rect((int) (353 * scaleConstant), (int) (598 * scaleConstant), (int) (6 * scaleConstant), (int) (402 * scaleConstant));
                shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                shapeRenderer.rect((int) (30 * scaleConstant), (int) (1000 * scaleConstant), (int) (550 * scaleConstant), (int) (2 * scaleConstant));
                shapeRenderer.rect((int) (255 * scaleConstant), (int) (600 * scaleConstant), (int) (2 * scaleConstant), (int) (400 * scaleConstant));
                shapeRenderer.rect((int) (355 * scaleConstant), (int) (600 * scaleConstant), (int) (2 * scaleConstant), (int) (400 * scaleConstant));

                // Statistics table
                shapeRenderer.setColor(new Color(0.0f, 0.0f, 0.8f, 1.0f));
                shapeRenderer.rect((int) (148 * scaleConstant), (int) (198 * scaleConstant), (int) (6 * scaleConstant), (int) (254 * scaleConstant));
                shapeRenderer.rect((int) (568 * scaleConstant), (int) (198 * scaleConstant), (int) (6 * scaleConstant), (int) (254 * scaleConstant));
                shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                shapeRenderer.rect((int) (150 * scaleConstant), (int) (200 * scaleConstant), (int) (2 * scaleConstant), (int) (250 * scaleConstant));
                shapeRenderer.rect((int) (570 * scaleConstant), (int) (200 * scaleConstant), (int) (2 * scaleConstant), (int) (250 * scaleConstant));

                shapeRenderer.end();
                break;

            case 3:
                spriteBatch.begin();

                String[] tempOpeningsTexts = {"20", "19", "18", "17", "16", "15", "BU"};
                // Score Table text
                for(int i = 0; i < 7; i++) {
                    // Innings
                    summaryFont.draw(spriteBatch, tempOpeningsTexts[i], (int) ((305 - 5 * String.valueOf(i + 1 + summaryDisplayScore).length()) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));

                    // Dart Scores
                    for(int player = 0; player < 2; player++) {

                        if(GameScreen.gameClass.scoreSystem.getInnings()[player][i] >= 1) {
                            summaryFontColored[1].draw(spriteBatch, "/", (int) ((225 + 150 * player) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                        }

                        if(GameScreen.gameClass.scoreSystem.getInnings()[player][i] >= 2) {
                            summaryFontColored[1].draw(spriteBatch, "\\", (int) ((225 + 150 * player) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                        }

                        if(GameScreen.gameClass.scoreSystem.getInnings()[player][i] >= 3) {
                            summaryFontColored[1].draw(spriteBatch, "0", (int) ((225 + 150 * player) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                        }

                        // Hits
                        summaryFontColored[1].draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.getInningHits()[player][i]), (int) ((100 + 400 * player) * scaleConstant), (int) ((990 - 40 * i) * scaleConstant));
                    }
                }

                summaryFontColored[1].draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.getScore()[0]), (int) ((100) * scaleConstant), (int) ((670) * scaleConstant));
                summaryFontColored[1].draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.getScore()[1]), (int) ((500) * scaleConstant), (int) ((670) * scaleConstant));

                // Statistics
                stats = GameScreen.gameClass.scoreSystem.gameStatistics;

                GlyphLayout summaryLayout4 = new GlyphLayout(summaryTextFont, text[156]);
                GlyphLayout summaryLayout5 = new GlyphLayout(summaryTextFont, text[162]);
                GlyphLayout summaryLayout6 = new GlyphLayout(summaryTextFont, text[153]);

                summaryTextFont.draw(spriteBatch, summaryLayout4, (int) ((360) * scaleConstant) - summaryLayout4.width / 2, (int) ((440) * scaleConstant));
                summaryTextFont.draw(spriteBatch, summaryLayout5, (int) ((360) * scaleConstant) - summaryLayout5.width / 2, (int) ((400) * scaleConstant));
                summaryTextFont.draw(spriteBatch, summaryLayout6, (int) ((360) * scaleConstant) - summaryLayout6.width / 2, (int) ((240) * scaleConstant));

                summaryFont.draw(spriteBatch, String.valueOf(Math.round(10 * stats[0][0]) / 10.0) + "%", (int) (40 * scaleConstant), (int) ((440) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(10 * stats[1][0]) / 10.0) + "%", (int) (620 * scaleConstant), (int) ((440) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[0][1]) / 100.0), (int) (40 * scaleConstant), (int) ((400) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[1][1]) / 100.0), (int) (620 * scaleConstant), (int) ((400) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * GameScreen.gameClass.scoreSystem.personalStatistics[0]) / 100.0), (int) (40 * scaleConstant), (int) ((240) * scaleConstant));
                if(GameScreen.gameClass.getCompetitionType() == 5) {
                    summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * ServerComms.oppStats) / 100.0), (int) (620 * scaleConstant), (int) ((240) * scaleConstant));     // Get multiplayer statistic
                }

                // Button animations
                if(buttonDown[0]) { spriteBatch.draw(selectedButton, (int) (210 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
                //if(buttonDown[1]) { spriteBatch.draw(selectedButton, (int) (380 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
                if(buttonDown[2]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (690 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
                if(buttonDown[3]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (600 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }


                spriteBatch.end();

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                // Score table
                shapeRenderer.setColor(new Color(0.0f, 0.0f, 0.8f, 1.0f));
                shapeRenderer.rect((int) (28 * scaleConstant), (int) (998 * scaleConstant), (int) (554 * scaleConstant), (int) (6 * scaleConstant));
                shapeRenderer.rect((int) (253 * scaleConstant), (int) (718 * scaleConstant), (int) (6 * scaleConstant), (int) (282 * scaleConstant));
                shapeRenderer.rect((int) (353 * scaleConstant), (int) (718 * scaleConstant), (int) (6 * scaleConstant), (int) (282 * scaleConstant));
                shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                shapeRenderer.rect((int) (30 * scaleConstant), (int) (1000 * scaleConstant), (int) (550 * scaleConstant), (int) (2 * scaleConstant));
                shapeRenderer.rect((int) (255 * scaleConstant), (int) (720 * scaleConstant), (int) (2 * scaleConstant), (int) (280 * scaleConstant));
                shapeRenderer.rect((int) (355 * scaleConstant), (int) (720 * scaleConstant), (int) (2 * scaleConstant), (int) (280 * scaleConstant));

                // Statistics table
                shapeRenderer.setColor(new Color(0.0f, 0.0f, 0.8f, 1.0f));
                shapeRenderer.rect((int) (148 * scaleConstant), (int) (198 * scaleConstant), (int) (6 * scaleConstant), (int) (254 * scaleConstant));
                shapeRenderer.rect((int) (568 * scaleConstant), (int) (198 * scaleConstant), (int) (6 * scaleConstant), (int) (254 * scaleConstant));
                shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                shapeRenderer.rect((int) (150 * scaleConstant), (int) (200 * scaleConstant), (int) (2 * scaleConstant), (int) (250 * scaleConstant));
                shapeRenderer.rect((int) (570 * scaleConstant), (int) (200 * scaleConstant), (int) (2 * scaleConstant), (int) (250 * scaleConstant));

                shapeRenderer.end();
                break;

            case 4:
                spriteBatch.begin();

                // Score Table text
                summaryFont.draw(spriteBatch, text[160], (int) ((267 - 6 * text[160].length()) * scaleConstant), (int) ((990) * scaleConstant));
                summaryFont.draw(spriteBatch, text[161], (int) ((265 - 6 * text[161].length()) * scaleConstant), (int) ((950) * scaleConstant));

                summaryFont.draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.getScore()[0]), (int) ((100 - 5 * String.valueOf(GameScreen.gameClass.scoreSystem.getScore()[0]).length()) * scaleConstant), (int) ((990) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.getScore()[1]), (int) ((410) * scaleConstant), (int) ((990) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.getTurnsBowling()[0]), (int) ((100 - 5 * String.valueOf(GameScreen.gameClass.scoreSystem.getTurnsBowling()[0]).length()) * scaleConstant), (int) ((950) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(GameScreen.gameClass.scoreSystem.getTurnsBowling()[1]), (int) ((410) * scaleConstant), (int) ((950) * scaleConstant));

                // Statistics
                stats = GameScreen.gameClass.scoreSystem.gameStatistics;

                GlyphLayout summaryLayout7 = new GlyphLayout(summaryTextFont, text[159]);
                GlyphLayout summaryLayout8 = new GlyphLayout(summaryTextFont, text[158]);
                GlyphLayout summaryLayout9 = new GlyphLayout(summaryTextFont, text[153]);

                summaryTextFont.draw(spriteBatch, summaryLayout7, (int) ((360) * scaleConstant) - summaryLayout7.width / 2, (int) ((440) * scaleConstant));
                summaryTextFont.draw(spriteBatch, summaryLayout8, (int) ((360) * scaleConstant) - summaryLayout8.width / 2, (int) ((400) * scaleConstant));
                summaryTextFont.draw(spriteBatch, summaryLayout9, (int) ((360) * scaleConstant) - summaryLayout9.width / 2, (int) ((240) * scaleConstant));

                summaryFont.draw(spriteBatch, String.valueOf(Math.round(10 * stats[0][0]) / 10.0) + "%", (int) (40 * scaleConstant), (int) ((440) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(10 * stats[1][0]) / 10.0) + "%", (int) (620 * scaleConstant), (int) ((440) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[0][1]) / 100.0), (int) (40 * scaleConstant), (int) ((400) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * stats[1][1]) / 100.0), (int) (620 * scaleConstant), (int) ((400) * scaleConstant));
                summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * GameScreen.gameClass.scoreSystem.personalStatistics[0]) / 100.0), (int) (40 * scaleConstant), (int) ((240) * scaleConstant));
                if(GameScreen.gameClass.getCompetitionType() == 5) {
                    summaryFont.draw(spriteBatch, String.valueOf(Math.round(100 * ServerComms.oppStats) / 100.0), (int) (620 * scaleConstant), (int) ((240) * scaleConstant));     // Get multiplayer statistic
                }

                // Button animations
                if(buttonDown[0]) { spriteBatch.draw(selectedButton, (int) (210 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
                //if(buttonDown[1]) { spriteBatch.draw(selectedButton, (int) (380 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }
                if(buttonDown[2]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (690 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
                if(buttonDown[3]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (600 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }


                spriteBatch.end();

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                // Score table
                shapeRenderer.setColor(new Color(0.0f, 0.0f, 0.8f, 1.0f));
                shapeRenderer.rect((int) (28 * scaleConstant), (int) (998 * scaleConstant), (int) (484 * scaleConstant), (int) (6 * scaleConstant));
                shapeRenderer.rect((int) (183 * scaleConstant), (int) (918 * scaleConstant), (int) (6 * scaleConstant), (int) (82 * scaleConstant));
                shapeRenderer.rect((int) (343 * scaleConstant), (int) (918 * scaleConstant), (int) (6 * scaleConstant), (int) (82 * scaleConstant));
                shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                shapeRenderer.rect((int) (30 * scaleConstant), (int) (1000 * scaleConstant), (int) (480 * scaleConstant), (int) (2 * scaleConstant));
                shapeRenderer.rect((int) (185 * scaleConstant), (int) (920 * scaleConstant), (int) (2 * scaleConstant), (int) (80 * scaleConstant));
                shapeRenderer.rect((int) (345 * scaleConstant), (int) (920 * scaleConstant), (int) (2 * scaleConstant), (int) (80 * scaleConstant));

                // Statistics table
                shapeRenderer.setColor(new Color(0.0f, 0.0f, 0.8f, 1.0f));
                shapeRenderer.rect((int) (148 * scaleConstant), (int) (198 * scaleConstant), (int) (6 * scaleConstant), (int) (254 * scaleConstant));
                shapeRenderer.rect((int) (568 * scaleConstant), (int) (198 * scaleConstant), (int) (6 * scaleConstant), (int) (254 * scaleConstant));
                shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
                shapeRenderer.rect((int) (150 * scaleConstant), (int) (200 * scaleConstant), (int) (2 * scaleConstant), (int) (250 * scaleConstant));
                shapeRenderer.rect((int) (570 * scaleConstant), (int) (200 * scaleConstant), (int) (2 * scaleConstant), (int) (250 * scaleConstant));

                shapeRenderer.end();
                break;
        }
    }

    private void drawTutorialScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Title text
        menuTitleFont.draw(spriteBatch, text[50], (int) (textIndent[50] * scaleConstant), screenHeight / 32 * 31);

        // Info text
        for(int i = 116; i < 135; i++) {
            menuDescriptionFont.draw(spriteBatch, text[i], (int) (30 * scaleConstant), (int) ((1120 - 30 * (i - 117)) * scaleConstant));
        }

        // Animations
        switch (anim1Stage) {
            case 0:
                spriteBatch.draw(anim1_1, (int) (50 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 1:
                spriteBatch.draw(anim1_2, (int) (50 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 2:
                spriteBatch.draw(anim1_3, (int) (50 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 3:
                spriteBatch.draw(anim1_4, (int) (50 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 4:
                spriteBatch.draw(anim1_2, (int) (50 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 5:
                spriteBatch.draw(anim1_5, (int) (50 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 6:
            case 7:
            case 8:
                spriteBatch.draw(anim1_6, (int) (50 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
        }


        switch (anim2Stage) {
            case 0:
                spriteBatch.draw(anim2_1, (int) (350 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 1:
                spriteBatch.draw(anim2_2, (int) (350 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 2:
                spriteBatch.draw(anim2_3, (int) (350 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 3:
                spriteBatch.draw(anim2_2, (int) (350 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 4:
                spriteBatch.draw(anim2_4, (int) (350 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
            case 5:
            case 6:
            case 7:
                spriteBatch.draw(anim2_5, (int) (350 * scaleConstant), (int) (400 * scaleConstant), (int) (250 * scaleConstant), (int) (250 * scaleConstant));
                break;
        }

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }

        spriteBatch.end();
    }

    private void drawMultiplayerTargetedConnectScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Begin game button
        spriteBatch.draw(defaultButton, screenWidth / 2 - ( ((int) (defaultButton.getWidth() * scaleConstant))) / 2, (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant));
        // Back button
        spriteBatch.draw(backButton, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        spriteBatch.draw(helpButton, (int) (110 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        // Confirm buttons
        spriteBatch.draw(connectButton, (int) (605 * scaleConstant), (int) (975 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        spriteBatch.draw(connectButton, (int) (605 * scaleConstant), (int) (735 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));


        // Title text
        menuTitleFont.draw(spriteBatch, text[17], (int) (textIndent[17] * scaleConstant), screenHeight / 32 * 31);
        // Button text
        if(showWaiting) {
            menuButtonFont.draw(spriteBatch, text[44], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[44] * scaleConstant), (int) (20 * scaleConstant) + (int) (96 * scaleConstant));
        } else{
            menuButtonFontBold.draw(spriteBatch, text[23], screenWidth / 2 - ((int) (defaultButton.getWidth() * scaleConstant)) / 2 + (int) (textIndent[23] * scaleConstant), (int) (20 * scaleConstant) + (int) (96 * scaleConstant));
        }
        // Sub heading text
        menuButtonFont.draw(spriteBatch, text[26], (int) (30 * scaleConstant), screenHeight / 32 * 28);
        menuButtonFont.draw(spriteBatch, text[27], (int) (30 * scaleConstant), screenHeight / 32 * 22);

        // Status text (informs user whether username is taken or whether opponent exists)
        if(usernameChecked) {
            if(usernameAvailable) {
                menuStatusFontGreen.draw(spriteBatch, text[32], (int) (30 * scaleConstant), (int) (970 * scaleConstant));
            } else{
                menuStatusFontRed.draw(spriteBatch, text[33], (int) (30 * scaleConstant), (int) (970 * scaleConstant));
            }
        }

        if(opponentChecked) {
            if(opponentAvailable) {
                menuStatusFontGreen.draw(spriteBatch, text[34], (int) (30 * scaleConstant), (int) (730 * scaleConstant));
            } else{
                menuStatusFontRed.draw(spriteBatch, text[35], (int) (30 * scaleConstant), (int) (730 * scaleConstant));
            }
        }

        if(showHelpMultiplayer) {
            // Information text
            menuDescriptionFont.draw(spriteBatch, text[36], (int) (30 * scaleConstant), (int) (650 * scaleConstant));
            menuDescriptionFont.draw(spriteBatch, text[37], (int) (30 * scaleConstant), (int) (610 * scaleConstant));
            menuDescriptionFont.draw(spriteBatch, text[38], (int) (30 * scaleConstant), (int) (570 * scaleConstant));
            menuDescriptionFont.draw(spriteBatch, text[39], (int) (30 * scaleConstant), (int) (530 * scaleConstant));
            menuDescriptionFont.draw(spriteBatch, text[40], (int) (30 * scaleConstant), (int) (490 * scaleConstant));
            menuDescriptionFont.draw(spriteBatch, text[41], (int) (30 * scaleConstant), (int) (450 * scaleConstant));
            menuDescriptionFont.draw(spriteBatch, text[42], (int) (30 * scaleConstant), (int) (410 * scaleConstant));
            menuDescriptionFont.draw(spriteBatch, text[43], (int) (30 * scaleConstant), (int) (370 * scaleConstant));
        }

        // Successful Connection Text
        if(showWaiting) {
            menuStatusFontGreen.draw(spriteBatch, text[167], (int) (textIndent[167] * scaleConstant), (int) (200 * scaleConstant));
        }

        // Connection Error Text
        if(connectionFailed) {
            menuStatusFontRed.draw(spriteBatch, text[164], (int) (230 * scaleConstant), (int) (200 * scaleConstant));
            menuStatusFontRed.draw(spriteBatch, "(" + text[166] + ")", (int) (400 * scaleConstant), (int) (200 * scaleConstant));
        }

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, (int) (110 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButton, (int) (210 * scaleConstant), (int) (20 * scaleConstant), (int) (300 * scaleConstant), (int) (150 * scaleConstant)); }

        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Temporary Username Box
        shapeRenderer.setColor(new Color(0, 0, 0.5f, 1.0f));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (975 * scaleConstant), (int) (550 * scaleConstant), (int) (80 * scaleConstant));
        shapeRenderer.rect((int) (30 * scaleConstant), (int) (735 * scaleConstant), (int) (550 * scaleConstant), (int) (80 * scaleConstant));
        shapeRenderer.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        shapeRenderer.rect((int) (34 * scaleConstant), (int) (979 * scaleConstant), (int) (542 * scaleConstant), (int) (72 * scaleConstant));
        shapeRenderer.rect((int) (34 * scaleConstant), (int) (739 * scaleConstant), (int) (542 * scaleConstant), (int) (72 * scaleConstant));

        shapeRenderer.end();

        spriteBatch.begin();

        // Input text
        menuInputFont.draw(spriteBatch, tempUsername, (int) (40 * scaleConstant), (int) (1035 * scaleConstant));
        menuInputFont.draw(spriteBatch, opponentUsername, (int) (40 * scaleConstant), (int) (795 * scaleConstant));

        spriteBatch.end();
    }

    private void drawGamemodeInfoScreen() {
        spriteBatch.begin();

        // Background Overlay
        spriteBatch.draw(submenuBackground, 0, 0, screenWidth, screenHeight);
        // Back button
        spriteBatch.draw(backButton, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Navigation Buttons
        spriteBatch.draw(leftButton, (int) (520 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));
        spriteBatch.draw(rightButton, (int) (620 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant));

        // Title text
        menuTitleFont.draw(spriteBatch, text[51], (int) (textIndent[51] * scaleConstant), screenHeight / 32 * 31);

        // Info text
        int firstLine = 0;
        int totalLines = 0;

        switch(gameInfoScreen) {
            case 1:
                menuButtonFont.draw(spriteBatch, text[10], (int) (30 * scaleConstant), screenHeight / 32 * 28);
                firstLine = 53;
                totalLines = 22;
                break;

            case 2:
                menuButtonFont.draw(spriteBatch, text[11] + " " + text[12], (int) (30 * scaleConstant), screenHeight / 32 * 28);
                firstLine = 75;
                totalLines = 9;
                break;

            case 3:
                menuButtonFont.draw(spriteBatch, text[13], (int) (30 * scaleConstant), screenHeight / 32 * 28);
                firstLine = 84;
                totalLines = 17;
                break;

            case 4:
                menuButtonFont.draw(spriteBatch, text[14] + " " + text[15], (int) (30 * scaleConstant), screenHeight / 32 * 28);
                firstLine = 101;
                totalLines = 15;
                break;
        }

        // Draw text to screen
        for(int i = firstLine; i < firstLine + totalLines; i++) {
            menuDescriptionFont.draw(spriteBatch, text[i], (int) (30 * scaleConstant), (int) ((1060 - 30 * (i - firstLine)) * scaleConstant));
        }

        if(buttonDown[0]) { spriteBatch.draw(selectedButtonSmall, (int) (20 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[1]) { spriteBatch.draw(selectedButtonSmall, (int) (520 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }
        if(buttonDown[2]) { spriteBatch.draw(selectedButtonSmall, (int) (620 * scaleConstant), (int) (20 * scaleConstant), (int) (80 * scaleConstant), (int) (80 * scaleConstant)); }

        spriteBatch.end();
    }
}
