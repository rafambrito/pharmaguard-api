package com.pharmaguard.api.intelligence.adapters.out.ollama;

import com.pharmaguard.api.intelligence.domain.GeradorInsightPort;
import com.pharmaguard.api.intelligence.domain.InsightPrompt;
import com.pharmaguard.api.shared.infrastructure.resilience.bulkhead.BulkheadExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker.CircuitBreaker;
import com.pharmaguard.api.shared.infrastructure.resilience.retry.RetryExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.timeout.TimeoutExecutor;

public class ResilientGeradorInsightAdapter implements GeradorInsightPort {

    private final GeradorInsightPort delegate;
    private final RetryExecutor retryExecutor;
    private final CircuitBreaker circuitBreaker;
    private final TimeoutExecutor timeoutExecutor;
    private final BulkheadExecutor bulkheadExecutor;

    public ResilientGeradorInsightAdapter(
            GeradorInsightPort delegate,
            RetryExecutor retryExecutor,
            CircuitBreaker circuitBreaker,
            TimeoutExecutor timeoutExecutor,
            BulkheadExecutor bulkheadExecutor) {
        this.delegate = delegate;
        this.retryExecutor = retryExecutor;
        this.circuitBreaker = circuitBreaker;
        this.timeoutExecutor = timeoutExecutor;
        this.bulkheadExecutor = bulkheadExecutor;
    }

    @Override
    public String gerar(InsightPrompt prompt) {
        return bulkheadExecutor.execute(() -> timeoutExecutor.execute(() -> circuitBreaker.execute(
                () -> retryExecutor.execute(() -> delegate.gerar(prompt)))));
    }
}