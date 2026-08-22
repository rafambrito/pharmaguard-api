package com.pharmaguard.api.reports.application;

import java.util.List;

public interface RelatorioVencimentosUseCase {

    RelatorioVencimentosResponse gerar(FiltroVencimentos filtro);

    interface RelatorioVencimentosRepositoryPort {
        List<RelatorioVencimentosResponse.ItemVencimento> listarItensVencendo(FiltroVencimentos filtro);
    }
}
