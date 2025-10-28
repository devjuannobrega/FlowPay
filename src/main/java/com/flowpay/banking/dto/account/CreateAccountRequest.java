package com.flowpay.banking.dto.account;

import com.flowpay.banking.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para requisição de criação de conta
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para criação de nova conta bancária")
public class CreateAccountRequest {

    @NotNull(message = "Tipo de conta é obrigatório")
    @Schema(description = "Tipo de conta bancária a ser criada", example = "CORRENTE", requiredMode = Schema.RequiredMode.REQUIRED)
    private AccountType tipo;

    @DecimalMin(value = "0.00", message = "Limite diário deve ser positivo")
    @Schema(description = "Limite diário para transações", example = "5000.00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal limiteDiario;

    // Depósito inicial (opcional)
    @DecimalMin(value = "0.00", message = "Depósito inicial deve ser positivo")
    @Schema(description = "Valor do depósito inicial (opcional)", example = "1000.00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BigDecimal depositoInicial;
}
