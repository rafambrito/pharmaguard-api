package com.pharmaguard.api.auth.application;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioUseCase {

    Usuario salvar(Usuario usuario);

    Usuario atualizar(Usuario usuario);

    void deletar(Long id);

    Optional<Usuario> buscarPorId(Long id);

    List<Usuario> buscarTodos();

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorLogin(String login);

    interface UsuarioRepositoryPort {
        Usuario salvar(Usuario usuario);

        Usuario atualizar(Usuario usuario);

        void deletar(Long id);

        Optional<Usuario> buscarPorId(Long id);

        List<Usuario> buscarTodos();

        Optional<Usuario> buscarPorEmail(String email);

        Optional<Usuario> buscarPorLogin(String login);
    }
}
