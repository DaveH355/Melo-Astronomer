package com.dave.astronomer.client.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.minlog.Log;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetFinder implements Disposable {
    @Getter private final AssetManager assetManager;
    private Map<String, FileHandle> map;

    public AssetFinder(AssetManager assetManager) {
        this.assetManager = assetManager;
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
    }

    public FileHandle getFileHandle(String fileNameWithExtension) {
        return map.get(fileNameWithExtension);
    }

    public void find() {
        String[] strings = Gdx.files.internal("assets.txt").readString().split("\n");
        Log.info(strings.length + " possible asset files found");

        map = new HashMap<>();

        for (String filePath : strings) {
            FileHandle file = Gdx.files.internal(filePath);

            if (map.containsKey(file.name())) {
                Log.error(getClass().getSimpleName(), String.format("""
                   "%s" collides with existing "%s"
                   This may cause unexpected results when accessing these files. Make sure file names are unique""",
                    file, map.get(file.name())));
                continue;
            }
            map.put(file.name(), file);

            switch (file.extension()) {
                case "mp3", "ogg", "wav" -> loadAudio(file);
                case "png", "jpg" -> load(file, Texture.class);
                case "fnt" -> load(file, BitmapFont.class);
                case "atlas" -> load(file, TextureAtlas.class);
                case "tmx" -> load(file, TiledMap.class);
                case "vert", "frag" -> load(file, ShaderProgram.class);
            }
        }
    }

    private void load(FileHandle fileHandle, Class<?> type) {
        assetManager.load(fileHandle.path(), type);
    }

    private void loadAudio(FileHandle fileHandle) {
        FileHandle topParent = new FileHandle(getTopParent(fileHandle.file()));

        if (topParent.name().equals("sound")) {
            load(fileHandle, Sound.class);
        } else if (topParent.name().equals("music")) {
            load(fileHandle, Music.class);
        } else {
            Log.warn(fileHandle.path() + " audio file not located in either sound or music folder. Defaulting to load as Sound");
            load(fileHandle, Sound.class);
        }

    }
    private File getTopParent(File file) {
        File parent = file.getParentFile();
        if (parent == null) {
            return file;
        }
        return getTopParent(file);
    }
    private String fileNameToPath(String fileNameWithExtension) {
        return map.get(fileNameWithExtension).path();
    }

    //wrap asset manager

    /**
        @param fileName Name of file with extension e.g. texture.png
     */
    public <T> T get (String fileName, Class<T> type) {
        return assetManager.get(fileNameToPath(fileName), type);
    }
    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
