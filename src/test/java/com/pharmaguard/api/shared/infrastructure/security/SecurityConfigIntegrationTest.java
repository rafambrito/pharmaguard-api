package com.pharmaguard.api.shared.infrastructure.security;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pharmaguard.api.auth.domain.Usuario;
import com.pharmaguard.api.auth.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude="
				+ "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
				+ "org.springframework.boot.orm.jpa.autoconfigure.HibernateJpaAutoConfiguration,"
				+ "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
})
class SecurityConfigIntegrationTest {
	private static final String BASIC_AUTH = "Basic " + Base64.getEncoder()
			.encodeToString("api:api123".getBytes(StandardCharsets.UTF_8));
	private static final String GESTOR_AUTH = "Basic " + Base64.getEncoder()
			.encodeToString("gestor:gestor123".getBytes(StandardCharsets.UTF_8));

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private JwtTokenService jwtTokenService;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.build();
	}

	@Test
	void shouldAllowPublicAuthEndpoints() throws Exception {
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
					.header("Authorization", BASIC_AUTH)
					.contentType(MediaType.APPLICATION_JSON)
					.content(criarUsuarioRequest))
				.andExpect(status().isCreated());

		String refreshRequest = """
		        {
		          "refreshToken": "%s"
		        }
		        """.formatted(jwtTokenService.gerarRefreshToken(criarUsuarioToken("bruno.lima", "bruno.lima@pharmaguard.com")));

		mockMvc.perform(post("/api/v1/auth/refresh-token")
					.contentType(MediaType.APPLICATION_JSON)
					.content(refreshRequest))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType", is("Bearer")));
	}

	private Usuario criarUsuarioToken(String login, String email) {
		Usuario usuario = new Usuario();
		usuario.setNome("Usuario Teste");
		usuario.setEmail(email);
		usuario.setLogin(login);
		usuario.setSenhaHash("hash");
		usuario.setStatus(Usuario.Status.ATIVO);
		return usuario;
	}

	@Test
	void shouldProtectUsuarioEndpoints() throws Exception {
		mockMvc.perform(get("/api/v1/usuarios"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldAllowAuthenticatedUsuarioEndpoints() throws Exception {
		String criarUsuarioRequest = """
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
					.header("Authorization", BASIC_AUTH)
					.contentType(MediaType.APPLICATION_JSON)
					.content(criarUsuarioRequest))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.login", is("ana.souza")));

		mockMvc.perform(get("/api/v1/usuarios")
					.header("Authorization", BASIC_AUTH))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].login", is("ana.souza")));
	}

	@Test
	void shouldDenyAccessForUserWithoutAdminRole() throws Exception {
		mockMvc.perform(get("/api/v1/usuarios")
					.header("Authorization", GESTOR_AUTH))
				.andExpect(status().isForbidden());
	}
}