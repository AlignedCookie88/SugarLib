package com.alignedcookie88.sugarlib.config.backend;

import com.alignedcookie88.sugarlib.config.ConfigOption;

import java.util.HashMap;
import java.util.Map;

/**
 * This backend doesn't actually do any syncing, it just stores the options purely in memory.
 */
public class SyncConfigBackend implements ConfigBackend {

    private Map<ConfigOption<?>, Object> optionMap = new HashMap<>();

    @Override
    public <T> T getValue(ConfigOption<T> option) {
        return (T) optionMap.get(option);
    }

    @Override
    public <T> void setValue(ConfigOption<T> option, T value) {
        optionMap.put(option, value);
    }

    @Override
    public void save() {

    }

    @Override
    public void load() {
        ConfigBackend.throwLoadError();
    }
}
