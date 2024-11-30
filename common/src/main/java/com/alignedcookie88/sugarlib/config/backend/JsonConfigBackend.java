package com.alignedcookie88.sugarlib.config.backend;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.Config;
import com.alignedcookie88.sugarlib.config.ConfigOption;
import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonConfigBackend implements ConfigBackend {

    private final Path configPath;

    private final Map<ConfigOption<?>, Object> values = new HashMap<>();

    private final Config config;

    public JsonConfigBackend(Config config) {
        this.configPath = config.getConfigPath("json");
        this.config = config;
    }

    @Override
    public <T> T getValue(ConfigOption<T> option) {
        return (T) values.get(option);
    }

    @Override
    public <T> void setValue(ConfigOption<T> option, T value) {
        values.put(option, value);
    }

    private JsonElement serializeOpt(ConfigOption<?> option) {
        Gson gson = new Gson();
        return gson.toJsonTree(option.get(), option.clazz);
    }

    private <T> void deserializeOpt(ConfigOption<T> option, JsonElement el) {
        Gson gson = new Gson();
        T value = gson.fromJson(el, option.clazz);
        setValue(option, value); // We don't call option.set() here as that ultimately calls the save() method, causing large IO usage.
    }

    @Override
    public void save() {
        SugarLib.LOGGER.info("Saving config at {}", configPath);

        JsonObject object = new JsonObject();

        object.add("___COMMENT!! READ ME!!", new JsonPrimitive("This config file is configurable in-game! You can access this through the mods screen on NeoForge, or through ModMenu on Fabric (if the mod this config belongs to doesn't show, set the `Libraries` option to `Shown` in the ModMenu settings)!"));

        for (ConfigOption<?> option : config.getOptions()) {
            object.add(option.id, serializeOpt(option));
        }

        String done = object.toString();


        try (FileOutputStream stream = new FileOutputStream(configPath.toFile())) {

            stream.write(done.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SugarLib.LOGGER.info("Saved!");
    }

    @Override
    public void load() {
        SugarLib.LOGGER.info("Loading config at {}", configPath);

        File configFile = configPath.toFile();
        if (!configFile.exists()) {
            SugarLib.LOGGER.info("File doesn't exist. Creating...");
            save();
            return;
        }

        try (FileInputStream stream = new FileInputStream(configFile)) {

            byte[] data = stream.readAllBytes();
            String serialized = new String(data, StandardCharsets.UTF_8);

            JsonObject json = JsonParser.parseString(serialized).getAsJsonObject();

            for (ConfigOption<?> option : config.getOptions()) {
                JsonElement el = json.get(option.id);
                if (el == null)
                    continue; // Option is not in file, we can skip it and continue using its default value, it will be added upon the next save.

                deserializeOpt(option, el);
            }

        } catch (Exception e) {
            throw new RuntimeException("Exception whilst loading config %s, if this continues to fail, try deleting the file.".formatted(configPath), e);
        }


        SugarLib.LOGGER.info("Loaded!");
    }

}
