package com.pharmaguard.api.shared.infrastructure.resilience.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.Objects;

public final class SchedulerMetrics {

    private final MeterRegistry registry;
    private final Counter successfulExecutions;
    private final Counter failedExecutions;
    private final Timer executionDuration;

    public SchedulerMetrics(MeterRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry e obrigatorio");
        this.successfulExecutions = Counter.builder("pharmaguard.scheduler.executions")
                .tag("outcome", "success")
                .description("Execucoes do scheduler concluídas com sucesso")
                .register(registry);
        this.failedExecutions = Counter.builder("pharmaguard.scheduler.executions")
                .tag("outcome", "failure")
                .description("Execucoes do scheduler que falharam")
                .register(registry);
        this.executionDuration = Timer.builder("pharmaguard.scheduler.execution.duration")
                .description("Duracao do processamento do scheduler")
                .register(registry);
    }

    public Timer.Sample start() {
        return Timer.start(registry);
    }

    public void recordSuccess() {
        successfulExecutions.increment();
    }

    public void recordFailure() {
        failedExecutions.increment();
    }

    public void recordDuration(Timer.Sample sample) {
        sample.stop(executionDuration);
    }
}
