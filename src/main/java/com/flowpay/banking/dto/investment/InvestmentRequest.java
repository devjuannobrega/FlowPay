package com.flowpay.banking.dto.investment;

import com.flowpay.banking.enums.InvestmentType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para aplicação de investimento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRequest {

    @NotNull(message = "Conta é obrigatória")
    private Long accountId;

    @NotNull(message = "Tipo de investimento é obrigatório")
    private InvestmentType investmentType;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valorInvestido;

    @NotNull(message = "Taxa de rendimento é obrigatória")
    @DecimalMin(value = "0.00", message = "Taxa deve ser positiva")
    @DecimalMax(value = "100.00", message = "Taxa não pode exceder 100%")
    private BigDecimal taxaRendimentoAnual;

    private LocalDate maturityDate;
}
