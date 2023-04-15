package com.dave.astronomer.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.DebugHud;
import com.dave.astronomer.client.GameScreenConfig;
import com.dave.astronomer.client.GameState;
import com.dave.astronomer.client.MAClient;
import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.screen.mainmenu.MainMenuScreen;
import com.dave.astronomer.client.world.*;
import com.dave.astronomer.client.world.entity.Knife;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.Constants;
import com.dave.astronomer.common.world.ecs.CoreEngine;
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
    private ClientPhysicsSystem physicsSytem;
    private Box2DDebugRenderer debugRenderer;

    //TODO: fix pixel wobble when rendering
    public GameScreen(GameScreenConfig config) {
        batch = new SpriteBatch(2000);

        camera = new OrthographicCamera();
        camera.zoom = 0.5f;
        viewport = new FillViewport(Constants.DEFAULT_WIDTH / Constants.PIXELS_PER_METER, Constants.DEFAULT_HEIGHT / Constants.PIXELS_PER_METER, camera);



        physicsSytem = new ClientPhysicsSystem();

        //get tiled map
        AssetManagerResolving assetManager = MeloAstronomer.getInstance().getAssetManager();
        TiledMap map = assetManager.get("map.tmx", TiledMap.class);

        ClientMapSystem mapSystem = new ClientMapSystem(map, physicsSytem.getWorld(), batch, camera);


        engine = new CoreEngine();

        engine.addPrioritySystems(
            physicsSytem
        );
        engine.addSystems(
            new InputSystem(),
            new MainPlayerSystem(),
            mapSystem,
            new SpriteRenderSystem(),
            mapSystem.new AlwaysFrontMapRenderer()
        );


        debugRenderer = new Box2DDebugRenderer();

        debugHud = new DebugHud(batch);



        client = new MAClient(engine);

        client.start();
        try {
            //network
            if (config.startServer) {
                WorldData data = new WorldData(map);
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
        Gdx.gl.glClearColor(0,0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


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
        debugHud.render(delta);


        //camera follow player
        MainPlayer player = GameState.getInstance().getMainPlayer();
        Vector3 target = new Vector3(player.getPosition(), 0);


        camera.position.lerp(target, delta * 4);


        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            float screenX = Gdx.input.getX();
            float screenY = Gdx.input.getY();

            Vector3 worldCoords = new Vector3(screenX, screenY, 0);
            camera.unproject(worldCoords);

            Knife knife = new Knife(null, engine);;
            Vector2 relativePos = new Vector2(worldCoords.x, worldCoords.y).sub(player.getPosition());
            knife.angleDeg = relativePos.angleDeg();
            knife.forcePosition(worldCoords.x, worldCoords.y, MathUtils.degreesToRadians * knife.angleDeg);

            engine.addEntity(knife);
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
