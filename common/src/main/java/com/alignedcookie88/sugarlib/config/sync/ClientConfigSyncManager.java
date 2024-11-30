package com.alignedcookie88.sugarlib.config.sync;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.Config;
import com.alignedcookie88.sugarlib.config.ConfigHandler;
import com.alignedcookie88.sugarlib.config.ConfigOption;
import com.alignedcookie88.sugarlib.config.serializers.SerializationFailureException;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public class ClientConfigSyncManager {

    @ApiStatus.Internal
    public static void setup() {

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ConfigSyncPackets.START_SYNC, ClientConfigSyncManager::onSyncStart);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ConfigSyncPackets.END_SYNC, ClientConfigSyncManager::onSyncEnd);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ConfigSyncPackets.SYNC_OPTION, ClientConfigSyncManager::onSyncOption);

    }

    private static Config currentConf = null;



    private static void onSyncStart(FriendlyByteBuf byteBuf, NetworkManager.PacketContext context) {
        ResourceLocation id = byteBuf.readResourceLocation();

        if (currentConf != null) {
            SugarLib.LOGGER.warn("Failed to start config sync of {}, there is already an active sync.", id);
            return;
        }

        Config found = ConfigHandler.getConfigFromId(id);
        if (found == null) {
            SugarLib.LOGGER.warn("Failed to start config sync of {}, there is no such config known to the client.", id);
            return;
        }

        found.prepareForSync();

        currentConf = found;
    }


    private static void onSyncEnd(FriendlyByteBuf byteBuf, NetworkManager.PacketContext context) {
        if (currentConf == null) {
            SugarLib.LOGGER.warn("Failed to end config sync, there is no active sync.");
            return;
        }
        currentConf = null;
    }


    private static void onSyncOption(FriendlyByteBuf byteBuf, NetworkManager.PacketContext context) {
        if (SugarLib.playerInSingleplayer())
            return; // Don't sync for singleplayer

        String optId = byteBuf.readUtf();

        if (currentConf == null) {
            SugarLib.LOGGER.warn("Failed to sync config option {}, there is no such active sync.", optId);
            return;
        }

        ConfigOption<?> opt = currentConf.getOption(optId);

        if (opt == null) {
            SugarLib.LOGGER.warn("Failed to sync config option {} for {}, there is no such option known to the client.", optId, currentConf.getFullId());
            return;
        }

        try {
            opt.deserialize(byteBuf);
        } catch (SerializationFailureException e) {
            SugarLib.LOGGER.warn("Failed to sync config option {}, there was an error deserializing it.", opt.getFullId(), e);
        }
    }

}
