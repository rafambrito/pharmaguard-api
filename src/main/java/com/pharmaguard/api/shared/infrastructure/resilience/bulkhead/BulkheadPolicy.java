package com.pharmaguard.api.shared.infrastructure.resilience.bulkhead;

public record BulkheadPolicy(int maxConcurrentCalls) {

    public BulkheadPolicy {
        if (maxConcurrentCalls < 1) {
            throw new IllegalArgumentException("maxConcurrentCalls deve ser maior que zero");
        }
    }
}
