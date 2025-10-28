package com.flowpay.banking.dto.pix;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para transferência PIX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PixTransferRequest {

    @NotNull(message = "Conta de origem é obrigatória")
    private Long sourceAccountId;

    @NotBlank(message = "Chave PIX de destino é obrigatória")
    private String destinationPixKey;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;

    private String description;
    private String idempotencyKey;
}
