package com.alignedcookie88.sugarlib.config.value_limiter;

import net.minecraft.network.chat.Component;

public interface ValueLimiter<T> {

    /**
     * Checks if the value is limited
     * @param value The value to check
     * @return The reason the value was limited, or null if not limited.
     */
    Component getLimit(T value);


    /**
     * Combines multiple value limiters.
     * If no limiters are provided, the provided value limiter will always return null.
     * If only one limiter is provided, it will simply be returned.
     * In all other cases, a CompoundValueLimiter instance will be returned.
     * @param limiters The limiters to combine.
     * @return A value limiter that encompasses the rules of all.
     */
    @SafeVarargs
    static <T> ValueLimiter<T> of(ValueLimiter<T>... limiters) {
        if (limiters.length == 0)
            return value -> null;

        if (limiters.length == 1)
            return limiters[0];

        return new CompoundValueLimiter<>(limiters);
    }
}
