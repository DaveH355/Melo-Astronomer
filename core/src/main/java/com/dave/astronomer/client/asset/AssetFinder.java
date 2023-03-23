package com.dave.astronomer.client.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetFinder {
    private final AssetManagerResolving assetManager;
    private static final List<String> blackList = new ArrayList<>();
    private static final Map<String, Class<?>> whiteList = new HashMap<>();

    static {
        whiteList.put("mp3", Sound.class);
        whiteList.put("ogg", Sound.class);
        whiteList.put("wav", Sound.class);
        whiteList.put("png", Texture.class);
        whiteList.put("fnt", BitmapFont.class);
        whiteList.put("atlas", TextureAtlas.class);
        whiteList.put("tmx", TiledMap.class);


        blackList.add("Pixeld16/");

    }
    public AssetFinder(AssetManagerResolving assetManager) {
        this.assetManager = assetManager;
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
    }
    public void load() {
        Array<String> allFiles = new Array<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal("assets.txt").read()));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                allFiles.add(line);
            }
        } catch (Exception e) {
            Log.error(getClass().getSimpleName(), e);
        }


        allFiles.forEach(string -> {
            FileHandle fileHandle = Gdx.files.internal(string);

            if (blackList.stream().anyMatch(blacklist -> fileHandle.path().startsWith(blacklist))) {
                return;
            }

            if (whiteList.containsKey(fileHandle.extension())) {
                assetManager.load(fileHandle.path(), whiteList.get(fileHandle.extension()));
            }
        });
        Log.info(allFiles.size + " possible files found");
    }
}
