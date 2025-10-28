package com.flowpay.banking.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para requisição de depósito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para realizar depósito em conta")
public class DepositRequest {

    @NotNull(message = "Conta é obrigatória")
    @Schema(description = "ID da conta que receberá o depósito", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long accountId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Schema(description = "Valor a ser depositado", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Descrição ou observação do depósito", example = "Depósito em dinheiro", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Chave de idempotência para prevenir depósitos duplicados", example = "b2c3d4e5-f6g7-8h9i-0j1k-l2m3n4o5p6q7", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idempotencyKey;
}
