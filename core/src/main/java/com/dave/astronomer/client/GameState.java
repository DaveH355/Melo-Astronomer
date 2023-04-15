package com.dave.astronomer.client;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dave.astronomer.client.world.ClientMapSystem;
import com.dave.astronomer.client.world.entity.MainPlayer;
import com.dave.astronomer.common.world.ecs.CoreEngine;
import lombok.Getter;
import lombok.Setter;

//variables here should only be relevant to gameplay
//should not be null after client is ready
public class GameState {
    @Getter private static GameState instance = new GameState();

    private GameState() {
    }

    @Getter @Setter private MainPlayer mainPlayer;
    @Getter @Setter private MAClient client;
    @Getter @Setter private CoreEngine engine;
    @Getter @Setter private OrthographicCamera gameCamera;
    @Getter @Setter private SpriteBatch gameBatch;
    @Getter @Setter private ClientMapSystem mapSystem;
}
