package com.pharmaguard.api.inventory.adapters.in.controller.doc;

import com.pharmaguard.api.inventory.adapters.in.dto.request.CriarUnidadeSaudeRequest;
import com.pharmaguard.api.inventory.adapters.in.dto.response.UnidadeSaudeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Unidades de Saude", description = "Cadastro de unidades de saude")
public interface UnidadeSaudeControllerDoc {
    @Operation(summary = "Cadastrar unidade de saude")
    ResponseEntity<UnidadeSaudeResponse> criar(@Valid CriarUnidadeSaudeRequest request);
    @Operation(summary = "Listar unidades de saude")
    ResponseEntity<List<UnidadeSaudeResponse>> listar();
    @Operation(summary = "Buscar unidade de saude")
    ResponseEntity<UnidadeSaudeResponse> buscar(Long id);
    @Operation(summary = "Atualizar unidade de saude")
    ResponseEntity<UnidadeSaudeResponse> atualizar(Long id, @Valid CriarUnidadeSaudeRequest request);
    @Operation(summary = "Inativar unidade de saude")
    ResponseEntity<Void> inativar(Long id);
}