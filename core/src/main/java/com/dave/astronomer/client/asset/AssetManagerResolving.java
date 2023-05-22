package com.dave.astronomer.client.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Null;
import com.esotericsoftware.minlog.Log;

import java.util.HashMap;
import java.util.Map;

public class AssetManagerResolving extends AssetManager {
    private boolean pathResolving;
    private final Map<String, String> fileToPathMap = new HashMap<>();

    public void setPathResolving(boolean pathResolving) {
        this.pathResolving = pathResolving;
    }

    @Override
    public synchronized <T> void load (String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        if (pathResolving) {
            FileHandle fileHandle = Gdx.files.internal(fileName);
            if (fileToPathMap.containsKey(fileHandle.name())) {
                String currentFileName = fileHandle.name();
                String currentFilePath = fileHandle.path();
                String existingFileName = fileToPathMap.get(fileHandle.name());


                String string = String.format("""
                   "%s" already exists as "%s"
                   "%s" was not loaded due to collision
                   Possible solutions: Disable path resolving or change the filenames
                    """, currentFileName, existingFileName, currentFilePath);


                Log.error(string);
            } else {
                super.load(fileName, type, parameter);
                fileToPathMap.put(fileHandle.name(), fileHandle.path());
            }
        }
    }

    @Override
    public synchronized @Null <T> T get(String fileName, boolean required) {
        boolean success = true;
        //check if filename is a relative path
        try {
            super.get(fileName, required);
        } catch (GdxRuntimeException e) {
            success = false;
        }

        if (pathResolving && !success) {
            //filename expected to be converted here to relative path
            fileName = fileToPathMap.get(fileName);
        }
        return super.get(fileName, required);
    }

    @Override
    public synchronized @Null <T> T get (String fileName, Class<T> type, boolean required) {
        boolean success = true;
        //check if filename is a relative path
        try {
            super.get(fileName, type, required);
        } catch (GdxRuntimeException e) {
            success = false;
        }

        if (pathResolving && !success) {
            //filename expected to be converted here to relative path
            fileName = fileToPathMap.get(fileName);
        }
        return super.get(fileName, type, required);
    }
}
