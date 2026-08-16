package com.pharmaguard.api.auth.application;

import com.pharmaguard.api.auth.domain.Usuario;
import java.time.Instant;

public interface TokenService {

    String gerarAccessToken(Usuario usuario);

    String gerarRefreshToken(Usuario usuario);

    RefreshTokenClaims extrairRefreshTokenClaimsValido(String token);

    void validarPoliticaRefreshToken(RefreshTokenClaims claims);

    void revogarRefreshToken(RefreshTokenClaims claims);

    record RefreshTokenClaims(String subject, String tokenId, Instant expiresAt) {
    }
}
