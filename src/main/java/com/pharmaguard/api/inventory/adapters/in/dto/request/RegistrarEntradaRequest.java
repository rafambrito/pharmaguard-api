package com.pharmaguard.api.inventory.adapters.in.dto.request;

import com.pharmaguard.api.inventory.domain.EntradaEstoque;
import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RegistrarEntradaRequest(
        @NotNull(message = MessageKeys.MSG_VALIDACAO_MEDICAMENTO_ID_OBRIGATORIO)
        @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO)
        Long medicamentoId,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_LOTE_ID_OBRIGATORIO)
        @Positive(message = MessageKeys.MSG_VALIDACAO_ID_POSITIVO)
        Long loteId,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_QUANTIDADE_OBRIGATORIA)
        @Positive(message = MessageKeys.MSG_VALIDACAO_QUANTIDADE_MINIMA)
        Integer quantidade,
        @NotNull(message = MessageKeys.MSG_VALIDACAO_ORIGEM_OBRIGATORIA)
        EntradaEstoque.Origem origem,
        @Size(max = 120, message = MessageKeys.MSG_VALIDACAO_DOCUMENTO_ESTOQUE_TAMANHO_MAXIMO)
        String documento,
        @Size(max = 500, message = MessageKeys.MSG_VALIDACAO_OBSERVACAO_TAMANHO_MAXIMO)
        String observacao) {
}
