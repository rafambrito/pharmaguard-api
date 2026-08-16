package com.pharmaguard.api.auth.infrastructure.repository;

import com.pharmaguard.api.auth.application.AutenticarUsuarioUseCase;
import com.pharmaguard.api.auth.application.RenovarSessaoUseCase;
import com.pharmaguard.api.auth.application.UsuarioUseCase;
import com.pharmaguard.api.auth.domain.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryAuthRepositoryAdapter implements UsuarioUseCase.UsuarioRepositoryPort,
        AutenticarUsuarioUseCase.UsuarioRepositoryPort,
        RenovarSessaoUseCase.RefreshTokenRepositoryPort {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final ConcurrentMap<Long, Usuario> usersById = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> idByEmail = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> idByLogin = new ConcurrentHashMap<>();

    @Override
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(sequence.getAndIncrement());
        }

        usuario.marcarCriacao();
        usersById.put(usuario.getId(), usuario);
        indexar(usuario);
        return usuario;
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        usersById.put(usuario.getId(), usuario);
        indexar(usuario);
        return usuario;
    }

    @Override
    public void deletar(Long id) {
        Usuario removido = usersById.remove(id);
        if (removido == null) {
            return;
        }

        idByEmail.remove(removido.getEmail());
        idByLogin.remove(removido.getLogin());
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public List<Usuario> buscarTodos() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        Long id = idByEmail.get(email);
        return id == null ? Optional.empty() : Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        Long id = idByLogin.get(login);
        return id == null ? Optional.empty() : Optional.ofNullable(usersById.get(id));
    }

    private void indexar(Usuario usuario) {
        idByEmail.put(usuario.getEmail(), usuario.getId());
        idByLogin.put(usuario.getLogin(), usuario.getId());
    }
}
