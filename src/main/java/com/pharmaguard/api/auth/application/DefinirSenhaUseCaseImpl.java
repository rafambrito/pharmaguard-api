package com.pharmaguard.api.auth.application;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.Objects;

public class DefinirSenhaUseCaseImpl implements DefinirSenhaUseCase {

    private final UsuarioUseCase.UsuarioRepositoryPort repository;
    private final AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder;

    public DefinirSenhaUseCaseImpl(UsuarioUseCase.UsuarioRepositoryPort repository) {
        this(repository, new PlainTextPasswordEncoderPort());
    }

    public DefinirSenhaUseCaseImpl(UsuarioUseCase.UsuarioRepositoryPort repository,
            AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder) {
        this.repository = Objects.requireNonNull(repository, "repository e obrigatorio");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder e obrigatorio");
    }

    @Override
    public void definirSenha(String login, String senha, String confirmarSenha) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login e obrigatorio");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("senha e obrigatoria");
        }
        if (confirmarSenha == null || confirmarSenha.isBlank()) {
            throw new IllegalArgumentException("confirmacao de senha e obrigatoria");
        }
        if (!senha.equals(confirmarSenha)) {
            throw new IllegalArgumentException("senhas divergentes");
        }

        Usuario usuario = repository.buscarPorLogin(login.trim())
                .orElseThrow(() -> new IllegalArgumentException("usuario nao encontrado"));

        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.marcarAtualizacao();
        repository.atualizar(usuario);
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