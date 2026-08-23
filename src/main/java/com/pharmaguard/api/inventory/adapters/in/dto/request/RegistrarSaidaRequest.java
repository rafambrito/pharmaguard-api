package com.pharmaguard.api.inventory.adapters.in.dto.request;

import com.pharmaguard.api.inventory.domain.SaidaEstoque;
import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegistrarSaidaRequest(
        @NotNull(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO)
        @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO)
        Long unidadeId,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_MEDICAMENTO_ID_OBRIGATORIO)
        @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO)
        Long medicamentoId,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_QUANTIDADE_OBRIGATORIA)
        @Positive(message = MessageKeys.MSG_VALIDACAO_QUANTIDADE_MINIMA)
        Integer quantidade,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_MOTIVO_OBRIGATORIO)
        SaidaEstoque.Motivo motivo,
        @Size(max = 500, message = MessageKeys.MSG_VALIDACAO_OBSERVACAO_TAMANHO_MAXIMO)
        String observacao) {
}
