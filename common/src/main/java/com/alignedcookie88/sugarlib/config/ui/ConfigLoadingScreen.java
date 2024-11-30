package com.alignedcookie88.sugarlib.config.ui;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigLoadingScreen extends Screen {

    private final ClientConfigView<?> view;

    private final Screen parent;

    private int frames = 0;

    protected ConfigLoadingScreen(ClientConfigView<?> view, Screen parent) {
        super(Component.empty());
        this.view = view;
        this.parent = parent;
        view.load();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        if (frames > 600) {
            guiGraphics.drawCenteredString(font, Component.translatable("sugarlib.config.slow_load"), (width/2), (height/2), 0xFFFFFF);
        }
        frames++;

        if (view.hasFinishedLoading())
            SugarLib.getClient().setScreen(new ConfigScreen(view, parent, 0));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
