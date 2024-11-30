package com.alignedcookie88.sugarlib.config.backend;

import com.alignedcookie88.sugarlib.config.ConfigOption;

public interface ConfigBackend {

    /**
     * Gets the value of the config option
     * Returns `null` instead of the default value, it is the caller's responsibility to handle default values.
     * @param option The option to get the value of
     * @return The options value, or null if not modified
     */
    <T> T getValue(ConfigOption<T> option);

    /**
     * Sets the value of the config option
     * @param option The option
     * @param value The value
     */
    <T> void setValue(ConfigOption<T> option, T value);

    /**
     * Saves the config to disk. If the config cannot be saved then it will silently do nothing.
     */
    void save();

    /**
     * Loads the config from disk.
     * @throws IllegalStateException If the current backend does not support loading.
     */
    void load();

    static void throwLoadError() {
        throw new IllegalStateException("The current config backend does not support loading.");
    }

}
