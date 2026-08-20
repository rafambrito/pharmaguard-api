package com.pharmaguard.api.inventory.api.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CadastrarLoteRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_NUMERO_LOTE_OBRIGATORIO)
        String numeroLote,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_DATA_VALIDADE_OBRIGATORIA)
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataValidade,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_QUANTIDADE_INICIAL_OBRIGATORIA)
        @Positive(message = MessageKeys.MSG_VALIDACAO_QUANTIDADE_INICIAL_MINIMA)
        Integer quantidadeInicial) {
}
