package com.alignedcookie88.sugarlib.config.value_limiter;

import net.minecraft.network.chat.Component;

public class NumberBoundsValueLimiter<T extends Number> implements ValueLimiter<T> {

    private final T min;

    private final T max;

    protected NumberBoundsValueLimiter(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public static <T extends Number> NumberBoundsValueLimiter<T> minMax(T min, T max) {
        return new NumberBoundsValueLimiter<>(min, max);
    }

    @Override
    public Component getLimit(T value) {
        if (value.doubleValue() < min.doubleValue()) {
            return Component.translatable("sugarlib.config.limiter.number_bounds.min", min, value);
        }
        if (value.doubleValue() > max.doubleValue()) {
            return Component.translatable("sugarlib.config.limiter.number_bounds.max", max, value);
        }
        return null;
    }
}
