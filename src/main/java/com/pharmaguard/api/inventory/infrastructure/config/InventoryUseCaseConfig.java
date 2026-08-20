package com.pharmaguard.api.inventory.infrastructure.config;

import com.pharmaguard.api.inventory.application.CategoriaUseCase;
import com.pharmaguard.api.inventory.application.CategoriaUseCaseImpl;
import com.pharmaguard.api.inventory.application.LoteUseCase;
import com.pharmaguard.api.inventory.application.LoteUseCaseImpl;
import com.pharmaguard.api.inventory.application.MedicamentoUseCase;
import com.pharmaguard.api.inventory.application.MedicamentoUseCaseImpl;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCase;
import com.pharmaguard.api.inventory.application.UnidadeMedidaUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryUseCaseConfig {

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
