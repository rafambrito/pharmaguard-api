package com.pharmaguard.api.reports.application;

import java.util.List;

public interface RelatorioReposicaoUseCase {

    RelatorioReposicaoResponse gerar(FiltroReposicao filtro);

    interface RelatorioReposicaoRepositoryPort {
        List<RelatorioReposicaoResponse.ItemReposicao> listarItensParaReposicao(FiltroReposicao filtro);
    }
}
