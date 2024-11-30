package com.alignedcookie88.sugarlib.config;

import com.alignedcookie88.sugarlib.ModInfo;
import com.alignedcookie88.sugarlib.config.backend.ConfigBackend;
import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.google.common.collect.ImmutableList;
import dev.architectury.platform.Platform;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Config {

    private Component customName = null;

    private ResourceLocation customIcon = null;

    private String configId;

    protected final List<ConfigOption<?>> options = new ArrayList<>();

    protected ConfigBackend backend;

    public Config() {
        configId = getDefaultId();
    }


    public void setCustomName(Component customName) {
        this.customName = customName;
    }

    public Component getName() {
        return customName == null ? getDefaultName() : customName;
    }

    protected abstract Component getDefaultName();


    public void setCustomIcon(ResourceLocation customIcon) {
        this.customIcon = customIcon;
    }

    public ResourceLocation getIcon() {
        return customIcon == null ? getDefaultIcon() : customIcon;
    }

    public void addOption(ConfigOption<?> option) {
        option.setOwningConfig(this);
        options.add(option);
    }

    protected abstract ResourceLocation getDefaultIcon();


    public String getId() {
        return configId;
    }

    public ResourceLocation getFullId() {
        ModInfo mod = ConfigHandler.getConfigOwner(this);
        return ResourceLocation.tryBuild(mod.getId(), this.getId());
    }

    protected abstract String getDefaultId();


    /**
     * Gets the current value of the config option.
     * @param option The option to get
     * @return The value of the config option, or the default value if the backend returned null.
     */
    public <T> T getValue(ConfigOption<T> option) {
        throwIfNotInitialised();
        T value = backend.getValue(option);
        return value == null ? option.defaultValue : value;
    }

    /**
     * Sets the value of this config option.
     * Unlike ConfigOption.set(), this method does not save the config.
     * If the config cannot be written to, this function will silently do nothing.
     * @param value The value to set
     */
    public <T> void setValue(ConfigOption<T> option, T value) {
        throwIfNotInitialised();
        backend.setValue(option, value);
    }

    /**
     * Saves the config to disk. If the config cannot be saved then it will silently do nothing.
     */
    public void save() {
        throwIfNotInitialised();
        backend.save();
    }

    /**
     * Loads the config from disk.
     * @throws IllegalStateException If the current backend does not support loading.
     */
    public void load() {
        throwIfNotInitialised();
        backend.load();
    }

    protected void throwIfNotInitialised() {
        if (backend == null) {
            throw new ConfigNotInitialisedException();
        }
    }





    protected Path getBaseConfigPath() {
        return Platform.getConfigFolder();
    }

    public Path getConfigPath(String extension) {
        ModInfo mod = ConfigHandler.getConfigOwner(this);
        Path base = getBaseConfigPath();
        return base.resolve(mod.getId()+"_"+configId+"."+extension);
    }


    public boolean readyNotified = false;

    /**
     * Notifies the config that it is ready
     * @throws IllegalStateException If the config has already been notified that it is ready
     */
    @ApiStatus.Internal
    public void readyNotify() throws IllegalStateException {
        if (readyNotified) {
            throw new IllegalStateException("This config has already been notified that it is ready.");
        }
        readyNotified = true;
        onReady();
    }

    protected void onReady() {

    }


    /**
     * Should be called when the server starts, this causes the config to prepare if it is world-specific.
     * @param server The server
     */
    @ApiStatus.Internal
    public void serverStartNotify(MinecraftServer server) {
        onServerStart(server);
    }

    protected void onServerStart(MinecraftServer server) {

    }


    /**
     * Should be called when the server stops, this causes the config to disable itself if it is world-specific.
     * @param server The server
     */
    @ApiStatus.Internal
    public void serverStopNotify(MinecraftServer server) {
        onServerStop(server);
    }

    protected void onServerStop(MinecraftServer server) {

    }


    /**
     * Checks if the config is able to be modified by the client. Do <b>NOT</b> call this on the server.
     * @return if the client can modify the config safely
     */
    public boolean isCurrentlyModifiable() {
        return getClientView() != null;
    }


    /**
     * Get the options this config has
     * @return An immutable collection of this config's options
     */
    public Collection<ConfigOption<?>> getOptions() {
        return ImmutableList.copyOf(options);
    }


    @ApiStatus.Internal
    public boolean shouldSyncToClient() {
        return false;
    }


    /**
     * Gets a config option from its ID.
     * @param id The option's id
     * @return The option
     */
    public ConfigOption<?> getOption(String id) {
        for (ConfigOption<?> opt : getOptions()) {
            if (opt.id.equals(id))
                return opt;
        }
        return null;
    }


    /**
     * Called by the ClientConfigSyncManager when the config is about to be synced.
     */
    @ApiStatus.Internal
    public void prepareForSync() {

    }

    /**
     * Called when the player disconnects from a world, if the config previously was in sync mode, it will return to local mode here.
     */
    @ApiStatus.Internal
    public void returnToLocal() {

    }



    @ApiStatus.Internal
    public abstract float sortFloat();


    /**
     * Gets a config view for this config.
     * @return The config view, or null if the player has no permission to access the config.
     */
    public abstract ClientConfigView<?> getClientView();
}
