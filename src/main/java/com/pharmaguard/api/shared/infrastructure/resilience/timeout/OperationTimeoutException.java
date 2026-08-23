package com.pharmaguard.api.shared.infrastructure.resilience.timeout;

public class OperationTimeoutException extends RuntimeException {

    public OperationTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
