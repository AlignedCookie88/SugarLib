package com.alignedcookie88.sugarlib.config.value_limiter;

import net.minecraft.network.chat.Component;

public class StringBoundsValueLimiter implements ValueLimiter<String> {

    private final int min;

    private final int max;

    protected StringBoundsValueLimiter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static StringBoundsValueLimiter min(int min) {
        return new StringBoundsValueLimiter(min, Integer.MAX_VALUE);
    }

    public static StringBoundsValueLimiter max(int max) {
        return new StringBoundsValueLimiter(0, max);
    }

    public static StringBoundsValueLimiter minMax(int min, int max) {
        return new StringBoundsValueLimiter(min, max);
    }



    @Override
    public Component getLimit(String value) {
        int length = value.length();
        if (length < min) {
            return Component.translatable("sugarlib.config.limiter.string_bounds.min", min, length);
        }
        if (length > max) {
            return Component.translatable("sugarlib.config.limiter.string_bounds.max", max, length);
        }
        return null;
    }
}
