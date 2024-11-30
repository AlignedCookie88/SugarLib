package com.alignedcookie88.sugarlib.config;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfigBuilder<T extends Config> {

    private final T config;

    ConfigBuilder(T config) {
        this.config = config;
    }


    public T build() {
        return config;
    }


    public ConfigBuilder<T> withCustomName(Component customName) {
        config.setCustomName(customName);
        return this;
    }

    public ConfigBuilder<T> withCustomIcon(ResourceLocation customIcon) {
        config.setCustomIcon(customIcon);
        return this;
    }

    public ConfigBuilder<T> withOption(ConfigOption<?> option) {
        config.addOption(option);
        return this;
    }

}
