package com.dave.astronomer.common;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class AnimationUtils {

    /**
     * @param frameDuration the duration of each frame in milliseconds
     */
    public static Animation<TextureRegion> loadAllFromTexture(Texture texture, int frameWidth, int frameHeight, float frameDuration) {
        int rows = texture.getHeight() / frameHeight;
        int cols = texture.getWidth() / frameWidth;
        int frameCount = rows * cols;

        TextureRegion[] frames = new TextureRegion[frameCount];
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                frames[index] = new TextureRegion(texture, col * frameWidth, row * frameHeight, frameWidth, frameHeight);
                index++;
            }
        }
        return new Animation<>(frameDuration / 1000f, frames);
    }
    /**
     * @param frameDuration the duration of each frame in milliseconds
     */
    public static Animation<TextureRegion> loadFromTexture(Texture texture, int frameWidth, int frameHeight, int numFrames, int startRow, int startCol, float frameDuration) {
        int cols = texture.getWidth() / frameWidth;
        int rows = (int) Math.ceil((float) numFrames / cols);

        TextureRegion[][] regions = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[numFrames];
        int index = 0;

        for (int row = startRow; row < startRow + rows && index < numFrames; row++) {
            for (int col = startCol; col < startCol + cols && index < numFrames; col++) {
                frames[index] = regions[row][col];
                index++;
            }
        }

        return new Animation<>(frameDuration / 1000f, frames);
    }
    public static Animation<TextureRegion> loadFromTexture(Texture texture, int frameSize, int numFrames, int startRow, int startCol, float frameDuration) {
        return loadFromTexture(texture, frameSize, frameSize, numFrames, startRow, startCol, frameDuration);
    }

    public static Map<String, Animation<TextureRegion>> loadFromAseprite(Texture texture, FileHandle json) {
        Map<String, Animation<TextureRegion>> animations = new HashMap<>();

        // parse JSON file
        JsonValue jsonValue = new JsonReader().parse(json.readString());
        JsonValue frames = jsonValue.get("frames");
        JsonValue tags = jsonValue.get("meta").get("frameTags");

        // create animations
        for (JsonValue tag : tags) {
            String name = tag.getString("name");
            int start = tag.getInt("from");
            int end = tag.getInt("to");
            int length = end - start + 1;

            float frameDuration = frames.get(start).getFloat("duration") / 1000f;
            TextureRegion[] frameTextures = new TextureRegion[length];

            for (int i = 0; i < length; i++) {
                int frameIndex = start + i;
                JsonValue frame = frames.get(frameIndex);
                int x = frame.get("frame").getInt("x");
                int y = frame.get("frame").getInt("y");
                int w = frame.get("frame").getInt("w");
                int h = frame.get("frame").getInt("h");
                frameTextures[i] = new TextureRegion(texture, x, y, w, h);
            }
            animations.put(name, new Animation<>(frameDuration, frameTextures));
        }
        return animations;
    }


}
