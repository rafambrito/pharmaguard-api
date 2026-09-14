package com.pharmaguard.api.intelligence.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pharmaguard.api.intelligence.domain.GeradorInsightPort;
import com.pharmaguard.api.intelligence.domain.TipoPainel;
import com.pharmaguard.api.reports.application.DashboardOverviewResponse;
import com.pharmaguard.api.reports.application.DashboardOverviewUseCase;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import com.pharmaguard.api.reports.application.RelatorioAlertasResponse;
import com.pharmaguard.api.reports.application.RelatorioConsumoResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class ExplicarPainelUseCaseImplTest {

    @Test
    void deveRetornarFallbackQuandoGeradorFalhar() {
        DashboardOverviewUseCase dashboardOverviewUseCase = mock(DashboardOverviewUseCase.class);
        GeradorInsightPort geradorInsightPort = mock(GeradorInsightPort.class);
        FiltroMetricasMotorEstatistico filtro = new FiltroMetricasMotorEstatistico(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, null, null, null, null);
        DashboardOverviewResponse overview = new DashboardOverviewResponse(
                filtro.periodoInicio(), filtro.periodoFim(), null, null, List.of(), List.of(), List.of(), null);
        when(dashboardOverviewUseCase.consultar(filtro)).thenReturn(overview);
        when(geradorInsightPort.gerar(org.mockito.ArgumentMatchers.any())).thenThrow(new IllegalStateException("offline"));

        ExplicarPainelUseCaseImpl useCase = new ExplicarPainelUseCaseImpl(
                dashboardOverviewUseCase, geradorInsightPort, new ObjectMapper());

        ExplicacaoPainelResponse response = useCase.explicar(new ExplicarPainelCommand(TipoPainel.ALERTAS, filtro));

        assertEquals(ExplicacaoPainelResponse.OrigemExplicacao.FALLBACK, response.origem());
        assertTrue(response.explicacao().contains("alertas"));
    }

    @Test
    void deveSerializarContextoComDatasSemLancarErro() {
        DashboardOverviewUseCase dashboardOverviewUseCase = mock(DashboardOverviewUseCase.class);
        GeradorInsightPort geradorInsightPort = mock(GeradorInsightPort.class);
        FiltroMetricasMotorEstatistico filtro = new FiltroMetricasMotorEstatistico(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, null, null, null, null);
        DashboardOverviewResponse overview = new DashboardOverviewResponse(
                filtro.periodoInicio(),
                filtro.periodoFim(),
                new MetricasMotorEstatisticoResponse(
                        filtro.periodoInicio(), filtro.periodoFim(), 5, 2, 1, 1, 100d, 30d, true, true, LocalDateTime.now()),
                new RelatorioAlertasResponse.ResumoAlertas(1, 1, 0, 100d, 2),
                List.of(),
                List.of(),
                List.of(),
                new DashboardOverviewResponse.ConsumoResumo(
                        100d, 3.3d, RelatorioConsumoResponse.TendenciaConsumo.ESTAVEL, List.of()));
        when(dashboardOverviewUseCase.consultar(filtro)).thenReturn(overview);
        when(geradorInsightPort.gerar(org.mockito.ArgumentMatchers.any())).thenReturn("O estoque apresenta 5 medicamentos monitorados.");

        ExplicarPainelUseCaseImpl useCase = new ExplicarPainelUseCaseImpl(
                dashboardOverviewUseCase, geradorInsightPort, new ObjectMapper());

        ExplicacaoPainelResponse response = useCase.explicar(new ExplicarPainelCommand(TipoPainel.DIAGNOSTICO_GERAL, filtro));

        assertEquals(ExplicacaoPainelResponse.OrigemExplicacao.OLLAMA, response.origem());
        assertEquals("O estoque apresenta 5 medicamentos monitorados.", response.explicacao());
    }
}