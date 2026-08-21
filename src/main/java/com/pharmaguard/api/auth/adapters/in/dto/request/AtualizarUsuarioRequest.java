package com.pharmaguard.api.auth.adapters.in.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_NOME_OBRIGATORIO) @Size(max = 100, message = MessageKeys.MSG_VALIDACAO_NOME_TAMANHO_MAXIMO) String nome,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_EMAIL_OBRIGATORIO) @Email(message = MessageKeys.MSG_VALIDACAO_EMAIL_FORMATO) @Size(max = 150, message = MessageKeys.MSG_VALIDACAO_EMAIL_TAMANHO_MAXIMO) String email,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_LOGIN_OBRIGATORIO) @Size(max = 50, message = MessageKeys.MSG_VALIDACAO_LOGIN_TAMANHO_MAXIMO) String login,
        @Pattern(regexp = "(?i)ATIVO|INATIVO|BLOQUEADO", message = MessageKeys.MSG_VALIDACAO_STATUS_INVALIDO) String status
) {
}
