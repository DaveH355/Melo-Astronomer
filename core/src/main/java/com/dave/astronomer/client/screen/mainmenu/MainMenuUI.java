package com.dave.astronomer.client.screen.mainmenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.GameScreenConfig;
import com.dave.astronomer.client.asset.AssetFinder;
import com.dave.astronomer.client.screen.GameScreen;
import com.dave.astronomer.client.screen.MainMenuScreen;
import com.dave.astronomer.client.screen.UIState;
import com.github.tommyettinger.textra.TypingLabel;
import com.ray3k.stripe.PopTable;


public class MainMenuUI extends UIState {

    public MainMenuUI(Stage stage, MainMenuScreen screen) {
        super(stage);

        AssetFinder assetFinder = MeloAstronomer.getInstance().getAssetFinder();
        Skin skin = MeloAstronomer.getInstance().getSkin();

        Table center = new Table();
        Table bottomRight = new Table().bottom().right();

        center.setFillParent(true);
        bottomRight.setFillParent(true);


        stage.addActor(center);
        stage.addActor(bottomRight);

        //UI BEGIN
        Image logo = new Image(assetFinder.get("game_logo.png", Texture.class));
        logo.setOrigin(Align.center);
        logo.setScale(3f);
        center.add(logo).pad(10);
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

        TextButton infoButton = new TextButton("?", skin);
        infoButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PopTable popTable = new PopTable(skin) {
                    @Override
                    public void hide() {
                        hide(Actions.delay(0));
                    }

                    @Override
                    public void show(Stage stage) {
                        show(stage, Actions.delay(0));
                    }
                };
                Label title = new Label("Melo Astronomer", skin);
                title.setColor(Color.SKY);
                title.setFontScale(1.2f);

                TypingLabel label = new TypingLabel("""
                    {COLOR=cobalt}* Made by Dave

                    {COLOR=apricot}* UI by Jack
                    """, skin);
                label.skipToTheEnd();

                popTable.add().width(250).row();
                popTable.add(title).align(Align.center).padBottom(20);
                popTable.row();
                popTable.add(label).align(Align.left).padLeft(5);
                popTable.row();
                popTable.add().height(50);

                Texture texture = assetFinder.get("black_bg.png", Texture.class);

                Color color = Color.BLACK.cpy();
                color.a = 0.4f;

                Drawable drawable = new TextureRegionDrawable(texture).tint(color);


                popTable.setHideOnUnfocus(true);
                popTable.setKeepCenteredInWindow(true);
                popTable.setStageBackground(drawable);

                popTable.show(stage);

            }
        });
        bottomRight.add(infoButton).width(32).height(32).pad(2);
    }

    @Override
    public void dispose() {

    }
}
