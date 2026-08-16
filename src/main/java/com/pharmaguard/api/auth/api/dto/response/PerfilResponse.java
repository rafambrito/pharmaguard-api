package com.pharmaguard.api.auth.api.dto.response;

public record PerfilResponse(
        Long id,
        String nome,
        String descricao,
        boolean ativo
) {
}
