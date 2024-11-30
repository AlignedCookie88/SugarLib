package com.alignedcookie88.sugarlib.config.ui;

import com.alignedcookie88.sugarlib.ModInfo;
import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.Config;
import com.alignedcookie88.sugarlib.config.ConfigHandler;
import com.alignedcookie88.sugarlib.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.Collection;

public class ConfigChooserScreen extends Screen {

    private Screen parent;
    private ModInfo mod;

    public ConfigChooserScreen(ModInfo mod, Screen parent) {
        super(Component.translatable("sugarlib.config.chooser_title", mod.getName()));
        mod.warnIfNotRegistered();
        this.parent = parent;
        this.mod = mod;
    }

    @Override
    protected void init() {
        SugarLib.LOGGER.info("Opened config chooser screen for mod {}.", mod);

        Collection<Config> configs = ConfigHandler.getConfigsForMod(mod);

        if (configs.size() == 1) {
            Config config = (Config) configs.toArray()[0];
            if (config.isCurrentlyModifiable()) {
                SugarLib.LOGGER.info("Automatically opening config {} as it is the only config.", config.getFullId());
                SugarLib.getClient().setScreen(ConfigScreen.create(config, parent));
            }
        }

        int icon_size = 64;

        int spacing = 15;
        int size = icon_size + 14; // Space for text

        int x = (width-((spacing+size)*configs.size())+spacing)/2;
        int y = (height-size)/2;

        for (Config config : configs) {
            Button button = Button.builder(Component.empty(), button1 -> {
                SugarLib.getClient().setScreen(ConfigScreen.create(config, this));
            }).bounds(x, y, size, size).build();
            button.active = config.isCurrentlyModifiable();
            addRenderableWidget(button);

            if (!button.active && config instanceof ServerConfig && SugarLib.playerInWorld()) {
                button.setTooltip(Tooltip.create(Component.translatable("sugarlib.config.server_config_editing_soon")));
            }

            int finalX = x;

            addRenderableOnly((guiGraphics, i, j, f) -> {
                guiGraphics.drawCenteredString(this.font, config.getName(), finalX+(size/2), y+size-12, 0xFFFFFF);
            });

            addRenderableOnly((guiGraphics, i, j, f) -> {
                guiGraphics.blit(config.getIcon(), finalX + 7, y, 0, 0, icon_size, icon_size);
            });

            x += spacing + size;
        }

        addRenderableOnly((guiGraphics, i, j, f) -> {
            guiGraphics.drawCenteredString(this.font, this.title, width/2, height/4, 0xFFFFFF);
        });

        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, button -> {
            onClose();
        }).bounds((width-100)/2, height/4*3, 100, 20).build());

    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
