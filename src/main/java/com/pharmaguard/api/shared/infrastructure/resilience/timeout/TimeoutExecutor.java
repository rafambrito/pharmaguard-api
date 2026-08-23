package com.pharmaguard.api.shared.infrastructure.resilience.timeout;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TimeoutExecutor {

    private static final Logger log = LoggerFactory.getLogger(TimeoutExecutor.class);

    private final TimeoutPolicy policy;
    private final ExecutorService executor;

    public TimeoutExecutor(TimeoutPolicy policy, ExecutorService executor) {
        this.policy = Objects.requireNonNull(policy, "policy e obrigatoria");
        this.executor = Objects.requireNonNull(executor, "executor e obrigatorio");
    }

    public <T> T execute(Supplier<T> operation) {
        Objects.requireNonNull(operation, "operation e obrigatoria");
        Future<T> future = executor.submit(operation::get);
        try {
            return future.get(policy.timeout().toNanos(), TimeUnit.NANOSECONDS);
        } catch (TimeoutException timeout) {
            future.cancel(true);
            log.warn("event=operation_timeout timeoutMs={} cancelled=true", policy.timeout().toMillis());
            throw new OperationTimeoutException("operacao excedeu o tempo limite", timeout);
        } catch (InterruptedException interrupted) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            log.warn("event=operation_interrupted timeoutMs={} cancelled=true", policy.timeout().toMillis());
            throw new OperationTimeoutException("operacao interrompida", interrupted);
        } catch (ExecutionException execution) {
            Throwable cause = execution.getCause();
            if (cause instanceof RuntimeException runtimeFailure) {
                throw runtimeFailure;
            }
            throw new IllegalStateException("falha na operacao", cause);
        }
    }
}
