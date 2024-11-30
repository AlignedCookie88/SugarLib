package com.alignedcookie88.sugarlib.config;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.backend.JsonConfigBackend;
import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.alignedcookie88.sugarlib.config.client_view.LocalConfigView;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientConfig extends Config {

    public static ConfigBuilder<ClientConfig> builder() {
        return new ConfigBuilder<>(new ClientConfig());
    }


    public ClientConfig() {
        super();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("sugarlib.config.default_name.client");
    }

    @Override
    protected ResourceLocation getDefaultIcon() {
        return ResourceLocation.tryBuild("sugarlib", "textures/config/client.png");
    }

    @Override
    protected String getDefaultId() {
        return "client";
    }

    @Override
    protected void onReady() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            backend = new JsonConfigBackend(this);
            load();
        } else {
            SugarLib.LOGGER.warn("Client config {} is initialised in the common constructor. It has not been loaded.", getFullId());
        }
    }

    @Override
    public float sortFloat() {
        return 0;
    }

    @Override
    public ClientConfigView<?> getClientView() {
        return new LocalConfigView(this);
    }
}
