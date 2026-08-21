package com.pharmaguard.api.auth.adapters.in.controller.doc;

import com.pharmaguard.api.auth.adapters.in.dto.request.AutenticarRequest;
import com.pharmaguard.api.auth.adapters.in.dto.request.DefinirSenhaRequest;
import com.pharmaguard.api.auth.adapters.in.dto.request.RenovarSessaoRequest;
import com.pharmaguard.api.auth.adapters.in.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "API de autenticacao")
public interface AuthControllerDoc {

    @Operation(summary = "Autenticar", description = "Autentica usuario e senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisicao invalida")
    })
    ResponseEntity<TokenResponse> autenticar(
            @Valid @RequestBody(description = "Credenciais", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AutenticarRequest.class), examples = @ExampleObject(value = "{\"usuario\":\"admin\",\"senha\":\"Senha@123\"}"))) AutenticarRequest request);

    @Operation(summary = "Definir senha", description = "Define senha do usuario por login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha definida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisicao invalida")
    })
    ResponseEntity<Void> definirSenha(
            @Valid @RequestBody(description = "Dados para definir senha", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefinirSenhaRequest.class), examples = @ExampleObject(value = "{\"login\":\"admin\",\"senha\":\"Senha@123\",\"confirmarSenha\":\"Senha@123\"}"))) DefinirSenhaRequest request);

    @Operation(summary = "Renovar token", description = "Renova sessao a partir de refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisicao invalida")
    })
    ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody(description = "Refresh token", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RenovarSessaoRequest.class), examples = @ExampleObject(value = "{\"refreshToken\":\"token-valido\"}"))) RenovarSessaoRequest request);
}
