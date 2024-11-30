package com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number;

import net.minecraft.network.chat.Component;

public class FloatOptionUI extends AbstractNumberOptionUI<Float> {

    /**
     * @param percentage If true the value should be displayed as a percentage. The resulting output for values 0%-100% is 0-1. For example, 50% would result in the value being set to 0.5, whilst 75% would result in the value being set to 0.75.
     */
    public FloatOptionUI(boolean percentage) {
        super(percentage);
    }

    @Override
    protected ConversionResult<Float> fromString(String string) {
        try {
            return ConversionResult.success(Float.parseFloat(string));
        } catch (NumberFormatException e) {
            return ConversionResult.failure(Component.translatable("sugarlib.config.invalid.decimal", string), Float.class);
        }
    }

    @Override
    protected String toString(Float value) {
        return value.toString();
    }

    @Override
    protected Float toPercent(Float value) {
        return value * 100f;
    }

    @Override
    protected Float fromPercent(Float value) {
        return value / 100f;
    }
}
