package com.pharmaguard.api.intelligence.infrastructure.config;

import com.pharmaguard.api.intelligence.adapters.out.ollama.OllamaInsightAdapter;
import com.pharmaguard.api.intelligence.adapters.out.ollama.ResilientGeradorInsightAdapter;
import com.pharmaguard.api.intelligence.application.ExplicarPainelUseCase;
import com.pharmaguard.api.intelligence.application.ExplicarPainelUseCaseImpl;
import com.pharmaguard.api.intelligence.domain.GeradorInsightPort;
import com.pharmaguard.api.reports.application.DashboardOverviewUseCase;
import com.pharmaguard.api.shared.infrastructure.resilience.bulkhead.BulkheadExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.bulkhead.BulkheadPolicy;
import com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker.CircuitBreaker;
import com.pharmaguard.api.shared.infrastructure.resilience.circuitbreaker.CircuitBreakerPolicy;
import com.pharmaguard.api.shared.infrastructure.resilience.retry.RetryExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.retry.RetryPolicy;
import com.pharmaguard.api.shared.infrastructure.resilience.timeout.TimeoutExecutor;
import com.pharmaguard.api.shared.infrastructure.resilience.timeout.TimeoutPolicy;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class IntelligenceConfig {

    @Bean
    @ConditionalOnProperty(name = "intelligence.enabled", havingValue = "true")
    public GeradorInsightPort ollamaInsightPort(
            @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${ollama.model:gemma3:4b}") String model,
            @Value("${resilience.retry.intelligence.max-attempts:2}") int maxAttempts,
            @Value("${resilience.retry.intelligence.backoff:200ms}") Duration backoff,
            @Value("${resilience.circuit-breaker.intelligence.failure-threshold:2}") int failureThreshold,
            @Value("${resilience.circuit-breaker.intelligence.open-duration:30s}") Duration openDuration,
            @Value("${ollama.timeout:30s}") Duration timeout,
            @Value("${resilience.bulkhead.intelligence.max-concurrent-calls:2}") int maxConcurrentCalls,
            ExecutorService intelligenceTimeoutExecutorService) {
        GeradorInsightPort ollama = new OllamaInsightAdapter(RestClient.builder().baseUrl(baseUrl).build(), model);
        return new ResilientGeradorInsightAdapter(
                ollama,
                new RetryExecutor(new RetryPolicy(maxAttempts, backoff), exception -> true),
                new CircuitBreaker(new CircuitBreakerPolicy(failureThreshold, openDuration), exception -> true),
                new TimeoutExecutor(new TimeoutPolicy(timeout), intelligenceTimeoutExecutorService),
                new BulkheadExecutor(new BulkheadPolicy(maxConcurrentCalls)));
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(name = "intelligence.enabled", havingValue = "true")
    public ExecutorService intelligenceTimeoutExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    @ConditionalOnProperty(name = "intelligence.enabled", havingValue = "false", matchIfMissing = true)
    public GeradorInsightPort fallbackInsightPort() {
        return prompt -> {
            throw new IllegalStateException("Inteligencia explicativa desabilitada");
        };
    }

    @Bean
    public ExplicarPainelUseCase explicarPainelUseCase(
            DashboardOverviewUseCase dashboardOverviewUseCase,
            GeradorInsightPort geradorInsightPort,
            ObjectMapper objectMapper) {
        return new ExplicarPainelUseCaseImpl(dashboardOverviewUseCase, geradorInsightPort, objectMapper);
    }
}