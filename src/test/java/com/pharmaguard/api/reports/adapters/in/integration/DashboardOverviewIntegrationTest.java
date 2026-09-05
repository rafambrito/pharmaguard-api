package com.pharmaguard.api.reports.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.reports.adapters.in.controller.DashboardOverviewController;
import com.pharmaguard.api.reports.application.DashboardOverviewResponse;
import com.pharmaguard.api.reports.application.DashboardOverviewUseCase;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import com.pharmaguard.api.reports.application.RelatorioAlertasResponse;
import com.pharmaguard.api.reports.application.RelatorioConsumoResponse;
import com.pharmaguard.api.reports.application.RelatorioReposicaoResponse;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DashboardOverviewIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(new DashboardOverviewController(new FakeDashboardOverviewUseCase()))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveRetornarVisaoGeralDoDashboard() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/overview")
                        .param("periodoInicio", "2026-01-01")
                        .param("periodoFim", "2026-01-31")
                        .param("unidadeSaudeId", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodoInicio", is("2026-01-01")))
                .andExpect(jsonPath("$.metricas.totalMedicamentosAnalisados", is(8)))
                .andExpect(jsonPath("$.resumoAlertas.totalItensCriticos", is(2)))
                .andExpect(jsonPath("$.alertas[0].nomeMedicamento", is("Dipirona")))
                .andExpect(jsonPath("$.unidades[0].unidadeSaudeId", is(12)))
                .andExpect(jsonPath("$.reposicoes[0].quantidadeSugerida", is(30)))
                .andExpect(jsonPath("$.consumo.tendencia", is("CRESCENTE")))
                .andExpect(jsonPath("$.consumo.pontos[0].label", is("01/01-15/01")))
                .andExpect(jsonPath("$.consumo.pontos[0].totalConsumido", is(100.0)));
    }

    private static final class FakeDashboardOverviewUseCase implements DashboardOverviewUseCase {

        @Override
        public DashboardOverviewResponse consultar(FiltroMetricasMotorEstatistico filtro) {
            return new DashboardOverviewResponse(
                    filtro.periodoInicio(),
                    filtro.periodoFim(),
                    new MetricasMotorEstatisticoResponse(
                            filtro.periodoInicio(),
                            filtro.periodoFim(),
                            8,
                            3,
                            2,
                            1,
                            240d,
                            18.5d,
                            true,
                            true,
                            LocalDateTime.now()),
                    new RelatorioAlertasResponse.ResumoAlertas(1, 1, 0, 240d, 2),
                    List.of(new RelatorioAlertasResponse.ItemAlerta(
                            1L,
                            "Dipirona",
                            RelatorioAlertasResponse.TipoAlerta.RUPTURA,
                            RelatorioAlertasResponse.SeveridadeAlerta.CRITICA,
                            20,
                            "Risco de ruptura")),
                    List.of(new DashboardOverviewResponse.ResumoUnidade(
                            filtro.unidadeSaudeId(),
                            "Unidade Norte",
                            2,
                            1)),
                    List.of(new RelatorioReposicaoResponse.ItemReposicao(
                            1L,
                            "Dipirona",
                            30,
                            RelatorioReposicaoResponse.Urgencia.ALTA,
                            RelatorioReposicaoResponse.Prioridade.ALTA,
                            null,
                            5,
                            "Reposicao sugerida")),
                    new DashboardOverviewResponse.ConsumoResumo(
                            240d,
                            7.7d,
                                                        RelatorioConsumoResponse.TendenciaConsumo.CRESCENTE,
                                                        List.of(new DashboardOverviewResponse.PontoConsumo(
                                                                        filtro.periodoInicio(),
                                                                        filtro.periodoInicio().plusDays(14),
                                                                        "01/01-15/01",
                                                                        100d))));
        }
    }
}