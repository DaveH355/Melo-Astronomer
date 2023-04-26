package com.dave.astronomer.client.screen.mainmenu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.GameScreenConfig;
import com.dave.astronomer.client.screen.GameScreen;
import com.dave.astronomer.client.screen.MainMenuScreen;
import com.dave.astronomer.client.screen.UIState;


public class DirectConnectUI extends UIState {

    public DirectConnectUI(Stage stage, MainMenuScreen screen) {
        super(stage);
        Skin skin = MeloAstronomer.getInstance().getSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);


        Label label = new Label("Server Address", skin);

        TextField address = new TextField("", skin);
        TextButton join = new TextButton("Join Server", skin);
        join.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (join.isDisabled()) return;
                GameScreenConfig config = new GameScreenConfig();
                config.address = address.getText();
                config.startServer = false;

                MeloAstronomer.getInstance().setScreen(new GameScreen(config));
            }
        });
        join.setDisabled(true);

        address.setAlignment(Align.center);
        address.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int length = address.getText().length();
                join.setDisabled(length == 0);
            }
        });


        TextButton back = new TextButton("Back", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.setActiveUI(screen.getMultiplayerUI());
            }
        });


        table.add(label).left().padBottom(10);
        table.row();
        table.add(address).width(250).padBottom(20);
        table.row();
        table.add(join).width(250).padBottom(10);
        table.row();
        table.add(back).width(250);

    }

    @Override
    public void dispose() {

    }
}
