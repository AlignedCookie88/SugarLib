package com.alignedcookie88.sugarlib.fabric;

import com.alignedcookie88.sugarlib.SugarLib;
import net.fabricmc.api.ModInitializer;

public final class SugarLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        SugarLib.init(FabricModInfo.fromId("sugarlib"));
    }
}
