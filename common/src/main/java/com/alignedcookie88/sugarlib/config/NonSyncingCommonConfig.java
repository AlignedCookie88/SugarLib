package com.alignedcookie88.sugarlib.config;

import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.alignedcookie88.sugarlib.config.client_view.LocalConfigView;
import net.minecraft.resources.ResourceLocation;

public class NonSyncingCommonConfig extends CommonConfig {


    @Override
    public boolean shouldSyncToClient() {
        return false;
    }

    @Override
    protected String getDefaultId() {
        return "common_ns";
    }

    @Override
    protected ResourceLocation getDefaultIcon() {
        return ResourceLocation.tryBuild("sugarlib", "textures/config/common_ns.png");
    }

    @Override
    public float sortFloat() {
        return 2;
    }

    @Override
    public ClientConfigView<?> getClientView() {
        return new LocalConfigView(this);
    }
}
