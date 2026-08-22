package com.pharmaguard.api.reports.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.reports.adapters.in.controller.RelatorioVencimentosController;
import com.pharmaguard.api.reports.application.FiltroVencimentos;
import com.pharmaguard.api.reports.application.RelatorioVencimentosResponse;
import com.pharmaguard.api.reports.application.RelatorioVencimentosUseCase;
import com.pharmaguard.api.reports.application.RelatorioVencimentosUseCaseImpl;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RelatorioVencimentosIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new RelatorioVencimentosController(new RelatorioVencimentosUseCaseImpl(new FakeRelatorioVencimentosRepository())))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveGerarRelatorioDeVencimentos() throws Exception {
        mockMvc.perform(get("/api/v1/relatorios/vencimentos")
                        .param("periodoInicio", "2026-01-02")
                        .param("periodoFim", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItensVencendo", is(1)))
                .andExpect(jsonPath("$.itens[0].nomeMedicamento", is("Amoxicilina")))
                .andExpect(jsonPath("$.itens[0].statusValidade", is("PROXIMO_VENCIMENTO")))
                .andExpect(jsonPath("$.itens[0].severidade", is("ALTA")));
    }

    private static final class FakeRelatorioVencimentosRepository implements RelatorioVencimentosUseCase.RelatorioVencimentosRepositoryPort {

        @Override
        public List<RelatorioVencimentosResponse.ItemVencimento> listarItensVencendo(FiltroVencimentos filtro) {
            return List.of(new RelatorioVencimentosResponse.ItemVencimento(
                    1L,
                    "Amoxicilina",
                    "LOT-003",
                    LocalDate.of(2026, 1, 15),
                    20,
                    RelatorioVencimentosResponse.StatusValidadeRelatorio.PROXIMO_VENCIMENTO,
                    RelatorioVencimentosResponse.SeveridadeVencimento.ALTA));
        }
    }
}
