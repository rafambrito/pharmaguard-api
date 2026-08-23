package com.pharmaguard.api.scheduler.adapters.out.scheduling;

import com.pharmaguard.api.scheduler.application.SchedulerJobUseCase;
import com.pharmaguard.api.shared.infrastructure.resilience.bulkhead.BulkheadExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker.CircuitBreaker;
import com.pharmaguard.api.shared.infrastructure.resilience.metrics.SchedulerMetrics;
import com.pharmaguard.api.shared.infrastructure.resilience.retry.RetryExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.timeout.TimeoutExecutor;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerDailyJob {

    private static final Logger log = LoggerFactory.getLogger(SchedulerDailyJob.class);

    private final SchedulerJobUseCase schedulerJobUseCase;
    private final RetryExecutor retryExecutor;
    private final CircuitBreaker circuitBreaker;
    private final TimeoutExecutor timeoutExecutor;
    private final BulkheadExecutor bulkheadExecutor;
    private final SchedulerMetrics schedulerMetrics;

    public SchedulerDailyJob(SchedulerJobUseCase schedulerJobUseCase, RetryExecutor retryExecutor,
            CircuitBreaker circuitBreaker, TimeoutExecutor timeoutExecutor, BulkheadExecutor bulkheadExecutor,
            SchedulerMetrics schedulerMetrics) {
        this.schedulerJobUseCase = schedulerJobUseCase;
        this.retryExecutor = retryExecutor;
        this.circuitBreaker = circuitBreaker;
        this.timeoutExecutor = timeoutExecutor;
        this.bulkheadExecutor = bulkheadExecutor;
        this.schedulerMetrics = schedulerMetrics;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void executarJobDiario() {
        Timer.Sample sample = schedulerMetrics.start();
        try {
            var resumo = bulkheadExecutor.execute(() -> timeoutExecutor.execute(() -> circuitBreaker.execute(
                    () -> retryExecutor.execute(schedulerJobUseCase::executarProcessamentoDiario))));
            log.info("Scheduler diário concluído: medicamentos={}, alertas={}, periodo={} a {}",
                    resumo.totalMedicamentosAnalisados(),
                    resumo.totalAlertas(),
                    resumo.periodoInicio(),
                    resumo.periodoFim());
            schedulerMetrics.recordSuccess();
        } catch (Exception e) {
            log.error("Falha ao executar scheduler diário", e);
            schedulerMetrics.recordFailure();
        } finally {
            schedulerMetrics.recordDuration(sample);
        }
    }
}
