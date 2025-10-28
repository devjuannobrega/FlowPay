package com.flowpay.banking.dto.transaction;

import com.flowpay.banking.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para requisição de transferência
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para realizar transferência entre contas")
public class TransferRequest {

    @NotNull(message = "Conta de origem é obrigatória")
    @Schema(description = "ID da conta de origem da transferência", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sourceAccountId;

    @NotNull(message = "Conta de destino é obrigatória")
    @Schema(description = "ID da conta de destino da transferência", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long destinationAccountId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Schema(description = "Valor a ser transferido", example = "250.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @NotNull(message = "Tipo de transação é obrigatório")
    @Schema(description = "Tipo da transação", example = "TRANSFER", requiredMode = Schema.RequiredMode.REQUIRED)
    private TransactionType type;

    @Schema(description = "Descrição ou observação da transferência", example = "Pagamento de aluguel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    // Chave de idempotência para evitar duplicatas
    @Schema(description = "Chave de idempotência para prevenir transações duplicadas", example = "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String idempotencyKey;
}
