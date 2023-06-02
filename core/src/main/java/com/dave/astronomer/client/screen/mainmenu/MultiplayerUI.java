package com.dave.astronomer.client.screen.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.GameScreenConfig;
import com.dave.astronomer.client.asset.AssetFinder;
import com.dave.astronomer.client.multiplayer.LanServer;
import com.dave.astronomer.client.multiplayer.LanServerDetector;
import com.dave.astronomer.client.screen.GameScreen;
import com.dave.astronomer.client.screen.MainMenuScreen;
import com.dave.astronomer.client.screen.UIState;
import com.esotericsoftware.minlog.Log;
import com.github.tommyettinger.textra.TypingLabel;

import java.io.IOException;


public class MultiplayerUI extends UIState {
    private Table scrollTable;
    private LanServerDetector lanDetector;

    public MultiplayerUI(Stage stage, MainMenuScreen screen) {
        super(stage);


        Skin skin = MeloAstronomer.getInstance().getSkin();;

        try {


            lanDetector = new LanServerDetector();
            lanDetector.start();
        } catch (IOException e) {
            Log.warn("Unable to start LAN server detection: " + e.getMessage());
        }

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);


        //UI BEGIN
        scrollTable = new Table().top().left();
        scrollTable.defaults().pad(5);


        ScrollPane scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setFadeScrollBars(false);

        TypingLabel message = new TypingLabel("Scanning for games on your local network {SICK}. . .", skin);
        message.skipToTheEnd();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.setActiveUI(screen.getMainMenuUI());
            }
        });
        TextButton refreshButton = new TextButton("Refresh", skin);
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                message.restart();
                scrollTable.clear();
            }
        });
        TextButton directConnectButton = new TextButton("Direct Connect", skin);
        directConnectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.setActiveUI(screen.getDirectConnectUI());
            }
        });


        table.add().height(10);
        table.row();
        table.add(message).colspan(5).pad(5);
        table.row();
        table.add(scrollPane).expand().fillY().colspan(5).width(600).pad(10);
        table.row();

        table.add().width(150);

        table.add(directConnectButton).width(200).pad(10);
        table.add(refreshButton).width(150).pad(10);
        table.add(backButton).width(150).pad(10);

        table.add().width(150);

        stage.setScrollFocus(scrollPane);

    }


    @Override
    public void render(float delta) {
        super.render(delta);

        if (lanDetector.dirty) {
            scrollTable.clear();
            lanDetector.getServerList().forEach(this::listServer);

            lanDetector.dirty = false;
        }
    }

    public void listServer(LanServer server) {
        Skin skin = MeloAstronomer.getInstance().getSkin();
        AssetFinder assetFinder = MeloAstronomer.getInstance().getAssetFinder();

        Table table = new Table();
        Table imageTable = new Table();
        Table contentTable = new Table();

        Button button = new Button(skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreenConfig config = new GameScreenConfig();
                config.startServer = false;
                config.address = server.address.getHostAddress();


                MeloAstronomer.getInstance().setScreen(new GameScreen(config));
            }
        });


        Image image = new Image(assetFinder.get("unknown.png", Texture.class));
        imageTable.add(image).expand().fill();


        Label info = new Label(server.serverInfo, skin);
        info.setFontScale(0.75f);

        contentTable.add(new Label(server.serverName, skin)).pad(5).padLeft(15).top();
        contentTable.row();
        contentTable.add(info).pad(5).padLeft(15).left();
        contentTable.row();
        //bottom filler
        contentTable.add().width(10).height(10);

        table.add(imageTable).width(48).height(48).left().padLeft(20);
        table.add(contentTable).expand().left();



        Stack stack = new Stack(
                button,
                table
        );

        scrollTable.add(stack).expandX().fill();
        scrollTable.row();
    }


    @Override
    public void dispose() {
        lanDetector.dispose();
    }
}
