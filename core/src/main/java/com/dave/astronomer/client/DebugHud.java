package com.dave.astronomer.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.ui.Linkable;
import com.dave.astronomer.client.ui.LinkedLabel;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.Constants;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;


public class DebugHud implements Disposable {

    @Getter
    private Stage stage;
    private Table table;
    public DebugHud(SpriteBatch batch) {
        stage = new Stage(new ExtendViewport(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT), batch);
        table = new Table();
        table.setFillParent(true);

        addMetric("x/y", () -> {
            MainPlayer player = GameState.getInstance().getMainPlayer();
            return String.format("%f / %f", player.getPosition().x, player.getPosition().y);
        });
        addMetric("Camera w/h/zoom", () -> {
            OrthographicCamera camera = GameState.getInstance().getGameCamera();
            return String.format("%f / %f / %f", camera.viewportWidth, camera.viewportHeight, camera.zoom);
        });
        addMetric("FPS", () -> String.valueOf(Gdx.graphics.getFramesPerSecond()));
        addMetric("Memory", () -> {
            double totalMemoryMB = Runtime.getRuntime().totalMemory() / 1000000f;
            double usedMemoryMB = Gdx.app.getJavaHeap() / 1000000f;
            return String.format("%.2f / %.2f MB", usedMemoryMB, totalMemoryMB);
        });
        addMetric("RTT ping", () -> {
            MAClient client = GameState.getInstance().getClient();
            return client.getReturnTripTime() + "";
        });
        addMetric("Network Down", () -> {
            MAClient client = GameState.getInstance().getClient();
            return String.format("↓ %d Packets/s", client.packetsDownPerSec);
        });
        addMetric("Network Up", () -> {
            MAClient client = GameState.getInstance().getClient();
            float kilobytesPerSec = client.bytesUpPerSec / 1000f;
            return String.format("↑ %d Packets/s %.2f KB/s", client.packetsUpPerSec, kilobytesPerSec);
        });
        addMetric("Main batch render calls", () -> {
            return batch.renderCalls + "";
        });
        addMetric("Peak sprites batched", () -> {
            return batch.maxSpritesInBatch + "";
        });

        addToggle("[+] Render server view");




        stage.addActor(table);

    }
    public void addToggle(String name) {
        Skin skin = MeloAstronomer.getInstance().getSkin();

        Label label = new Label(name, skin);
        Label value = new Label("false", skin);
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ClickListener clickListener = new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                label.setColor(Color.CORAL);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                label.setColor(Color.WHITE);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean previousValue = atomicBoolean.getAndSet(!atomicBoolean.get());
                value.setText(previousValue + "");

            }
        };
        label.addListener(clickListener);

        table.top().left().add(label).align(Align.left).padLeft(2.5f).padRight(50);
        table.left().add(value).align(Align.left).row();
    }
    public void addMetric(String name, Linkable<String> linkable) {
        Skin skin = MeloAstronomer.getInstance().getSkin();

        table.top().left().add(new Label(name, skin)).align(Align.left).padLeft(2.5f).padRight(50);
        table.left().add(new LinkedLabel(linkable, skin)).align(Align.left).row();
    }


    public void render(float delta) {
        stage.getViewport().apply();

        stage.act(delta);
        stage.draw();
    }


    @Override
    public void dispose() {
        stage.dispose();
    }

    public void update(int width, int height, boolean centerCamera) {
        stage.getViewport().update(width, height, centerCamera);
    }
}
