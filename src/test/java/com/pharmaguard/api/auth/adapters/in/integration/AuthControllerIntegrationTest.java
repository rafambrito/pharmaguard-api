package com.pharmaguard.api.auth.adapters.in.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.auth.adapters.in.controller.AuthController;
import com.pharmaguard.api.auth.adapters.in.controller.UsuarioController;
import com.pharmaguard.api.auth.adapters.in.mapper.UsuarioAdapterInMapper;
import com.pharmaguard.api.auth.application.AutenticarUsuarioUseCase;
import com.pharmaguard.api.auth.application.AutenticarUsuarioUseCaseImpl;
import com.pharmaguard.api.auth.application.DefinirSenhaUseCase;
import com.pharmaguard.api.auth.application.DefinirSenhaUseCaseImpl;
import com.pharmaguard.api.auth.application.RenovarSessaoUseCase;
import com.pharmaguard.api.auth.application.RenovarSessaoUseCaseImpl;
import com.pharmaguard.api.auth.application.UsuarioUseCase;
import com.pharmaguard.api.auth.application.UsuarioUseCaseImpl;
import com.pharmaguard.api.auth.adapters.out.security.JwtTokenService;
import com.pharmaguard.api.auth.adapters.out.repository.InMemoryAuthRepositoryAdapter;
import com.pharmaguard.api.shared.infrastructure.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerIntegrationTest {

    private MockMvc mockMvc;
        private JwtTokenService jwtTokenService;
        private InMemoryAuthRepositoryAdapter repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryAuthRepositoryAdapter();
                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder = new AutenticarUsuarioUseCase.PasswordEncoderPort() {
                        @Override
                        public String encode(CharSequence rawPassword) {
                                return bcrypt.encode(rawPassword.toString());
                        }

                        @Override
                        public boolean matches(CharSequence rawPassword, String encodedPassword) {
                                return rawPassword != null && bcrypt.matches(rawPassword, encodedPassword);
                        }
                };

                UsuarioUseCase usuarioUseCase = new UsuarioUseCaseImpl(repository, com.pharmaguard.api.auth.application.AuthAuditService.noop(), passwordEncoder);
        AutenticarUsuarioUseCase autenticarUsuarioUseCase = new AutenticarUsuarioUseCaseImpl(
                repository,
                                passwordEncoder);
                DefinirSenhaUseCase definirSenhaUseCase = new DefinirSenhaUseCaseImpl(repository, passwordEncoder);
        RenovarSessaoUseCase renovarSessaoUseCase = new RenovarSessaoUseCaseImpl(repository);
        jwtTokenService = new JwtTokenService("UGhhcm1hR3VhcmRBUElKV1RTZWNyZXRLZXlGb3JIbWFjU2hhMjU2", 3600, 1209600);
        UsuarioAdapterInMapper mapper = new UsuarioAdapterInMapper();

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("message");
        messageSource.setDefaultEncoding("UTF-8");

        UsuarioController usuarioController = new UsuarioController(usuarioUseCase, mapper);
        AuthController authController = new AuthController(autenticarUsuarioUseCase, definirSenhaUseCase, renovarSessaoUseCase, jwtTokenService);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController, authController)
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();
    }

    @Test
    void deveDefinirSenhaQuandoUsuarioExistirESenhasForemIguais() throws Exception {
        String criarUsuarioRequest = """
                {
                  "nome": "Bruno Lima",
                  "email": "bruno.lima@pharmaguard.com",
                  "login": "bruno.lima",
                  "tipo": "COLABORADOR",
                  "senha": "Senha@123",
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarUsuarioRequest))
                .andExpect(status().isCreated());

        String definirSenhaRequest = """
                {
                  "login": "bruno.lima",
                  "senha": "NovaSenha@456",
                  "confirmarSenha": "NovaSenha@456"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/definir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(definirSenhaRequest))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroQuandoSenhasDivergiremAoDefinirSenha() throws Exception {
        String definirSenhaRequest = """
                {
                  "login": "nao.existe",
                  "senha": "Senha@123",
                  "confirmarSenha": "Senha@1234"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/definir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(definirSenhaRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("senhas divergentes")));
    }

    @Test
    void deveRetornarErroQuandoUsuarioNaoExistirAoDefinirSenha() throws Exception {
        String definirSenhaRequest = """
                {
                  "login": "nao.existe",
                  "senha": "Senha@123",
                  "confirmarSenha": "Senha@123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/definir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(definirSenhaRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("usuario nao encontrado")));
    }

    @Test
    void deveAutenticarComUsuarioESenha() throws Exception {
        String criarUsuarioRequest = """
                {
                  "nome": "Bruno Lima",
                  "email": "bruno.lima@pharmaguard.com",
                  "login": "bruno.lima",
                  "tipo": "COLABORADOR",
                  "senha": "Senha@123",
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarUsuarioRequest))
                .andExpect(status().isCreated());

        String autenticarRequest = """
                {
                  "usuario": "bruno.lima",
                  "senha": "Senha@123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(autenticarRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType", is("Bearer")));
    }

    @Test
    void deveRenovarTokenQuandoRefreshTokenForValido() throws Exception {
        String criarUsuarioRequest = """
                {
                  "nome": "Bruno Lima",
                  "email": "bruno.lima@pharmaguard.com",
                  "login": "bruno.lima",
                                                                        "tipo": "COLABORADOR",
                  "senha": "Senha@123",
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarUsuarioRequest))
                .andExpect(status().isCreated());

        String refreshToken = jwtTokenService.gerarRefreshToken(repository.buscarPorLogin("bruno.lima").orElseThrow());

        String refreshRequest = """
                {
                  "refreshToken": "%s"
                }
                """.formatted(refreshToken);

        MvcResult resultado = mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andReturn();

        String body = resultado.getResponse().getContentAsString();
        String accessToken = body.replaceAll(".*\\\"accessToken\\\":\\\"([^\\\"]+)\\\".*", "$1");
        String novoRefreshToken = body.replaceAll(".*\\\"refreshToken\\\":\\\"([^\\\"]+)\\\".*", "$1");

        org.junit.jupiter.api.Assertions.assertTrue(jwtTokenService.validarToken(accessToken));
        org.junit.jupiter.api.Assertions.assertEquals("bruno.lima", jwtTokenService.extrairSubject(accessToken));
        org.junit.jupiter.api.Assertions.assertTrue(jwtTokenService.extrairClaims(accessToken).containsKey("perfis"));
        org.junit.jupiter.api.Assertions.assertTrue(jwtTokenService.extrairClaims(accessToken).containsKey("roles"));
        org.junit.jupiter.api.Assertions.assertTrue(jwtTokenService.extrairClaims(accessToken).toString().contains("COLABORADOR"));
        org.junit.jupiter.api.Assertions.assertEquals("bruno.lima", jwtTokenService.extrairSubjectSeRefreshTokenValido(novoRefreshToken));
    }

    @Test
    void deveRetornarErroQuandoRefreshTokenForInvalido() throws Exception {
        String refreshRequest = """
                {
                  "refreshToken": "token-invalido"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("sessao invalida ou expirada")));
    }

    @Test
    void deveRevogarRefreshTokenAposUso() throws Exception {
        String criarUsuarioRequest = """
                {
                  "nome": "Carla Nunes",
                  "email": "carla.nunes@pharmaguard.com",
                  "login": "carla.nunes",
                                                                        "tipo": "COLABORADOR",
                  "senha": "Senha@123",
                  "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criarUsuarioRequest))
                .andExpect(status().isCreated());

        String refreshToken = jwtTokenService.gerarRefreshToken(repository.buscarPorLogin("carla.nunes").orElseThrow());
        String refreshRequest = """
                {
                  "refreshToken": "%s"
                }
                """.formatted(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("sessao invalida ou expirada")));
    }
}
