package com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker;

public class CircuitBreakerOpenException extends RuntimeException {

    public CircuitBreakerOpenException(String message) {
        super(message);
    }
}
