package com.pharmaguard.api.inventory.adapters.in.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarUnidadeSaudeRequest(
        @NotBlank @Size(max = 30) String identificacao,
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 50) String tipo,
        @NotBlank @Size(max = 300) String endereco) {
}