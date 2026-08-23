package com.pharmaguard.api.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DefinirSenhaUseCaseImplTest {

    @Test
    void deveDefinirSenhaComLoginNormalizadoERegistrarAtualizacao() {
        Usuario usuario = criarUsuario();
        UsuarioRepositoryStub repository = new UsuarioRepositoryStub(usuario);
        PasswordEncoderStub encoder = new PasswordEncoderStub();
        DefinirSenhaUseCase useCase = new DefinirSenhaUseCaseImpl(repository, encoder);

        useCase.definirSenha("  ana  ", "nova-senha", "nova-senha");

        assertEquals("ana", repository.ultimoLoginBuscado);
        assertEquals("encoded-nova-senha", usuario.getSenhaHash());
        assertEquals(usuario, repository.usuarioAtualizado);
        assertEquals("nova-senha", encoder.ultimaSenha);
    }

    @Test
    void deveLancarErroQuandoSenhasForemDivergentes() {
        DefinirSenhaUseCase useCase = new DefinirSenhaUseCaseImpl(new UsuarioRepositoryStub(criarUsuario()));

        assertThrows(IllegalArgumentException.class,
                () -> useCase.definirSenha("ana", "senha-a", "senha-b"));
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoForEncontrado() {
        DefinirSenhaUseCase useCase = new DefinirSenhaUseCaseImpl(new UsuarioRepositoryStub(null));

        assertThrows(IllegalArgumentException.class,
                () -> useCase.definirSenha("ana", "nova-senha", "nova-senha"));
    }

    @Test
    void deveLancarErroQuandoSenhaForObrigatoria() {
        DefinirSenhaUseCase useCase = new DefinirSenhaUseCaseImpl(new UsuarioRepositoryStub(criarUsuario()));

        assertThrows(IllegalArgumentException.class,
                () -> useCase.definirSenha("ana", "   ", "   "));
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Ana");
        usuario.setEmail("ana@email.com");
        usuario.setLogin("ana");
        usuario.setSenhaHash("senha-antiga");
        usuario.setStatus(Usuario.Status.ATIVO);
        return usuario;
    }

    private static final class UsuarioRepositoryStub implements UsuarioUseCase.UsuarioRepositoryPort {
        private final Usuario usuario;
        private String ultimoLoginBuscado;
        private Usuario usuarioAtualizado;

        private UsuarioRepositoryStub(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Usuario salvar(Usuario usuario) {
            return usuario;
        }

        @Override
        public Usuario atualizar(Usuario usuario) {
            usuarioAtualizado = usuario;
            return usuario;
        }

        @Override
        public void deletar(Long id) {
        }

        @Override
        public Optional<Usuario> buscarPorId(Long id) {
            return Optional.empty();
        }

        @Override
        public List<Usuario> buscarTodos() {
            return new ArrayList<>();
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.empty();
        }

        @Override
        public Optional<Usuario> buscarPorLogin(String login) {
            ultimoLoginBuscado = login;
            return Optional.ofNullable(usuario);
        }
    }

    private static final class PasswordEncoderStub implements AutenticarUsuarioUseCase.PasswordEncoderPort {
        private String ultimaSenha;

        @Override
        public String encode(CharSequence rawPassword) {
            ultimaSenha = rawPassword.toString();
            return "encoded-" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return false;
        }
    }
}
