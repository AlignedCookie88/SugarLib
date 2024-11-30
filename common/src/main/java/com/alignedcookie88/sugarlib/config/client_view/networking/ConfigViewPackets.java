package com.alignedcookie88.sugarlib.config.client_view.networking;

import net.minecraft.resources.ResourceLocation;

public class ConfigViewPackets {


    public static ResourceLocation START_CONFIG_UPDATE = ResourceLocation.tryBuild("sugarlib", "config_update/start");
    public static ResourceLocation END_CONFIG_UPDATE = ResourceLocation.tryBuild("sugarlib", "config_update/end");
    public static ResourceLocation CONFIG_UPDATE_SET_OPTION = ResourceLocation.tryBuild("sugarlib", "config_update/value");


}
