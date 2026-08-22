package com.pharmaguard.api.reports.application;

import java.util.List;

public interface RelatorioAlertasUseCase {

    RelatorioAlertasResponse gerar(FiltroAlertas filtro);

    interface RelatorioAlertasRepositoryPort {
        SnapshotAlertas consultar(FiltroAlertas filtro);
    }

    record SnapshotAlertas(
            List<RelatorioAlertasResponse.ItemAlerta> alertas,
            RelatorioAlertasResponse.ResumoAlertas resumo) {
    }
}