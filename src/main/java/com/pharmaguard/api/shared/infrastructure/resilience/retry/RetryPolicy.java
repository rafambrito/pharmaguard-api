package com.pharmaguard.api.shared.infrastructure.resilience.retry;

import java.time.Duration;
import java.util.Objects;

public record RetryPolicy(int maxAttempts, Duration backoff) {

    public RetryPolicy {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts deve ser maior que zero");
        }
        Objects.requireNonNull(backoff, "backoff e obrigatorio");
        if (backoff.isNegative()) {
            throw new IllegalArgumentException("backoff nao pode ser negativo");
        }
    }
}
