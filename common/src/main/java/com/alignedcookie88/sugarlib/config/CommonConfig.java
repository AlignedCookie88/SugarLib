package com.alignedcookie88.sugarlib.config;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.backend.JsonConfigBackend;
import com.alignedcookie88.sugarlib.config.backend.SyncConfigBackend;
import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.alignedcookie88.sugarlib.config.client_view.LocalConfigView;
import com.alignedcookie88.sugarlib.config.client_view.PartiallyRemoteConfigView;
import com.alignedcookie88.sugarlib.config.client_view.ReadOnlyLocalConfigView;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CommonConfig extends Config {

    /**
     * Creates a config builder.
     * @return The config builder
     */
    public static ConfigBuilder<CommonConfig> builder() {
        return new ConfigBuilder<>(new CommonConfig());
    }

    /**
     * Same as .builder(), but the resulting config won't sync to the client. This should be avoided as there is no way to modify the server's config from the client.
     * @return The config builder
     */
    public static ConfigBuilder<NonSyncingCommonConfig> builderNonSyncing() {
        return new ConfigBuilder<>(new NonSyncingCommonConfig());
    }


    public CommonConfig() {
        super();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("sugarlib.config.default_name.common");
    }

    @Override
    protected ResourceLocation getDefaultIcon() {
        return ResourceLocation.tryBuild("sugarlib", "textures/config/common.png");
    }

    @Override
    protected String getDefaultId() {
        return "common";
    }

    private void setLocalBackend() {
        backend = new JsonConfigBackend(this);
        load();
    }

    @Override
    protected void onReady() {
        setLocalBackend();
    }

    @Override
    public boolean shouldSyncToClient() {
        return true;
    }


    @Override
    public void prepareForSync() {
        if (SugarLib.playerInSingleplayer())
            return;
        SugarLib.LOGGER.info("Config {}: Preparing for sync", getFullId());
        backend = new SyncConfigBackend();
    }

    @Override
    public void returnToLocal() {
        if (backend instanceof SyncConfigBackend) {
            SugarLib.LOGGER.info("Config {}: Returning to local storage", getFullId());
            setLocalBackend();
        }
    }

    @Override
    public float sortFloat() {
        return 1;
    }

    @Override
    public ClientConfigView<?> getClientView() {
        if (SugarLib.playerInWorld() && !SugarLib.playerInSingleplayer()) { // Remote server, we must sync with the server over the network.
            if (SugarLib.playerHasOp()) { // Player can write, as the player has a copy of all values already, we only need to send write packets.
                return new PartiallyRemoteConfigView(this);
            } else { // Player can only read, the player already has a copy of all common values on the client, so we can read them locally.
                return new ReadOnlyLocalConfigView(this);
            }
        } else { // Local world (or none), we can just write locally as the config object is shared.
            return new LocalConfigView(this);
        }
    }
}
