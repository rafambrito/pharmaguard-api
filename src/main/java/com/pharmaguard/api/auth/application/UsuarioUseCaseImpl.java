package com.pharmaguard.api.auth.application;

import com.pharmaguard.api.auth.domain.Perfil;
import com.pharmaguard.api.auth.domain.Usuario;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsuarioUseCaseImpl implements UsuarioUseCase {

    private final UsuarioRepositoryPort repository;
    private final AuthAuditService auditService;
    private final AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder;

    public UsuarioUseCaseImpl(UsuarioRepositoryPort repository) {
        this(repository, AuthAuditService.noop(), new PlainTextPasswordEncoderPort());
    }

    public UsuarioUseCaseImpl(UsuarioRepositoryPort repository, AuthAuditService auditService) {
        this(repository, auditService, new PlainTextPasswordEncoderPort());
    }

    public UsuarioUseCaseImpl(UsuarioRepositoryPort repository,
            AuthAuditService auditService,
            AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
        this.auditService = Objects.requireNonNull(auditService, "auditService e obrigatorio");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder e obrigatorio");
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("usuario e obrigatorio");
        }
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        Usuario salvo = repository.salvar(usuario);
        auditService.registrarUsuarioCriado(salvo.getLogin(), salvo.getId());
        return salvo;
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("usuario e obrigatorio");
        }
        if (usuario.getId() == null) {
            throw new IllegalArgumentException("id do usuario e obrigatorio");
        }
        Usuario existente = repository.buscarPorId(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("usuario nao encontrado"));

        String perfisAntes = perfilSummary(existente.getPerfis());
        Usuario atualizado = repository.atualizar(usuario);
        String perfisDepois = perfilSummary(atualizado.getPerfis());

        auditService.registrarUsuarioAtualizado(atualizado.getLogin(), atualizado.getId(), "dados_usuario");
        if (!Objects.equals(perfisAntes, perfisDepois)) {
            auditService.registrarPerfilAtualizado(atualizado.getLogin(), atualizado.getId(), perfisDepois);
        }
        return atualizado;
    }

    @Override
    public void deletar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id do usuario e obrigatorio");
        }
        Optional<Usuario> usuarioOptional = repository.buscarPorId(id);
        repository.deletar(id);
        usuarioOptional.ifPresent(usuario -> auditService.registrarUsuarioRemovido(usuario.getLogin(), id));
    }

    private String perfilSummary(List<Perfil> perfis) {
        return perfis.stream()
                .map(Perfil::getNome)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(","));
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id do usuario e obrigatorio");
        }
        return repository.buscarPorId(id);
    }

    @Override
    public List<Usuario> buscarTodos() {
        return repository.buscarTodos();
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email e obrigatorio");
        }
        return repository.buscarPorEmail(email.trim());
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login e obrigatorio");
        }
        return repository.buscarPorLogin(login.trim());
    }

    private static final class PlainTextPasswordEncoderPort implements AutenticarUsuarioUseCase.PasswordEncoderPort {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword == null ? null : rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword != null && rawPassword.toString().equals(encodedPassword);
        }
    }
}
