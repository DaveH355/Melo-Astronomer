package com.dave.astronomer.client.world;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.dave.astronomer.client.world.component.InputComponent;
import com.dave.astronomer.common.world.CoreEngine;

import java.util.ArrayList;
import java.util.List;

public class InputSystem extends EntitySystem implements InputProcessor {
    private InputMultiplexer multiplexer;
    private List<InputProcessor> processorList = new ArrayList<>();
    private CoreEngine engine;
    private ComponentMapper<InputComponent> mapper = ComponentMapper.getFor(InputComponent.class);


    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (CoreEngine) engine;

        multiplexer = new InputMultiplexer(
                this
        );
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void update(float delta) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(InputComponent.class).get());

        for (Entity entity : entities) {
            InputComponent inputComponent = mapper.get(entity);
            if (!processorList.contains(inputComponent)) {
                processorList.add(inputComponent);
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        processorList.forEach(i -> i.keyDown(keycode));
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        processorList.forEach(i -> i.keyUp(keycode));
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        processorList.forEach(i -> i.keyTyped(character));
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        processorList.forEach(i -> i.touchDown(screenX, screenY, pointer, button));
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        processorList.forEach(i -> i.touchUp(screenX, screenY, pointer, button));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        processorList.forEach(i -> i.touchDragged(screenX, screenY, pointer));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        processorList.forEach(i -> i.mouseMoved(screenX, screenY));
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        processorList.forEach(i -> i.scrolled(amountX, amountY));
        return true;
    }
}
