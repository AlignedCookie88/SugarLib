package com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number;

import com.alignedcookie88.sugarlib.config.client_view.ClientConfigView;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.OptionUIProvider;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public abstract class AbstractNumberOptionUI<T extends Number> implements OptionUIProvider<T> {

    private EditBox box;

    protected boolean percentage;

    protected AbstractNumberOptionUI(boolean percentage) {
        this.percentage = percentage;
    }


    @Override
    public int getRequiredHeight(UIInfo info) {
        return 20;
    }

    @Override
    public void setup(UIInfo info, ClientConfigView.Option<T, ?> option) {
        box = new EditBox(info.font(), info.x(), info.y(), info.width(), 20, Component.empty());
        box.setMaxLength(Integer.MAX_VALUE);
        box.setValue(doToString(option.get()));
        info.addWidget(box);
    }

    private ConversionResult<T> lastConversion;

    @Override
    public void render(UIInfo info, ClientConfigView.Option<T, ?> option) {
        lastConversion = doFromString(box.getValue());
        if (lastConversion.valid) {
            option.set(lastConversion.value);
        }
    }

    @Override
    public void notifyUpdate(T value) {
        box.setValue(doToString(value));
    }

    @Override
    public Component isValid() {
        if (lastConversion == null)
            return null;
        return lastConversion.errorMessage;
    }

    protected ConversionResult<T> doFromString(String string) {
        if (!percentage)
            return fromString(string);

        if (string.endsWith("%"))
            string = string.substring(0, string.length()-1);

        ConversionResult<T> r = fromString(string);
        if (!r.valid)
            return r;

        return ConversionResult.success(fromPercent(r.value));
    }

    protected String doToString(T value) {
        if (!percentage)
            return toString(value);

        return toString(toPercent(value))+"%";
    }

    protected abstract ConversionResult<T> fromString(String string);

    protected abstract String toString(T value);

    protected abstract T toPercent(T value);

    protected abstract T fromPercent(T value);

    protected static class ConversionResult<T> {

        public final boolean valid;

        public final T value;

        public final Component errorMessage;

        private ConversionResult(boolean valid, T value, Component errorMessage) {
            this.valid = valid;
            this.value = value;
            this.errorMessage = errorMessage;
        }

        public static <T> ConversionResult<T> success(T value) {
            return new ConversionResult<>(true, value, null);
        }

        public static <T> ConversionResult<T> failure(Component errorMessage, Class<T> clazz) {
            return new ConversionResult<>(false, null, errorMessage);
        }

    }
}
