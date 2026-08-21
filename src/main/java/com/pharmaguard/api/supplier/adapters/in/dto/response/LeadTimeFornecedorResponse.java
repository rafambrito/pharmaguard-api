package com.pharmaguard.api.supplier.adapters.in.dto.response;

import java.time.LocalDateTime;

public record LeadTimeFornecedorResponse(
        Long fornecedorId,
        Integer leadTimeDias,
        String classificacao,
        LocalDateTime dataUltimaAtualizacao) {
}