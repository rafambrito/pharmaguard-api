package com.pharmaguard.api.reports.application;

import com.pharmaguard.api.inventory.domain.MovimentacaoEstoque;
import java.util.List;

public interface RelatorioConsumoUseCase {

    RelatorioConsumoResponse gerar(FiltroConsumo filtro);

    interface RelatorioConsumoRepositoryPort {
        List<MovimentacaoEstoque> listarSaidas(FiltroConsumo filtro);
    }
}
