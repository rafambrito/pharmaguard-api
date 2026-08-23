package com.pharmaguard.api.inventory.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.inventory.adapters.in.controller.UnidadeSaudeController;
import com.pharmaguard.api.inventory.application.UnidadeSaudeUseCase;
import com.pharmaguard.api.inventory.application.UnidadeSaudeUseCaseImpl;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryInventoryStore;
import com.pharmaguard.api.inventory.adapters.out.repository.InMemoryUnidadeSaudeRepositoryAdapter;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UnidadeSaudeControllerIntegrationTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
        messages.setBasename("message");
        messages.setDefaultEncoding("UTF-8");
        UnidadeSaudeUseCase useCase = new UnidadeSaudeUseCaseImpl(
                new InMemoryUnidadeSaudeRepositoryAdapter(new InMemoryInventoryStore()));
        mockMvc = MockMvcBuilders.standaloneSetup(new UnidadeSaudeController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler(messages)).build();
    }

    @Test
        void deveExecutarCrudDeUnidadeDeSaude() throws Exception {
        mockMvc.perform(post("/api/v1/unidades-saude")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"identificacao":"UBS-01","nome":"Unidade Central","tipo":"UBS","endereco":"Rua Principal, 1"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.identificacao", is("UBS-01")))
            .andExpect(jsonPath("$.status", is("ATIVA")));

        mockMvc.perform(get("/api/v1/unidades-saude/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Unidade Central")));

        mockMvc.perform(put("/api/v1/unidades-saude/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"identificacao":"UBS-01","nome":"Unidade Norte","tipo":"UBS","endereco":"Rua Nova, 2"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome", is("Unidade Norte")));

        mockMvc.perform(get("/api/v1/unidades-saude"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].identificacao", is("UBS-01")));

        mockMvc.perform(delete("/api/v1/unidades-saude/1"))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/unidades-saude/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("INATIVA")));
        }

        @Test
        void deveRejeitarCadastroSemCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/api/v1/unidades-saude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"identificacao":"","nome":"","tipo":"","endereco":""}
                                """))
            .andExpect(status().isBadRequest());
    }
}