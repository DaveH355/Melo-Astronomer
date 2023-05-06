package com.dave.astronomer.server;

import com.badlogic.gdx.graphics.OrthographicCamera;
import lombok.Getter;
import lombok.Setter;

public class ServerState {
    @Getter
    private static ServerState instance = new ServerState();


    private ServerState() {
    }

    @Getter @Setter private WorldData worldData;
}
