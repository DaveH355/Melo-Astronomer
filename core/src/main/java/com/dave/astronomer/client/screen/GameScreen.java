package com.dave.astronomer.client.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.*;
import com.dave.astronomer.MeloAstronomer;
import com.dave.astronomer.client.DebugHud;
import com.dave.astronomer.client.GameScreenConfig;
import com.dave.astronomer.client.GameState;
import com.dave.astronomer.client.MAClient;
import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.screen.mainmenu.MainMenuScreen;
import com.dave.astronomer.client.world.*;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.Constants;
import com.dave.astronomer.common.world.CoreEngine;
import com.dave.astronomer.server.MAServer;
import com.dave.astronomer.server.WorldData;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;

public class GameScreen implements Screen {
    private ScreenViewport viewport;
    private ShaderProgram shader;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private CoreEngine engine;
    private MAClient client;
    private MAServer server;

    private DebugHud debugHud;
    private ClientPhysicsSystem physicsSytem;
    private Box2DDebugRenderer debugRenderer;
    private Vector3 cameraTarget = new Vector3();


    public GameScreen(GameScreenConfig config) {
        ShaderProgram.pedantic = false;
//        shader = new ShaderProgram(Gdx.files.internal("shaders/2xBilinear.vert").readString(),
//            Gdx.files.internal("shaders/2xBilinear.frag").readString());

//
//        if (!shader.isCompiled()) {
//            RuntimeException exception = new RuntimeException(shader.getLog());
//            errorToMainMenu(exception);
//
//        }



        batch = new SpriteBatch(2000);



        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1/Constants.PIXELS_PER_METER );



        physicsSytem = new ClientPhysicsSystem();

        //get tiled map
        AssetManagerResolving assetManager = MeloAstronomer.getInstance().getAssetManager();
        TiledMap map = assetManager.get("map.tmx", TiledMap.class);

        ClientMapSystem mapSystem = new ClientMapSystem(map, physicsSytem.getWorld(), batch, camera);


        engine = new CoreEngine();

        engine.addSystems(
                new InputSystem(),
                mapSystem,
                new SpriteRenderSystem(),
                mapSystem.new AlwaysFrontMapRenderer(),
                physicsSytem,
                new MainPlayerSystem(),
                new RemotePlayerSystem()
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
    private void errorToMainMenu(Exception e) {
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
        Gdx.gl.glClearColor(5/255f,23/255f,50/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        //TODO move this to separate thread. Box2d doesn't like this though
        //https://stackoverflow.com/questions/24924306/box2d-on-separate-thread
        if (server != null) server.update(delta);

        client.update();
        if (!client.isReadyForGame()) return;

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

        cameraTarget = cameraTarget.lerp(target, delta * 4);

        // snap camera position to full pixels
        float snappedX = cameraTarget.x;
        float snappedY = cameraTarget.y;

        snappedX = MathUtils.floor(snappedX * Constants.PIXELS_PER_METER) / Constants.PIXELS_PER_METER;
        snappedY = MathUtils.floor(snappedY * Constants.PIXELS_PER_METER) / Constants.PIXELS_PER_METER;


//        camera.position.set(snappedX, snappedY, 0);
        camera.position.set(cameraTarget);


        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            camera.zoom -= 0.05f;
        }


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        debugHud.update(width, height, true);

        float coveragePercentX = (float) width / Gdx.app.getGraphics().getDisplayMode().width * 100f;
        float coveragePercentY = (float) height / Gdx.app.getGraphics().getDisplayMode().height * 100f;

        if (coveragePercentX >= 80 && coveragePercentY >= 80) {
            viewport.setUnitsPerPixel(1/Constants.PIXELS_PER_METER /4);
        } else if (coveragePercentX >= 50 && coveragePercentY >= 50) {
            viewport.setUnitsPerPixel(1/Constants.PIXELS_PER_METER /2);
        }else {
            viewport.setUnitsPerPixel(1/Constants.PIXELS_PER_METER * 1);
        }

        viewport.update(viewport.getScreenWidth(), viewport.getScreenHeight(), false);

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
        if (shader != null) {
            shader.dispose();
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
