package com.pharmaguard.api.supplier.adapters.out.config;

import com.pharmaguard.api.supplier.application.ContatoFornecedorUseCase;
import com.pharmaguard.api.supplier.application.ContatoFornecedorUseCaseImpl;
import com.pharmaguard.api.supplier.application.FornecedorUseCase;
import com.pharmaguard.api.supplier.application.FornecedorUseCaseImpl;
import com.pharmaguard.api.supplier.application.LeadTimeFornecedorUseCase;
import com.pharmaguard.api.supplier.application.LeadTimeFornecedorUseCaseImpl;
import com.pharmaguard.api.supplier.adapters.out.repository.FornecedorJpaRepository;
import com.pharmaguard.api.supplier.adapters.out.repository.InMemorySupplierRepositoryAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupplierUseCaseConfig {

    @Bean
    @ConditionalOnMissingBean(FornecedorJpaRepository.class)
    public InMemorySupplierRepositoryAdapter inMemorySupplierRepositoryAdapter() {
        return new InMemorySupplierRepositoryAdapter();
    }

    @Bean
    public FornecedorUseCase fornecedorUseCase(FornecedorUseCase.FornecedorRepositoryPort repository) {
        return new FornecedorUseCaseImpl(repository);
    }

    @Bean
    public ContatoFornecedorUseCase contatoFornecedorUseCase(ContatoFornecedorUseCase.ContatoFornecedorRepositoryPort repository) {
        return new ContatoFornecedorUseCaseImpl(repository);
    }

    @Bean
    public LeadTimeFornecedorUseCase leadTimeFornecedorUseCase(LeadTimeFornecedorUseCase.LeadTimeFornecedorRepositoryPort repository) {
        return new LeadTimeFornecedorUseCaseImpl(repository);
    }
}