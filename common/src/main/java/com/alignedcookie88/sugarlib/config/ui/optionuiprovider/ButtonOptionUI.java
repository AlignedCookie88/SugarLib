package com.alignedcookie88.sugarlib.config.ui.optionuiprovider;

import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.alignedcookie88.sugarlib.config.ui.ConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A singular button that can be clicked, with a custom callback. Useful for revoking TOS agreements, etc.
 */
public class ButtonOptionUI<T> implements OptionUIProvider<T> {

    protected final Component text;
    protected final Component hover;
    protected final Consumer<ClickData<T>> onClick;

    public ButtonOptionUI(@NotNull Component text, @Nullable Component hover, @NotNull Consumer<ClickData<T>> onClick) {
        this.text = text;
        this.hover = hover;
        this.onClick = onClick;
    }

    @Override
    public int getRequiredHeight(UIInfo info) {
        return 20;
    }

    @Override
    public void setup(UIInfo info, ClientConfigView.Option<T, ?> option) {
        Button button = Button.builder(text, button1 -> {
            onClick.accept(new ClickData<>(info, option));
        }).bounds(info.x(), info.y(), info.width(), 20).build();
        if (hover != null)
            button.setTooltip(Tooltip.create(hover));
        info.addWidget(button);
    }

    @Override
    public void render(UIInfo info, ClientConfigView.Option<T, ?> option) {

    }

    @Override
    public void notifyUpdate(T value) {

    }

    public record ClickData<T>(UIInfo info, ClientConfigView.Option<T, ?> option) {

        public void set(T value) {
            option.set(value);
        }

        public ConfigScreen getScreen(T value) {
            return info.screen();
        }

    }
}
