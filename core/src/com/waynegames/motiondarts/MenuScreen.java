package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    private BitmapFont menuFont;

    private Sprite defaultButton;
    private Sprite settingsButton;
    private Sprite languageButton;
    private Sprite exitButton;
    private Sprite title;

    private static Array<ModelInstance> instances = new Array<ModelInstance>();

    private int screenWidth;
    private int screenHeight;

    private Timer animTimer;

    private int language = 0;
    private String[][] menuText = {{"SINGLE PLAYER", "MULTI-PLAYER", "CUSTOMISE"}, {}};
    private int[][] menuTextIndent = {{19, 25, 45}};

    MenuScreen(final MotionDarts game) {

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

        menuFont = new BitmapFont(Gdx.files.internal("agencyfb.fnt"), Gdx.files.internal("agencyfb.png"), false);

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
        Model dartModel1 = MotionDarts.assetManager.get("dart_01.g3db", Model.class);
        Model dartboardModel1 = MotionDarts.assetManager.get("dartboard_01.g3db", Model.class);
        Model environmentModel1 = MotionDarts.assetManager.get("environment_01.g3db", Model.class);
        Model spaceSurroundModel = MotionDarts.assetManager.get("menu_spacesurround.g3db", Model.class);

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
        Texture defaultButtonTexture = MotionDarts.assetManager.get("defaultButton.png", Texture.class);
        Texture settingsButtonTexture = MotionDarts.assetManager.get("settingsButton.png", Texture.class);
        Texture languageButtonTexture = MotionDarts.assetManager.get("languageButton.png", Texture.class);
        Texture exitButtonTexture = MotionDarts.assetManager.get("exitButton.png", Texture.class);
        Texture titleButtonTexture = MotionDarts.assetManager.get("title.png", Texture.class);

        defaultButton = new Sprite(defaultButtonTexture);
        settingsButton = new Sprite(settingsButtonTexture);
        languageButton = new Sprite(languageButtonTexture);
        exitButton = new Sprite(exitButtonTexture);
        title = new Sprite(titleButtonTexture);

        /* Game Environment */
        environment = new Environment();

        // Sets ambient lighting
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.0f));
        // Creates a new point light; first three parameters: color, next three: position, then intensity
        environment.add(new PointLight().set(0.6f, 0.6f, 0.8f, 0.0f, 4000.0f, -10000.0f, 150000000.0f));

        /* Background Animation */
        animTimer = new Timer();

        animTimer.scheduleAtFixedRate(new TimerTask() {

            int timeCounter = 0;
            int dart = 0;

            float[] dartY = new float[3];
            float[] dartZ = new float[3];
            float[] rot = new float[3];

            int throwTime = 0;

            @Override
            public void run() {

                // Constant dartboard rotation
                GameScreen.dartboardModelInst1.transform.rotate(1.0f, 0.0f, 0.0f, -0.15f);
                spaceSurroundModelInst.transform.rotate(1.0f, 0.0f, 0.0f, -0.05f);

                // Makes darts rotate with the dartboard, by moving them at different rates, depending on distance from dartboard centre
                for(int i = 0; i < dart; i++) {
                    if(i < 3) {
                        rot[i] += 0.15f;
                        GameScreen.dartModelInstances[i].transform.setToTranslation(0.0f - (float) Math.cos(Math.toRadians(-rot[i] + 90))
                                * 2.0f * dartY[i], dartY[i] * (float) Math.sin(Math.toRadians(-rot[i] +90)) * 2.0f, dartZ[i]);
                        GameScreen.dartModelInstances[i].transform.rotate(1.0f, 0.0f, 0.0f, 90);
                        GameScreen.dartModelInstances[i].transform.rotate(0.0f, 1.0f, 0.0f, 45);
                    }
                }

                if(dart < 3) {

                    // Throws a dart onto the rotating board
                    if (timeCounter == throwTime + 120) {

                        // Generates a random y position for the dart
                        dartY[dart] = new Random().nextInt(600) + 200.0f;
                        dartZ[dart] = -425.0f;

                        dart++;

                        throwTime = new Random().nextInt(120);

                        timeCounter = 0;
                    } else {
                        timeCounter++;
                    }

                } else{
                    // Resets the darts once they have rotated out of view, so that they can be thrown in again
                    for(int i = 0; i < 3; i++) {

                        if(rot[i] > 130) {

                            dartZ[i] = -19000.0f;

                            if(i == 2) {
                                for(int j = 0; j < 3; j++) {
                                    rot[j] = 0;
                                }
                                dart = 0;
                            }
                        }
                    }

                }

            }
        }, 0, 1000 / 60);

        /* Touch Input */
        InputAdapter inputAdapter = new InputAdapter() {
            @Override
            public boolean touchDown(int touchX, int touchY, int pointer, int button) {

                return true;
            }

            public boolean touchUp(int touchX, int touchY, int pointer, int button) {

                if(touchX >= screenWidth / 2 - defaultButton.getWidth() / 2 && touchX <= screenWidth / 2 - defaultButton.getWidth() / 2 + 300) {
                    if(touchY >= screenHeight / 16 * 7 && touchY <= screenHeight / 16 * 7 + 150) {
                        animTimer.cancel();
                        game.setScreen(new GameScreen(game));
                        GameScreen.gameClass = new GameClass(1, 4);
                    } else if(touchY >= screenHeight / 16 * 7 - 200 && touchY <= screenHeight / 16 * 7 - 350) {

                    } else if(touchY >= screenHeight / 16 * 7 - 400 && touchY <= screenHeight / 16 * 7 - 550) {

                    }
                }

                if(touchY >= screenHeight - 90 && touchY <= screenHeight - 10) {
                    if(touchX >= 10 && touchX <= 90) {

                    } else if(touchX >= 100 && touchX <= 180) {

                    } else if(touchX >= screenWidth - 90 && touchX <= screenWidth - 10) {
                        Gdx.app.exit();
                    }
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

        /* Draw 3D graphics to screen */
        modelBatch.begin(perspectiveCamera);
        modelBatch.render(instances, environment);
        modelBatch.end();

        /* Draw 2D graphics over the 3D graphics */
        spriteBatch.begin();

        // Title
        spriteBatch.draw(title, -screenWidth / 8, screenHeight - screenWidth / 16 * 11, screenWidth / 8 * 10, screenWidth / 8 * 5);

        // Main Buttons
        spriteBatch.draw(defaultButton, screenWidth / 2 - defaultButton.getWidth() / 2, screenHeight / 16 * 7);
        spriteBatch.draw(defaultButton, screenWidth / 2 - defaultButton.getWidth() / 2, screenHeight / 16 * 7 - 200);
        spriteBatch.draw(defaultButton, screenWidth / 2 - defaultButton.getWidth() / 2, screenHeight / 16 * 7 - 400);

        // Small Buttons
        spriteBatch.draw(settingsButton, 10, 10);
        spriteBatch.draw(languageButton, 100, 10);
        spriteBatch.draw(exitButton, screenWidth - 90, 10);

        // Main Button Text
        menuFont.draw(spriteBatch, menuText[language][0], screenWidth / 2 - defaultButton.getWidth() / 2 + menuTextIndent[language][0], screenHeight / 16 * 7 + 96);
        menuFont.draw(spriteBatch, menuText[language][1], screenWidth / 2 - defaultButton.getWidth() / 2 + menuTextIndent[language][1], screenHeight / 16 * 7 - 200 + 96);
        menuFont.draw(spriteBatch, menuText[language][2], screenWidth / 2 - defaultButton.getWidth() / 2 + menuTextIndent[language][2], screenHeight / 16 * 7 - 400 + 96);

        spriteBatch.end();
	}
	
	@Override
	public void dispose () {
        modelBatch.dispose();
        spriteBatch.dispose();
        GameScreen.instances.clear();
        MotionDarts.assetManager.dispose();
	}

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        perspectiveCamera.viewportWidth = width;
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.update();
    }
}
