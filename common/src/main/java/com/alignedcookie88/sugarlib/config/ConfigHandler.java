package com.alignedcookie88.sugarlib.config;

import com.alignedcookie88.sugarlib.ModInfo;
import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.sync.ServerConfigSyncManager;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ConfigHandler {

    private static final List<ModInfo> withConfigs = new ArrayList<>();

    private static final Map<ModInfo, List<Config>> configs = new HashMap<>();

    @ApiStatus.Internal
    public static Collection<ModInfo> getModsWithConfigs() {
        return ImmutableList.copyOf(withConfigs);
    }

    /**
     * Registers a config for a mod, allowing SugarLib to automatically construct config screens for it, and make them accessible via the appropriate methods (ModMenu or NeoForge's mod list)
     * @param config The config to register
     * @param mod The mod the config belongs to
     */
    public static void registerConfig(Config config, ModInfo mod) {
        mod.warnIfNotRegistered();
        SugarLib.LOGGER.info("Registering config of type {} for mod {}.", config.getClass(), mod);

        List<Config> modConfigs;
        if (withConfigs.contains(mod)) {
            modConfigs = configs.get(mod);
        } else {
            modConfigs = new ArrayList<>();
        }

        if (!modConfigs.contains(config))
            modConfigs.add(config);

        configs.put(mod, modConfigs);

        if (!withConfigs.contains(mod)) {
            withConfigs.add(mod);
            mod.registerConfigScreenIfRequired();
        }

        config.readyNotify();
    }


    /**
     * Get all the configs registered for a specific mod
     * @param mod The mod
     * @return The configs
     */
    public static Collection<Config> getConfigsForMod(ModInfo mod) {
        List<Config> c = configs.getOrDefault(mod, new ArrayList<>());
        c.sort(Comparator.comparingDouble(Config::sortFloat));
        return ImmutableList.copyOf(c);
    }


    /**
     * Gets the mod that a config is associated with
     * @param config The config
     * @return The mod
     */
    public static ModInfo getConfigOwner(Config config) {
        for (ModInfo mod : configs.keySet()) {
            List<Config> modConfigs = configs.get(mod);
            for (Config config1 : modConfigs) {
                if (config1 == config) {
                    return mod;
                }
            }
        }
        return null;
    }


    /**
     * Run some code on each config.
     * @param consumer The code to run.
     */
    public static void forEachConfig(Consumer<Config> consumer) {
        for (List<Config> configs1 : configs.values()) {
            for (Config config : configs1) {
                consumer.accept(config);
            }
        }
    }


    /**
     * Gets a config from its ID.
     * @param id The id of the config.
     * @return The config, or null if not found.
     */
    public static Config getConfigFromId(ResourceLocation id) {

        AtomicReference<Config> found = new AtomicReference<>();

        forEachConfig(config -> {
            ResourceLocation cid = config.getFullId();
            if (cid.toString().equals(id.toString()))
                found.set(config);
        });

        return found.get();
    }


    /**
     * Syncs all sync-able configs to a player
     * @param player The player
     */
    public static void syncConfigsToPlayer(ServerPlayer player) {
        forEachConfig(config -> {
            if (config.shouldSyncToClient())
                ServerConfigSyncManager.syncConfigToPlayer(config, player);
        });
    }
}
