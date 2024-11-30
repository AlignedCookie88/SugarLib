package com.alignedcookie88.sugarlib.config.client_view;

import com.alignedcookie88.sugarlib.ModInfo;
import com.alignedcookie88.sugarlib.config.Config;
import com.alignedcookie88.sugarlib.config.ConfigHandler;
import com.alignedcookie88.sugarlib.config.ConfigOption;
import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;

import java.util.*;

public class LocalConfigView implements ClientConfigView<LocalConfigView> {

    protected final Config config;

    protected final Map<String, LCVOption<?>> optionMap = new HashMap<>();
    protected final Map<String, ConfigOption<?>> configOptionMap = new HashMap<>();
    protected final List<LCVOption<?>> ordOpts = new ArrayList<>();

    public LocalConfigView(Config config) {
        this.config = config;
    }


    @Override
    public Collection<Option<?, LocalConfigView>> getOptions() {
        return ImmutableList.copyOf(ordOpts);
    }

    @Override
    public void load() {
        optionMap.clear();
        configOptionMap.clear();

        for (ConfigOption<?> option : config.getOptions()) {
            LCVOption<?> opt = new LCVOption<>(option, this);
            optionMap.put(option.id, opt);
            configOptionMap.put(option.id, option);
            ordOpts.add(opt);
        }
    }

    @Override
    public void save() {
        for (String id : optionMap.keySet()) {
            LCVOption<?> option = optionMap.get(id);

            if (option.hasChanged()) {
                option.setOpt();
            }
            option.resetChange();
        }
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public <T> T _internal_getValue(Option<T, LocalConfigView> opt) {
        return ((LCVOption<T>) opt).getFromOpt();
    }

    @Override
    public boolean hasFinishedLoading() {
        return true;
    }

    @Override
    public Component getName() {
        return config.getName();
    }

    @Override
    public ModInfo getMod() {
        return ConfigHandler.getConfigOwner(config);
    }


    public static class LCVOption<T> extends ClientConfigView.Option<T, LocalConfigView> {

        LCVOption(ConfigOption<T> opt, LocalConfigView view) {
            super(opt, view);
        }

        void setOpt() {
            this.opt.set(this.get());
        }

        T getFromOpt() {
            return this.opt.get();
        }

        void resetChange() {
            this.change = null;
        }

    }

}
