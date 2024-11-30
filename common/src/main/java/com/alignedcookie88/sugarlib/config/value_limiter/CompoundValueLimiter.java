package com.alignedcookie88.sugarlib.config.value_limiter;

import net.minecraft.network.chat.Component;

public class CompoundValueLimiter<T> implements ValueLimiter<T> {

    private final ValueLimiter<T>[] limiters;

    @SafeVarargs
    public CompoundValueLimiter(ValueLimiter<T>... limiters) {
        this.limiters = limiters;
    }

    @Override
    public Component getLimit(T value) {
        for (ValueLimiter<T> limiter : limiters) {
            Component reason = limiter.getLimit(value);
            if (reason != null)
                return reason;
        }
        return null;
    }

}
