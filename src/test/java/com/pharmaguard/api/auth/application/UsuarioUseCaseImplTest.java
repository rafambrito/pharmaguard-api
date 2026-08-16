package com.pharmaguard.api.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UsuarioUseCaseImplTest {

    @Test
    void deveSalvarUsuario() {
        Usuario usuario = criarUsuario(1L);

        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository);

        Usuario salvo = useCase.salvar(usuario);

        assertEquals(usuario, salvo);
        assertEquals(1, repository.salvos.size());
    }

    @Test
    void deveLancarErroAoSalvarSemUsuario() {
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(new InMemoryUsuarioRepository());

        assertThrows(IllegalArgumentException.class, () -> useCase.salvar(null));
    }

    @Test
    void deveLancarErroAoAtualizarSemId() {
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(new InMemoryUsuarioRepository());

        Usuario usuario = criarUsuario(null);
        assertThrows(IllegalArgumentException.class, () -> useCase.atualizar(usuario));
    }

    @Test
    void deveLancarErroAoAtualizarUsuarioNaoEncontrado() {
        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository);

        assertThrows(IllegalArgumentException.class, () -> useCase.atualizar(criarUsuario(99L)));
    }

    @Test
    void deveAtualizarUsuarioQuandoExistir() {
        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        Usuario existente = criarUsuario(10L);
        repository.usuariosPorId.add(existente);
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository);

        Usuario atualizado = criarUsuario(10L);
        atualizado.setNome("Ana Maria");

        Usuario resultado = useCase.atualizar(atualizado);

        assertEquals(atualizado, resultado);
        assertEquals(1, repository.atualizados.size());
    }

    @Test
    void deveDeletarUsuario() {
        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository);

        useCase.deletar(7L);

        assertEquals(7L, repository.ultimoIdDeletado);
    }

    @Test
    void deveLancarErroAoDeletarSemId() {
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(new InMemoryUsuarioRepository());

        assertThrows(IllegalArgumentException.class, () -> useCase.deletar(null));
    }

    @Test
    void deveBuscarUsuarioPorId() {
        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        Usuario existente = criarUsuario(5L);
        repository.usuariosPorId.add(existente);
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository);

        Optional<Usuario> resultado = useCase.buscarPorId(5L);

        assertTrue(resultado.isPresent());
        assertEquals(existente, resultado.get());
    }

    @Test
    void deveLancarErroAoBuscarPorIdSemId() {
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(new InMemoryUsuarioRepository());

        assertThrows(IllegalArgumentException.class, () -> useCase.buscarPorId(null));
    }

    @Test
    void deveBuscarTodos() {
        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        repository.usuariosPorId.add(criarUsuario(1L));
        repository.usuariosPorId.add(criarUsuario(2L));
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository);

        List<Usuario> resultado = useCase.buscarTodos();

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarPorEmailComValorNormalizado() {
        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        Usuario existente = criarUsuario(1L);
        repository.usuariosPorId.add(existente);
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository);

        Optional<Usuario> resultado = useCase.buscarPorEmail("  ana@email.com  ");

        assertTrue(resultado.isPresent());
        assertEquals("ana@email.com", repository.ultimoEmailBuscado);
    }

    @Test
    void deveLancarErroAoBuscarEmailEmBranco() {
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(new InMemoryUsuarioRepository());

        assertThrows(IllegalArgumentException.class, () -> useCase.buscarPorEmail("   "));
    }

    @Test
    void deveBuscarPorLoginComValorNormalizado() {
        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        Usuario existente = criarUsuario(1L);
        repository.usuariosPorId.add(existente);
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository);

        Optional<Usuario> resultado = useCase.buscarPorLogin("  ana  ");

        assertTrue(resultado.isPresent());
        assertEquals("ana", repository.ultimoLoginBuscado);
    }

    @Test
    void deveLancarErroAoBuscarLoginEmBranco() {
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(new InMemoryUsuarioRepository());

        assertThrows(IllegalArgumentException.class, () -> useCase.buscarPorLogin("   "));
    }

    @Test
    void deveRegistrarAuditoriaQuandoUsuarioForAtualizado() {
        InMemoryUsuarioRepository repository = new InMemoryUsuarioRepository();
        Usuario existente = criarUsuario(10L);
        repository.usuariosPorId.add(existente);
        InMemoryAuditService auditService = new InMemoryAuditService();
        UsuarioUseCase useCase = new UsuarioUseCaseImpl(repository, auditService);

        Usuario atualizado = criarUsuario(10L);
        atualizado.setNome("Ana Maria");
        useCase.atualizar(atualizado);

        assertEquals("usuario_atualizado", auditService.ultimoEvento);
        assertEquals("ana", auditService.ultimoLogin);
    }

    private Usuario criarUsuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Ana");
        usuario.setEmail("ana@email.com");
        usuario.setLogin("ana");
        usuario.setSenhaHash("hash");
        usuario.setStatus(Usuario.Status.ATIVO);
        return usuario;
    }

    private static final class InMemoryUsuarioRepository implements UsuarioUseCase.UsuarioRepositoryPort {
        private final List<Usuario> salvos = new ArrayList<>();
        private final List<Usuario> atualizados = new ArrayList<>();
        private final List<Usuario> usuariosPorId = new ArrayList<>();
        private Long ultimoIdDeletado;
        private String ultimoEmailBuscado;
        private String ultimoLoginBuscado;

        @Override
        public Usuario salvar(Usuario usuario) {
            salvos.add(usuario);
            return usuario;
        }

        @Override
        public Usuario atualizar(Usuario usuario) {
            atualizados.add(usuario);
            return usuario;
        }

        @Override
        public void deletar(Long id) {
            ultimoIdDeletado = id;
        }

        @Override
        public Optional<Usuario> buscarPorId(Long id) {
            return usuariosPorId.stream().filter(usuario -> id.equals(usuario.getId())).findFirst();
        }

        @Override
        public List<Usuario> buscarTodos() {
            return List.copyOf(usuariosPorId);
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            ultimoEmailBuscado = email;
            return usuariosPorId.stream().filter(usuario -> email.equals(usuario.getEmail())).findFirst();
        }

        @Override
        public Optional<Usuario> buscarPorLogin(String login) {
            ultimoLoginBuscado = login;
            return usuariosPorId.stream().filter(usuario -> login.equals(usuario.getLogin())).findFirst();
        }
    }

    private static final class InMemoryAuditService implements com.pharmaguard.api.auth.application.AuthAuditService {
        private String ultimoEvento;
        private String ultimoLogin;

        @Override
        public void registrarLoginSucesso(String login, String email) {
        }

        @Override
        public void registrarLoginFalha(String login, String motivo) {
        }

        @Override
        public void registrarUsuarioCriado(String login, Long usuarioId) {
        }

        @Override
        public void registrarUsuarioAtualizado(String login, Long usuarioId, String detalhe) {
            this.ultimoEvento = "usuario_atualizado";
            this.ultimoLogin = login;
        }

        @Override
        public void registrarUsuarioRemovido(String login, Long usuarioId) {
        }

        @Override
        public void registrarPerfilAtualizado(String login, Long usuarioId, String perfil) {
        }
    }
}
