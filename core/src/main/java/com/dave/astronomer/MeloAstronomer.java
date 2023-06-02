package com.dave.astronomer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.dave.astronomer.client.GameSkin;
import com.dave.astronomer.client.asset.AssetFinder;
import com.dave.astronomer.client.screen.SplashScreen;
import com.dave.astronomer.common.MALogger;
import com.esotericsoftware.minlog.Log;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class MeloAstronomer extends Game {
    private static MeloAstronomer instance;
    @Getter
    private Skin skin;
    @Getter
    private BitmapFont font;
    private TextureAtlas skinRegion;
    @Getter @Setter
    private AssetFinder assetFinder;
    private MALogger logger;

    public MeloAstronomer() {
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
        ShaderProgram.pedantic = false;
        Box2D.init();

        skin = new GameSkin(Gdx.files.internal("Pixeld16/Pixeld16.json"));
        skinRegion = new TextureAtlas(Gdx.files.internal("Pixeld16/Pixeld16.atlas"));
        skin.addRegions(skinRegion);

        font = skin.getFont("font_8");

        this.setScreen(new SplashScreen());
    }

    @Override
    public void render() {
        super.render();
    }



    @Override
    public void dispose() {
        super.dispose();

        assetFinder.dispose();
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
