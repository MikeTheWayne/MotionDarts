package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;

import sun.rmi.runtime.Log;

/**
 * Handles all of the touch input and graphics output specifically for the game
 *
 * @author Michael Wayne
 * @version v0.1.0
 */
public class GameScreen extends ScreenAdapter {
    MotionDarts game;

    private ModelBatch modelBatch;
    private PerspectiveCamera perspectiveCamera;
    private Environment environment;
    private CameraInputController cameraInputController;

    private AssetManager assetManager;

    private ModelInstance dartModelInst1;
    private ModelInstance dartboardModelInst1;
    private ModelInstance environmentModelInst1;

    private Array<ModelInstance> instances = new Array<ModelInstance>();

    private int screenWidth;
    private int screenHeight;

    /**
     * Game screen setup constructor<br>
     * Sets up:<br>
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

        dartboardModelInst1.transform.setToTranslation(0.0f, 0.0f, 23700.0f);
        dartboardModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 90);
        dartboardModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, 180);

        environmentModelInst1.transform.setToTranslation(0.0f, -4700.0f, 23900.0f);
        environmentModelInst1.transform.rotate(0.0f, 1.0f, 0.0f, 90);


        /* Game Environment */
        environment = new Environment();

        // Sets ambient lighting around the game environment
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.0f));
        // Creates a new point light; first three parameters: color, last three: direction
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 0.0f, 7000.0f, 10000.0f, 150000000.0f));


        /* Game Input */
        // InputMultiplexer for handling multiple input sources
        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        // InputAdapter, receives touch input from phone screen
        inputMultiplexer.addProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int touchX, int touchY, int pointer, int button) {
                // Dart aiming

                return true;
            }

            public boolean touchUp(int touchX, int touchY, int pointer, int button) {
                // Dart throwing
                dartThrow();
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

    }

    @Override
    public void dispose () {
        // Prevents memory leaks
        modelBatch.dispose();
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

        // Accelerometer data
        float accelX = Gdx.input.getAccelerometerX();   // Left / Right
        float accelY = Gdx.input.getAccelerometerY();   // Up / Down
        float accelZ = Gdx.input.getAccelerometerZ();   // Forward / Back

        // Rotation Data
        float rotX = Gdx.input.getPitch();
        float rotY = Gdx.input.getRoll();
        float rotZ = Gdx.input.getAzimuth();

        // Gyroscope (rotational acceleration) data
        float gyroX = Gdx.input.getGyroscopeX();
        float gyroY = Gdx.input.getGyroscopeY();
        float gyroZ = Gdx.input.getGyroscopeZ();

        System.out.println("Accelerometer: X - " + accelX + " Y - " + accelY + " Z - " + accelZ + " | Rotation: X - " + rotX + " Y - " + rotY + " Z - " + rotZ + " | Gyroscope: X - " + gyroX + " Y - " + gyroY + " Z - " + gyroZ);

    }
}
