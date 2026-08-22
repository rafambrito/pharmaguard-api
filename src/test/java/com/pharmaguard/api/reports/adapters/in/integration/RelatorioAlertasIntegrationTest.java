package com.pharmaguard.api.reports.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.reports.adapters.in.controller.RelatorioAlertasController;
import com.pharmaguard.api.reports.application.FiltroAlertas;
import com.pharmaguard.api.reports.application.RelatorioAlertasResponse;
import com.pharmaguard.api.reports.application.RelatorioAlertasUseCase;
import com.pharmaguard.api.reports.application.RelatorioAlertasUseCaseImpl;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RelatorioAlertasIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new RelatorioAlertasController(new RelatorioAlertasUseCaseImpl(new FakeRelatorioAlertasRepository())))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveGerarRelatorioDeAlertas() throws Exception {
        mockMvc.perform(get("/api/v1/relatorios/alertas")
                        .param("periodoInicio", "2026-01-01")
                        .param("periodoFim", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAlertas", is(2)))
                .andExpect(jsonPath("$.resumo.totalRuptura", is(1)))
                .andExpect(jsonPath("$.resumo.totalVencimento", is(1)))
                .andExpect(jsonPath("$.resumo.totalExcessoEstoque", is(0)))
                .andExpect(jsonPath("$.alertas[0].tipo", is("RUPTURA")))
                .andExpect(jsonPath("$.alertas[1].tipo", is("VENCIMENTO")));
    }

    private static final class FakeRelatorioAlertasRepository implements RelatorioAlertasUseCase.RelatorioAlertasRepositoryPort {

        @Override
        public RelatorioAlertasUseCase.SnapshotAlertas consultar(FiltroAlertas filtro) {
            List<RelatorioAlertasResponse.ItemAlerta> alertas = List.of(
                    new RelatorioAlertasResponse.ItemAlerta(
                            1L,
                            "Amoxicilina",
                            RelatorioAlertasResponse.TipoAlerta.RUPTURA,
                            RelatorioAlertasResponse.SeveridadeAlerta.ALTA,
                            30,
                            "Risco de ruptura"),
                    new RelatorioAlertasResponse.ItemAlerta(
                            2L,
                            "Dipirona",
                            RelatorioAlertasResponse.TipoAlerta.VENCIMENTO,
                            RelatorioAlertasResponse.SeveridadeAlerta.MEDIA,
                            15,
                            "Risco de vencimento"));

            RelatorioAlertasResponse.ResumoAlertas resumo = new RelatorioAlertasResponse.ResumoAlertas(
                    1,
                    1,
                    0,
                    150d,
                    1);

            return new RelatorioAlertasUseCase.SnapshotAlertas(alertas, resumo);
        }
    }
}