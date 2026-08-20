package com.pharmaguard.api.inventory.api.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarUnidadeMedidaRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_NOME_OBRIGATORIO)
        @Size(max = 100, message = MessageKeys.MSG_VALIDACAO_NOME_TAMANHO_MAXIMO)
        String nome,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_SIGLA_OBRIGATORIA)
        @Size(max = 10, message = MessageKeys.MSG_VALIDACAO_SIGLA_TAMANHO_MAXIMO)
        String sigla,
        Boolean ativo) {
}
