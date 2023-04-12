package com.dave.astronomer.client.screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;

public abstract class UIState implements Disposable {
    @Getter private Stage stage;
    public UIState(Stage stage) {
        this.stage = stage;
    }
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
}
