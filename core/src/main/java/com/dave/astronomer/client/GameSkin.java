package com.dave.astronomer.client;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class GameSkin extends Skin {

    public GameSkin(FileHandle skinFile) {
        super(skinFile);
    }
    @Override
    protected Json getJsonLoader(final FileHandle skinFile) {
        Json json = super.getJsonLoader(skinFile);
        final Skin skin = this;

        json.setSerializer(FreeTypeFontGenerator.class, new Json.ReadOnlySerializer<>() {
            @Override
            public FreeTypeFontGenerator read(Json json, JsonValue jsonData, Class type) {
                String path = json.readValue("font", String.class, jsonData);
                jsonData.remove("font");

                FreeTypeFontGenerator.Hinting hinting = FreeTypeFontGenerator.Hinting.valueOf(json.readValue("hinting", String.class, "AutoMedium", jsonData));
                jsonData.remove("hinting");

                Texture.TextureFilter minFilter = Texture.TextureFilter.valueOf(json.readValue("minFilter", String.class, "Nearest", jsonData));
                jsonData.remove("minFilter");

                Texture.TextureFilter magFilter = Texture.TextureFilter.valueOf(json.readValue("magFilter", String.class, "Nearest", jsonData));
                jsonData.remove("magFilter");

                FreeTypeFontGenerator.FreeTypeFontParameter parameter = json.readValue(FreeTypeFontGenerator.FreeTypeFontParameter.class, jsonData);
                parameter.hinting = hinting;
                parameter.minFilter = minFilter;
                parameter.magFilter = magFilter;
                FreeTypeFontGenerator generator = new FreeTypeFontGenerator(skinFile.parent().child(path));
                BitmapFont font = generator.generateFont(parameter);
                font.getData().markupEnabled = true;
                skin.add(jsonData.name, font);
                if (parameter.incremental) {
                    generator.dispose();
                    return null;
                } else {
                    return generator;
                }
            }
        });

        return json;
    }

}
