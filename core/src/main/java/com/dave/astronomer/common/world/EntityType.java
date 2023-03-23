package com.dave.astronomer.common.world;


import com.dave.astronomer.common.world.entity.Player;

//allow for easy creation of entities from a type
public class EntityType<T extends BaseEntity> {
    public static final EntityType<Player> PLAYER = register("player", Builder.createNothing());

    private EntityFactory<T> factory;
    private EntityType(EntityFactory<T> factory) {
        this.factory = factory;
    }

    private static <T extends BaseEntity> EntityType<T> register(String name, Builder<T> builder) {
        //TODO: register name
        return builder.build();

    }
    public T create(CoreEngine engine) {
        return this.factory.create(this, engine);
    }

    public static class Builder<T extends BaseEntity> {
        private final EntityFactory<T> factory;
        private Builder(EntityFactory<T> factory) {
            this.factory = factory;
        }
        public static <T extends BaseEntity> Builder<T> of(EntityFactory<T> factory) {
            return new Builder<>(factory);
        }

        public static <T extends BaseEntity> Builder<T> createNothing() {
            return new Builder<>((type, coreEngine) -> {
                return null;
            });
        }
        public EntityType<T> build() {
            return new EntityType<>(this.factory);
        }
    }

    //factory is fancy interface for the entity constructor
    public interface EntityFactory<T extends BaseEntity> {
        T create(EntityType<T> type, CoreEngine engine);
    }
}
