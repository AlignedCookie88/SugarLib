package com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number;

import net.minecraft.network.chat.Component;

public class DoubleOptionUI extends AbstractNumberOptionUI<Double> {

    /**
     * @param percentage If true the value should be displayed as a percentage. The resulting output for values 0%-100% is 0-1. For example, 50% would result in the value being set to 0.5, whilst 75% would result in the value being set to 0.75.
     */
    public DoubleOptionUI(boolean percentage) {
        super(percentage);
    }

    @Override
    protected ConversionResult<Double> fromString(String string) {
        try {
            return ConversionResult.success(Double.parseDouble(string));
        } catch (NumberFormatException e) {
            return ConversionResult.failure(Component.translatable("sugarlib.config.invalid.decimal", string), Double.class);
        }
    }

    @Override
    protected String toString(Double value) {
        return value.toString();
    }

    @Override
    protected Double toPercent(Double value) {
        return value * 100d;
    }

    @Override
    protected Double fromPercent(Double value) {
        return value / 100d;
    }
}
