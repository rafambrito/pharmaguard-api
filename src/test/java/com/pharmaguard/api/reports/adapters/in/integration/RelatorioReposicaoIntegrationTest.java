package com.pharmaguard.api.reports.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.reports.adapters.in.controller.RelatorioReposicaoController;
import com.pharmaguard.api.reports.application.FiltroReposicao;
import com.pharmaguard.api.reports.application.RelatorioReposicaoResponse;
import com.pharmaguard.api.reports.application.RelatorioReposicaoUseCase;
import com.pharmaguard.api.reports.application.RelatorioReposicaoUseCaseImpl;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RelatorioReposicaoIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new RelatorioReposicaoController(new RelatorioReposicaoUseCaseImpl(new FakeRelatorioReposicaoRepository())))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveGerarRelatorioDeReposicao() throws Exception {
        mockMvc.perform(get("/api/v1/relatorios/reposicao")
                        .param("periodoInicio", "2026-01-02")
                        .param("periodoFim", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItensParaReposicao", is(1)))
                .andExpect(jsonPath("$.itens[0].nomeMedicamento", is("Amoxicilina")))
                .andExpect(jsonPath("$.itens[0].urgencia", is("ALTA")))
                .andExpect(jsonPath("$.itens[0].prioridade", is("ALTA")))
                .andExpect(jsonPath("$.itens[0].quantidadeSugerida", is(20)));
    }

    private static final class FakeRelatorioReposicaoRepository implements RelatorioReposicaoUseCase.RelatorioReposicaoRepositoryPort {

        @Override
        public List<RelatorioReposicaoResponse.ItemReposicao> listarItensParaReposicao(FiltroReposicao filtro) {
            return List.of(new RelatorioReposicaoResponse.ItemReposicao(
                    1L,
                    "Amoxicilina",
                    20,
                    RelatorioReposicaoResponse.Urgencia.ALTA,
                    RelatorioReposicaoResponse.Prioridade.ALTA,
                    5L,
                    7,
                    "Consumo alto e estoque abaixo do mínimo"));
        }
    }
}
