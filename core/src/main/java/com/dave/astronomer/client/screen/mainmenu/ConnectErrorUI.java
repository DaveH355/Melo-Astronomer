package com.dave.astronomer.client.screen.mainmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.screen.MainMenuScreen;
import com.dave.astronomer.client.screen.UIState;


public class ConnectErrorUI extends UIState {
    private Label errorLabel;

    public ConnectErrorUI(Stage stage, MainMenuScreen screen) {
        super(stage);

        Skin skin = MeloAstronomer.getInstance().getSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);


        errorLabel = new Label("", skin);
        errorLabel.setWrap(true);
        errorLabel.setAlignment(Align.center);


        TextButton back = new TextButton("Back to server list", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.setActiveUI(screen.getMultiplayerUI());
            }
        });


        table.add(errorLabel).expandX().fillX().padBottom(10);
        table.row();
        table.add(back).width(250);


    }

    @Override
    public void dispose() {

    }

    public void postError(Exception e) {
        errorLabel.setText(e.toString());
    }
}
