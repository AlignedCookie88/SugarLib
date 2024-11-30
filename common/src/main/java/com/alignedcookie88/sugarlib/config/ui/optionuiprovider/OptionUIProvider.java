package com.alignedcookie88.sugarlib.config.ui.optionuiprovider;

import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.alignedcookie88.sugarlib.config.ui.ConfigScreen;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number.DoubleOptionUI;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number.FloatOptionUI;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number.IntegerOptionUI;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number.LongOptionUI;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.function.Supplier;

public interface OptionUIProvider<T> {


    int getRequiredHeight(UIInfo info);

    void setup(UIInfo info, ClientConfigView.Option<T, ?> option);

    void render(UIInfo info, ClientConfigView.Option<T, ?> option);

    void notifyUpdate(T value);

    default void notifyUpdateIfTypeCorrect(Object value) {
        try {
            notifyUpdate((T) value);
        } catch (ClassCastException ignored) {

        }
    }



    record UIInfo(int x, int y, int width, ConfigScreen screen, Font font) {

        public <T extends GuiEventListener & Renderable & NarratableEntry> void addWidget(T widget) {
            screen.addRenderableWidget(widget);
        }

    }

    record OptionRenderer<T>(OptionUIProvider<T> provider, UIInfo info, ClientConfigView.Option<T, ?> option, Button resetBeforeEditButton, Button resetOriginalButton) {

        public int getRequiredHeight() {
            return provider.getRequiredHeight(info);
        }

        public void setup() {
            provider.setup(info, option);
        }

        public void render() {
            provider.render(info, option);
        }

        public void notifyUpdate(T value) {
            provider.notifyUpdate(value);
        }

        public void notifyUpdateIfTypeCorrect(Object value) {
            try {
                notifyUpdate((T) value);
            } catch (ClassCastException ignored) {

            }
        }

    }



    @ApiStatus.Internal
    HashMap<Class<?>, Supplier<OptionUIProvider<?>>> registered = new HashMap<>();

    static <T> void register(Class<T> clazz, Supplier<OptionUIProvider<T>> provider) {
        registered.put(clazz, provider::get);
    }

    static <T> OptionUIProvider<T> getProviderOrNull(Class<T> clazz) {
        Supplier<OptionUIProvider<?>> supplier = registered.get(clazz);
        return (OptionUIProvider<T>) supplier.get();
    }



    @ApiStatus.Internal
    static void registerIncluded() {
        register(String.class, () -> new StringOptionUI(Integer.MAX_VALUE));
        register(Boolean.class, BooleanOptionUI::new);
        register(Integer.class, () -> new IntegerOptionUI(false));
        register(Long.class, () -> new LongOptionUI(false));
        register(Float.class, () -> new FloatOptionUI(false));
        register(Double.class, () -> new DoubleOptionUI(false));
    }

    default Component isValid() {
        return null;
    }


}
