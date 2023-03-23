package com.dave.astronomer.client.screen.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.GameScreenConfig;
import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.screen.GameScreen;
import com.dave.astronomer.client.screen.UIState;


public class MainMenuUI extends UIState {

    public MainMenuUI(Stage stage, MainMenuScreen screen) {
        super(stage);

        AssetManagerResolving assetManager = MeloAstronomer.getInstance().getAssetManager();
        Skin skin = MeloAstronomer.getInstance().getSkin();

        Table center = new Table();
        Table bottomRight = new Table().bottom().right();

        center.setFillParent(true);
        bottomRight.setFillParent(true);

        stage.addActor(center);
        stage.addActor(bottomRight);

        //UI BEGIN
        //UI BEGIN
        Image logo = new Image(assetManager.get("game_logo.png", Texture.class));
        logo.setOrigin(Align.center);
        logo.setScale(3f);
        center.add(logo).pad(20).padBottom(50);
        center.row();

        TextButton singlePlayerButton = new TextButton("Singleplayer", skin);
        singlePlayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreenConfig config = new GameScreenConfig();
                config.startServer = true;
                config.address = "localhost";
                MeloAstronomer.getInstance().setScreen(new GameScreen(config));
            }
        });
        center.add(singlePlayerButton).width(200).pad(10);
        center.row();

        TextButton multiplayerButton = new TextButton("Multiplayer", skin);
        multiplayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.setActiveUI(screen.getMultiplayerUI());
            }
        });
        center.add(multiplayerButton).width(200).pad(10);
        center.row();


        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MeloAstronomer.getInstance().dispose();
            }
        });
        center.add(quitButton).width(200).pad(10);

        TextButton helpButton = new TextButton("?", skin);
        bottomRight.add(helpButton).width(32).height(32).pad(2);
    }

    @Override
    public void dispose() {

    }
}
