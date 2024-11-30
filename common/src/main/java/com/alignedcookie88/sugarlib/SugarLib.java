package com.alignedcookie88.sugarlib;

import com.alignedcookie88.sugarlib.config.*;
import com.alignedcookie88.sugarlib.config.client_view.networking.ClientConfigViewNetworking;
import com.alignedcookie88.sugarlib.config.client_view.networking.ServerConfigViewNetworking;
import com.alignedcookie88.sugarlib.config.serializers.*;
import com.alignedcookie88.sugarlib.config.sync.ClientConfigSyncManager;
import com.alignedcookie88.sugarlib.config.sync.ServerConfigSyncManager;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.OptionUIProvider;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number.FloatOptionUI;
import com.alignedcookie88.sugarlib.config.value_limiter.StringBoundsValueLimiter;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class SugarLib {
    public static final String MOD_ID = "sugarlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("SugarLib");

    public static ModInfo modInfo;


    public static final ConfigOption<Boolean> OPT_DISPLAY_BOOLEANS_TRUE_FALSE = new ConfigOption<>("display_booleans_true_false", Component.translatable("sugarlib.opt.display_booleans_true_false"), false, Boolean.class);


    private static MinecraftServer server;


    /**
     * SugarLib initialisation function, DO NOT CALL
     */
    @ApiStatus.Internal
    public static void init(ModInfo modInfo1) {
        LOGGER.info("Starting...");

        // Setup mod info
        modInfo = modInfo1;
        registerMod(modInfo);

        // Setup config serializers
        LOGGER.info("Setting up config serializers");
        ConfigSerializer.register(String.class, new FunctionPairSerializer<>(FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf));
        ConfigSerializer.register(Integer.class, new FunctionPairSerializer<>(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt));
        ConfigSerializer.register(Long.class, new FunctionPairSerializer<>(FriendlyByteBuf::writeLong, FriendlyByteBuf::readLong));
        ConfigSerializer.register(Float.class, new FunctionPairSerializer<>(FriendlyByteBuf::writeFloat, FriendlyByteBuf::readFloat));
        ConfigSerializer.register(Double.class, new FunctionPairSerializer<>(FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble));
        ConfigSerializer.register(ResourceLocation.class, new FunctionPairSerializer<>(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation));
        ConfigSerializer.register(Boolean.class, new FunctionPairSerializer<>(FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean));


        // Register events
        LOGGER.info("Registering events");
        LifecycleEvent.SERVER_BEFORE_START.register(instance -> {
            server = instance;
            ConfigHandler.forEachConfig(config -> config.serverStartNotify(instance));
        });
        LifecycleEvent.SERVER_STOPPED.register(instance -> {
            ConfigHandler.forEachConfig(config -> config.serverStopNotify(instance));
            server = null;
        });

        // Other setup
        ServerConfigSyncManager.setup();
        ServerConfigViewNetworking.init();

        LOGGER.info("Ready!");
    }

    /**
     * SugarLib initialisation function, DO NOT CALL
     */
    @ApiStatus.Internal
    public static void initClient() {
        SugarLib.LOGGER.info("Setting up client...");

        ClientConfigSyncManager.setup();
        ClientConfigViewNetworking.init();
        OptionUIProvider.registerIncluded();

        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> ConfigHandler.forEachConfig(Config::returnToLocal));

        SugarLib.LOGGER.info("Setting up client config");
        ConfigHandler.registerConfig(ClientConfig.builder()
                .withOption(OPT_DISPLAY_BOOLEANS_TRUE_FALSE)
                .build(), modInfo);

        SugarLib.LOGGER.info("Done!");
    }


    private static List<ModInfo> mods = new ArrayList<>();

    /**
     * Gets a list of all mods registered with SugarLib.registerMod()
     * @return The list of all mods
     */
    public static Collection<ModInfo> getMods() {
        return ImmutableList.copyOf(mods);
    }

    /**
     * Registers a mod with SugarLib, allowing SugarLib to perform some actions related to the mod.
     * @param mod The ModInfo instance, created with either FabricModInfo.fromId() or NeoForgeModInfo.fromModObject().
     */
    public static void registerMod(ModInfo mod) {
        LOGGER.info("Dependant {} has registered.", mod);
        if (!mods.contains(mod))
            mods.add(mod);
    }

    /**
     * Checks if a mod has been registered with SugarLib.registerMod()
     * @param mod The mod to check for
     * @return Whether the mod has been registered
     */
    public static boolean isModRegistered(ModInfo mod) {
        return mods.contains(mod);
    }

    /**
     * Checks if a mod has been registered with SugarLib.registerMod()
     * @param id The mod ID to check for
     * @return Whether the mod has been registered
     */
    public static boolean isModRegistered(String id) {
        return mods.stream().anyMatch(modInfo1 -> Objects.equals(id, modInfo.id));
    }


    /**
     * Get the currently running server
     * @return The current server, or null if no server is running
     */
    public static MinecraftServer getServer() {
        return server;
    }

    /**
     * Gets the Minecraft client.
     * @return The client
     */
    public static Minecraft getClient() {
        RenderSystem.assertOnRenderThread();
        return Minecraft.getInstance();
    }

    /**
     * Checks if the client player is in a world.
     */
    public static boolean playerInWorld() {
        return getClient().level != null;
    }

    /**
     * Checks if the client player has OP permissions
     */
    public static boolean playerHasOp() {
        return getClient().player.hasPermissions(4);
    }

    /**
     * Checks if the client player is in singleplayer
     */
    public static boolean playerInSingleplayer() {
        return getClient().hasSingleplayerServer();
    }
}
