package com.pharmaguard.api.shared.infrastructure.resilience.timeout;

import java.time.Duration;
import java.util.Objects;

public record TimeoutPolicy(Duration timeout) {

    public TimeoutPolicy {
        Objects.requireNonNull(timeout, "timeout e obrigatorio");
        if (timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("timeout deve ser maior que zero");
        }
    }
}
