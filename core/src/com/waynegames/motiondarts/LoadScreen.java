package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.Menu;

public class LoadScreen extends ScreenAdapter {
    MotionDarts game;

    //private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private double scaleConstant;

    private int loadingStage = 0;

    private Sprite load1, load2, load3, load4;

    LoadScreen(MotionDarts game) {
        this.game = game;

        scaleConstant = Gdx.graphics.getWidth() / 720.00;

        //shapeRenderer = new ShapeRenderer();
        //shapeRenderer.setAutoShapeType(true);
        spriteBatch = new SpriteBatch();

        AssetManager am = new AssetManager();

        am.load("textures/load_flight.png", Texture.class);
        am.load("textures/load_flightshaft.png", Texture.class);
        am.load("textures/load_shaft.png", Texture.class);
        am.load("textures/load_point.png", Texture.class);

        am.finishLoading();

        Texture load1Texture = am.get("textures/load_flight.png", Texture.class);
        Texture load2Texture = am.get("textures/load_flightshaft.png", Texture.class);
        Texture load3Texture = am.get("textures/load_shaft.png", Texture.class);
        Texture load4Texture = am.get("textures/load_point.png", Texture.class);

        load1 = new Sprite(load1Texture);
        load2 = new Sprite(load2Texture);
        load3 = new Sprite(load3Texture);
        load4 = new Sprite(load4Texture);

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        switch (loadingStage) {
            case 0:
                MotionDarts.hitZones = MotionDarts.loadHitZones();
                loadingStage++;
                break;

            case 1:
                FileHandle langFile = Gdx.files.local("langSave.txt");
                if(langFile.exists()) {
                    MenuScreen.language = Integer.parseInt(langFile.readString());
                }
                MotionDarts.loadLanguage(MenuScreen.language);
                loadingStage++;
                break;

            case 2:
                FileHandle custFile = Gdx.files.local("customisationSave.txt");
                if(custFile.exists()) {
                    String[] input = custFile.readString().split("\n");
                    GameScreen.selectedDart = Integer.valueOf(input[0]);
                    GameScreen.selectedLocation = Integer.valueOf(input[1]);
                    GameScreen.tempSelectedLocation = Integer.valueOf(input[1]);
                }
                FileHandle setFile = Gdx.files.local("settingsSave.txt");
                if(setFile.exists()) {
                    String[] input = setFile.readString().split("\n");
                    GameScreen.sensitivityZ = Float.valueOf(input[0]);
                    MenuScreen.sound = Boolean.valueOf(input[1]);
                    MenuScreen.music = Boolean.valueOf(input[2]);
                }
                loadingStage++;
                break;

            case 3:
                MotionDarts.loadAssets();
                loadingStage++;
                break;

            default:
                loadingStage = 5;
                game.setScreen(new MenuScreen(game));
                break;
        }

        // Sets Viewport
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Clears Screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Draw loading bar
        /*shapeRenderer.begin();
        shapeRenderer.setColor(1.0f, 0.0f, 1.0f, 1.0f);
        shapeRenderer.rect(60, 600, 600, 80);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 0.0f, 1.0f, 1.0f);
        shapeRenderer.rect(60, 600, 120 * loadingStage, 80);
        shapeRenderer.end();*/

        spriteBatch.begin();
        switch (loadingStage) {
            case 5:
                spriteBatch.draw(load1, (int) (80 * scaleConstant), (int) (580 * scaleConstant), (int) (161 * scaleConstant), (int) (120 * scaleConstant));
                spriteBatch.draw(load4, (int) (556 * scaleConstant), (int) (637 * scaleConstant), (int) (85 * scaleConstant), (int) (6 * scaleConstant));
                spriteBatch.draw(load3, (int) (361 * scaleConstant), (int) (629 * scaleConstant), (int) (195 * scaleConstant), (int) (22 * scaleConstant));
                spriteBatch.draw(load2, (int) (195 * scaleConstant), (int) (630 * scaleConstant), (int) (166 * scaleConstant), (int) (20 * scaleConstant));
                break;
            case 4:
                spriteBatch.draw(load1, (int) (80 * scaleConstant), (int) (580 * scaleConstant), (int) (161 * scaleConstant), (int) (120 * scaleConstant));
                spriteBatch.draw(load3, (int) (361 * scaleConstant), (int) (629 * scaleConstant), (int) (195 * scaleConstant), (int) (22 * scaleConstant));
                spriteBatch.draw(load2, (int) (195 * scaleConstant), (int) (630 * scaleConstant), (int) (166 * scaleConstant), (int) (20 * scaleConstant));
                break;
            case 3:
                spriteBatch.draw(load1, (int) (80 * scaleConstant), (int) (580 * scaleConstant), (int) (161 * scaleConstant), (int) (120 * scaleConstant));
                spriteBatch.draw(load2, (int) (195 * scaleConstant), (int) (630 * scaleConstant), (int) (166 * scaleConstant), (int) (20 * scaleConstant));
                break;
            default:
                spriteBatch.draw(load1, (int) (80 * scaleConstant), (int) (580 * scaleConstant), (int) (161 * scaleConstant), (int) (120 * scaleConstant));
                break;
        }
        spriteBatch.end();

    }
}
