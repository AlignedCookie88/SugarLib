package com.alignedcookie88.sugarlib.config.ui.optionuiprovider;

import com.alignedcookie88.sugarlib.SugarLib;
import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BooleanOptionUI implements OptionUIProvider<Boolean> {

    private Button button;

    @Override
    public int getRequiredHeight(UIInfo info) {
        return 20;
    }

    @Override
    public void setup(UIInfo info, ClientConfigView.Option<Boolean, ?> option) {
        button = Button.builder(Component.empty(), button1 -> {
           option.set(!option.get());
           notifyUpdate(option.get());
        }).bounds(info.x(), info.y(), info.width(), 20).build();
        notifyUpdate(option.get());
        info.addWidget(button);
    }

    @Override
    public void render(UIInfo info, ClientConfigView.Option<Boolean, ?> option) {

    }

    @Override
    public void notifyUpdate(Boolean value) {
        if (value) {
            button.setMessage(
                    (SugarLib.OPT_DISPLAY_BOOLEANS_TRUE_FALSE.get() ? Component.translatable("sugarlib.config.boolean.true") : Component.translatable("sugarlib.config.boolean.yes"))
                            .withStyle(ChatFormatting.GREEN)
            );
        } else {
            button.setMessage(
                    (SugarLib.OPT_DISPLAY_BOOLEANS_TRUE_FALSE.get() ? Component.translatable("sugarlib.config.boolean.false") : Component.translatable("sugarlib.config.boolean.no"))
                            .withStyle(ChatFormatting.RED)
            );
        }
    }
}
