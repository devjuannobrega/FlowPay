package com.flowpay.banking.dto.loan;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para solicitação de empréstimo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {

    @NotNull(message = "Conta é obrigatória")
    private Long accountId;

    @NotNull(message = "Valor solicitado é obrigatório")
    @DecimalMin(value = "100.00", message = "Valor mínimo é R$ 100,00")
    @DecimalMax(value = "100000.00", message = "Valor máximo é R$ 100.000,00")
    private BigDecimal requestedAmount;

    @NotNull(message = "Número de parcelas é obrigatório")
    @Min(value = 2, message = "Mínimo de 2 parcelas")
    @Max(value = 60, message = "Máximo de 60 parcelas")
    private Integer numberOfInstallments;

    @NotNull(message = "Renda mensal é obrigatória")
    @DecimalMin(value = "0.01", message = "Renda mensal deve ser positiva")
    private BigDecimal monthlyIncome;

    private String purpose; // Finalidade do empréstimo
}
