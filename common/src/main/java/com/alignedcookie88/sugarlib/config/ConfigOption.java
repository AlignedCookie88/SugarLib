package com.alignedcookie88.sugarlib.config;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.serializers.ConfigSerializer;
import com.alignedcookie88.sugarlib.config.serializers.SerializationFailureException;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.OptionUIProvider;
import com.alignedcookie88.sugarlib.config.value_limiter.ValueLimiter;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigOption<T> {

    public final String id;
    public final Component name;
    public final T defaultValue;
    public final ValueLimiter<T> valueLimiter;
    public final Class<T> clazz;

    private OptionUIProvider<T> uiProvider = null;

    private Config owningConfig = null;

    private Consumer<T> updateListener = null;

    public ConfigOption(String id, Component name, T defaultValue, Class<T> clazz) {
        this(id, name, defaultValue, null, clazz);
    }

    public ConfigOption(String id, Component name, T defaultValue, ValueLimiter<T> valueLimiter, Class<T> clazz) {
        this.id = id;
        this.name = name;
        this.defaultValue = defaultValue;
        this.valueLimiter = valueLimiter;
        this.clazz = clazz;
    }


    /**
     * Sets an optional override for the UI provider
     * @param provider A supplier that supplies UI providers. Used to prevent class loading errors when running this function on the server.
     * @return This option
     */
    public ConfigOption<T> withUIProvider(Supplier<OptionUIProvider<T>> provider) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            this.uiProvider = provider.get();
        }
        return this;
    }

    /**
     * Sets an optional listener for updates
     * @param listener The listener
     * @return This option
     */
    public ConfigOption<T> withUpdateListener(Consumer<T> listener) {
        this.updateListener = listener;
        return this;
    }


    /**
     * Gets the UI provider that this config option should use.
     * @return The UI provider.
     */
    @Environment(EnvType.CLIENT)
    public OptionUIProvider<T> getUIProvider() {
        if (uiProvider != null)
            return uiProvider;

        return OptionUIProvider.getProviderOrNull(clazz);
    }


    /**
     * Gets the value of this config option
     * @return The current value
     */
    public T get() {
        T value = owningConfig.getValue(this);
        return value == null ? defaultValue : value;
    }

    /**
     * Sets the value of this config option.
     * This function also saves the config to disk if appropriate.
     * The value is not checked to be valid, you must check that with ConfigOption.validity().
     * If the config cannot be written to, this function will silently do nothing.
     * @param value The value to set
     */
    public void set(T value) {
        if (updateListener != null)
            updateListener.accept(value);
        owningConfig.setValue(this, value);
        owningConfig.save();
    }

    /**
     * Checks if the provided value is valid for this option.
     * @param value The value to check
     * @return The reason the value is invalid, or null if the value is valid.
     */
    public Component validity(T value) {
        if (valueLimiter != null)
            return valueLimiter.getLimit(value);
        return null;
    }



    @ApiStatus.Internal
    void setOwningConfig(Config owningConfig) {
        if (this.owningConfig != null)
            throw new IllegalStateException("A config value cannot belong to multiple configs.");
        this.owningConfig = owningConfig;

        if (!ConfigSerializer.isClassSerializable(clazz))
            SugarLib.LOGGER.warn("Config option {} (\"{}\") has been created with an non-serializable type ({}). The config option will fail to sync and be edited if it is part of either a CommonConfig or ServerConfig. You can register a serializer for it with ConfigSerializer.register().", id, name.getString(), clazz);
    }


    /**
     * Serializes the current value to a byte buffer
     * @param byteBuf The byte buffer
     * @throws SerializationFailureException If the serialization failed for any reason
     */
    @ApiStatus.Internal
    public void serialize(FriendlyByteBuf byteBuf) throws SerializationFailureException {
        ConfigSerializer.serialize(byteBuf, get(), clazz);
    }

    /**
     * Sets this option's current value to one deserialized from a byte buffer
     * @param byteBuf The byte buffer
     * @throws SerializationFailureException If the deserialization failed for any reason
     */
    @ApiStatus.Internal
    public void deserialize(FriendlyByteBuf byteBuf) throws SerializationFailureException {
        T value = ConfigSerializer.deserialize(byteBuf, clazz);
        set(value);
    }


    /**
     * Gets the full ID of this config value, based off the owner config's ID.
     * @return The full ID
     */
    public ResourceLocation getFullId() {
        ResourceLocation owningId = owningConfig.getFullId();
        return ResourceLocation.tryBuild(owningId.getNamespace(), owningId.getPath()+"/"+id);
    }

}
