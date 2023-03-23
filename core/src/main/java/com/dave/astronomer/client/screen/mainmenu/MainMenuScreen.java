package com.dave.astronomer.client.screen.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dave.astronomer.MeloAstronomer;

import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.screen.UIState;
import com.dave.astronomer.common.Constants;
import lombok.Getter;


public class MainMenuScreen implements Screen {
    private UIState activeUI;

    @Getter private UIState mainMenuUI;
    @Getter private UIState multiplayerUI;
    @Getter private UIState directConnectUI;
    @Getter private ConnectErrorUI connectErrorUI;

    //batch shared with all stages
    @Getter private SpriteBatch batch = new SpriteBatch();
    @Getter private OrthographicCamera camera = new OrthographicCamera();
    @Getter private ExtendViewport viewport = new ExtendViewport(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT, camera);


    //background
    private Texture[] backgrounds = new Texture[4];
    private float[] backgroundOffset = new float[backgrounds.length];
    private float maxscrollspeed = 75f;

    public MainMenuScreen() {

        mainMenuUI = new MainMenuUI(createDefaultStage(), this);
        multiplayerUI = new MultiplayerUI(createDefaultStage(), this);
        directConnectUI = new DirectConnectUI(createDefaultStage(), this);
        connectErrorUI = new ConnectErrorUI(createDefaultStage(), this);

        setActiveUI(mainMenuUI);

        //load bg
        AssetManagerResolving assetManager = MeloAstronomer.getInstance().getAssetManager();

        backgrounds[0] = assetManager.get("bglayer.png", Texture.class);
        backgrounds[1] = assetManager.get("layer2.png", Texture.class);
        backgrounds[2] = assetManager.get("layer3.png", Texture.class);
        backgrounds[3] = assetManager.get("layer4.png", Texture.class);


    }
    @Override
    public void show() {


    }

    public void setActiveUI(UIState state) {
        this.activeUI = state;
        Gdx.input.setInputProcessor(state.getStage());

    }
    public Stage createDefaultStage() {
        return new Stage(viewport, batch);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(5/255f,23/255f,50/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        if (activeUI == mainMenuUI) {
            batch.begin();
            renderBg(batch, delta);
            batch.end();
        }





        activeUI.render(delta);
    }

    private void renderBg(SpriteBatch batch, float delta) {
        float height = camera.viewportHeight;
        float width = camera.viewportWidth;



        backgroundOffset[0] += delta * maxscrollspeed / 16;
        backgroundOffset[1] += delta * maxscrollspeed / 8;
        backgroundOffset[2] += delta * maxscrollspeed / 4;
        backgroundOffset[3] += delta * maxscrollspeed / 2;


        for (int i = 0; i < backgroundOffset.length; i++) {
            if (backgroundOffset[i] > width) {
                backgroundOffset[i] = 0;
            }
            Texture texture = backgrounds[i];

            batch.draw(texture, -backgroundOffset[i], 0,
                    width, height);
            batch.draw(texture, -backgroundOffset[i] + width, 0,
                    width, height);
        }
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

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
        batch.dispose();

        mainMenuUI.dispose();
        multiplayerUI.dispose();
        directConnectUI.dispose();



    }


}
