package com.alignedcookie88.sugarlib.fabric.client;

import com.alignedcookie88.sugarlib.SugarLib;
import net.fabricmc.api.ClientModInitializer;

public final class SugarLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        SugarLib.initClient();
    }
}
