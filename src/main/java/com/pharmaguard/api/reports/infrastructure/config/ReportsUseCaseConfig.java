package com.pharmaguard.api.reports.infrastructure.config;

import com.pharmaguard.api.reports.application.RelatorioConsumoUseCase;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoUseCase;
import com.pharmaguard.api.reports.application.RelatorioAlertasUseCase;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoUseCase;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosUseCase;
import com.pharmaguard.api.reports.application.RelatorioReposicaoUseCase;
import com.pharmaguard.api.reports.application.RelatorioVencimentosUseCase;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportsUseCaseConfig {

    // ConditionalOnMissingBean(<JpaRepository>) is unreliable here because this
    // @Configuration is processed before JPA repository beans are registered by auto-configuration.
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.url", havingValue = "never-set", matchIfMissing = true)
    public RelatorioConsumoUseCase.RelatorioConsumoRepositoryPort inMemoryRelatorioConsumoRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.datasource.url", havingValue = "never-set", matchIfMissing = true)
    public RelatorioEstoqueMinimoUseCase.RelatorioEstoqueMinimoRepositoryPort inMemoryRelatorioEstoqueMinimoRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.datasource.url", havingValue = "never-set", matchIfMissing = true)
    public RelatorioProdutosCriticosUseCase.RelatorioProdutosCriticosRepositoryPort inMemoryRelatorioProdutosCriticosRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.datasource.url", havingValue = "never-set", matchIfMissing = true)
    public RelatorioReposicaoUseCase.RelatorioReposicaoRepositoryPort inMemoryRelatorioReposicaoRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.datasource.url", havingValue = "never-set", matchIfMissing = true)
    public RelatorioVencimentosUseCase.RelatorioVencimentosRepositoryPort inMemoryRelatorioVencimentosRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.datasource.url", havingValue = "never-set", matchIfMissing = true)
    public RelatorioAlertasUseCase.RelatorioAlertasRepositoryPort inMemoryRelatorioAlertasRepositoryPort() {
        return filtro -> new RelatorioAlertasUseCase.SnapshotAlertas(
                List.of(),
                new com.pharmaguard.api.reports.application.RelatorioAlertasResponse.ResumoAlertas(0, 0, 0, 0d, 0));
    }

    @Bean
    @ConditionalOnProperty(name = "spring.datasource.url", havingValue = "never-set", matchIfMissing = true)
    public MetricasMotorEstatisticoUseCase.MetricasMotorEstatisticoRepositoryPort inMemoryMetricasMotorEstatisticoRepositoryPort() {
        return filtro -> new MetricasMotorEstatisticoResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                0,
                0,
                0,
                0,
                0d,
                0d,
                true,
                true,
                LocalDateTime.now());
    }
}
