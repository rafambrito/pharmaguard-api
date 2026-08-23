package com.pharmaguard.api.scheduler.adapters.out.scheduling;

import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoUseCase;
import com.pharmaguard.api.reports.application.RelatorioAlertasUseCase;
import com.pharmaguard.api.scheduler.application.SchedulerJobUseCase;
import com.pharmaguard.api.scheduler.application.SchedulerJobUseCaseImpl;
import com.pharmaguard.api.shared.infrastructure.resilience.bulkhead.BulkheadExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.bulkhead.BulkheadPolicy;
import com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker.CircuitBreaker;
import com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker.CircuitBreakerPolicy;
import com.pharmaguard.api.shared.infrastructure.resilience.retry.RetryExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.retry.RetryPolicy;
import com.pharmaguard.api.shared.infrastructure.resilience.timeout.TimeoutExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.timeout.TimeoutPolicy;
import com.pharmaguard.api.shared.infrastructure.resilience.metrics.SchedulerMetrics;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {

    @Bean
    public SchedulerJobUseCase schedulerJobUseCase(
            MetricasMotorEstatisticoUseCase metricasUseCase,
            RelatorioAlertasUseCase alertasUseCase) {
        return new SchedulerJobUseCaseImpl(metricasUseCase, alertasUseCase);
    }

    @Bean
    public RetryExecutor schedulerRetryExecutor(
            @Value("${resilience.retry.scheduler.max-attempts:3}") int maxAttempts,
            @Value("${resilience.retry.scheduler.backoff:500ms}") Duration backoff) {
        return new RetryExecutor(new RetryPolicy(maxAttempts, backoff), SchedulerConfig::isRetryable);
    }

    @Bean
    public CircuitBreaker schedulerCircuitBreaker(
            @Value("${resilience.circuit-breaker.scheduler.failure-threshold:3}") int failureThreshold,
            @Value("${resilience.circuit-breaker.scheduler.open-duration:30s}") Duration openDuration) {
        return new CircuitBreaker(
                new CircuitBreakerPolicy(failureThreshold, openDuration), SchedulerConfig::isRetryable);
    }

    @Bean(destroyMethod = "close")
    public ExecutorService schedulerTimeoutExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public TimeoutExecutor schedulerTimeoutExecutor(
            @Value("${resilience.timeout.scheduler:30s}") Duration timeout,
            ExecutorService schedulerTimeoutExecutorService) {
        return new TimeoutExecutor(new TimeoutPolicy(timeout), schedulerTimeoutExecutorService);
    }

    @Bean
    public BulkheadExecutor schedulerBulkheadExecutor(
            @Value("${resilience.bulkhead.scheduler.max-concurrent-calls:1}") int maxConcurrentCalls) {
        return new BulkheadExecutor(new BulkheadPolicy(maxConcurrentCalls));
    }

    @Bean
    public SchedulerMetrics schedulerMetrics(io.micrometer.core.instrument.MeterRegistry registry) {
        return new SchedulerMetrics(registry);
    }

    private static boolean isRetryable(RuntimeException failure) {
        return failure instanceof TransientDataAccessException || failure instanceof QueryTimeoutException;
    }
}
