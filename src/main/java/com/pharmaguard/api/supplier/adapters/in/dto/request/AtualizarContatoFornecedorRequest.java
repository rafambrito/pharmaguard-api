package com.pharmaguard.api.supplier.adapters.in.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarContatoFornecedorRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_NOME_OBRIGATORIO)
        @Size(max = 150, message = MessageKeys.MSG_VALIDACAO_NOME_TAMANHO_MAXIMO)
        String nome,
        @Size(max = 100, message = MessageKeys.MSG_VALIDACAO_CARGO_TAMANHO_MAXIMO)
        String cargo,
        @Size(max = 40, message = MessageKeys.MSG_VALIDACAO_TELEFONE_TAMANHO_MAXIMO)
        String telefone,
        @Email(message = MessageKeys.MSG_VALIDACAO_EMAIL_FORMATO)
        @Size(max = 150, message = MessageKeys.MSG_VALIDACAO_EMAIL_TAMANHO_MAXIMO)
        String email,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_CANAL_PRINCIPAL_OBRIGATORIO)
        @Size(max = 20, message = MessageKeys.MSG_VALIDACAO_CANAL_PRINCIPAL_TAMANHO_MAXIMO)
        String canalPrincipal,
        Boolean ativo) {
}