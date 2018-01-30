package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
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

    /* Customisation Variables */
    static int selectedLocation = 0;
    static int selectedDart = 0;

    static String[] locationFiles = {"environment_01", "", "", ""};
    static String[] dartFiles = {"dart_01", "", "", ""};

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

        /* Font Setup */
        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfbbold.ttf"));
        freeTypeFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        freeTypeFontParameter.color = new Color(255, 255, 255, 255);
        freeTypeFontParameter.borderColor = new Color(0, 0, 132, 255);
        freeTypeFontParameter.borderWidth = 2;

        // Current player font
        freeTypeFontParameter.size = 40 * scaleConstant;
        currentPlayerFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        // Score Fonts
        freeTypeFontParameter.size = 60 * scaleConstant;

        scoreFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter);

        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/agencyfb.ttf"));

        freeTypeFontParameter.size = 30 * scaleConstant;
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
        dartModelInstances[0].transform.rotate(1.0f, 0.0f, 0.0f, 90);
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
                    game.setScreen(new MenuScreen(game));
                    MenuScreen.menuScreen = 8;
                    endGame = false;
                    gameClass.writeSaveData();
                    dispose();
                }

                if(!inFlight && !viewLock) {
                    // Revert darts to be in hand
                    if(gameClass.scoreSystem.dartsThrown == 0 && !dartsReset) {
                        for(int i = 0; i < 3; i++) {
                            dartModelInstances[i].transform.setToTranslation(4000.0f + 100.0f * i, -10000.0f, -1000.0f);
                            dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                            dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
                        }
                        dartsReset = true;
                    }

                    // Places dart in right hand from left hand (avoids annoying graphical flash of the dart not being centred)
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(0.0f, 0.0f, -500.0f);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                    setViewPosition(1);

                    /* Velocity Calculation */
                    // Reset velocity
                    velX = 0;
                    velY = 0;
                    velZ = 0;

                    resetTimer = 0;

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
                        MenuScreen.menuScreen = 8;
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

                    // Stop velocity calculation
                    t.cancel();
                    t.purge();
                }

                if(!inFlight && !viewLock) {
                    // Dart throwing
                    if(gameClass.scoreSystem.dartsThrown < 3) { // This avoids crash caused by misbehaving gopnik timer, occasionally running once more, even though it's been cancelled
                        dartThrow();
                    }

                    // Stop velocity calculation
                    t.cancel();
                    t.purge();
                }

                // Prevents dart from being thrown when popup is closed
                if(viewLock && !inFlight && !menuPopup) {
                    viewLock = !viewLock;
                }
                return true;
            }

        });

        Gdx.input.setInputProcessor(inputMultiplexer);

    }

    @Override
    public void render (float delta) {

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

        Color darkBlue = new Color(0, 0, 0.6f, 0.6f);
        Color lightBlue = new Color(0, 0, 0.8f, 0.6f);

        switch (gameClass.getGameMode()) {

            case 1:     // 501

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
                int turn = (gameClass.scoreSystem.dartsThrown == 0 && gameClass.scoreSystem.currentPlayer == 0 && gameClass.scoreSystem.turn > 0) ? gameClass.scoreSystem.turn - 1 : gameClass.scoreSystem.turn;

                for(int i = 0; i < 3; i++) {
                    scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][0][i], 10 * scaleConstant, (140 + 45 * i) * scaleConstant);
                }

                for(int i = 0; i < 3; i++) {
                    scoreFontSmall.draw(spriteBatch, "" + gameClass.scoreSystem.dartScore[turn][1][i], (710 - 13 * String.valueOf(gameClass.scoreSystem.dartScore[turn][1][i]).length()) * scaleConstant, (140 + 45 * i) * scaleConstant);
                }

                // Draw back button chevron
                scoreFont.draw(spriteBatch, "<", 48 * scaleConstant, 1268 * scaleConstant);

                spriteBatch.end();

                break;

            case 2:     // Around the Clock

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
                break;

            case 3:     // Cricket

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
                break;

            case 4:     // British Cricket

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

                shapeRenderer.rect(0, 0, 200 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
                shapeRenderer.triangle(200 * scaleConstant, 0, 200 * scaleConstant, 100 * scaleConstant, 250 * scaleConstant, 0, darkBlue, lightBlue, darkBlue);

                shapeRenderer.rect(620 * scaleConstant, 0, 100 * scaleConstant, 100 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
                shapeRenderer.rect(650 * scaleConstant, 100 * scaleConstant, 70 * scaleConstant, 40 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);
                shapeRenderer.triangle(620 * scaleConstant, 100 * scaleConstant, 650 * scaleConstant, 140 * scaleConstant, 650 * scaleConstant, 100 * scaleConstant, darkBlue, lightBlue, darkBlue);

                // Back button
                shapeRenderer.rect(0, 1205 * scaleConstant, 100 * scaleConstant, 75 * scaleConstant, lightBlue, lightBlue, darkBlue, darkBlue);
                shapeRenderer.triangle(100 * scaleConstant, 1205 * scaleConstant, 100 * scaleConstant, 1280 * scaleConstant, 156 * scaleConstant, 1280 * scaleConstant, lightBlue, darkBlue, darkBlue);

                // Dart score blocks
                shapeRenderer.rect(0, 100 * scaleConstant, 75 * scaleConstant, 200 * scaleConstant, darkBlue, darkBlue, lightBlue, lightBlue);

                shapeRenderer.end();
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


        /* Touch Down */
        if(Gdx.input.isTouched()) {

            // Dart aiming
            if(!inFlight && !viewLock) {

                // If player holds down finger during camera closeup, touchDown doesn't get called, but this does
                if(gameClass.scoreSystem.dartsThrown == 0 && !dartsReset) {
                    for(int i = 0; i < 3; i++) {
                        dartModelInstances[i].transform.setToTranslation(4000.0f + 100.0f * i, -10000.0f, -1000.0f);
                        dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                        dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
                        dartsReset = true;
                    }
                }

                float aimSensitivity = 5.6f;

                // isTouched() is called before InputAdapter, this avoids crashes
                if(gameClass.scoreSystem.dartsThrown < 3) {
                    aimX = -(Gdx.input.getX() - (screenWidth / 2f)) * aimSensitivity;
                    aimY = -(Gdx.input.getY() - (screenHeight / 2f)) * aimSensitivity;

                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.setToTranslation(aimX, aimY, -500.0f);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                    dartModelInstances[gameClass.scoreSystem.dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                    perspectiveCamera.position.set(aimX, aimY + 20.0f, -2500.0f);
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

                    gameClass.newThrow(landX, landY);

                    // Camera zoom in on dartboard
                    if(gameClass.scoreSystem.dartsThrown == 0) {

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
                perspectiveCamera.position.set(0.0f, 20.0f, -2500.0f);
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
}
