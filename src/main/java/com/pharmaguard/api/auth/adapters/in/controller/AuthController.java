package com.pharmaguard.api.auth.adapters.in.controller;

import com.pharmaguard.api.auth.adapters.in.controller.doc.AuthControllerDoc;
import com.pharmaguard.api.auth.adapters.in.dto.request.AutenticarRequest;
import com.pharmaguard.api.auth.adapters.in.dto.request.DefinirSenhaRequest;
import com.pharmaguard.api.auth.adapters.in.dto.request.RenovarSessaoRequest;
import com.pharmaguard.api.auth.adapters.in.dto.response.TokenResponse;
import com.pharmaguard.api.auth.application.AutenticarUsuarioUseCase;
import com.pharmaguard.api.auth.application.DefinirSenhaUseCase;
import com.pharmaguard.api.auth.application.RenovarSessaoUseCase;
import com.pharmaguard.api.auth.application.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerDoc {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final DefinirSenhaUseCase definirSenhaUseCase;
    private final RenovarSessaoUseCase renovarSessaoUseCase;
    private final TokenService jwtTokenService;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase,
            DefinirSenhaUseCase definirSenhaUseCase,
            RenovarSessaoUseCase renovarSessaoUseCase,
            TokenService jwtTokenService) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.definirSenhaUseCase = definirSenhaUseCase;
        this.renovarSessaoUseCase = renovarSessaoUseCase;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> autenticar(@Valid @RequestBody AutenticarRequest request) {
        var usuario = autenticarUsuarioUseCase.autenticar(request.usuario(), request.senha());
        var accessToken = jwtTokenService.gerarAccessToken(usuario);
        var refreshToken = jwtTokenService.gerarRefreshToken(usuario);
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken, "Bearer"));
    }

    @Override
    @PostMapping("/definir-senha")
    public ResponseEntity<Void> definirSenha(@Valid @RequestBody DefinirSenhaRequest request) {
        definirSenhaUseCase.definirSenha(request.login(), request.senha(), request.confirmarSenha());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RenovarSessaoRequest request) {
        TokenService.RefreshTokenClaims refreshClaims;
        try {
            refreshClaims = jwtTokenService.extrairRefreshTokenClaimsValido(request.refreshToken());
            jwtTokenService.validarPoliticaRefreshToken(refreshClaims);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("sessao invalida ou expirada");
        }

        var usuario = renovarSessaoUseCase.renovarSessao(refreshClaims.subject());
        jwtTokenService.revogarRefreshToken(refreshClaims);
        var accessToken = jwtTokenService.gerarAccessToken(usuario);
        var refreshToken = jwtTokenService.gerarRefreshToken(usuario);
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken, "Bearer"));
    }
}
