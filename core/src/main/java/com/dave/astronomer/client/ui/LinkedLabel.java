
package com.dave.astronomer.client.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class LinkedLabel extends Label {
    private Linkable<String> linkable;
    private static final String DEFAULT_TEXT = "Null";

    public LinkedLabel(Linkable<String> linkable, LabelStyle labelStyle) {
        super(DEFAULT_TEXT, labelStyle);
        this.linkable = linkable;
    }
    public LinkedLabel(Linkable<String> linkable, Skin skin) {
        super(DEFAULT_TEXT, skin);
        this.linkable = linkable;
    }

    public LinkedLabel(Linkable<String> linkable, Skin skin, String styleName) {
        super(DEFAULT_TEXT, skin, styleName);
        this.linkable = linkable;
    }

    public LinkedLabel(Linkable<String> linkable, Skin skin, String fontName, Color color) {
        super(DEFAULT_TEXT, skin, fontName, color);
        this.linkable = linkable;
    }

    public LinkedLabel(Linkable<String> linkable, Skin skin, String fontName, String colorName) {
        super(DEFAULT_TEXT, skin, fontName, colorName);
        this.linkable = linkable;
    }

    @Override
    public void act(float delta) {
        if (linkable.getValue() != null) setText(linkable.getValue());
        else setText(DEFAULT_TEXT);

        super.act(delta);
    }
}
