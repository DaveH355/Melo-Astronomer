package com.dave.astronomer.client.world.component;

import com.badlogic.gdx.physics.box2d.Body;
import com.dave.astronomer.common.world.BaseComponent;
import lombok.Getter;

public class BodyComponent extends BaseComponent {
    @Getter private Body body;

    public BodyComponent(Body body) {
        this.body = body;
    }


}
