package com.flowpay.banking.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição de autenticação do usuário")
public class LoginRequest {

    @NotBlank(message = "Email é obrigatório")
    @Schema(description = "E-mail do usuário para autenticação", example = "usuario@exemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "Senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String senha;

    // Campos opcionais para 2FA
    @Schema(description = "Código de autenticação de dois fatores (se habilitado)", example = "123456", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String twoFactorCode;
}
