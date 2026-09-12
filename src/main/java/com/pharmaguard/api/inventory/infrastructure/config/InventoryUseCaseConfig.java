package com.pharmaguard.api.inventory.infrastructure.config;

import com.pharmaguard.api.inventory.adapters.out.repository.CategoriaJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.EntradaEstoqueJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryEntradaEstoqueRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryCategoriaRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryHistoricoEstoqueRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryInventoryStore;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryLoteRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryMedicamentoRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemorySaidaEstoqueRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemorySaldoEstoqueRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryUnidadeMedidaRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryUnidadeSaudeRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.LoteJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.MedicamentoJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.MovimentacaoEstoqueJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.SaidaEstoqueJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.SaldoLoteEstoqueJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.UnidadeMedidaJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.UnidadeSaudeJpaRepository;
import com.pharmaguard.api.inventory.application.CategoriaUseCase;
import com.pharmaguard.api.inventory.application.CategoriaUseCaseImpl;
import com.pharmaguard.api.inventory.application.EntradaEstoqueUseCase;
import com.pharmaguard.api.inventory.application.EntradaEstoqueUseCaseImpl;
import com.pharmaguard.api.inventory.application.HistoricoEstoqueUseCase;
import com.pharmaguard.api.inventory.application.HistoricoEstoqueUseCaseImpl;
import com.pharmaguard.api.inventory.application.LoteUseCase;
import com.pharmaguard.api.inventory.application.LoteUseCaseImpl;
import com.pharmaguard.api.inventory.application.MedicamentoUseCase;
import com.pharmaguard.api.inventory.application.MedicamentoUseCaseImpl;
import com.pharmaguard.api.inventory.application.SaidaEstoqueUseCase;
import com.pharmaguard.api.inventory.application.SaidaEstoqueUseCaseImpl;
import com.pharmaguard.api.inventory.application.SaldoEstoqueUseCase;
import com.pharmaguard.api.inventory.application.SaldoEstoqueUseCaseImpl;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCase;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCaseImpl;
import com.pharmaguard.api.inventory.application.UnidadeSaudeUseCase;
import com.pharmaguard.api.inventory.application.UnidadeSaudeUseCaseImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryUseCaseConfig {

    @Bean
    public InMemoryInventoryStore inMemoryInventoryStore() {
        return new InMemoryInventoryStore();
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(CategoriaJpaRepository.class)
    public CategoriaUseCase.CategoriaRepositoryPort inMemoryCategoriaRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryCategoriaRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(UnidadeMedidaJpaRepository.class)
    public UnidadeMedidaUseCase.UnidadeMedidaRepositoryPort inMemoryUnidadeMedidaRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryUnidadeMedidaRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(UnidadeSaudeJpaRepository.class)
    public UnidadeSaudeUseCase.UnidadeSaudeRepositoryPort inMemoryUnidadeSaudeRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryUnidadeSaudeRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(MedicamentoJpaRepository.class)
    public MedicamentoUseCase.MedicamentoRepositoryPort inMemoryMedicamentoRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryMedicamentoRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(LoteJpaRepository.class)
    public LoteUseCase.LoteRepositoryPort inMemoryLoteRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryLoteRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(EntradaEstoqueJpaRepository.class)
    public EntradaEstoqueUseCase.EntradaEstoqueRepositoryPort inMemoryEntradaEstoqueRepositoryPort(
            InMemoryInventoryStore store) {
        return new InMemoryEntradaEstoqueRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(SaidaEstoqueJpaRepository.class)
    public SaidaEstoqueUseCase.SaidaEstoqueRepositoryPort inMemorySaidaEstoqueRepositoryPort(
            InMemoryInventoryStore store) {
        return new InMemorySaidaEstoqueRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(SaldoLoteEstoqueJpaRepository.class)
    public SaldoEstoqueUseCase.SaldoEstoqueRepositoryPort inMemorySaldoEstoqueRepositoryPort(
            InMemoryInventoryStore store) {
        return new InMemorySaldoEstoqueRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnProperty(name = "app.use-in-memory-inventory", havingValue = "true")
    @ConditionalOnMissingBean(MovimentacaoEstoqueJpaRepository.class)
    public HistoricoEstoqueUseCase.HistoricoEstoqueRepositoryPort inMemoryHistoricoEstoqueRepositoryPort(
            InMemoryInventoryStore store) {
        return new InMemoryHistoricoEstoqueRepositoryAdapter(store);
    }

    @Bean
    public CategoriaUseCase categoriaUseCase(CategoriaUseCase.CategoriaRepositoryPort repository) {
        return new CategoriaUseCaseImpl(repository);
    }

    @Bean
    public UnidadeMedidaUseCase unidadeMedidaUseCase(UnidadeMedidaUseCase.UnidadeMedidaRepositoryPort repository) {
        return new UnidadeMedidaUseCaseImpl(repository);
    }

    @Bean
    public UnidadeSaudeUseCase unidadeSaudeUseCase(UnidadeSaudeUseCase.UnidadeSaudeRepositoryPort repository) {
        return new UnidadeSaudeUseCaseImpl(repository);
    }

    @Bean
    public MedicamentoUseCase medicamentoUseCase(MedicamentoUseCase.MedicamentoRepositoryPort repository) {
        return new MedicamentoUseCaseImpl(repository);
    }

    @Bean
    public LoteUseCase loteUseCase(LoteUseCase.LoteRepositoryPort repository) {
        return new LoteUseCaseImpl(repository);
    }

    @Bean
    public EntradaEstoqueUseCase entradaEstoqueUseCase(
            EntradaEstoqueUseCase.EntradaEstoqueRepositoryPort repository) {
        return new EntradaEstoqueUseCaseImpl(repository);
    }

    @Bean
    public SaidaEstoqueUseCase saidaEstoqueUseCase(
            SaidaEstoqueUseCase.SaidaEstoqueRepositoryPort repository) {
        return new SaidaEstoqueUseCaseImpl(repository);
    }

    @Bean
    public SaldoEstoqueUseCase saldoEstoqueUseCase(
            SaldoEstoqueUseCase.SaldoEstoqueRepositoryPort repository) {
        return new SaldoEstoqueUseCaseImpl(repository);
    }

    @Bean
    public HistoricoEstoqueUseCase historicoEstoqueUseCase(
            HistoricoEstoqueUseCase.HistoricoEstoqueRepositoryPort repository) {
        return new HistoricoEstoqueUseCaseImpl(repository);
    }
}
