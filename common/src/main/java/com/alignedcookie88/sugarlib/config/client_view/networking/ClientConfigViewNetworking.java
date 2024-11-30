package com.alignedcookie88.sugarlib.config.client_view.networking;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.serializers.ConfigSerializer;
import com.alignedcookie88.sugarlib.config.serializers.SerializationFailureException;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public class ClientConfigViewNetworking {


    @ApiStatus.Internal
    public static void init() {

    }


    public static class ConfigUpdate {


        public static void start(ResourceLocation configId) {

            RegistryFriendlyByteBuf byteBuf = new RegistryFriendlyByteBuf(new FriendlyByteBuf(Unpooled.buffer()), RegistryAccess.EMPTY);

            byteBuf.writeResourceLocation(configId);

            NetworkManager.sendToServer(ConfigViewPackets.START_CONFIG_UPDATE, byteBuf);

        }

        public static <T> void setOption(String id, T value, Class<T> clazz) {

            RegistryFriendlyByteBuf byteBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);

            byteBuf.writeUtf(id);

            try {
                ConfigSerializer.serialize(byteBuf, value, clazz);
            } catch (SerializationFailureException e) {
                SugarLib.LOGGER.warn("Failed to send value to server {}.", id);
            }

            NetworkManager.sendToServer(ConfigViewPackets.CONFIG_UPDATE_SET_OPTION, byteBuf);

        }

        public static void end() {

            NetworkManager.sendToServer(ConfigViewPackets.END_CONFIG_UPDATE, new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY));

        }

    }


}
