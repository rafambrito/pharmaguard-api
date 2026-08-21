package com.pharmaguard.api.inventory.infrastructure.config;

import com.pharmaguard.api.inventory.adapters.out.repository.CategoriaJpaRepository;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryCategoriaRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryInventoryStore;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryLoteRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryMedicamentoRepositoryAdapter;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryUnidadeMedidaRepositoryAdapter;
import com.pharmaguard.api.inventory.application.CategoriaUseCase;
import com.pharmaguard.api.inventory.application.CategoriaUseCaseImpl;
import com.pharmaguard.api.inventory.application.LoteUseCase;
import com.pharmaguard.api.inventory.application.LoteUseCaseImpl;
import com.pharmaguard.api.inventory.application.MedicamentoUseCase;
import com.pharmaguard.api.inventory.application.MedicamentoUseCaseImpl;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCase;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCaseImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryUseCaseConfig {

    @Bean
    @ConditionalOnMissingBean(CategoriaJpaRepository.class)
    public InMemoryInventoryStore inMemoryInventoryStore() {
        return new InMemoryInventoryStore();
    }

    @Bean
    @ConditionalOnMissingBean(CategoriaJpaRepository.class)
    public CategoriaUseCase.CategoriaRepositoryPort inMemoryCategoriaRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryCategoriaRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnMissingBean(CategoriaJpaRepository.class)
    public UnidadeMedidaUseCase.UnidadeMedidaRepositoryPort inMemoryUnidadeMedidaRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryUnidadeMedidaRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnMissingBean(CategoriaJpaRepository.class)
    public MedicamentoUseCase.MedicamentoRepositoryPort inMemoryMedicamentoRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryMedicamentoRepositoryAdapter(store);
    }

    @Bean
    @ConditionalOnMissingBean(CategoriaJpaRepository.class)
    public LoteUseCase.LoteRepositoryPort inMemoryLoteRepositoryPort(InMemoryInventoryStore store) {
        return new InMemoryLoteRepositoryAdapter(store);
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
    public MedicamentoUseCase medicamentoUseCase(MedicamentoUseCase.MedicamentoRepositoryPort repository) {
        return new MedicamentoUseCaseImpl(repository);
    }

    @Bean
    public LoteUseCase loteUseCase(LoteUseCase.LoteRepositoryPort repository) {
        return new LoteUseCaseImpl(repository);
    }
}
