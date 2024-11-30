package com.alignedcookie88.sugarlib.fabric;

import com.alignedcookie88.sugarlib.ModInfo;
import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.ui.ConfigChooserScreen;
import com.alignedcookie88.sugarlib.config.ConfigHandler;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.util.HashMap;
import java.util.Map;

public class SugarLibModMenuApiImpl implements ModMenuApi {


    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();

        for (ModInfo mod : ConfigHandler.getModsWithConfigs()) {
            mod.warnIfNotRegistered();
            factories.put(mod.getId(), parent -> new ConfigChooserScreen(mod, parent));
        }

        return factories;
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigChooserScreen(SugarLib.modInfo, parent);
    }
}
