package com.pharmaguard.api.auth.application;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.Objects;

public class AutenticarUsuarioUseCaseImpl implements AutenticarUsuarioUseCase {

    private final AutenticarUsuarioUseCase.UsuarioRepositoryPort usuarioRepository;
    private final AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder;
    private final AuthAuditService auditService;

    public AutenticarUsuarioUseCaseImpl(
            AutenticarUsuarioUseCase.UsuarioRepositoryPort usuarioRepository,
            AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder) {
        this(usuarioRepository, passwordEncoder, AuthAuditService.noop());
    }

    public AutenticarUsuarioUseCaseImpl(
            AutenticarUsuarioUseCase.UsuarioRepositoryPort usuarioRepository,
            AutenticarUsuarioUseCase.PasswordEncoderPort passwordEncoder,
            AuthAuditService auditService) {
        this.usuarioRepository = Objects.requireNonNull(usuarioRepository, "usuarioRepository e obrigatorio");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder, "passwordEncoder e obrigatorio");
        this.auditService = Objects.requireNonNull(auditService, "auditService e obrigatorio");
    }

    @Override
    public Usuario autenticar(String loginOuEmail, String senha) {
        String loginNormalizado = loginOuEmail == null ? null : loginOuEmail.trim();
        if (loginNormalizado == null || loginNormalizado.isBlank()) {
            throw new IllegalArgumentException("login ou email e obrigatorio");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("senha e obrigatoria");
        }

        Usuario usuario = usuarioRepository.buscarPorLogin(loginNormalizado)
                .orElseThrow(() -> {
                    auditService.registrarLoginFalha(loginNormalizado, "usuario_nao_encontrado");
                    return new IllegalArgumentException("usuario nao encontrado");
                });

        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            auditService.registrarLoginFalha(loginNormalizado, "credenciais_invalidas");
            throw new IllegalArgumentException("credenciais invalidas");
        }

        auditService.registrarLoginSucesso(loginNormalizado, usuario.getEmail());
        return usuario;
    }
}
