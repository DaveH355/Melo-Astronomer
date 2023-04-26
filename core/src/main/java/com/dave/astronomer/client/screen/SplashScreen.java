package com.dave.astronomer.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.asset.AssetFinder;
import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.ui.LinkedLabel;
import com.esotericsoftware.minlog.Log;



public class SplashScreen extends InputAdapter implements Screen {
    private Stage stage;
    private Table table;
    private Image logo;

    @Override
    public void show() {
        AssetManagerResolving assetManager = new AssetManagerResolving();
        MeloAstronomer game = MeloAstronomer.getInstance();
        Skin skin = game.getSkin();

        AssetFinder assetFinder = new AssetFinder(assetManager);
        assetManager.setPathResolving(true);
        assetFinder.load();


        stage = new Stage();

        table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        logo = new Image(new Texture("splashScreen/studio_logo.png"));
        logo.setScaling(Scaling.fit);
        logo.setAlign(Align.center);
        logo.addAction(Actions.sequence(Actions.alpha(0.0F), Actions.fadeIn(1.75F), Actions.delay(2F), Actions.fadeOut(0.5F)));
        table.add(logo).pad(10);
        table.row();

        Label label = new Label("Loading resources", skin, "light");
        label.setColor(Color.WHITE);
        label.setAlignment(Align.center);
        table.add(label).pad(10);
        table.row();


        Label countLeft = new LinkedLabel(() -> (int) assetManager.getProgress() * 100 + "%", skin, "light");
        label.setColor(Color.WHITE);
        label.setAlignment(Align.center);
        table.add(countLeft).pad(10);
        table.row();

        Gdx.input.setInputProcessor(stage);
        game.setAssetManager(assetManager);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(5/255f,23/255f,50/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        MeloAstronomer game = MeloAstronomer.getInstance();

        if (game.getAssetManager().update()) {
            Log.info(game.getAssetManager().getLoadedAssets() + " assets loaded");
            Log.info("Gdx Version: " + Version.VERSION);

            game.setScreen(new MainMenuScreen());
            return;
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();

    }

}
