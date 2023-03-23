package com.dave.astronomer.common.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.UUID;

public class UUIDSerializer extends Serializer<UUID> {
    @Override
    public void write(Kryo kryo, Output output, UUID object) {
        output.writeString(object.toString());
    }

    @Override
    public UUID read(Kryo kryo, Input input, Class<? extends UUID> type) {
        String string = input.readString();
        return UUID.fromString(string);
    }
}
