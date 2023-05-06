package com.dave.astronomer.common.world;


import com.dave.astronomer.client.world.entity.Knife;
import com.dave.astronomer.common.world.entity.Player;

//easily access entity specific data
public class EntityType<T extends BaseEntity> {
    public static final EntityType<Player> PLAYER = register(Builder.<Player>begin().speed(3.5f));
    public static final EntityType<Knife> KNIFE = register(Builder.<Knife>begin().speed(8f));

    public final float speed;

    private EntityType(float speed) {
        this.speed = speed;
    }

    private static <T extends BaseEntity> EntityType<T> register(Builder<T> builder) {
        return builder.build();
    }


    public static class Builder<T extends BaseEntity> {
        private float maxSpeed;

        public static <T extends BaseEntity> Builder<T> begin() {
            return new Builder<>();
        }

        public EntityType.Builder<T> speed(float speed) {
            this.maxSpeed = speed;
            return this;
        }


        public EntityType<T> build() {
            return new EntityType<>(maxSpeed);
        }
    }
}
