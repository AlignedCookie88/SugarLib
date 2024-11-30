package com.alignedcookie88.sugarlib.config.ui.optionuiprovider;

import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class StringOptionUI implements OptionUIProvider<String> {

    private EditBox box;

    private String original;

    private final int maxLength;

    public StringOptionUI(int maxLength) {
        this.maxLength = maxLength;
    }


    @Override
    public int getRequiredHeight(UIInfo info) {
        return 20;
    }

    @Override
    public void setup(UIInfo info, ClientConfigView.Option<String, ?> option) {
        box = new EditBox(info.font(), info.x(), info.y(), info.width(), 20, Component.empty());
        box.setMaxLength(maxLength);
        box.setValue(option.get());
        original = option.get();
        info.addWidget(box);
    }

    @Override
    public void render(UIInfo info, ClientConfigView.Option<String, ?> option) {
        String value = box.getValue();
        if (value.equals(original))
            option.set(original);
        else option.set(value);
    }

    @Override
    public void notifyUpdate(String value) {
        box.setValue(value);
    }
}
