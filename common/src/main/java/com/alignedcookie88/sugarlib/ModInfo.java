package com.alignedcookie88.sugarlib;

import net.minecraft.network.chat.Component;

public abstract class ModInfo {

    public final String id;

    public final Component name;

    protected ModInfo(String id, Component name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the ID of the mod
     * @return The mod ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the mod
     * @return The mod name
     */
    public Component getName() {
        return name;
    }

    /**
     * Gets the name of the mod with all cosmetic data stripped
     * @return The mod name
     */
    public String getRawName() {
        return name.getString();
    }


    /**
     * Logs a warning if this ModInfo instance has not been registered with SugarLib.registerMod()
     */
    public void warnIfNotRegistered() {
        if (!SugarLib.isModRegistered(this))
            SugarLib.LOGGER.warn("Dependant {} ({}) has not registered itself with SugarLib.registerMod(), some features may not work properly.", getRawName(), getId());
    }


    @Override
    public String toString() {
        return getRawName() + " (" + getId() + ")";
    }


    /**
     * Registers the config screen for a mod if required. Currently, only NeoForge does anything here.
     */
    public abstract void registerConfigScreenIfRequired();
}
