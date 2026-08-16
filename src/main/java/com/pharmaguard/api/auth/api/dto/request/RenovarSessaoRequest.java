package com.pharmaguard.api.auth.api.dto.request;

import com.pharmaguard.api.shared.config.MessageKeys;
import jakarta.validation.constraints.NotBlank;

public record RenovarSessaoRequest(
        @NotBlank(message = MessageKeys.MSG_VALIDACAO_REFRESH_TOKEN_OBRIGATORIO) String refreshToken
) {
}
