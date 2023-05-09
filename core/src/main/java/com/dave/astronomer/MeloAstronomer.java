package com.dave.astronomer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.dave.astronomer.client.GameSkin;
import com.dave.astronomer.client.NonGameClient;
import com.dave.astronomer.client.asset.AssetManagerResolving;
import com.dave.astronomer.client.screen.SplashScreen;
import com.dave.astronomer.common.MALogger;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class MeloAstronomer extends Game {
    private static MeloAstronomer instance;
    @Getter private Skin skin;
    private TextureAtlas skinRegion;
    @Getter private Client nonGameClient = new NonGameClient();
    @Getter @Setter private AssetManagerResolving assetManager;
    private MALogger logger;

    public MeloAstronomer() {
        ShaderProgram.pedantic = false;
        instance = this;
    }

    @Override
    public void create() {
        Log.info("Starting Game...");
        File gameDirectory = new File("game");


        gameDirectory.mkdir();


        try {
            logger = new MALogger(gameDirectory);

            Log.setLogger(logger);
            Log.set(Log.LEVEL_DEBUG);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        skin = new GameSkin(Gdx.files.internal("Pixeld16/Pixeld16.json"));
        skinRegion = new TextureAtlas(Gdx.files.internal("Pixeld16/Pixeld16.atlas"));
        skin.addRegions(skinRegion);

        this.setScreen(new SplashScreen());
    }

    @Override
    public void render() {
        super.render();
    }



    @Override
    public void dispose() {
        super.dispose();

        try {
            nonGameClient.dispose();
        } catch (IOException e) {
            Log.error("", e);
        }

        assetManager.dispose();
        skin.dispose();
        skinRegion.dispose();
        logger.dispose();

        Gdx.app.exit();
        System.exit(0);
    }

    public static MeloAstronomer getInstance() {
        return instance;
    }
}
