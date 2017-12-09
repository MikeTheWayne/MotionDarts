package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.utils.Array;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

    private AssetManager assetManager;

    private ModelInstance[] dartModelInstances = new ModelInstance[3];
    private ModelInstance dartboardModelInst1;
    private ModelInstance environmentModelInst1;

    private Array<ModelInstance> instances = new Array<ModelInstance>();

    private int dartsThrown = 0;

    private int screenWidth;
    private int screenHeight;

    private String[] textOut = new String[11];

    private float sensitivityZ = 1.0f;

    private float aimX = 0.0f;
    private float aimY = 0.0f;

    private float distanceToBoard = 18700.0f;

    private float accelX, accelY, accelZ;
    private float velX, velY, velZ;
    private float rotX, rotY;

    private boolean inFlight = false;
    private boolean viewLock = false;

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
    public GameScreen (MotionDarts game) {

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        bitmapFont = new BitmapFont(Gdx.files.internal("consolas.fnt"), Gdx.files.internal("consolas.png"), false);


        /* Viewpoint Setup */
        // PerspectiveCamera setup: Field of Vision, viewpoint width, viewpoint height
        perspectiveCamera = new PerspectiveCamera(70, screenWidth, screenHeight);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 100000.0f;
        setViewPosition(1);


        /* Model Loading */
        // Load assets into game, to be moved into separate load() method, called on startup for efficiency
        assetManager = new AssetManager();

        assetManager.load("dart_01.g3db", Model.class);
        assetManager.load("dartboard_01.g3db", Model.class);
        assetManager.load("environment_01.g3db", Model.class);
        assetManager.finishLoading();

        // Get the loaded models from the assetManager
        Model dartModel1 = assetManager.get("dart_01.g3db", Model.class);
        Model dartboardModel1 = assetManager.get("dartboard_01.g3db", Model.class);
        Model environmentModel1 = assetManager.get("environment_01.g3db", Model.class);

        dartModelInstances[0] = new ModelInstance(dartModel1);
        dartModelInstances[1] = new ModelInstance(dartModel1);
        dartModelInstances[2] = new ModelInstance(dartModel1);
        dartboardModelInst1 = new ModelInstance(dartboardModel1);
        environmentModelInst1 = new ModelInstance(environmentModel1);

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


        /* Game Environment */
        environment = new Environment();

        // Sets ambient lighting around the game environment
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.0f));
        // Creates a new point light; first three parameters: color, next three: position, then intensity
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 0.0f, 7000.0f, 8000.0f, 150000000.0f));


        /* Game Input */
        // InputMultiplexer for handling multiple input sources
        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        // InputAdapter, receives touch input from phone screen
        inputMultiplexer.addProcessor(new InputAdapter() {

            Timer t;

            @Override
            public boolean touchDown(int touchX, int touchY, int pointer, int button) {

                if(!inFlight && !viewLock) {
                    // Revert darts to be in hand
                    if(dartsThrown == 3) {
                        dartsThrown = 0;

                        for(int i = 0; i < 3; i++) {
                            dartModelInstances[i].transform.setToTranslation(4000.0f + 100.0f * i, -10000.0f, -1000.0f);
                            dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                            dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
                        }
                    }

                    // Places dart in right hand from left hand (avoids annoying graphical flash of the dart not being centred)
                    dartModelInstances[dartsThrown].transform.setToTranslation(0.0f, 0.0f, -500.0f);
                    dartModelInstances[dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                    dartModelInstances[dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                    setViewPosition(1);

                    /* Velocity Calculation */
                    // Reset velocity
                    velX = 0;
                    velY = 0;
                    velZ = 0;

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
                            accelX = -(accelX + (float) (9.80665 * Math.sin(Math.toRadians(rotY)) * Math.cos(Math.toRadians(rotX))));
                            accelY = accelY + (float) (9.80665 * Math.sin(Math.toRadians(rotX)));
                            accelZ = -(accelZ - (float) (9.80665 * Math.cos(Math.toRadians(rotX)) * Math.cos(Math.toRadians(rotY))));

                            // Velocity calculation
                            velX += accelX / 50;
                            velY += accelY / 50;
                            velZ += accelZ / 50;

                        }
                    }, 0, 20);
                }

                return true;
            }

            public boolean touchUp(int touchX, int touchY, int pointer, int button) {
                if(!inFlight && !viewLock) {
                    // Dart throwing
                    if(dartsThrown < 3) { // This avoids crash caused by misbehaving gopnik timer, occasionally running once more, even though it's been cancelled
                        dartThrow();
                    }

                    // Stop velocity calculation
                    t.cancel();
                    t.purge();
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

        // Draw 2D Graphics to screen
        spriteBatch.begin();

        // Diagnostic text output
        for(int i = 0; i < textOut.length; i++) {
            if(textOut[i] != null) {
                bitmapFont.draw(spriteBatch, textOut[i], 10, screenHeight - 20 * (i + 1));
            }
        }

        spriteBatch.end();

        // Touch Down
        if(Gdx.input.isTouched()) {

            // Dart aiming
            if(!inFlight && !viewLock) {
                float aimSensitivity = 5.6f;

                // isTouched() is called before InputAdapter, this avoids crashes
                if(dartsThrown < 3) {
                    aimX = -(Gdx.input.getX() - (screenWidth / 2f)) * aimSensitivity;
                    aimY = -(Gdx.input.getY() - (screenHeight / 2f)) * aimSensitivity;

                    dartModelInstances[dartsThrown].transform.setToTranslation(aimX, aimY, -500.0f);
                    dartModelInstances[dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                    dartModelInstances[dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

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
        assetManager.dispose();
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
        velZ = (velZ * velZ) / (0.5f * sensitivityZ);

        // Damping
        if(velX < 11.5 && velX >= 0) {
            velX = (float) Math.pow(2, (velX - 8));
        } else if(velX > -11.5) {
            velX = (float) -Math.pow(2, (velX - 8));
        }

        if(velY >= -20 && velY <= 20) {
            velY = (velY * velY) / 20;
        }

        // Time
        final float time = (distanceToBoard / 10000.0f) / velZ; // Time = Distance / Speed

        // Landing Site x, y
        final float landX = (aimX / 10000.0f) + velX * time;   // Distance = Speed * time
        final float landY = (aimY / 10000.0f) + (velY * time) + (-4.905f * time * time); // Vertical Distance = Speed * Time + 0.5 * Acceleration * time * time

        // Temporary test output to screen
        textOut[0] = String.valueOf(accelX);
        textOut[1] = String.valueOf(accelY);
        textOut[2] = String.valueOf(accelZ);

        textOut[3] = String.valueOf(rotX);
        textOut[4] = String.valueOf(rotY);

        textOut[5] = String.valueOf(time);

        textOut[6] = String.valueOf(landX);
        textOut[7] = String.valueOf(landY);

        textOut[8] = String.valueOf(velX);
        textOut[9] = String.valueOf(velY);
        textOut[10] = String.valueOf(velZ);

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
                    dartModelInstances[dartsThrown].transform.setToTranslation(aimX + ((landX - aimX / 10000.0f) * 10000.0f) / translations * translationCount, aimY + (yPos * 10000.0f), (distanceToBoard - 500.0f) / translations * translationCount);
                } else {
                    // Translate the dart to the exact position that it was calculated to land
                    dartModelInstances[dartsThrown].transform.setToTranslation((landX * 10000.0f), (landY * 10000.0f), (distanceToBoard - 500.0f) / translations * translationCount);
                }

                // Correct the rotation
                dartModelInstances[dartsThrown].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                dartModelInstances[dartsThrown].transform.rotate(0.0f, 1.0f, 0.0f, 45);

                // Check whether the animation should be ended
                if(translationCount == translations  || yPos <= -1.73f) {

                    t.cancel();
                    t.purge();

                    inFlight = false;

                    dartsThrown++;

                    // Camera zoom in on dartboard
                    if(dartsThrown == 3) {

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
     * @param position
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
