package com.alignedcookie88.sugarlib.config;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.backend.JsonConfigBackend;
import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.alignedcookie88.sugarlib.config.client_view.LocalConfigView;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.nio.file.Path;

public class ServerConfig extends Config {

    public static ConfigBuilder<ServerConfig> builder() {
        return new ConfigBuilder<>(new ServerConfig());
    }


    public ServerConfig() {
        super();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("sugarlib.config.default_name.server");
    }

    @Override
    protected ResourceLocation getDefaultIcon() {
        return ResourceLocation.tryBuild("sugarlib", "textures/config/server.png");
    }

    @Override
    protected String getDefaultId() {
        return "server";
    }

    @Override
    protected Path getBaseConfigPath() {
        MinecraftServer server = SugarLib.getServer();

        if (server == null)
            return super.getBaseConfigPath();

        Path path = server.getWorldPath(LevelResource.ROOT).resolve("config");
        File fPath = path.toFile();
        if (!fPath.exists()) {
            fPath.mkdirs();
        }
        return path.toAbsolutePath();
    }

    @Override
    protected void onServerStart(MinecraftServer server) {
        backend = new JsonConfigBackend(this);
        load();
    }

    @Override
    protected void onServerStop(MinecraftServer server) {
        save();
        backend = null;
    }

    @Override
    public float sortFloat() {
        return 3;
    }

    @Override
    public ClientConfigView<?> getClientView() {
        if (SugarLib.playerInWorld()) { // Player is in a world, we must allow access
            if (SugarLib.playerInSingleplayer()) { // Local world, we can just give local access as the config object is shared
                return new LocalConfigView(this);
            } else if (SugarLib.playerHasOp()){ // Remote world, player has op, we must sync with the server
                return null; // TODO: Implement RemoteConfigView.
            } else { // Remote world, player does not have op, they should not be able to access the config.
                return null;
            }
        } else { // Player is not in a world, they should not be able to access the config as it is not loaded.
            return null;
        }
    }
}
