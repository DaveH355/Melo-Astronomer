package com.dave.astronomer.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.ui.Linkable;
import com.dave.astronomer.client.ui.LinkedLabel;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.Constants;
import lombok.Getter;


public class DebugHud implements Disposable {

    @Getter
    private Stage stage;
    private Table table;
    private ShapeRenderer shapeRenderer;
    public DebugHud(SpriteBatch batch) {
        shapeRenderer = new ShapeRenderer();

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
            return String.valueOf(client.getReturnTripTime());
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
        addMetric("Batch render calls", () -> {
            return String.valueOf(batch.renderCalls);
        });
        addMetric("Peak sprites batched", () -> {
            return String.valueOf(batch.maxSpritesInBatch);
        });


        stage.addActor(table);

    }

    public void addMetric(String name, Linkable<String> linkable) {
        Skin skin = MeloAstronomer.getInstance().getSkin();

        table.top().left().add(new Label(name, skin)).align(Align.left).padLeft(2.5f).padRight(50);
        table.left().add(new LinkedLabel(linkable, skin)).align(Align.left).row();
    }


    public void render(float delta, Matrix4 projection) {
        renderWorldDebug(projection);
        stage.getViewport().apply();

        stage.act(delta);
        stage.draw();


    }

    private void renderWorldDebug(Matrix4 projectionMatrix) {
        MainPlayer player = GameState.getInstance().getMainPlayer();
        Sprite sprite = player.getSpriteComponent().getSprite();

        shapeRenderer.setProjectionMatrix(projectionMatrix);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);

        //world coordinates of the sprite's origin
        float originX = sprite.getX() + sprite.getOriginX();
        float originY = sprite.getY() + sprite.getOriginY();
        shapeRenderer.setColor(Color.MAROON);
        shapeRenderer.circle(originX, originY, 1/32f, 8);

        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.circle(player.getPosition().x, player.getPosition().y, 1/32f, 8);


        shapeRenderer.end();

    }


    @Override
    public void dispose() {
        stage.dispose();

        shapeRenderer.dispose();
    }

    public void update(int width, int height, boolean centerCamera) {
        stage.getViewport().update(width, height, centerCamera);
    }
}
