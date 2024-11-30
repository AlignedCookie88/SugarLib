package com.alignedcookie88.sugarlib.config.serializers;

import com.alignedcookie88.sugarlib.SugarLib;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public interface ConfigSerializer<T> {

    void write(FriendlyByteBuf byteBuf, T value) throws SerializationFailureException;

    T read(FriendlyByteBuf byteBuf) throws SerializationFailureException;




    Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<>();

    static <T> void register(Class<T> clazz, ConfigSerializer<T> serializer) {
        SugarLib.LOGGER.info("Registered serializer {} for {}.", serializer.getClass().getName(), clazz);
        serializers.put(clazz, serializer);
    }


    static <T> void serialize(FriendlyByteBuf byteBuf, T value, Class<T> clazz) throws SerializationFailureException {
        try {
            ConfigSerializer<T> serializer = (ConfigSerializer<T>) serializers.get(clazz);
            serializer.write(byteBuf, value);
        } catch (ClassCastException | NullPointerException exception) {
            throw new SerializationFailureException("Cannot serialize type %s. Register a ConfigSerializer with ConfigSerializer.register() to fix this.".formatted(clazz.getName()));
        }
    }

    static <T> T deserialize(FriendlyByteBuf byteBuf, Class<T> clazz) throws SerializationFailureException {
        try {
            ConfigSerializer<T> serializer = (ConfigSerializer<T>) serializers.get(clazz);
            return serializer.read(byteBuf);
        } catch (ClassCastException | NullPointerException exception) {
            throw new SerializationFailureException("Cannot deserialize type %s. Register a ConfigSerializer with ConfigSerializer.register() to fix this.".formatted(clazz.getName()));
        }
    }

    static <T> T deserializeWithDefault(FriendlyByteBuf byteBuf, Class<T> clazz, T defaultValue) {
        try {
            return deserialize(byteBuf, clazz);
        } catch (SerializationFailureException e) {
            return defaultValue;
        }
    }

    static boolean isClassSerializable(Class<?> clazz) {
        return serializers.containsKey(clazz);
    }

}
