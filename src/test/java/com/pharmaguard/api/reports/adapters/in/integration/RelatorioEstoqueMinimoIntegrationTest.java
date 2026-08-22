package com.pharmaguard.api.reports.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.reports.adapters.in.controller.RelatorioEstoqueMinimoController;
import com.pharmaguard.api.reports.application.FiltroEstoqueMinimo;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoResponse;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoUseCase;
import com.pharmaguard.api.reports.application.RelatorioEstoqueMinimoUseCaseImpl;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RelatorioEstoqueMinimoIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new RelatorioEstoqueMinimoController(new RelatorioEstoqueMinimoUseCaseImpl(new FakeRelatorioEstoqueMinimoRepository())))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveGerarRelatorioDeEstoqueMinimo() throws Exception {
        mockMvc.perform(get("/api/v1/relatorios/estoque-minimo")
                        .param("periodoInicio", "2026-01-02")
                        .param("periodoFim", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItensAbaixoMinimo", is(1)))
                .andExpect(jsonPath("$.itens[0].nomeMedicamento", is("Amoxicilina")))
                .andExpect(jsonPath("$.itens[0].status", is("RUPTURA")))
                .andExpect(jsonPath("$.itens[0].necessidadeReposicao", is(20)));
    }

    private static final class FakeRelatorioEstoqueMinimoRepository implements RelatorioEstoqueMinimoUseCase.RelatorioEstoqueMinimoRepositoryPort {

        @Override
        public List<RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo> listarItensAbaixoMinimo(FiltroEstoqueMinimo filtro) {
            return List.of(new RelatorioEstoqueMinimoResponse.ItemEstoqueMinimo(
                    1L,
                    "Amoxicilina",
                    "Antibioticos",
                    0,
                    10,
                    RelatorioEstoqueMinimoResponse.StatusEstoque.RUPTURA,
                    20));
        }
    }
}
