package com.pharmaguard.api.shared.infrastructure.resilience.bulkhead;

public class BulkheadFullException extends RuntimeException {

    public BulkheadFullException(String message) {
        super(message);
    }
}
