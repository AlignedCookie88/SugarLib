package com.alignedcookie88.sugarlib.neoforge;

import com.alignedcookie88.sugarlib.SugarLib;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.neoforged.fml.common.Mod;

@Mod(SugarLib.MOD_ID)
public final class SugarLibNeoForge {
    public SugarLibNeoForge() {
        // Run our common setup.
        SugarLib.init(NeoForgeModInfo.fromModObject(this));

        // Client init. On fabric initClient is called in the client initializer.
        ClientLifecycleEvent.CLIENT_SETUP.register(instance -> SugarLib.initClient());
    }
}
