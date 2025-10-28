package com.flowpay.banking.dto.investment;

import com.flowpay.banking.enums.InvestmentStatus;
import com.flowpay.banking.enums.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta de investimento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentResponse {

    private Long id;
    private Long accountId;
    private InvestmentType investmentType;
    private BigDecimal valorInvestido;
    private BigDecimal rendimentoAcumulado;
    private BigDecimal valorAtual;
    private BigDecimal taxaRendimentoAnual;
    private InvestmentStatus status;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private LocalDate redemptionDate;
    private LocalDateTime createdAt;
}
