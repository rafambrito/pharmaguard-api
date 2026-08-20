package com.pharmaguard.api.inventory.api.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarCategoriaRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_NOME_OBRIGATORIO)
        @Size(max = 100, message = MessageKeys.MSG_VALIDACAO_NOME_TAMANHO_MAXIMO)
        String nome,
        @Size(max = 500, message = MessageKeys.MSG_VALIDACAO_DESCRICAO_TAMANHO_MAXIMO)
        String descricao,
        Boolean ativo) {
}
