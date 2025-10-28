package com.flowpay.banking.dto.auth;

import com.flowpay.banking.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para resposta de login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta contendo informações de autenticação do usuário")
public class LoginResponse {

    @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Token para renovação de autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Builder.Default
    @Schema(description = "Tipo do token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "ID único do usuário", example = "1")
    private Long userId;

    @Schema(description = "E-mail do usuário", example = "usuario@exemplo.com")
    private String email;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String nomeCompleto;

    @Schema(description = "Perfis de acesso do usuário", example = "[\"USER\", \"ADMIN\"]")
    private Set<UserRole> roles;

    @Schema(description = "Indica se autenticação de dois fatores é necessária", example = "false")
    private boolean twoFactorRequired;
}
