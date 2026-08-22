package com.pharmaguard.api.reports.infrastructure.config;

import com.pharmaguard.api.inventory.adapters.out.repository.LoteJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.MovimentacaoEstoqueJpaRepository;
import com.pharmaguard.api.reports.application.RelatorioConsumoUseCase;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoUseCase;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosUseCase;
import com.pharmaguard.api.reports.application.RelatorioReposicaoUseCase;
import com.pharmaguard.api.reports.application.RelatorioVencimentosUseCase;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportsUseCaseConfig {

    @Bean
    @ConditionalOnMissingBean(MovimentacaoEstoqueJpaRepository.class)
    public RelatorioConsumoUseCase.RelatorioConsumoRepositoryPort inMemoryRelatorioConsumoRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnMissingBean(LoteJpaRepository.class)
    public RelatorioEstoqueMinimoUseCase.RelatorioEstoqueMinimoRepositoryPort inMemoryRelatorioEstoqueMinimoRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnMissingBean(LoteJpaRepository.class)
    public RelatorioProdutosCriticosUseCase.RelatorioProdutosCriticosRepositoryPort inMemoryRelatorioProdutosCriticosRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnMissingBean(LoteJpaRepository.class)
    public RelatorioReposicaoUseCase.RelatorioReposicaoRepositoryPort inMemoryRelatorioReposicaoRepositoryPort() {
        return filtro -> List.of();
    }

    @Bean
    @ConditionalOnMissingBean(LoteJpaRepository.class)
    public RelatorioVencimentosUseCase.RelatorioVencimentosRepositoryPort inMemoryRelatorioVencimentosRepositoryPort() {
        return filtro -> List.of();
    }
}
