package com.dave.astronomer.client.world.component;

import com.badlogic.gdx.InputProcessor;
import com.dave.astronomer.common.world.BaseComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class InputComponent extends BaseComponent implements InputProcessor {

    private Map<Integer, KeyAction> keyMap = new HashMap<>();

    public static class KeyAction {
        @Getter private int key;
        @Setter boolean isDown;
        @Getter @Setter boolean disabled;

        public boolean isDown() {
            if (disabled) return false;
            else return isDown;
        }
        public KeyAction(int key) {
            this.key = key;
        }

    }
    public void addKeyAction(KeyAction... actions) {
        for (KeyAction action : actions) {
            keyMap.put(action.key, action);
        }
    }
    public void disableAll() {
        keyMap.values().forEach(keyAction -> keyAction.setDisabled(true));
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keyMap.containsKey(keycode)) {
            keyMap.get(keycode).setDown(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keyMap.containsKey(keycode)) {
            keyMap.get(keycode).setDown(false);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
