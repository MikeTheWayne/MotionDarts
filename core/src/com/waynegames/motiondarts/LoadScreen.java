package com.waynegames.motiondarts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LoadScreen extends ScreenAdapter {
    MotionDarts game;

    private ShapeRenderer shapeRenderer;
    private int loadingStage = 0;

    LoadScreen(MotionDarts game) {
        this.game = game;

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

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
        shapeRenderer.begin();
        shapeRenderer.setColor(1.0f, 0.0f, 1.0f, 1.0f);
        shapeRenderer.rect(60, 600, 600, 80);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1.0f, 0.0f, 1.0f, 1.0f);
        shapeRenderer.rect(60, 600, 120 * loadingStage, 80);
        shapeRenderer.end();

    }
}
