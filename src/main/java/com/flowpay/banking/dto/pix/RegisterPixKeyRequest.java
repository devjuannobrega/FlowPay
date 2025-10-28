package com.flowpay.banking.dto.pix;

import com.flowpay.banking.enums.PixKeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registro de chave PIX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterPixKeyRequest {

    @NotNull(message = "Conta é obrigatória")
    private Long accountId;

    @NotNull(message = "Tipo de chave é obrigatório")
    private PixKeyType keyType;

    @NotBlank(message = "Valor da chave é obrigatório")
    private String keyValue;
}
