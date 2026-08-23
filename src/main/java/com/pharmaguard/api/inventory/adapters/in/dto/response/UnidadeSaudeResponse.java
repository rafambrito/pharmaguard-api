package com.pharmaguard.api.inventory.adapters.in.dto.response;

import com.pharmaguard.api.inventory.domain.UnidadeSaude;
import java.time.LocalDateTime;

public record UnidadeSaudeResponse(Long id, String identificacao, String nome, String tipo, String endereco,
        UnidadeSaude.Status status, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {
}