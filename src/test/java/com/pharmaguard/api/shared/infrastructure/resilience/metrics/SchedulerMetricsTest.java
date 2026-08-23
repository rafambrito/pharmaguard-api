package com.pharmaguard.api.shared.infrastructure.resilience.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

class SchedulerMetricsTest {

    @Test
    void deveRegistrarResultadosEDuracaoDoScheduler() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        SchedulerMetrics metrics = new SchedulerMetrics(registry);

        var sample = metrics.start();
        metrics.recordSuccess();
        metrics.recordFailure();
        metrics.recordDuration(sample);

        assertThat(registry.counter("pharmaguard.scheduler.executions", "outcome", "success").count())
                .isEqualTo(1);
        assertThat(registry.counter("pharmaguard.scheduler.executions", "outcome", "failure").count())
                .isEqualTo(1);
        assertThat(registry.timer("pharmaguard.scheduler.execution.duration").count()).isEqualTo(1);
    }
}
