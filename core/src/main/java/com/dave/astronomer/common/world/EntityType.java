package com.dave.astronomer.common.world;


import com.dave.astronomer.client.world.entity.Knife;
import com.dave.astronomer.common.world.entity.Player;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

//allow network to easily create entities or access entity specific data
public class EntityType<T extends BaseEntity> {
    private static final AtomicInteger UNIQUE_ID_POOL = new AtomicInteger();
    private static Map<Integer, EntityType<?>> map = new HashMap<>();
    public static final EntityType<Player> PLAYER = register(Builder.<Player>createNothing().speed(4f));
    public static final EntityType<Knife> KNIFE = register(Builder.<Knife>of(Knife::new).speed(9f));

    public final float speed;
    private final int uniqueID = UNIQUE_ID_POOL.incrementAndGet();
    private EntityFactory<T> factory;

    private EntityType(EntityFactory<T> factory, float speed) {
        this.factory = factory;
        this.speed = speed;
    }

    private static <T extends BaseEntity> EntityType<T> register(Builder<T> builder) {
        EntityType<T> entityType = builder.build();
        map.put(entityType.uniqueID, entityType);
        return entityType;
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
    //fancy interface for the entity constructor
    public interface EntityFactory<T extends BaseEntity> {
        T create(EntityType<T> type, CoreEngine engine);
    }

    public static class EntityTypeSerializer extends Serializer<EntityType<?>> {
        @Override
        public void write(Kryo kryo, Output output, EntityType<?> entityType) {
            output.writeInt(entityType.uniqueID);
        }

        @Override
        public EntityType<?> read(Kryo kryo, Input input, Class<? extends EntityType<?>> type) {
            int uniqueID = input.readInt();
            return map.get(uniqueID);
        }
    }

}
