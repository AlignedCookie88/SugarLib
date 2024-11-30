package com.alignedcookie88.sugarlib.config.sync;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.Config;
import com.alignedcookie88.sugarlib.config.ConfigHandler;
import com.alignedcookie88.sugarlib.config.ConfigOption;
import com.alignedcookie88.sugarlib.config.serializers.SerializationFailureException;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.impl.NetworkAggregator;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class ServerConfigSyncManager {

    @ApiStatus.Internal
    public static void setup() {

        PlayerEvent.PLAYER_JOIN.register(ConfigHandler::syncConfigsToPlayer);

        LifecycleEvent.SERVER_BEFORE_START.register(minecraftServer -> {
            tryRegister(ConfigSyncPackets.START_SYNC);
            tryRegister(ConfigSyncPackets.END_SYNC);
            tryRegister(ConfigSyncPackets.SYNC_OPTION);
        });

    }

    private static void tryRegister(ResourceLocation resourceLocation) {
        try {
            NetworkAggregator.registerS2CType(resourceLocation, List.of());
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    /**
     * Syncs a config to a player. You should ALWAYS check if the config is designed to be synced with Config.shouldSyncToClient(), as not doing so could cause unintended consequences, especially in singleplayer.
     * @param config The config to sync
     * @param player The player to sync the config too.
     */
    public static void syncConfigToPlayer(Config config, ServerPlayer player) {

        SugarLib.LOGGER.info("Syncing config {} to {}.", config.getFullId(), player.getName().getString());

        if (player.isLocalPlayer()) {
            SugarLib.LOGGER.info("Not syncing config to player, as it is the local player.");
            return;
        }

        ResourceLocation configId = config.getFullId();

        sendStartPacket(configId, player);

        for (ConfigOption<?> option : config.getOptions()) {
            sendOption(option, player);
        }

        sendEndPacket(player);

        SugarLib.LOGGER.info("Done!");
    }

    private static void sendStartPacket(ResourceLocation configId, ServerPlayer player) {
        RegistryFriendlyByteBuf byteBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        byteBuf.writeResourceLocation(configId);
        NetworkManager.sendToPlayer(player, ConfigSyncPackets.START_SYNC, byteBuf);
    }

    private static void sendOption(ConfigOption<?> option, ServerPlayer player) {
        RegistryFriendlyByteBuf byteBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        byteBuf.writeUtf(option.id);
        try {
            option.serialize(byteBuf);
        } catch (SerializationFailureException e) {
            SugarLib.LOGGER.warn("Failed to sync config option {}.", option.getFullId(), e);
        }
        NetworkManager.sendToPlayer(player, ConfigSyncPackets.SYNC_OPTION, byteBuf);
    }

    private static void sendEndPacket(ServerPlayer player) {
        NetworkManager.sendToPlayer(player, ConfigSyncPackets.END_SYNC, new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY));
    }

}
