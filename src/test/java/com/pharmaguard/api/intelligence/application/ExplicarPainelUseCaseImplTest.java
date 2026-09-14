package com.pharmaguard.api.intelligence.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmaguard.api.intelligence.domain.GeradorInsightPort;
import com.pharmaguard.api.intelligence.domain.TipoPainel;
import com.pharmaguard.api.reports.application.DashboardOverviewResponse;
import com.pharmaguard.api.reports.application.DashboardOverviewUseCase;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExplicarPainelUseCaseImplTest {

    @Test
    void deveRetornarResumoOperacionalQuandoGeradorRetornarJsonParaEstoquePorUnidade() {
        DashboardOverviewUseCase dashboardOverviewUseCase = mock(DashboardOverviewUseCase.class);
        GeradorInsightPort geradorInsightPort = mock(GeradorInsightPort.class);
        FiltroMetricasMotorEstatistico filtro = new FiltroMetricasMotorEstatistico(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, null, null, null, null);
        DashboardOverviewResponse overview = new DashboardOverviewResponse(
                filtro.periodoInicio(), filtro.periodoFim(), null, null, List.of(),
                List.of(new DashboardOverviewResponse.ResumoUnidade(1L, null, 1, 3)), List.of(), null);
        when(dashboardOverviewUseCase.consultar(filtro)).thenReturn(overview);
        when(geradorInsightPort.gerar(org.mockito.ArgumentMatchers.any())).thenReturn("[{\"itensCriticos\":1}]");

        ExplicarPainelUseCaseImpl useCase = new ExplicarPainelUseCaseImpl(
                dashboardOverviewUseCase, geradorInsightPort, new ObjectMapper());

        ExplicacaoPainelResponse response = useCase.explicar(
                new ExplicarPainelCommand(TipoPainel.ESTOQUE_POR_UNIDADE, filtro));

        assertEquals(ExplicacaoPainelResponse.OrigemExplicacao.FALLBACK, response.origem());
        assertEquals("Todas as unidades apresentam 1 item critico e 3 lotes proximos do vencimento.",
                response.explicacao());
    }

    @Test
    void deveRetornarFallbackQuandoGeradorRetornarJson() {
        DashboardOverviewUseCase dashboardOverviewUseCase = mock(DashboardOverviewUseCase.class);
        GeradorInsightPort geradorInsightPort = mock(GeradorInsightPort.class);
        FiltroMetricasMotorEstatistico filtro = new FiltroMetricasMotorEstatistico(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, null, null, null, null);
        DashboardOverviewResponse overview = new DashboardOverviewResponse(
                filtro.periodoInicio(), filtro.periodoFim(), null, null, List.of(), List.of(), List.of(), null);
        when(dashboardOverviewUseCase.consultar(filtro)).thenReturn(overview);
        when(geradorInsightPort.gerar(org.mockito.ArgumentMatchers.any())).thenReturn("[{\"itensCriticos\":1}]");

        ExplicarPainelUseCaseImpl useCase = new ExplicarPainelUseCaseImpl(
                dashboardOverviewUseCase, geradorInsightPort, new ObjectMapper());

        ExplicacaoPainelResponse response = useCase.explicar(new ExplicarPainelCommand(TipoPainel.ALERTAS, filtro));

        assertEquals(ExplicacaoPainelResponse.OrigemExplicacao.FALLBACK, response.origem());
        assertTrue(response.explicacao().contains("alertas"));
    }

    @Test
    void deveRetornarResumoOperacionalQuandoGeradorFalharParaConsumo() {
        DashboardOverviewUseCase dashboardOverviewUseCase = mock(DashboardOverviewUseCase.class);
        GeradorInsightPort geradorInsightPort = mock(GeradorInsightPort.class);
        FiltroMetricasMotorEstatistico filtro = new FiltroMetricasMotorEstatistico(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, null, null, null, null);
        DashboardOverviewResponse overview = new DashboardOverviewResponse(
                filtro.periodoInicio(), filtro.periodoFim(), null, null, List.of(), List.of(), List.of(),
                new DashboardOverviewResponse.ConsumoResumo(315d, 10.5d,
                        com.pharmaguard.api.reports.application.RelatorioConsumoResponse.TendenciaConsumo.CRESCENTE,
                        List.of()));
        when(dashboardOverviewUseCase.consultar(filtro)).thenReturn(overview);
        when(geradorInsightPort.gerar(org.mockito.ArgumentMatchers.any())).thenThrow(new IllegalStateException("offline"));

        ExplicarPainelUseCaseImpl useCase = new ExplicarPainelUseCaseImpl(
                dashboardOverviewUseCase, geradorInsightPort, new ObjectMapper());

        ExplicacaoPainelResponse response = useCase.explicar(new ExplicarPainelCommand(TipoPainel.CONSUMO, filtro));

        assertEquals(ExplicacaoPainelResponse.OrigemExplicacao.FALLBACK, response.origem());
        assertEquals("No periodo analisado, foram dispensadas 315 unidades, com media de 10.5 por dia. O consumo esta em alta.",
                response.explicacao());
    }

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
}