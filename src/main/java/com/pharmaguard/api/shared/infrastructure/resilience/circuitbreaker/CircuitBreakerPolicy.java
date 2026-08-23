package com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker;

import java.time.Duration;
import java.util.Objects;

public record CircuitBreakerPolicy(int failureThreshold, Duration openDuration) {

    public CircuitBreakerPolicy {
        if (failureThreshold < 1) {
            throw new IllegalArgumentException("failureThreshold deve ser maior que zero");
        }
        Objects.requireNonNull(openDuration, "openDuration e obrigatorio");
        if (openDuration.isNegative() || openDuration.isZero()) {
            throw new IllegalArgumentException("openDuration deve ser maior que zero");
        }
    }
}
