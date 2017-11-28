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

    private ModelInstance dartModelInst1;
    private ModelInstance dartboardModelInst1;
    private ModelInstance environmentModelInst1;

    private Array<ModelInstance> instances = new Array<ModelInstance>();

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
        perspectiveCamera.position.set(0.0f, 20.0f, -2500.0f);
        perspectiveCamera.lookAt(0.0f, 0.0f, 1.0f);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 100000.0f;
        perspectiveCamera.update();


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

        dartModelInst1 = new ModelInstance(dartModel1);
        dartboardModelInst1 = new ModelInstance(dartboardModel1);
        environmentModelInst1 = new ModelInstance(environmentModel1);

        instances.add(dartModelInst1);
        instances.add(dartboardModelInst1);
        instances.add(environmentModelInst1);

        // Moving everything into the right place for setup
        // 100.0f = 1 cm
        // Game plays in the positive z axis (forwards)
        dartModelInst1.transform.setToTranslation(0.0f, 0.0f, -500.0f);
        dartModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 90);
        dartModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 45);

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

                // Revert dart to throwing position -- temporary
                dartModelInst1.transform.setToTranslation(0.0f, 0.0f, -500.0f);
                dartModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 90);
                dartModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 45);

                perspectiveCamera.position.set(0.0f, 20.0f, -2500.0f);
                perspectiveCamera.lookAt(0.0f, 0.0f, 1.0f);
                perspectiveCamera.update();

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

                return true;
            }

            public boolean touchUp(int touchX, int touchY, int pointer, int button) {
                // Dart throwing
                dartThrow();

                // Stop velocity calculation
                t.cancel();
                t.purge();
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
            float aimSensitivity = 5.6f;

            aimX = -(Gdx.input.getX() - (screenWidth / 2f)) * aimSensitivity;
            aimY = -(Gdx.input.getY() - (screenHeight / 2f)) * aimSensitivity;

            dartModelInst1.transform.setToTranslation(aimX, aimY, -500.0f);
            dartModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 90);
            dartModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 45);
            perspectiveCamera.position.set(aimX, aimY + 20.0f, -2500.0f);
            perspectiveCamera.update();

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
     * Receives accelerometer input, which gets translated into landing coordinates.
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
        float time = (distanceToBoard / 10000.0f) / velZ; // Time = Distance / Speed

        // Landing Site x, y
        float landX = (aimX / 10000.0f) + velX * time;   // Distance = Speed * time
        float landY = (aimY / 10000.0f) + (velY * time) + (-4.905f * time * time); // Vertical Distance = Speed * Time + 0.5 * Acceleration * time * time

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

        // Translation to dartboard, to be replaced later with smooth animation
        dartModelInst1.transform.setToTranslation(landX * 10000.0f, landY * 10000.0f, distanceToBoard - 700.0f);
        dartModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 90);
        dartModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 45);
    }
}
