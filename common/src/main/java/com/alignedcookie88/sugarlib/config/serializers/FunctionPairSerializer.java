package com.alignedcookie88.sugarlib.config.serializers;

import net.minecraft.network.FriendlyByteBuf;

public class FunctionPairSerializer<T> implements ConfigSerializer<T> {


    private final WriteFunc<T> writeFunc;
    private final ReadFunc<T> readFunc;


    public FunctionPairSerializer(WriteFunc<T> writeFunc, ReadFunc<T> readFunc) {
        this.writeFunc = writeFunc;
        this.readFunc = readFunc;
    }


    @Override
    public void write(FriendlyByteBuf byteBuf, T value) throws SerializationFailureException {
        this.writeFunc.write(byteBuf, value);
    }

    @Override
    public T read(FriendlyByteBuf byteBuf) throws SerializationFailureException {
        return this.readFunc.read(byteBuf);
    }


    public interface WriteFunc<T> {

        void write(FriendlyByteBuf byteBuf, T value);

    }

    public interface ReadFunc<T> {

        T read(FriendlyByteBuf byteBuf);

    }
}
