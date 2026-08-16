package com.pharmaguard.api.auth.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String login,
        String tipo,
        String status,
        List<PerfilResponse> perfis,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaAlteracao
) {
}
