package com.dave.astronomer.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.DebugHud;
import com.dave.astronomer.client.GameScreenConfig;
import com.dave.astronomer.client.GameState;
import com.dave.astronomer.client.MAClient;
import com.dave.astronomer.client.asset.AssetFinder;
import com.dave.astronomer.client.world.ClientMapSystem;
import com.dave.astronomer.client.world.InputSystem;
import com.dave.astronomer.client.world.MainPlayerSystem;
import com.dave.astronomer.client.world.SpriteRenderSystem;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.network.packet.ServerboundUseItemPacket;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.common.world.PhysicsSystem;
import com.dave.astronomer.server.MAServer;
import com.dave.astronomer.server.WorldData;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;

public class GameScreen implements Screen {
    private Viewport viewport;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private CoreEngine engine;
    private MAClient client;
    private MAServer server;
    private DebugHud debugHud;
    private PhysicsSystem physicsSytem;
    private Box2DDebugRenderer debugRenderer;

    public GameScreen(GameScreenConfig config) {
        batch = new SpriteBatch(2000);

        camera = new OrthographicCamera();

        //TODO: fix pixel wobble when rendering
        viewport = new FillViewport(480 / 32f, 270 /32f, camera);


        physicsSytem = new PhysicsSystem();

        //get tiled map
        AssetFinder assetFinder = MeloAstronomer.getInstance().getAssetFinder();
        TiledMap map = assetFinder.get("map.tmx", TiledMap.class);

        ClientMapSystem mapSystem = new ClientMapSystem(map, physicsSytem.getWorld(), batch, camera);

        //debug
        debugRenderer = new Box2DDebugRenderer();
        debugHud = new DebugHud(batch);

        //input
        InputMultiplexer multiplexer = new InputMultiplexer(
            debugHud.getStage()
        );

        engine = new CoreEngine();

        engine.addPrioritySystems(
            physicsSytem
        );
        engine.addSystems(
            new InputSystem(multiplexer),
            new MainPlayerSystem(),
            mapSystem,
            new SpriteRenderSystem(),
            mapSystem.new AlwaysFrontMapRenderer()
        );

        client = new MAClient(engine);

        client.start();
        try {
            //network
            if (config.startServer) {
                CoreEngine.EngineMetaData clientEngineMetaData = CoreEngine.getEngineMetaData(engine);
                WorldData data = new WorldData(clientEngineMetaData, map);
                server = new MAServer(data);
                server.start();
            }

            client.connect(config.connectTimeout, config.address, config.tcpPort, config.udpPort);
        } catch (IOException e) {
            errorToMainMenu(e);

            return;
        }


        //set globals
        GameState.getInstance().setClient(client);
        GameState.getInstance().setEngine(engine);
        GameState.getInstance().setGameCamera(camera);
        GameState.getInstance().setGameBatch(batch);
        GameState.getInstance().setMapSystem(mapSystem);


        client.requestGameStart();
    }
    public void errorToMainMenu(Exception e) {
        Log.error("", e);

        Gdx.app.postRunnable(() -> {
            MainMenuScreen screen = new MainMenuScreen();
            screen.getConnectErrorUI().postError(e);
            screen.setActiveUI(screen.getConnectErrorUI());


            MeloAstronomer.getInstance().setScreen(screen);
        });

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK, true);

        //TODO move this to separate thread. Box2d doesn't like this though
        //https://stackoverflow.com/questions/24924306/box2d-on-separate-thread
        if (server != null) server.update(delta);

        client.update();
        if (!client.isReadyForGame()) return;

        viewport.apply();
        camera.update();


        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        engine.update(delta);
        batch.end();



        //debug
        debugRenderer.render(physicsSytem.getWorld(), camera.combined);
        debugHud.render(delta, camera.combined);



        //camera follow player
        MainPlayer player = GameState.getInstance().getMainPlayer();
        Vector3 target = new Vector3(player.getPosition(), 0);


        if (player.getDashKey().isDown()) {
            camera.position.lerp(target, delta * 6);
        } else {
            camera.position.lerp(target, delta * 3);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float screenX = Gdx.input.getX();
            float screenY = Gdx.input.getY();

            Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));

            Vector2 position = new Vector2(worldCoords.x, worldCoords.y).sub(player.getBody().getWorldCenter());

            float angleRad = position.angleRad();
            player.throwKnife(angleRad);

            ServerboundUseItemPacket useItemPacket = new ServerboundUseItemPacket();
            useItemPacket.targetAngleRad = angleRad;

            client.sendTCP(useItemPacket);
        }


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        debugHud.update(width, height, true);

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

        if (engine != null) {
            engine.dispose();
        }


        if (debugHud != null) {
            debugHud.dispose();
        }


        try {
            if (client != null) {
                client.stop();
                client.dispose();
            }



            if (server != null) {
                server.stop();
                server.dispose();
            }

        } catch (IOException e) {
            Log.error("", e);
        }


    }
}
