package com.pharmaguard.api.auth.adapters.in.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.auth.adapters.in.controller.UsuarioController;
import com.pharmaguard.api.auth.adapters.in.mapper.UsuarioAdapterInMapper;
import com.pharmaguard.api.auth.application.UsuarioUseCase;
import com.pharmaguard.api.auth.application.UsuarioUseCaseImpl;
import com.pharmaguard.api.auth.adapters.out.repository.InMemoryAuthRepositoryAdapter;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UsuarioControllerIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InMemoryAuthRepositoryAdapter repository = new InMemoryAuthRepositoryAdapter();
        UsuarioUseCase usuarioUseCase = new UsuarioUseCaseImpl(repository);
        UsuarioAdapterInMapper mapper = new UsuarioAdapterInMapper();

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        UsuarioController controller = new UsuarioController(usuarioUseCase, mapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveExecutarCicloCrudDeUsuario() throws Exception {
        String criarRequest = """
                {
                  "nome": "Ana Souza",
                  "email": "ana.souza@pharmaguard.com",
                  "login": "ana.souza",
                                                                        "tipo": "COLABORADOR",
                  "senha": "Senha@123",
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/usuarios/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Ana Souza")))
                .andExpect(jsonPath("$.email", is("ana.souza@pharmaguard.com")))
                .andExpect(jsonPath("$.login", is("ana.souza")))
                .andExpect(jsonPath("$.tipo", is("COLABORADOR")))
                .andExpect(jsonPath("$.perfis", hasSize(1)))
                .andExpect(jsonPath("$.perfis[0].nome", is("COLABORADOR")))
                .andExpect(jsonPath("$.status", is("ATIVO")));

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Ana Souza")));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        String atualizarRequest = """
                {
                  "nome": "Ana Souza Atualizada",
                  "email": "ana.souza@pharmaguard.com",
                  "login": "ana.souza",
                  "status": "BLOQUEADO"
                }
                """;

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atualizarRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Ana Souza Atualizada")))
                .andExpect(jsonPath("$.status", is("BLOQUEADO")));

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", is("usuario nao encontrado")));
    }
}
