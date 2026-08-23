package com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.LongSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CircuitBreaker {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreaker.class);

    private final CircuitBreakerPolicy policy;
    private final Predicate<RuntimeException> failureToCount;
    private final LongSupplier clock;
    private State state = State.CLOSED;
    private int consecutiveFailures;
    private long openedAtNanos;

    public CircuitBreaker(CircuitBreakerPolicy policy, Predicate<RuntimeException> failureToCount) {
        this(policy, failureToCount, System::nanoTime);
    }

    CircuitBreaker(CircuitBreakerPolicy policy, Predicate<RuntimeException> failureToCount, LongSupplier clock) {
        this.policy = Objects.requireNonNull(policy, "policy e obrigatoria");
        this.failureToCount = Objects.requireNonNull(failureToCount, "failureToCount e obrigatorio");
        this.clock = Objects.requireNonNull(clock, "clock e obrigatorio");
    }

    public synchronized <T> T execute(Supplier<T> operation) {
        Objects.requireNonNull(operation, "operation e obrigatoria");
        permitirExecucaoSeJanelaEncerrada();

        try {
            T result = operation.get();
            fechar();
            return result;
        } catch (RuntimeException failure) {
            registrarFalha(failure);
            throw failure;
        }
    }

    private void permitirExecucaoSeJanelaEncerrada() {
        if (state != State.OPEN) {
            return;
        }
        long janelaNanos = policy.openDuration().toNanos();
        if (clock.getAsLong() - openedAtNanos < janelaNanos) {
            log.warn("event=circuit_breaker_rejected state=OPEN");
            throw new CircuitBreakerOpenException("circuit breaker aberto");
        }
        state = State.HALF_OPEN;
        log.info("event=circuit_breaker_half_open state=HALF_OPEN");
    }

    private void registrarFalha(RuntimeException failure) {
        if (!failureToCount.test(failure)) {
            return;
        }
        consecutiveFailures++;
        if (consecutiveFailures >= policy.failureThreshold()) {
            state = State.OPEN;
            openedAtNanos = clock.getAsLong();
            log.warn("event=circuit_breaker_open state=OPEN failureCount={} threshold={}",
                    consecutiveFailures, policy.failureThreshold());
        }
    }

    private void fechar() {
        if (state != State.CLOSED) {
            log.info("event=circuit_breaker_closed state=CLOSED");
        }
        state = State.CLOSED;
        consecutiveFailures = 0;
    }

    enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }
}
