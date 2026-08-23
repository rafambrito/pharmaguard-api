package com.pharmaguard.api.scheduler.application;

import com.pharmaguard.api.reports.application.FiltroAlertas;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoUseCase;
import com.pharmaguard.api.reports.application.RelatorioAlertasResponse;
import com.pharmaguard.api.reports.application.RelatorioAlertasUseCase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class SchedulerJobUseCaseImpl implements SchedulerJobUseCase {

    private final MetricasMotorEstatisticoUseCase metricasUseCase;
    private final RelatorioAlertasUseCase alertasUseCase;

    public SchedulerJobUseCaseImpl(
            MetricasMotorEstatisticoUseCase metricasUseCase,
            RelatorioAlertasUseCase alertasUseCase) {
        this.metricasUseCase = Objects.requireNonNull(metricasUseCase, "metricasUseCase e obrigatorio");
        this.alertasUseCase = Objects.requireNonNull(alertasUseCase, "alertasUseCase e obrigatorio");
    }

    @Override
    public SchedulerExecutionSummary executarProcessamentoDiario() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusDays(30);

        FiltroMetricasMotorEstatistico filtroMetricas = new FiltroMetricasMotorEstatistico(
                inicio, hoje, null, null, null, null);
        MetricasMotorEstatisticoResponse metricas = metricasUseCase.consultar(filtroMetricas);

        FiltroAlertas filtroAlertas = new FiltroAlertas(inicio, hoje, null, null, null, null);
        RelatorioAlertasResponse alertas = alertasUseCase.gerar(filtroAlertas);

        List<RelatorioAlertasResponse.ItemAlerta> itens = alertas.alertas() == null ? List.of() : alertas.alertas();

        return new SchedulerExecutionSummary(
                inicio,
                hoje,
                metricas.totalMedicamentosAnalisados(),
                itens.size(),
                true,
                "Processamento diário do scheduler concluído com sucesso",
                LocalDateTime.now());
    }
}
