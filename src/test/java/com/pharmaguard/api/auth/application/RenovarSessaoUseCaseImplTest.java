package com.pharmaguard.api.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RenovarSessaoUseCaseImplTest {

    @Test
    void deveLancarErroQuandoRepositorioNaoForInformado() {
        assertThrows(NullPointerException.class, () -> new RenovarSessaoUseCaseImpl(null));
    }

    @Test
    void deveRenovarSessaoQuandoRefreshTokenEhValido() {
        Usuario usuario = criarUsuarioPadrao();

        RefreshTokenRepositoryStub repository = new RefreshTokenRepositoryStub(usuario);
        RenovarSessaoUseCase useCase = new RenovarSessaoUseCaseImpl(repository);

        Usuario renovado = useCase.renovarSessao("  login-valido  ");

        assertEquals(usuario, renovado);
        assertEquals("login-valido", repository.ultimoLoginBuscado);
    }

    @Test
    void deveLancarErroQuandoRefreshTokenEhInvalido() {
        RefreshTokenRepositoryStub repository = new RefreshTokenRepositoryStub(null);
        RenovarSessaoUseCase useCase = new RenovarSessaoUseCaseImpl(repository);

        assertThrows(IllegalArgumentException.class, () -> useCase.renovarSessao("login-invalido"));
    }

    @Test
    void deveLancarErroQuandoRefreshTokenForInvalido() {
        RefreshTokenRepositoryStub repository = new RefreshTokenRepositoryStub(criarUsuarioPadrao());
        RenovarSessaoUseCase useCase = new RenovarSessaoUseCaseImpl(repository);

        assertThrows(IllegalArgumentException.class, () -> useCase.renovarSessao("   "));
    }

    private Usuario criarUsuarioPadrao() {
        Usuario usuario = new Usuario();
        usuario.setNome("Ana");
        usuario.setEmail("ana@email.com");
        usuario.setLogin("ana");
        usuario.setSenhaHash("hash");
        usuario.setStatus(Usuario.Status.ATIVO);
        return usuario;
    }

    private static final class RefreshTokenRepositoryStub implements RenovarSessaoUseCase.RefreshTokenRepositoryPort {
        private final Usuario usuario;
        private String ultimoLoginBuscado;

        private RefreshTokenRepositoryStub(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Optional<Usuario> buscarPorLogin(String login) {
            ultimoLoginBuscado = login;
            return Objects.equals("login-valido", login) ? Optional.ofNullable(usuario) : Optional.empty();
        }
    }
}
