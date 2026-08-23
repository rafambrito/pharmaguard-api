package com.pharmaguard.api.shared.infrastructure.resilience.retry;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RetryExecutor {

    private static final Logger log = LoggerFactory.getLogger(RetryExecutor.class);

    private final RetryPolicy policy;
    private final Predicate<RuntimeException> retryable;
    private final Sleeper sleeper;

    public RetryExecutor(RetryPolicy policy, Predicate<RuntimeException> retryable) {
        this(policy, retryable, RetryExecutor::sleep);
    }

    RetryExecutor(RetryPolicy policy, Predicate<RuntimeException> retryable, Sleeper sleeper) {
        this.policy = Objects.requireNonNull(policy, "policy e obrigatoria");
        this.retryable = Objects.requireNonNull(retryable, "retryable e obrigatorio");
        this.sleeper = Objects.requireNonNull(sleeper, "sleeper e obrigatorio");
    }

    public <T> T execute(Supplier<T> operation) {
        Objects.requireNonNull(operation, "operation e obrigatoria");
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= policy.maxAttempts(); attempt++) {
            try {
                return operation.get();
            } catch (RuntimeException failure) {
                lastFailure = failure;
                if (attempt == policy.maxAttempts() || !retryable.test(failure)) {
                    throw failure;
                }
                log.warn("event=retry_scheduled attempt={} maxAttempts={} backoffMs={} failureType={}",
                        attempt + 1,
                        policy.maxAttempts(),
                        policy.backoff().multipliedBy(attempt).toMillis(),
                        failure.getClass().getSimpleName());
                sleeper.sleep(policy.backoff().multipliedBy(attempt));
            }
        }

        throw lastFailure;
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("retry interrompido", interrupted);
        }
    }

    @FunctionalInterface
    interface Sleeper {
        void sleep(Duration duration);
    }
}
