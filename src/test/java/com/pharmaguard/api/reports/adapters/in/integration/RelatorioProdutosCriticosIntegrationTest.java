package com.pharmaguard.api.reports.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.reports.adapters.in.controller.RelatorioProdutosCriticosController;
import com.pharmaguard.api.reports.application.FiltroProdutosCriticos;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosResponse;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosUseCase;
import com.pharmaguard.api.reports.application.RelatorioProdutosCriticosUseCaseImpl;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RelatorioProdutosCriticosIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(
                        new RelatorioProdutosCriticosController(new RelatorioProdutosCriticosUseCaseImpl(new FakeRelatorioProdutosCriticosRepository())))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveGerarRelatorioDeProdutosCriticos() throws Exception {
        mockMvc.perform(get("/api/v1/relatorios/produtos-criticos")
                        .param("periodoInicio", "2026-01-02")
                        .param("periodoFim", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProdutosCriticos", is(1)))
                .andExpect(jsonPath("$.itens[0].nomeMedicamento", is("Amoxicilina")))
                .andExpect(jsonPath("$.itens[0].risco", is("CRITICO")))
                .andExpect(jsonPath("$.itens[0].urgencia", is("ALTA")));
    }

    private static final class FakeRelatorioProdutosCriticosRepository implements RelatorioProdutosCriticosUseCase.RelatorioProdutosCriticosRepositoryPort {

        @Override
        public List<RelatorioProdutosCriticosResponse.ItemCritico> listarProdutosCriticos(FiltroProdutosCriticos filtro) {
            return List.of(new RelatorioProdutosCriticosResponse.ItemCritico(
                    1L,
                    "Amoxicilina",
                    "Antibioticos",
                    5,
                    2.5,
                    RelatorioProdutosCriticosResponse.Risco.CRITICO,
                    RelatorioProdutosCriticosResponse.Urgencia.ALTA,
                    "Estoque baixo com consumo medio elevado"));
        }
    }
}
