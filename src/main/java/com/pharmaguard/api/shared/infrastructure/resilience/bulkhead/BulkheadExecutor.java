package com.pharmaguard.api.shared.infrastructure.resilience.bulkhead;

import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BulkheadExecutor {

    private static final Logger log = LoggerFactory.getLogger(BulkheadExecutor.class);

    private final Semaphore permits;

    public BulkheadExecutor(BulkheadPolicy policy) {
        Objects.requireNonNull(policy, "policy e obrigatoria");
        this.permits = new Semaphore(policy.maxConcurrentCalls(), true);
    }

    public <T> T execute(Supplier<T> operation) {
        Objects.requireNonNull(operation, "operation e obrigatoria");
        if (!permits.tryAcquire()) {
            log.warn("event=bulkhead_rejected");
            throw new BulkheadFullException("limite de execucoes concorrentes atingido");
        }
        try {
            return operation.get();
        } finally {
            permits.release();
        }
    }
}
