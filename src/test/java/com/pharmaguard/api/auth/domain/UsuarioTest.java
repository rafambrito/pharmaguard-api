package com.pharmaguard.api.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void deveNormalizarCamposObrigatorios() {
        Usuario usuario = new Usuario();

        usuario.setNome("  Ana  ");
        usuario.setEmail("  ana@email.com  ");
        usuario.setLogin("  ana  ");
        usuario.setSenhaHash("  hash  ");
        usuario.setStatus(Usuario.Status.ATIVO);

        assertEquals("Ana", usuario.getNome());
        assertEquals("ana@email.com", usuario.getEmail());
        assertEquals("ana", usuario.getLogin());
        assertEquals("hash", usuario.getSenhaHash());
    }

    @Test
    void deveLancarErroQuandoEmailForInvalido() {
        Usuario usuario = new Usuario();

        assertThrows(IllegalArgumentException.class, () -> usuario.setEmail("sem-arroba"));
    }

    @Test
    void deveManterApenasUmPerfilPorUsuario() {
        Usuario usuario = criarUsuarioBase();
        Perfil admin1 = criarPerfil(1L, "ADMIN");
        Perfil admin2 = criarPerfil(2L, "GESTOR");

        usuario.adicionarPerfil(admin1);
        usuario.adicionarPerfil(admin2);

        assertEquals(1, usuario.getPerfis().size());
        assertEquals("GESTOR", usuario.getPerfis().get(0).getNome());
        assertEquals("GESTOR", usuario.getTipo());
    }

    @Test
    void deveRemoverPerfilQuandoExistir() {
        Usuario usuario = criarUsuarioBase();
        Perfil admin = criarPerfil(1L, "ADMIN");
        Perfil gestor = criarPerfil(2L, "GESTOR");
        usuario.adicionarPerfil(admin);
        usuario.adicionarPerfil(gestor);

        usuario.removerPerfil(criarPerfil(1L, "ADMIN"));

        assertEquals(1, usuario.getPerfis().size());
        assertEquals("GESTOR", usuario.getPerfis().get(0).getNome());
    }

    @Test
    void deveRetornarFalseQuandoPerfilForNulo() {
        Usuario usuario = criarUsuarioBase();

        assertFalse(usuario.possuiPerfil(null));
    }

    @Test
    void deveIdentificarQuandoUsuarioPossuiPerfil() {
        Usuario usuario = criarUsuarioBase();
        Perfil admin = criarPerfil(1L, "ADMIN");
        usuario.adicionarPerfil(admin);

        assertTrue(usuario.possuiPerfil(criarPerfil(1L, "ADMIN")));
    }

    @Test
    void deveRetornarListaDePerfisImutavel() {
        Usuario usuario = criarUsuarioBase();
        usuario.adicionarPerfil(criarPerfil(1L, "ADMIN"));

        List<Perfil> perfis = usuario.getPerfis();

        assertThrows(UnsupportedOperationException.class, () -> perfis.add(criarPerfil(2L, "GESTOR")));
    }

    @Test
    void deveMarcarDatasDeCriacaoEAtualizacao() {
        Usuario usuario = criarUsuarioBase();

        usuario.marcarCriacao();
        usuario.marcarAtualizacao();

        assertNotNull(usuario.getDataCriacao());
        assertNotNull(usuario.getDataUltimaAlteracao());
    }

    @Test
    void deveLancarErroQuandoEmailJaExistirNaValidacaoDeIdentidadeUnica() {
        Usuario usuario = criarUsuarioBase();

        assertThrows(IllegalArgumentException.class,
                () -> usuario.validarIdentidadeUnica(new UsuarioIdentidadeUnicaStub(true, false)));
    }

    @Test
    void deveLancarErroQuandoLoginJaExistirNaValidacaoDeIdentidadeUnica() {
        Usuario usuario = criarUsuarioBase();

        assertThrows(IllegalArgumentException.class,
                () -> usuario.validarIdentidadeUnica(new UsuarioIdentidadeUnicaStub(false, true)));
    }

    @Test
    void deveValidarIdentidadeUnicaQuandoDadosEstaoDisponiveis() {
        Usuario usuario = criarUsuarioBase();

        usuario.validarIdentidadeUnica(new UsuarioIdentidadeUnicaStub(false, false));
    }

    private Usuario criarUsuarioBase() {
        Usuario usuario = new Usuario();
        usuario.setNome("Ana");
        usuario.setEmail("ana@email.com");
        usuario.setLogin("ana");
        usuario.setSenhaHash("hash");
        usuario.setStatus(Usuario.Status.ATIVO);
        return usuario;
    }

    private Perfil criarPerfil(Long id, String nome) {
        Perfil perfil = new Perfil();
        perfil.setId(id);
        perfil.setNome(nome);
        perfil.setDescricao("perfil " + nome);
        perfil.setAtivo(true);
        return perfil;
    }

    private static final class UsuarioIdentidadeUnicaStub implements UsuarioIdentidadeUnicaPort {
        private final boolean emailExiste;
        private final boolean loginExiste;

        private UsuarioIdentidadeUnicaStub(boolean emailExiste, boolean loginExiste) {
            this.emailExiste = emailExiste;
            this.loginExiste = loginExiste;
        }

        @Override
        public boolean existePorEmail(String email) {
            return emailExiste;
        }

        @Override
        public boolean existePorLogin(String login) {
            return loginExiste;
        }
    }
}
