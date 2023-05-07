package com.dave.astronomer.common.world;


import com.dave.astronomer.client.world.entity.Knife;
import com.dave.astronomer.common.world.entity.Player;

//allow network to easily create entities or access entity specific data
public class EntityType<T extends BaseEntity> {
    public static final EntityType<Player> PLAYER = register(Builder.<Player>createNothing().speed(3.5f));
    public static final EntityType<Knife> KNIFE = register(Builder.<Knife>of(Knife::new).speed(8f));

    public final float speed;
    private EntityFactory<T> factory;

    private EntityType(EntityFactory<T> factory, float speed) {
        this.factory = factory;
        this.speed = speed;
    }

    private static <T extends BaseEntity> EntityType<T> register(Builder<T> builder) {
        return builder.build();
    }

    public T create(CoreEngine engine) {
        return this.factory.create(this, engine);
    }


    public static class Builder<T extends BaseEntity> {
        private float maxSpeed;

        private final EntityFactory<T> factory;
        private Builder(EntityFactory<T> factory) {
            this.factory = factory;
        }

        public static <T extends BaseEntity> Builder<T> createNothing() {
            return new Builder<>((type, coreEngine) -> null);
        }
        public static <T extends BaseEntity> Builder<T> of(EntityFactory<T> factory) {
            return new Builder<>(factory);
        }

        public EntityType.Builder<T> speed(float speed) {
            this.maxSpeed = speed;
            return this;
        }


        public EntityType<T> build() {
            return new EntityType<>(this.factory, maxSpeed);
        }
    }
    //factory is fancy interface for the entity constructor
    public interface EntityFactory<T extends BaseEntity> {
        T create(EntityType<T> type, CoreEngine engine);
    }
}
