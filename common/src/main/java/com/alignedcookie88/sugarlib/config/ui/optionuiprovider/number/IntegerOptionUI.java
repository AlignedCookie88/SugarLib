package com.alignedcookie88.sugarlib.config.ui.optionuiprovider.number;

import net.minecraft.network.chat.Component;

public class IntegerOptionUI extends AbstractNumberOptionUI<Integer> {

    /**
     * @param percentage If the value should be displayed as a percentage. The value is not adjusted.
     */
    public IntegerOptionUI(boolean percentage) {
        super(percentage);
    }

    @Override
    protected ConversionResult<Integer> fromString(String string) {
        try {
            return ConversionResult.success(Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return ConversionResult.failure(Component.translatable("sugarlib.config.invalid.integer", string), Integer.class);
        }
    }

    @Override
    protected String toString(Integer value) {
        return value.toString();
    }

    @Override
    protected Integer toPercent(Integer value) {
        return value;
    }

    @Override
    protected Integer fromPercent(Integer value) {
        return value;
    }
}
