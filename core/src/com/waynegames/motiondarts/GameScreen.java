package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
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
import com.sun.corba.se.spi.activation.Server;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import sun.security.krb5.SCDynamicStoreConfig;

import static com.waynegames.motiondarts.MenuScreen.text;
import static com.waynegames.motiondarts.MenuScreen.textIndent;

/**
 * Handles all of the touch input and graphics output specifically for the game
 *
 * @author Michael Wayne
 * @version v0.1.0
 */
public class GameScreen extends ScreenAdapter {
    MotionDarts game;

    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;

    private ModelBatch modelBatch;
    private PerspectiveCamera perspectiveCamera;
    private Environment environment;

    private ShapeRenderer shapeRenderer;

    private ModelInstance[] dartModelInstances = new ModelInstance[3];
    static ModelInstance dartboardModelInst1;
    private ModelInstance environmentModelInst1;

    private Array<ModelInstance> instances = new Array<ModelInstance>();

    private Sprite popup;

    private BitmapFont scoreFont;
    private BitmapFont scoreFontSmall;
    private BitmapFont descriptionFont;
    private BitmapFont playerFont;
    private BitmapFont currentPlayerFont;
    private BitmapFont scoreFontCricket;
    private BitmapFont scoreFontCricketMarks;
    private BitmapFont scoreFontCricketOpen;
    private BitmapFont scoreFontCricketClosed;

    private FreeTypeFontGenerator freeTypeFontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter;

    private int screenWidth;
    private int screenHeight;

    static float sensitivityZ = 1.0f;

    private float aimX = 0.0f;
    private float aimY = 0.0f;

    private float distanceToBoard = 18700.0f;

    private float accelX, accelY, accelZ;
    private float velX, velY, velZ;
    private float rotX, rotY;

    private boolean inFlight = false;
    private boolean viewLock = false;

    static boolean dartsReset = false;

    private int resetTimer = 0;
    private int prevZ = 0;

    private int scaleConstant;

    private boolean menuPopup = false;

    static GameClass gameClass;

    static boolean endGame = false;

    private float[] velGraph = new float[50];
    private int velGraphHead = 0;

    private Color darkBlue = new Color(0, 0, 0.6f, 0.6f);
    private Color lightBlue = new Color(0, 0, 0.8f, 0.6f);

    /* Customisation Variables */
    static int selectedLocation = 0;
    static int selectedDart = 0;

    static String[] locationFiles = {"environment_01", "", "", ""};
    static String[] dartFiles = {"dart_01", "", "", ""};

    /* AI Variables */
    private int animTimeCounter = 0;

    /* Animation Variables */
    private int translats = 0;
    private int translatCount = 0;

    /**
     * Game screen setup constructor<br>
     * Handles:<br>
     * <ul>
     *     <li>Viewpoint Setup</li>
     *     <li>3D Models Loading & Positioning</li>
     *     <li>Lighting Setup</li>
     *     <li>Input</li>
     * </ul>
     * @param game For changing the game screen
     */
    GameScreen (final MotionDarts game) {
        this.game = game;

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        bitmapFont = new BitmapFont(Gdx.files.internal("consolas.fnt"), Gdx.files.internal("consolas.png"), false);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        scaleConstant = screenWidth / 720;

        endGame = false;

        /* Font Setup */
        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbbold.ttf"));
        freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        freeTypeFontParameter.color = new Color(255, 255, 255, 255);
        freeTypeFontParameter.borderColor = new Color(0, 0, 1.0f, 255);
        freeTypeFontParameter.borderWidth = 2;

        // Current player font
        freeTypeFontParameter.size = 40 * scaleConstant;
        currentPlayerFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        // Score Fonts
        freeTypeFontParameter.size = 60 * scaleConstant;

        scoreFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.size = 20 * scaleConstant;
        freeTypeFontParameter.borderWidth = 1;
        scoreFontCricket = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.color = new Color(0.5f, 0.5f, 0.5f, 1.0f);
        scoreFontCricketClosed = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.color = new Color(0.0f, 0.9f, 0.0f, 1.0f);
        scoreFontCricketOpen = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.size = 20 * scaleConstant;
        freeTypeFontParameter.color = new Color(255, 255, 255, 255);
        freeTypeFontParameter.borderWidth = 0;
        scoreFontCricketMarks = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfb.ttf"));

        freeTypeFontParameter.size = 30 * scaleConstant;
        freeTypeFontParameter.borderWidth = 2;
        scoreFontSmall = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        // Description and Player Fonts
        freeTypeFontParameter.size = 20 * scaleConstant;
        descriptionFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontParameter.size = 40 * scaleConstant;
        playerFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);


        /* Viewpoint Setup */
        // PerspectiveCamera setup: Field of Vision, viewpoint width, viewpoint height
        perspectiveCamera = new PerspectiveCamera(70, screenWidth, screenHeight);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 100000.0f;
        setViewPosition(1);


        /* Models Setup */
        Model dartModel1 = MotionDarts.assetManager.get("models/" + dartFiles[selectedDart] + ".g3db", Model.class);
        Model dartboardModel1 = MotionDarts.assetManager.get("models/dartboard_01.g3db", Model.class);
        Model environmentModel1 = MotionDarts.assetManager.get("models/" + locationFiles[selectedLocation] + ".g3db", Model.class);

        // Assign Models to ModelInstances
        dartModelInstances[0] = new ModelInstance(dartModel1);
        dartModelInstances[1] = new ModelInstance(dartModel1);
        dartModelInstances[2] = new ModelInstance(dartModel1);
        dartboardModelInst1 = new ModelInstance(dartboardModel1);
        environmentModelInst1 = new ModelInstance(environmentModel1);

        // Add ModelInstances to ModelInstance array
        instances.add(dartModelInstances[0]);
        instances.add(dartModelInstances[1]);
        instances.add(dartModelInstances[2]);
        instances.add(dartboardModelInst1);
        instances.add(environmentModelInst1);

        // Moving everything into the right place for setup
        // 100.0f = 1 cm
        // Game plays in the positive z axis (forwards)
        dartModelInstances[0].transform.setToTranslation(0.0f, 0.0f, -500.0f);
        dartModelInstances[0].transform.rotate(1.0f, 0.0f, 0.0f, 89);
        dartModelInstances[0].transform.rotate(0.0f, 1.0f, 0.0f, 45);

        dartModelInstances[1].transform.setToTranslation(4100.0f, -10000.0f, -1000.0f);
        dartModelInstances[1].transform.rotate(1.0f, 0.0f, 0.0f, 225);
        dartModelInstances[1].transform.rotate(0.0f, 1.0f, 0.0f, 45);

        dartModelInstances[2].transform.setToTranslation(4200.0f, -10000.0f, -1000.0f);
        dartModelInstances[2].transform.rotate(1.0f, 0.0f, 0.0f, 225);
        dartModelInstances[2].transform.rotate(0.0f, 1.0f, 0.0f, 45);

        dartboardModelInst1.transform.setToTranslation(0.0f, 0.0f, distanceToBoard);
        dartboardModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 90);
        dartboardModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 180);

        environmentModelInst1.transform.setToTranslation(0.0f, -4700.0f, distanceToBoard + 200.0f);
        environmentModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 90);

        /* Textures */
        Texture popupTexture = MotionDarts.assetManager.get("textures/menuPopup.png", Texture.class);

        popup = new Sprite(popupTexture);

        /* Game Environment */
        environment = new Environment();

        // Sets ambient lighting around the game environment
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.0f));
        // Creates a new point light; first three parameters: color, next three: position, then intensity
        environment.add(new PointLight().set(0.8f, 0.8f, 0.7f, 10000.0f, 10000.0f, 1000.0f, 300000000.0f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.7f, -10000.0f, 10000.0f, 1000.0f, 100000000.0f));


        /* Game Input */
        // InputMultiplexer for handling multiple input sources
        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        // InputAdapter, receives touch input from phone screen
        inputMultiplexer.addProcessor(new InputAdapter() {

            Timer t;

            @Override
            public boolean touchDown(int touchX, int touchY, int pointer, int button) {

                /* End the game if necessary, return to main menu */
                if(endGame) {
                    endGame = false;
                    game.setScreen(new MenuScreen(game));
                    if(gameClass.getCompetitionType() > 0) {
                        MenuScreen.menuScreen = 8;
                        if(gameClass.getCompetitionType() == 5) {
                            ServerComms.disconnectFromServer();
                            ServerComms.disconnection = false;
                        }
                    } else{
                        MenuScreen.menuScreen = 1;
                        MenuScreen.selectedPracticeMode = false;
                    }
                    gameClass.writeSaveData();
                    dispose();
                }

                if(!inFlight && !viewLock && !gameClass.aiTurn && !gameClass.oppTurn) {
                    // Revert darts to be in hand
                    if(gameClass.scoreSystem.dartsThrown == 0 && !dartsReset) {
                        resetDartPositions();
                    }

                    // Places dart in right hand from left hand (avoids annoying graphical flash of the dart not being centred)
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(0.0f, 0.0f, -500.0f);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 89);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                    setViewPosition(1);

                    /* Velocity Calculation */
                    // Reset velocity
                    velX = 0;
                    velY = 0;
                    velZ = 0;

                    resetTimer = 0;

                    if(t != null) {
                        t.cancel();
                        t.purge();
                    }
                    t = new Timer();

                    // Timer to receive acceleration readings 50 times a second
                    t.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {

                            // Accelerometer data
                            accelX = Gdx.input.getAccelerometerX();
                            accelY = Gdx.input.getAccelerometerY();
                            accelZ = Gdx.input.getAccelerometerZ();

                            // Rotation Data
                            rotX = Gdx.input.getPitch();
                            rotY = Gdx.input.getRoll();

                            // Rotation Error Solution; If Rotation sensors spontaneously stop working, put in a most likely rotation
                            rotX = (rotX == 0f) ? -70 : rotX;
                            rotY = (rotY == 0f) ? -90 : rotY;

                            // Gravity elimination
                            accelX = (int) -(accelX + (float) (9.80665 * Math.sin(Math.toRadians(rotY)) * Math.cos(Math.toRadians(rotX))));
                            accelY = (int) accelY + (float) (9.80665 * Math.sin(Math.toRadians(rotX)));
                            accelZ = (int) -(accelZ - (float) (9.80665 * Math.cos(Math.toRadians(rotX)) * Math.cos(Math.toRadians(rotY))));

                            // Velocity calculation
                            velX += accelX / 50;
                            velY += accelY / 50;
                            velZ += accelZ / 50;

                            // Resets
                            // Reset Timer (resets if the player shakes phone, then holds for a bit and releases)

                            if(resetTimer > 8) {
                                velX = 0;
                                velY = 0;
                                velZ = 0;
                                resetTimer = 0;
                            } else if(prevZ == (int) accelZ) {
                                resetTimer++;
                            } else{
                                if(resetTimer > 0) {
                                    resetTimer--;
                                }
                            }

                            prevZ = (int) accelZ;

                            // Ensures the player can't shake the device to spoof the velocity calculator into going faster than they are
                            if(velZ > 4) {
                                velX = 0;
                                velY = 0;
                                velZ = 0;
                            }

                            velGraph[velGraphHead] = velZ;
                            velGraph[velGraphHead] = (Math.abs(velGraph[velGraphHead]) * 25 < 50) ? Math.abs(velGraph[velGraphHead]) * 25 / 3 : Math.abs(velGraph[velGraphHead]) * 25 * 2.333333f - 100;
                            velGraphHead = (velGraphHead < velGraph.length - 1) ? ++velGraphHead : 0;

                        }
                    }, 0, 20);
                }

                if(menuPopup) {
                    if(touchX > 160 && touchX < 360 && touchY < screenHeight - 490 && touchY > screenHeight - 580) {
                        game.setScreen(new MenuScreen(game));
                        if(gameClass.getCompetitionType() > 0) {
                            MenuScreen.menuScreen = 8;

                            if(gameClass.getCompetitionType() == 5) {
                                ServerComms.disconnectFromServer();
                                ServerComms.newThrow = false;
                            }
                        } else{
                            MenuScreen.menuScreen = 1;
                            MenuScreen.selectedPracticeMode = false;
                        }
                        gameClass.scoreSystem.overallScore[gameClass.scoreSystem.turn][0] = gameClass.scoreSystem.getScore()[0];
                        gameClass.scoreSystem.overallScore[gameClass.scoreSystem.turn][1] = gameClass.scoreSystem.getScore()[1];
                        dispose();
                    } else{
                        menuPopup = false;
                    }
                }

                return true;
            }

            public boolean touchUp(int touchX, int touchY, int pointer, int button) {

                if(touchX < 156 && touchY < 75) {
                    // Return to main menu popup
                    viewLock = true;
                    menuPopup = true;
                }

                if(!inFlight && !viewLock && !gameClass.aiTurn && !gameClass.oppTurn) {
                    // Dart throwing
                    if(gameClass.scoreSystem.dartsThrown < 3) { // This avoids crash caused by misbehaving timer, occasionally running once more, even though it's been cancelled
                        dartThrow();
                    }
                }

                // Prevents dart from being thrown when popup is closed
                if(viewLock && !inFlight && !menuPopup) {
                    viewLock = !viewLock;
                }

                // Stop velocity calculation
                if(t != null) {
                    t.cancel();
                    t.purge();
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

        });

        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    @Override
    public void render (float delta) {

        final int CURRENT_FPS = (Gdx.graphics.getFramesPerSecond() > 10) ? Gdx.graphics.getFramesPerSecond() : 30;

        // Sets Viewport
        Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
        // Clears Screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Draw 3D graphics to screen
        modelBatch.begin(perspectiveCamera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        /* Draw user interface to screen */
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        switch (gameClass.getGameMode()) {

            case 0:     // Practice
                drawUI_Practice();
                break;

            case 1:     // 501
                drawUI_501();
                break;

            case 2:     // Around the Clock
                drawUI_RTC();
                break;

            case 3:     // Cricket
                drawUI_Cricket();
                break;

            case 4:     // British Cricket
                drawUI_UKCricket();
                break;
        }

        // Velocity Graph
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(new Color(0.5f, 0.5f, 1.0f, 0.6f));

        for(int i = velGraphHead; i < velGraph.length; i++) {
            shapeRenderer.rect((310 + (i * (100 / velGraph.length) - velGraphHead * (100 / velGraph.length))) * scaleConstant, 20 * scaleConstant, (100 / velGraph.length) * scaleConstant, velGraph[i] * scaleConstant);
        }

        for(int i = 0; i < velGraphHead; i++) {
            shapeRenderer.rect((310 + (i * (100 / velGraph.length) + 100 - velGraphHead * (100 / velGraph.length))) * scaleConstant, 20 * scaleConstant, (100 / velGraph.length) * scaleConstant, velGraph[i] * scaleConstant);
        }

        shapeRenderer.setColor(new Color(1.0f, 0.5f, 0.0f, 0.6f));
        shapeRenderer.rect(407 * scaleConstant, (20 + velGraph[(velGraphHead >= 1) ? velGraphHead - 1 : velGraph.length - 1] - 3) * scaleConstant, 6 * scaleConstant, 6 * scaleConstant);

        shapeRenderer.setColor(new Color(1.0f, 0.5f, 0.0f, 0.6f));
        for(int i = 0; i < 10; i++) {
            shapeRenderer.rect((310 + i * 10) * scaleConstant, 94 * scaleConstant, 5 * scaleConstant, scaleConstant);
        }

        shapeRenderer.end();

        // Timer
        if(gameClass.getCompetitionType() == 5) {
            spriteBatch.begin();
            scoreFont.draw(spriteBatch, ServerComms.turnTimer + "", (700 - 20 * String.valueOf(ServerComms.turnTimer).length()) * scaleConstant, 1260 * scaleConstant);
            spriteBatch.end();
        }

        // Game start text
        if(gameClass.scoreSystem.currentPlayer == 0 && !gameClass.gameStarted && gameClass.getGameMode() > 0 && gameClass.getGameMode() <= 3) {
            spriteBatch.begin();
            scoreFontSmall.draw(spriteBatch, text[163], (360 - text[163].length() * 6) * scaleConstant, 950 * scaleConstant);
            spriteBatch.end();
        }

        // Game end
        if(endGame) {
            spriteBatch.begin();

            switch (gameClass.scoreSystem.winner) {

                case 0:
                    scoreFont.draw(spriteBatch, text[145], (360 - text[145].length() * 12) * scaleConstant, 800 * scaleConstant);
                    break;

                case 1:
                    scoreFont.draw(spriteBatch, gameClass.playerNames[0] + " " + text[146], (360 - (text[146].length() + 1 + gameClass.playerNames[0].length()) * 12) * scaleConstant, 800 * scaleConstant);
                    break;

                case 2:
                    scoreFont.draw(spriteBatch, gameClass.playerNames[1] + " " +  text[146], (360 - (text[146].length() + 1 + gameClass.playerNames[0].length()) * 12) * scaleConstant, 800 * scaleConstant);
                    break;

            }
            scoreFontSmall.draw(spriteBatch, text[147], (360 - 6 * text[147].length()) * scaleConstant, 740 * scaleConstant);
            if(ServerComms.disconnection) {
                scoreFontSmall.draw(spriteBatch, text[168], (360 - 6 * text[168].length()) * scaleConstant, 700 * scaleConstant);
            }

            spriteBatch.end();
        }

        // Popup menu
        if(menuPopup) {
            spriteBatch.begin();

            spriteBatch.draw(popup, 160 * scaleConstant, 490 * scaleConstant, 400 * scaleConstant, 300 * scaleConstant);

            scoreFont.draw(spriteBatch, text[135], 170 * scaleConstant, 778 * scaleConstant);
            scoreFont.draw(spriteBatch, text[136], 170 * scaleConstant, 723 * scaleConstant);
            scoreFont.draw(spriteBatch, text[137], (170 + textIndent[137]) * scaleConstant, 566 * scaleConstant);
            scoreFont.draw(spriteBatch, text[138], (170 + textIndent[138]) * scaleConstant, 566 * scaleConstant);

            spriteBatch.end();
        }

        /* AI and Server Opponent */
        if((gameClass.aiTurn || ServerComms.newThrow) && !endGame && !viewLock && !inFlight) {

            if(gameClass.scoreSystem.dartsThrown == 0 && !dartsReset) {
                resetDartPositions();
            }

            float aimingX = (gameClass.getCompetitionType() == 5) ? ServerComms.serverIn[2] : -(gameClass.ai.targetX - 500) * 5.6f;
            float aimingY = (gameClass.getCompetitionType() == 5) ? ServerComms.serverIn[3] : -(gameClass.ai.targetY - 500) * 5.6f;

            float landingX = (gameClass.getCompetitionType() == 5) ? ServerComms.serverIn[0] : gameClass.ai.landingPosX;
            float landingY = (gameClass.getCompetitionType() == 5) ? ServerComms.serverIn[1] : gameClass.ai.landingPosY;

            if(gameClass.aiTurn && !gameClass.ai.throwing && !gameClass.oppTurn) {

                int target = 0;

                // Get target based on score and game mode
                switch (gameClass.getGameMode()) {
                    case 1:
                        target = gameClass.ai.targeting_501(gameClass.scoreSystem.getScore()[1]);
                        break;
                    case 2:
                        target = gameClass.ai.targeting_RTC(gameClass.scoreSystem.getScore()[1]);
                        break;
                    case 3:
                        target = gameClass.ai.targeting_Cricket(gameClass.scoreSystem.getScore(), gameClass.scoreSystem.getInnings());
                        break;
                    case 4:
                        target = gameClass.ai.targeting_UKCricket(gameClass.scoreSystem.getPlayerBatting() == 1);
                        break;
                }

                // Receive location of target from target position array
                gameClass.ai.targetX = gameClass.ai.TARGETS[target][0];
                gameClass.ai.targetY = gameClass.ai.TARGETS[target][1];

                // Overrides target to bullseye if game hasn't started
                if(!gameClass.gameStarted && gameClass.getGameMode() <= 3) {
                    gameClass.ai.targetX = 500;
                    gameClass.ai.targetY = 504;
                }

                // Generate randomised landing site around target
                gameClass.ai.aiThrow();

                gameClass.ai.throwing = true;
            }

            translats = CURRENT_FPS / 4;

            float animLength = CURRENT_FPS / 2;

            if(animTimeCounter < animLength) {
                animTimeCounter++;

                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimingX / animLength * animTimeCounter, aimingY / animLength * animTimeCounter, -500.0f);
                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 89);
                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                perspectiveCamera.position.set(aimingX / animLength * animTimeCounter, aimingY / animLength * animTimeCounter, -2500.0f);
                perspectiveCamera.lookAt(aimingX / animLength * animTimeCounter, aimingY / animLength * animTimeCounter, 18000.0f);
                perspectiveCamera.update();
            } else if(animTimeCounter < animLength * 2) {
                animTimeCounter++;
            } else {

                // Throw dart
                float yPos = (1.65f * (30 / 1000.0f) * translatCount) + (-4.905f * ((30 / 1000.0f) * translatCount) * ((30 / 1000.0f) * translatCount));

                if (translatCount < translats) {
                    // Translate the dart to the calculate position where it would be at that point in flight
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimingX + ((landingX - aimingX / 10000.0f) * 10000.0f) / translats * translatCount, aimingY + (yPos * 10000.0f), (distanceToBoard - 500.0f) / translats * translatCount);
                } else {
                    // Translate the dart to the exact position that it was calculated to land
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation((landingX * 10000.0f), (landingY * 10000.0f), (distanceToBoard - 500.0f) / translats * translatCount);
                }

                // Correct the rotation
                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                // Check whether the animation should be ended
                if(translatCount >= translats  || yPos <= -1.73f) {

                    inFlight = false;
                    if(gameClass.aiTurn) {
                        gameClass.ai.throwing = false;
                    }

                    if(gameClass.getCompetitionType() == 5) {
                        if(ServerComms.backLog == 0) {
                            ServerComms.newThrow = false;
                        } else{
                            // Clear backlog

                            while(true) {
                                if(ServerComms.readsLeft == 4) {
                                    for (int i = 0; i < 4; i++) {
                                        ServerComms.serverIn[i] = ServerComms.serverInBuffer[0][i];
                                        ServerComms.serverInBuffer[0][i] = ServerComms.serverInBuffer[1][i];
                                    }
                                    ServerComms.backLog--;
                                    break;
                                }
                            }
                        }
                    }

                    translatCount = 0;
                    translats = 0;
                    animTimeCounter = 0;

                    if(!gameClass.gameStarted && gameClass.getGameMode() <= 3 && gameClass.getGameMode() > 0) {
                        gameClass.firstThrow(landingX, landingY);
                    } else{
                        gameClass.newThrow(landingX, landingY);
                    }

                    // Camera zoom in on dartboard
                    if(gameClass.scoreSystem.dartsThrown == 0 || endGame) {

                        viewLock = true;

                        setViewPosition(new Random().nextInt(3) + 3);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                setViewPosition(1);
                                viewLock = false;
                            }
                        }, 2000);

                    }
                }

                translatCount++;
            }


        }


        /* Touch Down */
        if(Gdx.input.isTouched()) {

            // Dart aiming
            if(!inFlight && !viewLock && !gameClass.aiTurn && !gameClass.oppTurn) {

                // If player holds down finger during camera closeup, touchDown doesn't get called, but this does
                if(gameClass.scoreSystem.dartsThrown == 0 && !dartsReset) {
                    resetDartPositions();
                }

                if(gameClass.scoreSystem.dartsThrown < 3) {
                    aimX = -(Gdx.input.getX() - (screenWidth / 2f)) * 5.6f;
                    aimY = -(Gdx.input.getY() - (screenHeight / 2f)) * 5.6f;

                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimX, aimY, -500.0f);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 89);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                    perspectiveCamera.position.set(aimX, aimY, -2500.0f);
                    perspectiveCamera.lookAt(aimX, aimY, 18000.0f);
                    perspectiveCamera.update();
                }
            }

        }

    }

    @Override
    public void dispose () {
        // Prevents memory leaks
        modelBatch.dispose();
        spriteBatch.dispose();
        instances.clear();
        ServerComms.disconnectFromServer();
    }

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        perspectiveCamera.viewportWidth = width;
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.update();
    }

    /**
     * Converts velocity data into landing coordinates. The dart is then
     * smoothly animated as it flies through the air towards those coordinates.
     */
    private void dartThrow() {

        // Z Velocity exaggeration
        velZ = (velZ * velZ) / (0.5f) * sensitivityZ;

        // Damping
        if(velX < 11.5 && velX >= 0) {
            velX = (float) Math.pow(2, (velX - 8));
        } else if(velX > -11.5) {
            velX = (float) -Math.pow(2, (velX - 8));
        }

        if(velY >= -20 && velY <= 20) {
            velY = (velY * velY) / 40;
        }

        // Time
        final float time = (distanceToBoard / 10000.0f) / velZ; // Time = Distance / Speed

        // Landing Site x, y
        final float landX = (aimX / 10000.0f) + velX * time;   // Distance = Speed * time
        final float landY = (aimY / 10000.0f) + (velY * time) + (-4.905f * time * time); // Vertical Distance = Speed * Time + 0.5 * Acceleration * time * time

        // Animating dart flight to dartboard
        final Timer t = new Timer();

        // Stops other methods resetting or changing the dart while in flight
        inFlight = true;

        final float THROW_FPS = 30;     // Frames per second drawn to screen for dart throw

        // Send to server
        if(gameClass.getCompetitionType() == 5) {
            ServerComms.sendToServer("" + ((Float.isInfinite(landX)) ? 0 : landX));
            ServerComms.sendToServer("" + ((Float.isNaN(landY)) ? -1000 : landY));
            ServerComms.sendToServer("" + aimX);
            ServerComms.sendToServer("" + aimY);
        }

        t.scheduleAtFixedRate(new TimerTask() {

            int translations = (int) Math.ceil(time * THROW_FPS);
            int translationCount = 1;

            @Override
            public void run() {

                // Calculate the vertical position of the dart at its current point during in its flight
                float yPos = (velY * (THROW_FPS / 1000) * translationCount) + (-4.905f * ((THROW_FPS / 1000) * translationCount) * ((THROW_FPS / 1000) * translationCount));

                if (translationCount < translations) {
                    // Translate the dart to the calculate position where it would be at that point in flight
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimX + ((landX - aimX / 10000.0f) * 10000.0f) / translations * translationCount, aimY + (yPos * 10000.0f), (distanceToBoard - 500.0f) / translations * translationCount);
                } else {
                    // Translate the dart to the exact position that it was calculated to land
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation((landX * 10000.0f), (landY * 10000.0f), (distanceToBoard - 500.0f) / translations * translationCount);
                }

                // Correct the rotation
                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                // Check whether the animation should be ended
                if(translationCount == translations  || yPos <= -1.73f) {

                    t.cancel();
                    t.purge();

                    inFlight = false;

                    if(!gameClass.gameStarted && gameClass.getGameMode() <= 3 && gameClass.getGameMode() > 0) {
                        gameClass.firstThrow(landX, landY);
                    } else {
                        gameClass.newThrow(landX, landY);
                    }

                    // Camera zoom in on dartboard
                    if(gameClass.scoreSystem.dartsThrown == 0 || (gameClass.scoreSystem.getScore()[gameClass.scoreSystem.currentPlayer] == 0 && gameClass.getGameMode() == 1)) {

                        viewLock = true;

                        setViewPosition(new Random().nextInt(3) + 3);

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                setViewPosition(1);
                                viewLock = false;
                            }
                        }, 2000);

                    }
                }

                translationCount++;

            }
        }, 0, 1000 / (int) THROW_FPS);
    }

    /**
     * Sets the position of the viewpoint to a different predefined location.
     * Positions:
     * 1 - Thrower position, behind dart
     * 2 - Observer position, to the side of the thrower
     * 3, 4, 5 - Close ups of dartboard
     *
     * @param position receives an integer, which sets the predefined position to be
     */
    private void setViewPosition(int position) {

        switch(position) {
            case 1:
                // Normal viewport position, as though looking directly at dartboard
                perspectiveCamera.position.set(0.0f, 0.0f, -2500.0f);
                perspectiveCamera.up.set(0.0f, 1.0f, 0.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 18700.0f);
                break;
            case 2:
                // Side on standing view, waiting for turn view
                perspectiveCamera.position.set(-10000.0f, 20.0f, -5000.0f);
                perspectiveCamera.lookAt(2000.0f, 0.0f, 18700.0f);
                break;
            case 3:
                // Close up view of dartboard #1
                perspectiveCamera.position.set(-3000.0f, 0.0f, 14700.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 18700.0f);
                break;
            case 4:
                // Close up view of dartboard #2
                perspectiveCamera.position.set(3000.0f, -1000.0f, 12700.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 18700.0f);
                break;
            case 5:
                // Close up view of dartboard #3
                perspectiveCamera.position.set(0.0f, 3000.0f, 14700.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 18700.0f);
                break;
            default:
                // Prints error message
                System.out.println("ERROR: In setViewPosition(int position) method call - position parameter invalid");
                break;
        }

        perspectiveCamera.update();
    }

    private void resetDartPositions() {
        for(int i = 0; i < 3; i++) {
            dartModelInstances[i].transform.setToTranslation(4000.0f + 100.0f * i, -10000.0f, -1000.0f);
            dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 89);
            dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
        }
        dartsReset = true;
    }

    private void drawUI_501() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(0, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle(200 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, 250 * scaleConstant, 0, darkBlue, lightBlue, darkBlue);

        shapeRenderer.rect(520 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle(470 * scaleConstant, 0, 520 * scaleConstant, 100 * scaleConstant, 520 * scaleConstant, 0, darkBlue, lightBlue, darkBlue);

        // Back button
        shapeRenderer.rect(0, 1205 * scaleConstant, 100 * scaleConstant, 75 * scaleConstant, lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle(100 * scaleConstant, 1205 * scaleConstant, 100 * scaleConstant, 1280 * scaleConstant, 156 * scaleConstant, 1280 * scaleConstant, lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(0, 100 * scaleConstant, 60 * scaleConstant, 150 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.rect(660 * scaleConstant, 100 * scaleConstant, 60 * scaleConstant, 150 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw description text
        descriptionFont.draw(spriteBatch, text[139], 9 * scaleConstant, 90 * scaleConstant);
        descriptionFont.draw(spriteBatch, text[139], (695 - 5 * text[139].length()) * scaleConstant, 90 * scaleConstant);

        // Draw user names
        switch (gameClass.scoreSystem.currentPlayer) {

            case 0:
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[0], 100 * scaleConstant, 150 * scaleConstant);
                playerFont.draw(spriteBatch, gameClass.playerNames[1], (575 - gameClass.playerNames[1].length() * 10) * scaleConstant, 150 * scaleConstant);
                break;

            case 1:
                playerFont.draw(spriteBatch, gameClass.playerNames[0], 100 * scaleConstant, 150 * scaleConstant);
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[1], (580 - gameClass.playerNames[1].length() * 10) * scaleConstant, 150 * scaleConstant);
                break;

        }

        // Draw score text
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[0], (75 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[0]).length())) * scaleConstant, 70 * scaleConstant);
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[1], (570 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[1]).length())) * scaleConstant, 70 * scaleConstant);

        // Draw dart score
        int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.firstPlayer && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn;

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][0][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][0][i], 10 * scaleConstant, (140 + 45 * i) * scaleConstant);
            }
        }

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][1][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][1][i], (710 - 13 * String.valueOf(gameClass.scoreSystem.dartScore[turn][1][i]).length()) * scaleConstant, (140 + 45 * i) * scaleConstant);
            }
        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", 48 * scaleConstant, 1268 * scaleConstant);

        spriteBatch.end();

    }

    private void drawUI_RTC() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(0, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle(200 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, 250 * scaleConstant, 0, darkBlue, lightBlue, darkBlue);

        shapeRenderer.rect(520 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle(470 * scaleConstant, 0, 520 * scaleConstant, 100 * scaleConstant, 520 * scaleConstant, 0, darkBlue, lightBlue, darkBlue);

        // Back button
        shapeRenderer.rect(0, 1205 * scaleConstant, 100 * scaleConstant, 75 * scaleConstant, lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle(100 * scaleConstant, 1205 * scaleConstant, 100 * scaleConstant, 1280 * scaleConstant, 156 * scaleConstant, 1280 * scaleConstant, lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(0, 100 * scaleConstant, 60 * scaleConstant, 150 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.rect(660 * scaleConstant, 100 * scaleConstant, 60 * scaleConstant, 150 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw description text
        descriptionFont.draw(spriteBatch, text[154], 9 * scaleConstant, 90 * scaleConstant);
        descriptionFont.draw(spriteBatch, text[154], (695 - 5 * text[154].length()) * scaleConstant, 90 * scaleConstant);

        // Draw user names
        switch (gameClass.scoreSystem.currentPlayer) {

            case 0:
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[0], 100 * scaleConstant, 150 * scaleConstant);
                playerFont.draw(spriteBatch, gameClass.playerNames[1], (575 - gameClass.playerNames[1].length() * 10) * scaleConstant, 150 * scaleConstant);
                break;

            case 1:
                playerFont.draw(spriteBatch, gameClass.playerNames[0], 100 * scaleConstant, 150 * scaleConstant);
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[1], (580 - gameClass.playerNames[1].length() * 10) * scaleConstant, 150 * scaleConstant);
                break;

        }

        // Draw score text
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[0], (75 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[0]).length())) * scaleConstant, 70 * scaleConstant);
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[1], (570 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[1]).length())) * scaleConstant, 70 * scaleConstant);

        // Draw dart score
        int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.firstPlayer && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn;

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][0][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][0][i], 10 * scaleConstant, (140 + 45 * i) * scaleConstant);
            }
        }

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][1][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][1][i], (710 - 13 * String.valueOf(gameClass.scoreSystem.dartScore[turn][1][i]).length()) * scaleConstant, (140 + 45 * i) * scaleConstant);
            }
        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", 48 * scaleConstant, 1268 * scaleConstant);

        spriteBatch.end();

    }

    private void drawUI_Cricket() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(0, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle(200 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, 250 * scaleConstant, 0, darkBlue, lightBlue, darkBlue);

        shapeRenderer.rect(520 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle(470 * scaleConstant, 0, 520 * scaleConstant, 100 * scaleConstant, 520 * scaleConstant, 0, darkBlue, lightBlue, darkBlue);

        // Back button
        shapeRenderer.rect(0, 1205 * scaleConstant, 100 * scaleConstant, 75 * scaleConstant, lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle(100 * scaleConstant, 1205 * scaleConstant, 100 * scaleConstant, 1280 * scaleConstant, 156 * scaleConstant, 1280 * scaleConstant, lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(75 * scaleConstant, 100 * scaleConstant, 60 * scaleConstant, 150 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.rect(585 * scaleConstant, 100 * scaleConstant, 60 * scaleConstant, 150 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);

        // Cricket Scoreboard
        shapeRenderer.rect(0, 100 * scaleConstant, 75 * scaleConstant, 200 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.rect(645 * scaleConstant, 100 * scaleConstant, 75 * scaleConstant, 200 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.rect(40 * scaleConstant, 103 * scaleConstant, 32 * scaleConstant, 194 * scaleConstant, new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f));
        shapeRenderer.rect(647 * scaleConstant, 103 * scaleConstant, 32 * scaleConstant, 194 * scaleConstant, new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f), new Color(0, 0, 0, 0.7f));

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw description text
        descriptionFont.draw(spriteBatch, text[139], 9 * scaleConstant, 90 * scaleConstant);
        descriptionFont.draw(spriteBatch, text[139], (695 - 5 * text[139].length()) * scaleConstant, 90 * scaleConstant);

        // Draw user names
        switch (gameClass.scoreSystem.currentPlayer) {

            case 0:
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[0], 150 * scaleConstant, 150 * scaleConstant);
                playerFont.draw(spriteBatch, gameClass.playerNames[1], (525 - gameClass.playerNames[1].length() * 10) * scaleConstant, 150 * scaleConstant);
                break;

            case 1:
                playerFont.draw(spriteBatch, gameClass.playerNames[0], 150 * scaleConstant, 150 * scaleConstant);
                currentPlayerFont.draw(spriteBatch, gameClass.playerNames[1], (525 - gameClass.playerNames[1].length() * 12) * scaleConstant, 150 * scaleConstant);
                break;

        }

        // Draw score text
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[0], (75 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[0]).length())) * scaleConstant, 70 * scaleConstant);
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[1], (570 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[1]).length())) * scaleConstant, 70 * scaleConstant);

        // Draw dart score
        int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.firstPlayer && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn;

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][0][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][0][i], 85 * scaleConstant, (140 + 45 * i) * scaleConstant);
            }
        }

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][1][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][1][i], (635 - 13 * String.valueOf(gameClass.scoreSystem.dartScore[turn][1][i]).length()) * scaleConstant, (140 + 45 * i) * scaleConstant);
            }
        }

        // Cricket Scoreboard Text
        String[] tempOpeningsTexts = {"20", "19", "18", "17", "16", "15", "BU"};

        for(int i = 0; i < 7; i++) {

            for(int j = 0; j < 2; j++) {

                if(gameClass.scoreSystem.getInnings()[j][i] >= 3 && gameClass.scoreSystem.getInnings()[1-j][i] < 3) {
                    scoreFontCricketOpen.draw(spriteBatch, tempOpeningsTexts[i], (10 + 680 * j) * scaleConstant, (287 - 27 * i) * scaleConstant);
                } else if(gameClass.scoreSystem.getInnings()[j][i] >= 3 && gameClass.scoreSystem.getInnings()[1-j][i] >= 3) {
                    scoreFontCricketClosed.draw(spriteBatch, tempOpeningsTexts[i], (10 + 680 * j) * scaleConstant, (287 - 27 * i) * scaleConstant);
                } else{
                    scoreFontCricket.draw(spriteBatch, tempOpeningsTexts[i], (10 + 680 * j) * scaleConstant, (287 - 27 * i) * scaleConstant);
                }

                if(gameClass.scoreSystem.getInnings()[j][i] >= 1) {
                    scoreFontCricketMarks.draw(spriteBatch, "/", (50 + 607 * j) * scaleConstant, (287 - 27 * i) * scaleConstant);
                }

                if(gameClass.scoreSystem.getInnings()[j][i] >= 2) {
                    scoreFontCricketMarks.draw(spriteBatch, "\\", (50 + 607 * j) * scaleConstant, (287 - 27 * i) * scaleConstant);
                }

                if(gameClass.scoreSystem.getInnings()[j][i] >= 3) {
                    scoreFontCricketMarks.draw(spriteBatch, "0", (50 + 607 * j) * scaleConstant, (287 - 27 * i) * scaleConstant);
                }

            }

        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", 48 * scaleConstant, 1268 * scaleConstant);

        spriteBatch.end();

    }

    private void drawUI_UKCricket() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.rect(0, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
        shapeRenderer.triangle(200 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, 250 * scaleConstant, 0, darkBlue, lightBlue, darkBlue);

        shapeRenderer.rect(570 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);

        // Back button
        shapeRenderer.rect(0, 1205 * scaleConstant, 100 * scaleConstant, 75 * scaleConstant, lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle(100 * scaleConstant, 1205 * scaleConstant, 100 * scaleConstant, 1280 * scaleConstant, 156 * scaleConstant, 1280 * scaleConstant, lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(0, 100 * scaleConstant, 60 * scaleConstant, 150 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw description text
        descriptionFont.draw(spriteBatch, text[139], 9 * scaleConstant, 90 * scaleConstant);
        descriptionFont.draw(spriteBatch, text[155], (695 - 6 * text[155].length()) * scaleConstant, 90 * scaleConstant);

        // Draw user names
        if(gameClass.scoreSystem.getPlayerBatting() == 0) {
            switch (gameClass.scoreSystem.currentPlayer) {

                case 0:
                    currentPlayerFont.draw(spriteBatch, gameClass.playerNames[gameClass.scoreSystem.getPlayerBatting()], 100 * scaleConstant, 150 * scaleConstant);
                    playerFont.draw(spriteBatch, gameClass.playerNames[1 - gameClass.scoreSystem.getPlayerBatting()], (575 - gameClass.playerNames[1 - gameClass.scoreSystem.getPlayerBatting()].length() * 10) * scaleConstant, 150 * scaleConstant);
                    break;

                case 1:
                    playerFont.draw(spriteBatch, gameClass.playerNames[gameClass.scoreSystem.getPlayerBatting()], 100 * scaleConstant, 150 * scaleConstant);
                    currentPlayerFont.draw(spriteBatch, gameClass.playerNames[1 - gameClass.scoreSystem.getPlayerBatting()], (580 - gameClass.playerNames[1 - gameClass.scoreSystem.getPlayerBatting()].length() * 10) * scaleConstant, 150 * scaleConstant);
                    break;

            }
        } else{
            switch (gameClass.scoreSystem.currentPlayer) {

                case 0:
                    playerFont.draw(spriteBatch, gameClass.playerNames[gameClass.scoreSystem.getPlayerBatting()], 100 * scaleConstant, 150 * scaleConstant);
                    currentPlayerFont.draw(spriteBatch, gameClass.playerNames[1 - gameClass.scoreSystem.getPlayerBatting()], (575 - gameClass.playerNames[1 - gameClass.scoreSystem.getPlayerBatting()].length() * 10) * scaleConstant, 150 * scaleConstant);
                    break;

                case 1:
                    currentPlayerFont.draw(spriteBatch, gameClass.playerNames[gameClass.scoreSystem.getPlayerBatting()], 100 * scaleConstant, 150 * scaleConstant);
                    playerFont.draw(spriteBatch, gameClass.playerNames[1 - gameClass.scoreSystem.getPlayerBatting()], (580 - gameClass.playerNames[1 - gameClass.scoreSystem.getPlayerBatting()].length() * 10) * scaleConstant, 150 * scaleConstant);
                    break;

            }
        }
        scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[1 - gameClass.scoreSystem.getPlayerBatting()], (540 - String.valueOf(gameClass.scoreSystem.getScore()[1 - gameClass.scoreSystem.getPlayerBatting()]).length() * 6) * scaleConstant, 35 * scaleConstant);

        // Draw score text
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getScore()[gameClass.scoreSystem.getPlayerBatting()], (75 + 14 * (3 - String.valueOf(gameClass.scoreSystem.getScore()[gameClass.scoreSystem.getPlayerBatting()]).length())) * scaleConstant, 70 * scaleConstant);
        scoreFont.draw(spriteBatch, "" + gameClass.scoreSystem.getWickets(), (614) * scaleConstant, 60 * scaleConstant);

        // Draw dart score
        int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.getPlayerBatting() && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn - ((gameClass.scoreSystem.currentPlayer == gameClass.scoreSystem.getPlayerBatting()) ? 0 : gameClass.scoreSystem.getPlayerBatting());

        for(int i = 0; i < 3; i++) {
            if(gameClass.scoreSystem.dartNature[turn][gameClass.scoreSystem.getPlayerBatting()][i] > 0) {
                scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][gameClass.scoreSystem.getPlayerBatting()][i], 10 * scaleConstant, (140 + 45 * i) * scaleConstant);
            }
        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", 48 * scaleConstant, 1268 * scaleConstant);

        spriteBatch.end();

    }

    private void drawUI_Practice() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Back button
        shapeRenderer.rect(0, 1205 * scaleConstant, 100 * scaleConstant, 75 * scaleConstant, lightBlue, lightBlue, darkBlue, darkBlue);
        shapeRenderer.triangle(100 * scaleConstant, 1205 * scaleConstant, 100 * scaleConstant, 1280 * scaleConstant, 156 * scaleConstant, 1280 * scaleConstant, lightBlue, darkBlue, darkBlue);

        // Dart score blocks
        shapeRenderer.rect(0, 0, 60 * scaleConstant, 150 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);

        shapeRenderer.end();

        spriteBatch.begin();

        // Draw dart score
        for(int i = 0; i < 3; i++) {
            scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[0][0][i], 10 * scaleConstant, (40 + 45 * i) * scaleConstant);
        }

        // Draw back button chevron
        scoreFont.draw(spriteBatch, "<", 48 * scaleConstant, 1268 * scaleConstant);

        spriteBatch.end();
    }

}
