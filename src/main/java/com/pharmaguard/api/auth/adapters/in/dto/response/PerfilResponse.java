package com.pharmaguard.api.auth.adapters.in.dto.response;

public record PerfilResponse(
        Long id,
        String nome,
        String descricao,
        boolean ativo
) {
}
