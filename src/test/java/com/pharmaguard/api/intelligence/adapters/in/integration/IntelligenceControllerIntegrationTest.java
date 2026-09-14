package com.pharmaguard.api.intelligence.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.intelligence.adapters.in.controller.IntelligenceController;
import com.pharmaguard.api.intelligence.application.ExplicacaoPainelResponse;
import com.pharmaguard.api.intelligence.application.ExplicarPainelCommand;
import com.pharmaguard.api.intelligence.application.ExplicarPainelUseCase;
import com.pharmaguard.api.intelligence.domain.TipoPainel;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class IntelligenceControllerIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(new IntelligenceController(new FakeExplicarPainelUseCase()))
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveExplicarPainelComSucesso() throws Exception {
        String json = """
                {
                    "tipoPainel": "DIAGNOSTICO_GERAL",
                    "periodoInicio": "2026-01-01",
                    "periodoFim": "2026-01-31",
                    "unidadeSaudeId": 1
                }
                """;

        mockMvc.perform(post("/api/v1/intelligence/explicar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoPainel", is("DIAGNOSTICO_GERAL")))
                .andExpect(jsonPath("$.explicacao", is("Analise consolidada com sucesso.")))
                .andExpect(jsonPath("$.origem", is("FALLBACK")));
    }

    private static final class FakeExplicarPainelUseCase implements ExplicarPainelUseCase {

        @Override
        public ExplicacaoPainelResponse explicar(ExplicarPainelCommand command) {
            return new ExplicacaoPainelResponse(
                    command.tipoPainel(),
                    "Analise consolidada com sucesso.",
                    ExplicacaoPainelResponse.OrigemExplicacao.FALLBACK,
                    Instant.parse("2026-01-31T12:00:00Z"));
        }
    }
}
