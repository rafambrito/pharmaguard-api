package com.pharmaguard.api.auth.adapters.in.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
