package com.pharmaguard.api.auth.adapters.in.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotBlank;

public record DefinirSenhaRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_LOGIN_OBRIGATORIO) String login,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_SENHA_OBRIGATORIA) String senha,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_CONFIRMACAO_SENHA_OBRIGATORIA) String confirmarSenha
) {
}