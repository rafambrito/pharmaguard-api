package com.pharmaguard.api.auth.api.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotBlank;

public record AutenticarRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_USUARIO_OBRIGATORIO) String usuario,
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_SENHA_OBRIGATORIA) String senha
) {
}