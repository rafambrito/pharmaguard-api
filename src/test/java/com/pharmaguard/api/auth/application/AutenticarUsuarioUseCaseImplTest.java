package com.pharmaguard.api.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AutenticarUsuarioUseCaseImplTest {

    @Test
    void deveLancarErroQuandoRepositorioNaoForInformado() {
        PasswordEncoderStub encoder = new PasswordEncoderStub();

        assertThrows(NullPointerException.class, () -> new AutenticarUsuarioUseCaseImpl(null, encoder));
    }

    @Test
    void deveLancarErroQuandoPasswordEncoderNaoForInformado() {
        UsuarioRepositoryStub repository = new UsuarioRepositoryStub(criarUsuarioPadrao());

        assertThrows(NullPointerException.class, () -> new AutenticarUsuarioUseCaseImpl(repository, null));
    }

    @Test
    void deveAutenticarUsuarioComLoginESenhaValidos() {
        Usuario usuario = criarUsuarioPadrao();

        UsuarioRepositoryStub repository = new UsuarioRepositoryStub(usuario);
        PasswordEncoderStub encoder = new PasswordEncoderStub();
        AutenticarUsuarioUseCase useCase = new AutenticarUsuarioUseCaseImpl(repository, encoder);

        Usuario autenticado = useCase.autenticar("  ana  ", "senha123");

        assertEquals(usuario, autenticado);
        assertEquals("ana", repository.ultimoLoginBuscado);
    }

    @Test
    void deveLancarErroQuandoCredenciaisInvalidas() {
        Usuario usuario = criarUsuarioPadrao();

        UsuarioRepositoryStub repository = new UsuarioRepositoryStub(usuario);
        PasswordEncoderStub encoder = new PasswordEncoderStub();
        AutenticarUsuarioUseCase useCase = new AutenticarUsuarioUseCaseImpl(repository, encoder);

        assertThrows(IllegalArgumentException.class, () -> useCase.autenticar("ana", "errada"));
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoEncontrado() {
        UsuarioRepositoryStub repository = new UsuarioRepositoryStub(null);
        PasswordEncoderStub encoder = new PasswordEncoderStub();
        AutenticarUsuarioUseCase useCase = new AutenticarUsuarioUseCaseImpl(repository, encoder);

        assertThrows(IllegalArgumentException.class, () -> useCase.autenticar("nao-existe", "senha123"));
    }

    @Test
    void deveLancarErroQuandoLoginOuEmailForInvalido() {
        UsuarioRepositoryStub repository = new UsuarioRepositoryStub(criarUsuarioPadrao());
        PasswordEncoderStub encoder = new PasswordEncoderStub();
        AutenticarUsuarioUseCase useCase = new AutenticarUsuarioUseCaseImpl(repository, encoder);

        assertThrows(IllegalArgumentException.class, () -> useCase.autenticar("   ", "senha123"));
    }

    @Test
    void deveLancarErroQuandoSenhaForInvalida() {
        UsuarioRepositoryStub repository = new UsuarioRepositoryStub(criarUsuarioPadrao());
        PasswordEncoderStub encoder = new PasswordEncoderStub();
        AutenticarUsuarioUseCase useCase = new AutenticarUsuarioUseCaseImpl(repository, encoder);

        assertThrows(IllegalArgumentException.class, () -> useCase.autenticar("ana", "   "));
    }

    @Test
    void deveRegistrarAuditoriaQuandoLoginForBemSucedido() {
        Usuario usuario = criarUsuarioPadrao();
        UsuarioRepositoryStub repository = new UsuarioRepositoryStub(usuario);
        PasswordEncoderStub encoder = new PasswordEncoderStub();
        InMemoryAuditService auditService = new InMemoryAuditService();
        AutenticarUsuarioUseCase useCase = new AutenticarUsuarioUseCaseImpl(repository, encoder, auditService);

        useCase.autenticar("ana", "senha123");

        assertEquals("auth_login_success", auditService.ultimoEvento);
        assertEquals("ana", auditService.ultimoLogin);
    }

    private Usuario criarUsuarioPadrao() {
        Usuario usuario = new Usuario();
        usuario.setNome("Ana");
        usuario.setEmail("ana@email.com");
        usuario.setLogin("ana");
        usuario.setSenhaHash("hash-enc");
        usuario.setStatus(Usuario.Status.ATIVO);
        return usuario;
    }

    private static final class UsuarioRepositoryStub implements AutenticarUsuarioUseCase.UsuarioRepositoryPort {
        private final Usuario usuario;
        private String ultimoLoginBuscado;

        private UsuarioRepositoryStub(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Optional<Usuario> buscarPorLogin(String login) {
            ultimoLoginBuscado = login;
            return Objects.equals("ana", login) ? Optional.ofNullable(usuario) : Optional.empty();
        }
    }

    private static final class PasswordEncoderStub implements AutenticarUsuarioUseCase.PasswordEncoderPort {
        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return "senha123".equals(rawPassword.toString()) && "hash-enc".equals(encodedPassword);
        }
    }

    private static final class InMemoryAuditService implements com.pharmaguard.api.auth.application.AuthAuditService {
        private String ultimoEvento;
        private String ultimoLogin;

        @Override
        public void registrarLoginSucesso(String login, String email) {
            this.ultimoEvento = "auth_login_success";
            this.ultimoLogin = login;
        }

        @Override
        public void registrarLoginFalha(String login, String motivo) {
            this.ultimoEvento = "auth_login_failure";
            this.ultimoLogin = login;
        }

        @Override
        public void registrarUsuarioCriado(String login, Long usuarioId) {
        }

        @Override
        public void registrarUsuarioAtualizado(String login, Long usuarioId, String detalhe) {
        }

        @Override
        public void registrarUsuarioRemovido(String login, Long usuarioId) {
        }

        @Override
        public void registrarPerfilAtualizado(String login, Long usuarioId, String perfil) {
        }
    }
}
