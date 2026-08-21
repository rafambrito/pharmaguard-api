package com.pharmaguard.api.supplier.adapters.in.dto.response;

import java.time.LocalDateTime;

public record FornecedorResponse(
        Long id,
        String nome,
        String codigo,
        String documento,
        String observacao,
        Integer leadTimeDias,
        String statusLeadTime,
        boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaAlteracao) {
}