package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.util.List;

public record DashboardOverviewResponse(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        MetricasMotorEstatisticoResponse metricas,
        RelatorioAlertasResponse.ResumoAlertas resumoAlertas,
        List<RelatorioAlertasResponse.ItemAlerta> alertas,
        List<ResumoUnidade> unidades,
        List<RelatorioReposicaoResponse.ItemReposicao> reposicoes,
        ConsumoResumo consumo) {

    public record ResumoUnidade(
            Long unidadeSaudeId,
            String nomeUnidadeSaude,
            int itensCriticos,
            int lotesAVencer) {
    }

    public record ConsumoResumo(
            double totalConsumido,
            double mediaDiaria,
            RelatorioConsumoResponse.TendenciaConsumo tendencia,
            List<PontoConsumo> pontos) {
    }

    public record PontoConsumo(
            LocalDate periodoInicio,
            LocalDate periodoFim,
            String label,
            double totalConsumido) {
    }
}