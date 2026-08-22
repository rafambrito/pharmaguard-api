package com.pharmaguard.api.reports.application;

import java.util.List;

public interface RelatorioEstoqueMinimoUseCase {

    RelatorioEstoqueMinimoResponse gerar(FiltroEstoqueMinimo filtro);

    interface RelatorioEstoqueMinimoRepositoryPort {
        List<RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo> listarItensAbaixoMinimo(FiltroEstoqueMinimo filtro);
    }
}
