package com.pharmaguard.api.supplier.adapters.in.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarFornecedorRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_NOME_OBRIGATORIO)
        @Size(max = 150, message = MessageKeys.MSG_VALIDACAO_NOME_TAMANHO_MAXIMO)
        String nome,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_CODIGO_OBRIGATORIO)
        @Size(max = 50, message = MessageKeys.MSG_VALIDACAO_CODIGO_TAMANHO_MAXIMO)
        String codigo,
        @Size(max = 100, message = MessageKeys.MSG_VALIDACAO_DOCUMENTO_TAMANHO_MAXIMO)
        String documento,
        @Size(max = 500, message = MessageKeys.MSG_VALIDACAO_DESCRICAO_TAMANHO_MAXIMO)
        String observacao,
        Integer leadTimeDias,
        Boolean ativo) {
}