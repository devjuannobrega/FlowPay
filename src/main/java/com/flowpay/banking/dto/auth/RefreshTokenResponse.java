package com.flowpay.banking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de refresh token
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
}
