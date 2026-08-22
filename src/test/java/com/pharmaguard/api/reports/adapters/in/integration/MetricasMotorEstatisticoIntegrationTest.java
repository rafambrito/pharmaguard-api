package com.pharmaguard.api.reports.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.reports.adapters.in.controller.MetricasMotorEstatisticoController;
import com.pharmaguard.api.reports.application.FiltroMetricasMotorEstatistico;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoResponse;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoUseCase;
import com.pharmaguard.api.reports.application.MetricasMotorEstatisticoUseCaseImpl;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MetricasMotorEstatisticoIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new MetricasMotorEstatisticoController(
                                new MetricasMotorEstatisticoUseCaseImpl(new FakeMetricasRepository())))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveRetornarMetricasIntegradasDoMotorEstatistico() throws Exception {
        mockMvc.perform(get("/api/v1/relatorios/metricas-motor")
                        .param("periodoInicio", "2026-01-01")
                        .param("periodoFim", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMedicamentosAnalisados", is(8)))
                .andExpect(jsonPath("$.totalItensComReposicaoSugerida", is(3)))
                .andExpect(jsonPath("$.totalItensComRiscoRuptura", is(2)))
                .andExpect(jsonPath("$.totalItensComRiscoValidade", is(1)))
                .andExpect(jsonPath("$.totalConsumoPeriodo", is(240.0)))
                .andExpect(jsonPath("$.integracaoSchedulerPreparada", is(true)))
                .andExpect(jsonPath("$.integracaoDashboardPreparada", is(true)));
    }

    private static final class FakeMetricasRepository implements MetricasMotorEstatisticoUseCase.MetricasMotorEstatisticoRepositoryPort {

        @Override
        public MetricasMotorEstatisticoResponse consultarMetricas(FiltroMetricasMotorEstatistico filtro) {
            return new MetricasMotorEstatisticoResponse(
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
                    LocalDateTime.now());
        }
    }
}