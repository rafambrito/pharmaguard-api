package com.pharmaguard.api.auth.adapters.out.security;

import com.pharmaguard.api.auth.application.TokenService;
import com.pharmaguard.api.auth.domain.Perfil;
import com.pharmaguard.api.auth.domain.Usuario;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService implements TokenService {

    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String CLAIM_TOKEN_ID = "jti";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final SecretKey signingKey;
    private final long accessTokenValiditySeconds;
    private final long refreshTokenValiditySeconds;
    private final ConcurrentMap<String, String> activeRefreshTokenIdBySubject = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Instant> revokedRefreshTokenIds = new ConcurrentHashMap<>();

    public JwtTokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-validity-seconds:3600}") long accessTokenValiditySeconds,
            @Value("${security.jwt.refresh-token-validity-seconds:1209600}") long refreshTokenValiditySeconds) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public String gerarAccessToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(accessTokenValiditySeconds);
        List<String> perfis = usuario.getPerfis().stream()
            .filter(Perfil::isAtivo)
            .map(Perfil::getNome)
            .map(String::trim)
            .map(String::toUpperCase)
            .distinct()
            .toList();
        List<String> roles = new ArrayList<>(perfis.size());
        for (String perfil : perfis) {
            roles.add("ROLE_" + perfil);
        }

        return Jwts.builder()
                .subject(usuario.getLogin())
                .claim("email", usuario.getEmail())
                .claim("status", usuario.getStatus().name())
                .claim("perfis", perfis)
                .claim("roles", roles)
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(signingKey)
                .compact();
    }

    public String gerarRefreshToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(refreshTokenValiditySeconds);
        String tokenId = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .subject(usuario.getLogin())
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
                .id(tokenId)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(signingKey)
                .compact();

        activeRefreshTokenIdBySubject.put(usuario.getLogin(), tokenId);
        return token;
    }

    public RefreshTokenClaims extrairRefreshTokenClaimsValido(String token) {
        var claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
        if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new JwtException("token invalido para refresh");
        }

        String tokenId = claims.getId();
        if (tokenId == null || tokenId.isBlank()) {
            throw new JwtException("refresh token invalido");
        }

        return new RefreshTokenClaims(claims.getSubject(), tokenId, claims.getExpiration().toInstant());
    }

    public void validarPoliticaRefreshToken(RefreshTokenClaims claims) {
        limparRevogadosExpirados();
        Instant revogadoAte = revokedRefreshTokenIds.get(claims.tokenId());
        if (revogadoAte != null && revogadoAte.isAfter(Instant.now())) {
            throw new JwtException("refresh token revogado");
        }

        String tokenAtivo = activeRefreshTokenIdBySubject.get(claims.subject());
        if (tokenAtivo != null && !tokenAtivo.equals(claims.tokenId())) {
            throw new JwtException("refresh token invalido");
        }
    }

    public void revogarRefreshToken(RefreshTokenClaims claims) {
        activeRefreshTokenIdBySubject.remove(claims.subject(), claims.tokenId());
        revokedRefreshTokenIds.put(claims.tokenId(), claims.expiresAt());
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extrairSubject(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String extrairSubjectSeRefreshTokenValido(String token) {
        return extrairRefreshTokenClaimsValido(token).subject();
    }

    public Map<String, Object> extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void limparRevogadosExpirados() {
        Instant agora = Instant.now();
        revokedRefreshTokenIds.entrySet().removeIf(entry -> !entry.getValue().isAfter(agora));
    }

}
