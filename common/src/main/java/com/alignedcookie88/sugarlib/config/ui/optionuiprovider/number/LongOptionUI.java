package com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number;

import net.minecraft.network.chat.Component;

public class LongOptionUI extends AbstractNumberOptionUI<Long> {

    /**
     * @param percentage If the value should be displayed as a percentage. The value is not adjusted.
     */
    public LongOptionUI(boolean percentage) {
        super(percentage);
    }

    @Override
    protected ConversionResult<Long> fromString(String string) {
        try {
            return ConversionResult.success(Long.parseLong(string));
        } catch (NumberFormatException e) {
            return ConversionResult.failure(Component.translatable("sugarlib.config.invalid.integer", string), Long.class);
        }
    }

    @Override
    protected String toString(Long value) {
        return value.toString();
    }

    @Override
    protected Long toPercent(Long value) {
        return value;
    }

    @Override
    protected Long fromPercent(Long value) {
        return value;
    }
}
