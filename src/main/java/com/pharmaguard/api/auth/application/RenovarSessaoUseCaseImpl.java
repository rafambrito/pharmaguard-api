package com.pharmaguard.api.auth.application;

import com.pharmaguard.api.auth.domain.Usuario;
import java.util.Objects;

public class RenovarSessaoUseCaseImpl implements RenovarSessaoUseCase {

    private final RenovarSessaoUseCase.RefreshTokenRepositoryPort refreshTokenRepository;

    public RenovarSessaoUseCaseImpl(
            RenovarSessaoUseCase.RefreshTokenRepositoryPort refreshTokenRepository) {
        this.refreshTokenRepository = Objects.requireNonNull(refreshTokenRepository, "refreshTokenRepository e obrigatorio");
    }

    @Override
    public Usuario renovarSessao(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login e obrigatorio");
        }

        return refreshTokenRepository.buscarPorLogin(login.trim())
                .orElseThrow(() -> new IllegalArgumentException("sessao invalida ou expirada"));
    }
}
