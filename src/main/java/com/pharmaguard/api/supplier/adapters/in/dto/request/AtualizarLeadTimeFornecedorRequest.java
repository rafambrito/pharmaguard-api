package com.pharmaguard.api.supplier.adapters.in.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AtualizarLeadTimeFornecedorRequest(
        @NotNull(message = MessageKeys.MSG_VALIDACAO_LEAD_TIME_OBRIGATORIO)
        @Min(value = 0, message = MessageKeys.MSG_VALIDACAO_LEAD_TIME_NEGATIVO)
        Integer leadTimeDias) {
}