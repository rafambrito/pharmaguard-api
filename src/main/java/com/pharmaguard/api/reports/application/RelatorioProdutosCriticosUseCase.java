package com.pharmaguard.api.reports.application;

import java.util.List;

public interface RelatorioProdutosCriticosUseCase {

    RelatorioProdutosCriticosResponse gerar(FiltroProdutosCriticos filtro);

    interface RelatorioProdutosCriticosRepositoryPort {
        List<RelatorioProdutosCriticosResponse.ItemCritico> listarProdutosCriticos(FiltroProdutosCriticos filtro);
    }
}
