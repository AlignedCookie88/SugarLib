package com.alignedcookie88.sugarlib.config.client_view.networking;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.Config;
import com.alignedcookie88.sugarlib.config.ConfigHandler;
import com.alignedcookie88.sugarlib.config.ConfigOption;
import com.alignedcookie88.sugarlib.config.serializers.SerializationFailureException;
import com.alignedcookie88.sugarlib.config.sync.ServerConfigSyncManager;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

public class ServerConfigViewNetworking {


    @ApiStatus.Internal
    public static void init() {

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ConfigViewPackets.START_CONFIG_UPDATE, ServerConfigViewNetworking.ConfigUpdate::onStart);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ConfigViewPackets.END_CONFIG_UPDATE, ServerConfigViewNetworking.ConfigUpdate::onEnd);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ConfigViewPackets.CONFIG_UPDATE_SET_OPTION, ServerConfigViewNetworking.ConfigUpdate::onOption);

    }


    private static boolean doesPlayerHavePermission(NetworkManager.PacketContext context) {
        Player player = context.getPlayer();

        // Scuffed way to check if the player is local
        if (!SugarLib.getServer().isDedicatedServer() && SugarLib.getClient().player.getUUID().toString().equals(player.getUUID().toString())) {
            return true;
        }

        return player.hasPermissions(4);
    }


    public static class ConfigUpdate {


        private static Config currentConf;


        private static void onStart(RegistryFriendlyByteBuf byteBuf, NetworkManager.PacketContext context) {

            if (!doesPlayerHavePermission(context))
                return;

            ResourceLocation id = byteBuf.readResourceLocation();

            currentConf = ConfigHandler.getConfigFromId(id);
        }


        private static void onEnd(RegistryFriendlyByteBuf byteBuf, NetworkManager.PacketContext context) {

            if (!doesPlayerHavePermission(context))
                return;

            if (currentConf == null)
                return;

            if (currentConf.shouldSyncToClient()) {
                MinecraftServer server = SugarLib.getServer();

                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    ServerConfigSyncManager.syncConfigToPlayer(currentConf, player);
                }
            }

            currentConf = null;
        }


        private static void onOption(RegistryFriendlyByteBuf byteBuf, NetworkManager.PacketContext context) {

            if (!doesPlayerHavePermission(context))
                return;

            if (currentConf == null)
                return;

            String id = byteBuf.readUtf();

            ConfigOption<?> option = currentConf.getOption(id);

            if (option == null)
                return;

            try {
                option.deserialize(byteBuf);
            } catch (SerializationFailureException e) {
                SugarLib.LOGGER.info("Couldn't read value from client {}.", option.getFullId());
            }


        }
    }


}
